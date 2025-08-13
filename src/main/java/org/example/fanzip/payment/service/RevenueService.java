package org.example.fanzip.payment.service;

import org.example.fanzip.payment.dto.RevenueResponseDto;

import java.util.List;

public interface RevenueService {
    List<RevenueResponseDto> getMonthlyRevenue(Long influencerId);
    RevenueResponseDto getTodayRevenue(Long influencerId);
    RevenueResponseDto getTotalRevenue(Long influencerId);
}