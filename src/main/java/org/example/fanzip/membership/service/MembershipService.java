package org.example.fanzip.membership.service;


import org.example.fanzip.membership.dto.MembershipGradeDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.dto.UserMembershipInfoDTO;

import java.util.List;


public interface MembershipService{


    /**
     * 멤버십 구독 처리
     */
    MembershipSubscribeResponseDTO subscribe(MembershipSubscribeRequestDTO requestDTO, long userId);


    /**
     * 멤버십 등급 목록 조회
     * (인플루언서별 가격이 없으므로 influencerId는 필수 아님)
     */
    List<MembershipGradeDTO> getMembershipGrades();
    
    /**
     * 사용자의 현재 등급 조회 (구독중인 멤버십 중 최고 등급)
     */
    UserMembershipInfoDTO getUserMembershipInfo(Long userId);
    
    /**
     * 특정 인플루언서에 대한 사용자의 구독 정보 조회
     */
    UserMembershipInfoDTO.UserMembershipSubscriptionDTO getUserSubscriptionByInfluencer(Long userId, Long influencerId);
}