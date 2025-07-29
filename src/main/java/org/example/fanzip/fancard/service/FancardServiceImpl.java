package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.response.*;
import org.example.fanzip.fancard.domain.Fancard;
import org.example.fanzip.fancard.exception.FancardNotFoundException;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.fancard.constants.FancardConstants;
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
        String timestamp = LocalDateTime.now().toString().replace(":", "").replace(".", "");
        String qrCode = FancardConstants.QrCode.CODE_PREFIX + "1_RES" + reservationId + 
                       FancardConstants.QrCode.CODE_SEPARATOR + timestamp;
        String qrCodeUrl = FancardConstants.QrCode.URL_PREFIX + qrCode;
        
        ReservationDto reservation = ReservationDto.builder()
                .reservationId(reservationId)
                .reservationNumber(FancardConstants.TestData.TEST_RESERVATION_NUMBER)
                .meetingTitle(FancardConstants.TestData.TEST_MEETING_TITLE)
                .meetingDate(LocalDateTime.now().plusDays(30))
                .venueName(FancardConstants.TestData.TEST_VENUE_NAME)
                .seatNumber(FancardConstants.TestData.TEST_SEAT_NUMBER)
                .build();
        
        return QrCodeResponse.builder()
                .qrCode(qrCode)
                .qrCodeUrl(qrCodeUrl)
                .status(FancardConstants.QrCode.STATUS_ACTIVE)
                .generatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(FancardConstants.QrCode.EXPIRY_HOURS))
                .reservation(reservation)
                .build();
    }

    private FancardListResponse convertToListResponse(Fancard fancard) {
        MembershipGradeDto membershipGrade = MembershipGradeDto.builder()
                .gradeId(FancardConstants.TestData.TEST_GRADE_ID)
                .gradeName(FancardConstants.TestData.TEST_GRADE_NAME)
                .color(FancardConstants.TestData.TEST_GRADE_COLOR)
                .build();

        return FancardListResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .influencerId(FancardConstants.TestData.TEST_INFLUENCER_ID)
                .influencerName(FancardConstants.TestData.TEST_INFLUENCER_NAME)
                .category(FancardConstants.TestData.TEST_CATEGORY)
                .membershipGrade(membershipGrade)
                .cardDesignUrl(fancard.getCardDesignUrl())
                .isActive(fancard.getIsActive())
                .issueDate(fancard.getIssueDate())
                .expiryDate(fancard.getExpiryDate())
                .build();
    }
    
    private FancardDetailResponse convertToDetailResponse(Fancard fancard) {
        InfluencerDto influencer = InfluencerDto.builder()
                .influencerId(FancardConstants.TestData.TEST_INFLUENCER_ID)
                .category(FancardConstants.TestData.TEST_CATEGORY)
                .profileImage(FancardConstants.TestData.TEST_PROFILE_IMAGE)
                .isVerified(FancardConstants.TestData.TEST_IS_VERIFIED)
                .build();
        
        MembershipGradeDto grade = MembershipGradeDto.builder()
                .gradeId(FancardConstants.TestData.TEST_GRADE_ID)
                .gradeName(FancardConstants.TestData.TEST_GRADE_NAME)
                .color(FancardConstants.TestData.TEST_GRADE_COLOR)
                .benefitsDescription(FancardConstants.TestData.TEST_BENEFITS_DESCRIPTION)
                .build();
        
        MembershipDto membership = MembershipDto.builder()
                .membershipId(fancard.getMembershipId())
                .subscriptionStart(fancard.getIssueDate())
                .monthlyAmount(FancardConstants.TestData.TEST_MONTHLY_AMOUNT)
                .totalPaidAmount(FancardConstants.TestData.TEST_TOTAL_PAID_AMOUNT)
                .status(FancardConstants.TestData.TEST_MEMBERSHIP_STATUS)
                .autoRenewal(FancardConstants.TestData.TEST_AUTO_RENEWAL)
                .grade(grade)
                .build();
        
        return FancardDetailResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .cardDesignUrl(fancard.getCardDesignUrl())
                .influencer(influencer)
                .membership(membership)
                .benefits(FancardConstants.TestData.TEST_BENEFITS)
                .build();
    }

    private List<Long> getMembershipIdsByUserId(Long userId) {
        // TODO: MembershipService 의존성 주입 후 실제 조회로 변경 필요
        if (userId.equals(1L)) {
            return List.of(1L);
        }
        return Collections.emptyList();
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
