package org.example.fanzip.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.fanzip.dto.PaymentsDto;

public interface PaymentMapper {
    int insertPayment(PaymentsDto paymentsDto); // 결제 등록

    PaymentsDto selectPayment(Long paymentId); // 결제 내역 조회

    int updatePayment(@Param("paymentId") Long paymentId, @Param("status") PaymentsDto.Status status); // 상태 업데이트
    //d delete
}
