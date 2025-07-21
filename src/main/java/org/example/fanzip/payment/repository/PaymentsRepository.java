package org.example.fanzip.payment.repository;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PaymentsRepository {
    private final PaymentsMapper paymentsMapper;


    public void save(Payments payments){
        paymentsMapper.insertPayment(payments);
    }

    public Payments findById(Long paymentId) {
        return paymentsMapper.selectPayment(paymentId);
    }

    public void updateStatus(Long paymentId, PaymentStatus status) {
        Map<String, Object> param = new HashMap<>();
        param.put("paymentId", paymentId);
        param.put("status", status.name()); // enum to String (DB 저장용)

        paymentsMapper.updatePayment(param);
    }
}
