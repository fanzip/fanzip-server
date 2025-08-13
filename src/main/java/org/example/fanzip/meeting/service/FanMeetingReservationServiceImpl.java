package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.meeting.domain.*;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.dto.PaymentIntentResponseDTO;
import org.example.fanzip.meeting.dto.SeatHold;
import org.example.fanzip.meeting.mapper.FanMeetingMapper;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.service.PaymentService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FanMeetingReservationServiceImpl implements FanMeetingReservationService {

    private final FanMeetingReservationMapper reservationMapper;
    private final FanMeetingSeatMapper seatMapper;
    private final RedissonClient redissonClient;
    private final FanMeetingMapper meetingMapper;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public FanMeetingReservationResponseDTO reserveSeat(Long meetingId, Long seatId, Long userId) {

        String seatLockKey = "lock:seat:" + seatId;
        String userMeetingLockKey = "lock:meeting:" + meetingId + ":user:" + userId;
        RLock seatLock = redissonClient.getLock(seatLockKey);
        RLock userMeetingLock = redissonClient.getLock(userMeetingLockKey);

        boolean seatLocked = false, userLocked = false;
        try {
            userLocked = userMeetingLock.tryLock(3, 5, TimeUnit.SECONDS);
            if (!userLocked) throw new IllegalStateException("예약 처리 중입니다. 잠시 후 다시 시도해주세요.");

            seatLocked = seatLock.tryLock(3, 5, TimeUnit.SECONDS);
            if (!seatLocked) throw new IllegalStateException("좌석이 잠시 점유 중입니다. 잠시 후 다시 시도해주세요.");

            // 1) 이미 예약했는지 (1인1좌석 - PENDING 또는 RESERVED 상태)
            if (reservationMapper.existAnyReservationByUserAndMeeting(userId, meetingId)) {
                throw new IllegalStateException("이미 예약 중이거나 예약 완료한 팬미팅입니다.");
            }

            // 2) 좌석 검증 (존재 + 미팅 일치 + 미예약)
            FanMeetingSeatVO seat = seatMapper.findById(seatId);
            if (seat == null || !meetingId.equals(seat.getMeetingId())) {
                throw new IllegalStateException("잘못된 좌석입니다.");
            }
            if (seat.isReserved()) {
                throw new IllegalStateException("이미 선점된 좌석입니다.");
            }

            // 3) 미팅/오픈시간/상태 체크 (유저등급은 JWT/DB)
            // (예시) meetingMapper.findOpenInfo(meetingId) 로 가져왔다고 가정
            var open = meetingMapper.findOpenInfo(meetingId); // status, vip/gold/... times
            if (open == null || open.getStatus() != FanMeetingStatus.PLANNED) {
                throw new IllegalStateException("예약 불가한 팬미팅입니다.");
            }
            UserGrade grade = /* JWT or DB에서 */ UserGrade.VIP; // TODO 실제 로딩
            LocalDateTime now = LocalDateTime.now(); // 서버 TZ 설정 확인
            LocalDateTime openTime = switch (grade) {
                case VIP -> open.getVipOpenTime();
                case GOLD -> open.getGoldOpenTime();
                case SILVER -> open.getSilverOpenTime();
                case WHITE -> open.getWhiteOpenTime();
                case GENERAL -> open.getGeneralOpenTime();
            };
            if (now.isBefore(openTime)) {
                throw new IllegalStateException("등급 오픈 전입니다.");
            }

            // 4) 좌석 선점(낙관적 락)
            int updated = seatMapper.updateSeatWithVersionCheck(seatId, true, seat.getVersion());
            if (updated == 0) {
                throw new IllegalStateException("좌석 예약 실패: 다른 유저가 먼저 예약했습니다.");
            }

            // 5) available_seats 감소(원자적, 언더플로 방지)
            int dec = meetingMapper.decrementAvailableSeats(meetingId);
            if (dec == 0) {
                // 좌석은 잡았는데 수량 감소 실패 → 좌석 롤백 후 에러
                seatMapper.updateSeatWithVersionCheck(seatId, false, seat.getVersion() + 1);
                throw new IllegalStateException("잔여 좌석이 없습니다.");
            }

            // 6) 예약 레코드 생성
            FanMeetingReservationVO vo = new FanMeetingReservationVO();
            vo.setMeetingId(meetingId);
            vo.setUserId(userId);
            vo.setInfluencerId(meetingMapper.findInfluencerIdByMeetingId(meetingId));
            vo.setSeatId(seatId);
            // 외부(프론트/결제사) 연동 시 내부 PK 대신 안전하게 노출하기 위해 설계한 식별자
            // 현재 결제 연동은 reservationId(PK) 기반으로 구현되어 있어 실제로는 사용되지 않음
            // 추후 외부 식별자 전환 시 활용할 수 있도록 UUID로 발급 후 DB에 저장
            vo.setReservationNumber(UUID.randomUUID().toString());
            vo.setStatus(ReservationStatus.RESERVED);
            vo.setReservedAt(LocalDateTime.now());
            reservationMapper.insertReservation(vo);

            return new FanMeetingReservationResponseDTO(
                    vo.getReservationId(),
                    vo.getReservationNumber(),
                    vo.getStatus(),
                    vo.getSeatId()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("좌석 예약 도중 인터럽트");
        } finally {
            if (seatLocked && seatLock.isHeldByCurrentThread()) seatLock.unlock();
            if (userLocked && userMeetingLock.isHeldByCurrentThread()) userMeetingLock.unlock();
        }
    }

    @Override
    @Transactional
    public void cancelReservation(Long meetingId, Long userId) {
        // 본인 예약 찾기
        var res = reservationMapper.findByUserAndMeeting(userId, meetingId);
        if (res == null || res.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalStateException("예약 내역이 존재하지 않거나 이미 취소됨");
        }

        // 좌석 낙관적 해제
        FanMeetingSeatVO seat = seatMapper.findById(res.getSeatId());
        int seatUpd = seatMapper.updateSeatWithVersionCheck(seat.getSeatId(), false, seat.getVersion());
        if (seatUpd == 0) throw new IllegalStateException("좌석 상태 갱신 실패");

        // available_seats +1
        int inc = meetingMapper.incrementAvailableSeats(meetingId);
        if (inc == 0) throw new IllegalStateException("좌석 수 복구 실패");

        // 예약 상태 변경
        reservationMapper.updateStatusToCancelled(res.getReservationId(), LocalDateTime.now());
    }

    public boolean hasReserved(Long meetingId, Long userId) {
        return reservationMapper.existsByMeetingIdAndUserId(meetingId, userId);
    }

    @Override
    @Transactional
    public PaymentIntentResponseDTO startPayment(Long meetingId, Long seatId, Long userId) {
        log.info("startPayment(meetingId={}, seatId={}, userId={})", meetingId, seatId, userId);

        // 1인 1좌석(이미 확정 예약 여부)
        if (reservationMapper.existConfirmedByUserAndMeeting(userId, meetingId)) {
            throw new IllegalStateException("이미 예약 완료한 팬미팅입니다.");
        }

        // 좌석 검증
        FanMeetingSeatVO seat = seatMapper.findById(seatId);
        if (seat == null || !meetingId.equals(seat.getMeetingId())) {
            throw new IllegalStateException("잘못된 좌석입니다.");
        }
        if (seat.isReserved()) {
            throw new IllegalStateException("이미 선점된 좌석입니다.");
        }

        // 오픈시간/상태 검증
        var open = meetingMapper.findOpenInfo(meetingId);
        if (open == null || open.getStatus() != FanMeetingStatus.PLANNED) {
            throw new IllegalStateException("예약 불가한 팬미팅입니다.");
        }
        UserGrade grade = UserGrade.GENERAL;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime openTime = switch (grade) {
            case VIP -> open.getVipOpenTime();
            case GOLD -> open.getGoldOpenTime();
            case SILVER -> open.getSilverOpenTime();
            case WHITE -> open.getWhiteOpenTime();
            case GENERAL -> open.getGeneralOpenTime();
        };
        if (now.isBefore(openTime)) {
            throw new IllegalStateException("등급 오픈 전입니다.");
        }

        Long influencerId = meetingMapper.findInfluencerIdByMeetingId(meetingId);
        if (influencerId == null) throw new IllegalStateException("팬미팅의 인플루언서를 찾을 수 없습니다.");

        // Redis 홀드(10분)
        var bucket = redissonClient.<SeatHold>getBucket(holdKey(seatId));
        boolean ok = bucket.trySet(new SeatHold(userId, meetingId, seat.getVersion()),
                10, TimeUnit.MINUTES);
        if (!ok) throw new IllegalStateException("이미 결제 진행 중인 좌석입니다.");
        long ttlSec = bucket.remainTimeToLive() / 1000;

        // available_seats 감소 (PENDING 상태에서 좌석 차감)
        int dec = meetingMapper.decrementAvailableSeats(meetingId);
        if (dec == 0) {
            // 좌석 수 감소 실패 → Redis 홀드 롤백 후 에러
            redissonClient.getBucket(holdKey(seatId)).delete();
            throw new IllegalStateException("잔여 좌석이 없습니다.");
        }

        // 예약 PENDING 선행 생성
        FanMeetingReservationVO pending = new FanMeetingReservationVO();
        pending.setMeetingId(meetingId);
        pending.setInfluencerId(influencerId);
        pending.setUserId(userId);
        pending.setSeatId(seatId);
        pending.setReservationNumber(UUID.randomUUID().toString());
        pending.setStatus(ReservationStatus.PENDING);
        pending.setReservedAt(LocalDateTime.now());
        reservationMapper.insertPending(pending); // useGeneratedKeys로 reservationId 채워짐

        Long reservationId = pending.getReservationId();
        if (reservationId == null) {
            reservationId = reservationMapper.findIdByReservationNumber(pending.getReservationNumber());
            if (reservationId == null) {
                redissonClient.getBucket(holdKey(seatId)).delete();
                throw new IllegalStateException("예약 ID 발급 실패");
            }
            pending.setReservationId(reservationId);
        }

        // 결제의도 생성 (RESERVATION, orderId=null)
        PaymentRequestDto req = PaymentRequestDto.builder()
                .userId(userId)
                .orderId(null)
                .reservationId(reservationId)   // ★ 유일하게 채움
                .membershipId(null)
                .transactionId(null)
                .paymentType(PaymentType.RESERVATION)
                .paymentMethod(PaymentMethod.TOSSPAY)
                .amount(seat.getPrice())
                .build();

        var pay = paymentService.createPayment(req);

        // 응답
        return PaymentIntentResponseDTO.builder()
                .paymentId(pay.getPaymentId())
                .amount(seat.getPrice())
                .ttlSeconds(ttlSec)
                .reservationId(reservationId)
                .build();
    }


    @Override
    @Transactional
    public void confirmByPaymentId(Long paymentId) {
        var r = reservationMapper.findByPaymentId(paymentId);
        if (r == null) return;
        if (r.getStatus() == ReservationStatus.RESERVED) return; // 멱등

        var hold = redissonClient.<SeatHold>getBucket(holdKey(r.getSeatId())).get();
        if (hold == null || !hold.getUserId().equals(r.getUserId()) || !hold.getMeetingId().equals(r.getMeetingId()))
            throw new IllegalStateException("홀드가 유효하지 않습니다.");

        var seat = seatMapper.findById(r.getSeatId());
        int ok = seatMapper.updateSeatWithVersionCheck(seat.getSeatId(), true, hold.getVersion());
        if (ok == 0) throw new IllegalStateException("좌석 확정 실패");

        // available_seats는 이미 startPayment에서 차감됨 (중복 차감 제거)

        reservationMapper.markConfirmed(r.getReservationId(), LocalDateTime.now());
        redissonClient.getBucket(holdKey(r.getSeatId())).delete();
    }

    @Override
    @Transactional
    public void cancelByPaymentId(Long paymentId) {
        var r = reservationMapper.findByPaymentId(paymentId);
        if (r == null) return;
        
        // PENDING -> CANCELLED 변경
        reservationMapper.updateStatusToCancelled(r.getReservationId(), LocalDateTime.now());
        
        // Redis 홀드 삭제
        redissonClient.getBucket(holdKey(r.getSeatId())).delete();
        
        // startPayment에서 차감된 좌석 수 복구
        meetingMapper.incrementAvailableSeats(r.getMeetingId());
    }

    private String holdKey(Long seatId) {
        return "hold:seat:" + seatId;
    }
}