package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.domain.ReservationStatus;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
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


    @Override
    @Transactional
    public FanMeetingReservationResponseDTO reserveSeat(Long meetingId, Long seatId, Long userId) {

        String lockKey = "lock:seat:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("좌석 예약 중입니다. 잠시 후 다시 시도해주세요.");
            }

            if (reservationMapper.existByUserAndMeeting(userId, meetingId)) {
                throw new IllegalStateException("이미 예약한 팬미팅입니다.");
            }

            FanMeetingSeatVO seat = seatMapper.findById(seatId);
            if (seat == null || seat.isReserved()) {
                throw new IllegalStateException("존재하지 않거나 이미 예약한 좌석입니다.");
            }

            int updated = seatMapper.updateSeatWithVersionCheck(seatId, true, seat.getVersion());
            if (updated == 0) {
                throw new IllegalStateException("좌석 예약 실패: 다른 유저가 먼저 예약했을 수 있습니다.");
            }

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
            throw new IllegalStateException("좌석 예약 도중 인터럽트 발생");
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public void cancelReservation(Long meetingId, Long userId) {
        FanMeetingReservationVO reservation = reservationMapper.findByUserAndMeeting(userId, meetingId);

        if (reservation == null) {
            throw new IllegalStateException("예약 내역이 존재하지 않습니다.");
        }

        reservationMapper.updateStatusToCanceled(reservation.getReservationId(), LocalDateTime.now());
        seatMapper.updateSeatReservation(reservation.getSeatId(), false);
    }
}