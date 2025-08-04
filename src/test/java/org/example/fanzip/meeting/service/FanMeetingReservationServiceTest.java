package org.example.fanzip.meeting.service;

import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.domain.ReservationStatus;
import org.example.fanzip.meeting.dto.FanMeetingReservationResponseDTO;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FanMeetingReservationServiceTest {

    @Mock  RedissonClient redissonClient;
    @Mock  RLock lock;

    @Mock  FanMeetingReservationMapper reservationMapper;
    @Mock  FanMeetingSeatMapper       seatMapper;

    @InjectMocks
    FanMeetingReservationServiceImpl service;

    private final Long MEETING_ID = 1L;
    private final Long USER_ID    = 777L;
    private final Long SEAT_ID    = 10L;

    @BeforeEach
    void setUp() {}

    private void stubLock() throws InterruptedException {
        when(redissonClient.getLock("lock:seat:" + SEAT_ID)).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);  // 반드시 있어야 함
        when(lock.isHeldByCurrentThread()).thenReturn(true);
    }

    @Test
    @DisplayName("reserveSeat: 예약 성공 시 예약 번호와 좌석 ID가 정상 반환")
    void reserveSeat_success() throws Exception {
        stubLock();
        FanMeetingSeatVO seat = dummySeat(false, 0);
        when(seatMapper.findById(SEAT_ID)).thenReturn(seat);
        when(seatMapper.updateSeatWithVersionCheck(SEAT_ID, true, 0)).thenReturn(1);
        when(reservationMapper.existByUserAndMeeting(USER_ID, MEETING_ID)).thenReturn(false);
        doAnswer(inv -> { ((FanMeetingReservationVO)inv.getArgument(0)).setReservationId(123L); return 1; })
                .when(reservationMapper).insertReservation(any());

        FanMeetingReservationResponseDTO dto =
                service.reserveSeat(MEETING_ID, SEAT_ID, USER_ID);

        assertThat(dto)
                .extracting("seatId", "status")
                .containsExactly(SEAT_ID, ReservationStatus.RESERVED);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("reserveSeat: 이미 예약한 유저가 다시 예약하면 예외가 발생한다")
    void reserveSeat_alreadyReservedByUser() throws Exception {
        stubLock();
        when(reservationMapper.existByUserAndMeeting(USER_ID, MEETING_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.reserveSeat(MEETING_ID, SEAT_ID, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 예약한 팬미팅");
        verify(lock).unlock();
    }

    @Test
    @DisplayName("reserveSeat: 낙관적 락 충돌 시 예외가 발생한다")
    void reserveSeat_optimisticLockFail() throws Exception {
        stubLock();
        FanMeetingSeatVO seat = dummySeat(false, 0);
        when(seatMapper.findById(SEAT_ID)).thenReturn(seat);
        when(seatMapper.updateSeatWithVersionCheck(SEAT_ID, true, 0)).thenReturn(0);
        when(reservationMapper.existByUserAndMeeting(USER_ID, MEETING_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.reserveSeat(MEETING_ID, SEAT_ID, USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("좌석 예약 실패");
        verify(lock).unlock();
    }

    private FanMeetingSeatVO dummySeat(boolean reserved, int version) {
        FanMeetingSeatVO s = new FanMeetingSeatVO();
        s.setSeatId(SEAT_ID);
        s.setMeetingId(MEETING_ID);
        s.setReserved(reserved);
        s.setVersion(version);
        s.setPrice(BigDecimal.valueOf(33000));
        s.setSeatNumber("A-1");
        s.setSeatType("VIP");
        s.setCreatedAt(LocalDateTime.now());
        return s;
    }

    @Test
    @DisplayName("cancelReservation: 정상 취소되면 좌석 상태도 false로 변경된다")
    void cancelReservation_success() {
        // given – 예약 VO 모킹
        var reservationVO = new org.example.fanzip.meeting.domain.FanMeetingReservationVO();
        reservationVO.setReservationId(123L);
        reservationVO.setMeetingId(MEETING_ID);
        reservationVO.setSeatId(SEAT_ID);
        reservationVO.setUserId(USER_ID);
        reservationVO.setStatus(ReservationStatus.RESERVED);
        reservationVO.setReservationNumber(UUID.randomUUID().toString());

        when(reservationMapper.findByUserAndMeeting(USER_ID, MEETING_ID))
                .thenReturn(reservationVO);

        // when
        service.cancelReservation(MEETING_ID, USER_ID);

        // then
        verify(reservationMapper)
                .updateStatusToCanceled(eq(123L), any(LocalDateTime.class));
        verify(seatMapper).updateSeatReservation(SEAT_ID, false);
    }
}
