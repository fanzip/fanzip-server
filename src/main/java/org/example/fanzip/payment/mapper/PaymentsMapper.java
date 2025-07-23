package org.example.fanzip.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.fanzip.payment.domain.Payments;

import java.util.Map;

@Mapper
public interface PaymentsMapper {
    void insertPayment(Payments payments);
    Payments selectPayment(Long paymentId);
    void updatePayment(Map<String, Object> param);
    boolean existsByTransactionId(String transactionId);
}
