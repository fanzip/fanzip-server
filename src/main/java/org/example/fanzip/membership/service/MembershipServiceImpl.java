package org.example.fanzip.membership.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;
import org.example.fanzip.membership.dto.MembershipGradeDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.dto.UserMembershipInfoDTO;
import org.example.fanzip.meeting.domain.UserGrade;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipServiceImpl implements MembershipService{

    private final MembershipMapper membershipMapper;

    @Override
    public MembershipSubscribeResponseDTO subscribe(
            MembershipSubscribeRequestDTO requestDTO, long userId) {

        MembershipVO existingMembership = membershipMapper
                .findByUserIdAndInfluencerId(userId, requestDTO.getInfluencerId());

        if (existingMembership != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 구독 중입니다.");
        }

        MembershipVO membershipVO = requestDTO.toEntity(userId);

        membershipMapper.insertMembership(membershipVO);

        MembershipVO inserted = membershipMapper.findByUserIdAndInfluencerId(userId, requestDTO.getInfluencerId());

        return MembershipSubscribeResponseDTO.from(inserted);
    }

    @Override
    public List<MembershipGradeDTO> getMembershipGrades() {
        return membershipMapper.findGradesByInfluencerId(null);
    }
    
    @Override
    public UserMembershipInfoDTO getUserMembershipInfo(Long userId) {
        String highestGrade = membershipMapper.findHighestGradeByUserId(userId);
        
        UserGrade userGrade = highestGrade != null ? 
            UserGrade.from(highestGrade) : UserGrade.GENERAL;
            
        return UserMembershipInfoDTO.builder()
                .userGrade(userGrade)
                .subscription(null)
                .build();
    }
    
    @Override
    public UserMembershipInfoDTO.UserMembershipSubscriptionDTO getUserSubscriptionByInfluencer(Long userId, Long influencerId) {
        return membershipMapper.findUserSubscriptionByInfluencerId(userId, influencerId);
    }
}
