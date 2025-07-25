package org.example.fanzip.payment.repository;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.domain.Payments;
import org.example.fanzip.payment.domain.enums.PaymentStatus;
import org.example.fanzip.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PaymentRepository {
    private final PaymentMapper paymentMapper;


    public void save(Payments payments){
        paymentMapper.insertPayment(payments);
    }

    public Payments findById(Long paymentId) {
        return paymentMapper.selectPayment(paymentId);
    }

    public List<Payments> findByUserId(Long userId){
        return paymentMapper.selectPaymentsByUserId(userId);
    }

    public void updateStatus(Long paymentId, PaymentStatus status) {
        Map<String, Object> param = new HashMap<>();
        param.put("paymentId", paymentId);
        param.put("status", status.name()); // enum to String (DB 저장용)

        paymentMapper.updatePayment(param);
    }
    public boolean existsByTransactionId(String transactionId){
        return paymentMapper.existsByTransactionId(transactionId);
    }

    public boolean existsMembershipPayment(Long userId, Long membershipId) {
        return paymentMapper.existsByMembershipId(userId, membershipId);
    }
    public List<Payments> findAllByReservationId(Long reservationId) {
        return paymentMapper.findAllByReservationId(reservationId);
    }
}
