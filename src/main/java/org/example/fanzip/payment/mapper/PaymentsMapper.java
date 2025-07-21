package org.example.fanzip.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.fanzip.payment.dto.PaymentsDto;

import java.util.Map;

@Mapper
public interface PaymentsMapper {
    void insertPayment(PaymentsDto paymentsDto);
    PaymentsDto selectPayment(Long paymentId);
    void updatePayment(Map<String, Object> param);
}
