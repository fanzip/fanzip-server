package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.*;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.mapper.FanMeetingMapper;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FanMeetingReservationServiceImpl implements FanMeetingReservationService {

    private final FanMeetingReservationMapper reservationMapper;
    private final FanMeetingSeatMapper seatMapper;
    private final RedissonClient redissonClient;
    private final FanMeetingMapper meetingMapper;

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

            // 1) 이미 예약했는지 (1인1좌석)
            if (reservationMapper.existByUserAndMeeting(userId, meetingId)) {
                throw new IllegalStateException("이미 예약한 팬미팅입니다.");
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
            vo.setSeatId(seatId);
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
        reservationMapper.updateStatusToCanceled(res.getReservationId(), LocalDateTime.now());
    }

    public boolean hasReserved(Long meetingId, Long userId) {
        return reservationMapper.existsByMeetingIdAndUserId(meetingId, userId);
    }
}