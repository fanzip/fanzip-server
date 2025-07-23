package org.example.fanzip.membership.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipServiceImpl implements MembershipService{

    private final MembershipMapper membershipMapper;

    @Override
    public MembershipSubscribeResponseDTO subscribe(
            MembershipSubscribeRequestDTO requestDTO, BigInteger userId) {

        MembershipVO existingMembership = membershipMapper
                .findByUserIdAndInfluencerId(userId, requestDTO.getInfluencerId());

        if (existingMembership != null) {
            return MembershipSubscribeResponseDTO.from(existingMembership);
        }


        // 날짜 설정
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, 1);
        Date oneMonthLater = calendar.getTime();


        MembershipVO membershipVO = MembershipVO.builder()  // DTO -> VO
                .userId(userId.longValue())
                .influencerId(requestDTO.getInfluencerId())
                .gradeId(requestDTO.getGradeId())
                .status(MembershipStatus.ACTIVE)
                .subscriptionStart(now)
                .subscriptionEnd(oneMonthLater)
                .monthlyAmount(3000.0)
                .totalPaidAmount(3000.0)
                .build();

        membershipMapper.insertMembership(membershipVO);

        MembershipVO inserted = membershipMapper.findByUserIdAndInfluencerId(
                userId, requestDTO.getInfluencerId());

        return MembershipSubscribeResponseDTO.from(inserted);
    }

}
