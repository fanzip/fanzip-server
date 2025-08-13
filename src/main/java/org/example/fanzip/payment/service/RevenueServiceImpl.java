package org.example.fanzip.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.payment.dto.RevenueResponseDto;
import org.example.fanzip.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    
    private final PaymentRepository paymentRepository;

    @Override
    public List<RevenueResponseDto> getMonthlyRevenue(Long influencerId) {
        return paymentRepository.findMonthlyRevenue(influencerId);
    }

    @Override
    public RevenueResponseDto getTodayRevenue(Long influencerId) {
        return paymentRepository.findTodayRevenue(influencerId);
    }

    @Override
    public RevenueResponseDto getTotalRevenue(Long influencerId) {
        return paymentRepository.findTotalRevenue(influencerId);
    }
}