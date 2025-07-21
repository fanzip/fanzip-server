package org.example.fanzip.repository;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.dto.PaymentsDto;
import org.example.fanzip.mapper.PaymentsMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PaymentsRepository {
    private final PaymentsMapper paymentsMapper;

    public void save(PaymentsDto paymentsDto){
        paymentsMapper.insertPayment(paymentsDto);
    }
    public PaymentsDto findById(Long paymentId) {
        return paymentsMapper.selectPayment(paymentId);
    }

    public void updateStatus(Long paymentId, String status) {
        Map<String, Object> param = Map.of(
                "paymentId", paymentId,
                "status", status
        );
        paymentsMapper.updatePayment(param);
    }
}
