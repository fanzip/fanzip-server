package org.example.fanzip.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.dto.RevenueResponseDto;

import java.util.List;

@Mapper
public interface PaymentMapper {
    void insertPayment(Payments payments);

    // 결제 조회
    Payments selectPaymentById(@Param("paymentId") Long paymentId);

    // ★ 추가: 결제 행 잠금 조회
    Payments selectPaymentForUpdate(@Param("paymentId") Long paymentId);

    List<Payments> selectPaymentsByUserId(Long userId);
    void updatePayment(Payments payments);
    boolean existsByTransactionId(String transactionId);
    boolean existsByMembershipId(@Param("userId") Long userId, @Param("membershipId") Long membershipId);
    List<Payments> findAllByReservationId(Long reservationId);
    List<RevenueResponseDto> selectMonthlyRevenue(@Param("influencerId") Long influencerId);
    RevenueResponseDto selectTodayRevenue(@Param("influencerId") Long influencerId);
    RevenueResponseDto selectTotalRevenue(@Param("influencerId") Long influencerId);
}
