package org.example.fanzip.payment.repository;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.mapper.PaymentsMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
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

    public List<Payments> findByUserId(Long userId){
        return paymentsMapper.selectPaymentsByUserId(userId);
    }

    public void updateStatus(Long paymentId, PaymentStatus status) {
        Map<String, Object> param = new HashMap<>();
        param.put("paymentId", paymentId);
        param.put("status", status.name()); // enum to String (DB 저장용)

        paymentsMapper.updatePayment(param);
    }
    public boolean existsByTransactionId(String transactionId){
        return paymentsMapper.existsByTransactionId(transactionId);
    }

    public boolean existsMembershipPayment(Long userId, Long membershipId) {
        return paymentsMapper.existsByMembershipId(userId, membershipId);
    }
}
