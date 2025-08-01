package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.domain.ReservationStatus;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FanMeetingReservationServiceImpl implements FanMeetingReservationService {

    private final FanMeetingReservationMapper reservationMapper;
    private final FanMeetingSeatMapper seatMapper;

    @Override
    @Transactional
    public FanMeetingReservationResponseDTO reserveSeat(Long meetingId, Long seatId, Long userId) {

        // 이미 이 팬미팅을 예약했는지 확인
        if (reservationMapper.existByUserAndMeeting(userId, meetingId)) {
            throw new IllegalStateException("이미 예약한 팬미팅입니다.");
        }

        // 현재 좌석 정보 조회 (version 포함)
        FanMeetingSeatVO seat = seatMapper.findById(seatId);
        System.out.println("seat version = " + seat.getVersion());
        if (seat == null || seat.isReserved()) {
            throw new IllegalStateException("존재하지 않거나 이미 예약된 좌석입니다.");
        }

        // 낙관적 락 기반 업데이트 시도
        int updated = seatMapper.updateSeatWithVersionCheck(seatId, true, seat.getVersion());
        if (updated == 0) {
            throw new IllegalStateException("좌석 예약 실패: 다른 유저가 먼저 예약했을 수 있습니다.");
        }

        // 예약 객체 생성 및 저장
        FanMeetingReservationVO vo = new FanMeetingReservationVO();
        vo.setMeetingId(meetingId);
        vo.setUserId(userId);
        vo.setSeatId(seatId);
        vo.setReservationNumber(UUID.randomUUID().toString());
        vo.setQrCode("QR-" + UUID.randomUUID());
        vo.setStatus(ReservationStatus.RESERVED);
        vo.setReservedAt(LocalDateTime.now());

        reservationMapper.insertReservation(vo);

        // 응답 반환
        return new FanMeetingReservationResponseDTO(
                vo.getReservationId(),
                vo.getReservationNumber(),
                vo.getQrCode(),
                vo.getStatus(),
                vo.getSeatId()
        );
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