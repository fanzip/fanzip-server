package org.example.fanzip.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.RevenueResponseDto;

import java.util.List;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payments payments);
    Payments selectPayment(Long paymentId);
    List<Payments> selectPaymentsByUserId(Long userId);
    void updatePayment(Payments payments);
    boolean existsByTransactionId(String transactionId);
    boolean existsByMembershipId(@Param("userId") Long userId, @Param("membershipId") Long membershipId);
    List<Payments> findAllByReservationId(Long reservationId);
    List<RevenueResponseDto> selectMonthlyRevenue(@Param("influencerId") Long influencerId);
    RevenueResponseDto selectTodayRevenue(@Param("influencerId") Long influencerId);
    RevenueResponseDto selectTotalRevenue(@Param("influencerId") Long influencerId);
}
