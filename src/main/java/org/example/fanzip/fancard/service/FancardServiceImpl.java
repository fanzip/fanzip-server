package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.response.*;
import org.example.fanzip.fancard.domain.Fancard;
import org.example.fanzip.fancard.exception.FancardNotFoundException;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class FancardServiceImpl implements FancardService {

    private final FancardMapper fancardMapper;

    @Autowired
    public FancardServiceImpl(FancardMapper fancardMapper) {
        this.fancardMapper = fancardMapper;
    }

    @Override
    public FancardListWrapper getUserFancards(Long userId) {
        List<Long> membershipIds = getMembershipIdsByUserId(userId);
        List<Fancard> fancards = fancardMapper.findActiveCardsByMembershipIds(membershipIds);

        List<FancardListResponse> fancardList = fancards.stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
        
        return FancardListWrapper.builder()
                .fancards(fancardList)
                .build();
    }

    @Override
    public FancardDetailResponse getFancardDetail(Long cardId) {
        Fancard fancard = fancardMapper.findById(cardId);
        if (fancard == null) {
            throw new FancardNotFoundException(cardId);
        }

        return convertToDetailResponse(fancard);
    }

    @Override
    public QrCodeResponse generateQrCode(Long reservationId) {
        // TODO: 실제 예약 정보 조회 로직 구현
        String qrCode = "ENTRY_USER1_RES" + reservationId + "_" + LocalDateTime.now().toString().replace(":", "").replace(".", "");
        String qrCodeUrl = "https://api.fanzip.com/qr/entry?code=" + qrCode;
        
        ReservationDto reservation = ReservationDto.builder()
                .reservationId(reservationId)
                .reservationNumber("FM20250722001")
                .meetingTitle("테스트 인플루언서 팬미팅 2025")
                .meetingDate(LocalDateTime.now().plusDays(30))
                .venueName("올림픽공원 K-아트홀")
                .seatNumber("A-15")
                .build();
        
        return QrCodeResponse.builder()
                .qrCode(qrCode)
                .qrCodeUrl(qrCodeUrl)
                .status("ACTIVE")
                .generatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .reservation(reservation)
                .build();
    }

    private FancardListResponse convertToListResponse(Fancard fancard) {
        MembershipGradeDto membershipGrade = MembershipGradeDto.builder()
                .gradeId(4L)
                .gradeName("VIP")
                .color("#8B008B")
                .build();

        return FancardListResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .influencerId(1L)
                .influencerName("테스트 인플루언서")
                .category("BEAUTY")
                .membershipGrade(membershipGrade)
                .cardDesignUrl(fancard.getCardDesignUrl())
                .isActive(fancard.getIsActive())
                .issueDate(fancard.getIssueDate())
                .expiryDate(fancard.getExpiryDate())
                .build();
    }
    
    private FancardDetailResponse convertToDetailResponse(Fancard fancard) {
        InfluencerDto influencer = InfluencerDto.builder()
                .influencerId(1L)
                .category("BEAUTY")
                .profileImage("https://example.com/profiles/test.jpg")
                .isVerified(true)
                .build();
        
        MembershipGradeDto grade = MembershipGradeDto.builder()
                .gradeId(4L)
                .gradeName("VIP")
                .color("#8B008B")
                .benefitsDescription("VIP 등급 최고 혜택")
                .build();
        
        MembershipDto membership = MembershipDto.builder()
                .membershipId(fancard.getMembershipId())
                .subscriptionStart(fancard.getIssueDate())
                .monthlyAmount(BigDecimal.valueOf(10000.00))
                .totalPaidAmount(BigDecimal.valueOf(120000.00))
                .status("ACTIVE")
                .autoRenewal(true)
                .grade(grade)
                .build();
        
        List<BenefitDto> benefits = List.of(
                BenefitDto.builder()
                        .benefitId(1L)
                        .benefitType("DISCOUNT")
                        .benefitName("상품 할인")
                        .benefitValue("20%")
                        .description("모든 굿즈 20% 할인")
                        .isActive(true)
                        .build(),
                BenefitDto.builder()
                        .benefitId(2L)
                        .benefitType("PRIORITY")
                        .benefitName("우선 예매")
                        .benefitValue("VIP_PRIORITY")
                        .description("팬미팅 VIP 등급 우선 예매")
                        .isActive(true)
                        .build()
        );
        
        return FancardDetailResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .cardDesignUrl(fancard.getCardDesignUrl())
                .influencer(influencer)
                .membership(membership)
                .benefits(benefits)
                .build();
    }

    private List<Long> getMembershipIdsByUserId(Long userId) {
        // TODO: Membership 서비스나 매퍼를 통해 실제 메버십 ID 목록 조회
        // 현재는 테스트 데이터 반환
        if (userId.equals(1L)) {
            return List.of(1L);
        }
        return List.of();
    }

    private String getInfluencerNameByMembershipId(Long membershipId) {
        // TODO: Membership 서비스를 통해 실제 인플루언서 이름 조회
        return "테스트 인플루언서";
    }

    private String getGradeNameByMembershipId(Long membershipId) {
        // TODO: Membership 서비스를 통해 실제 등급 이름 조회
        return "VIP";
    }

    private String getGradeColorByMembershipId(Long membershipId) {
        // TODO: Membership 서비스를 통해 실제 등급 색상 조회
        return "#8B008B";
    }
}
