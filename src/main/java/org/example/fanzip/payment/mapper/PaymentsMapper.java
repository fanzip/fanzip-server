package org.example.fanzip.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.payment.domain.Payments;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentsMapper {
    void insertPayment(Payments payments);
    Payments selectPayment(Long paymentId);
    List<Payments> selectPaymentsByUserId(Long userId);
    void updatePayment(Map<String, Object> param);
    boolean existsByTransactionId(String transactionId);
    boolean existsByMembershipId(@Param("userId") Long userId, @Param("membershipId") Long membershipId);
}
