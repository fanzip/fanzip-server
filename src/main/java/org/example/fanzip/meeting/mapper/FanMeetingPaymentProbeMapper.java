package org.example.fanzip.meeting.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FanMeetingPaymentProbeMapper {
    List<Long> findPaidReservationPaymentIdsNeedingConfirm(@Param("limit") int limit);
    List<Long> findFailedOrCancelledReservationPaymentIdsNeedingCancel(@Param("limit") int limit);
}