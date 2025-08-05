package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.request.QrCodeRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class FancardServiceImpl implements FancardService {

    private final FancardMapper fancardMapper;
    private final LocationService locationService;
    private final QrCodeGeneratorService qrCodeGeneratorService;

    public FancardServiceImpl(FancardMapper fancardMapper, LocationService locationService, QrCodeGeneratorService qrCodeGeneratorService) {
        this.fancardMapper = fancardMapper;
        this.locationService = locationService;
        this.qrCodeGeneratorService = qrCodeGeneratorService;
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
    public QrCodeResponse generateQrCode(QrCodeRequest request) {
        // 위치 기반 검증
        if (!locationService.isWithinVenueRange(request.getLatitude(), request.getLongitude(), request.getFanMeetingId())) {
            return QrCodeResponse.builder()
                    .qrCode(null)
                    .qrCodeUrl(null)
                    .status(FancardConstants.QrCode.STATUS_LOCATION_ERROR)
                    .generatedAt(LocalDateTime.now())
                    .expiresAt(null)
                    .reservation(null)
                    .build();
        }
        
        // QR 코드 데이터 생성
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String qrData = qrCodeGeneratorService.generateQrDataString(
                request.getUserId(), 
                request.getFanMeetingId(), 
                request.getReservationId(), 
                timestamp
        );
        
        // QR 코드 이미지 생성 (Base64)
        String qrCodeImage = qrCodeGeneratorService.generateQrCodeImage(qrData);
        String qrCodeUrl = "data:image/png;base64," + qrCodeImage;
        
        // 예약 정보 조회 (현재는 테스트 데이터)
        ReservationDto reservation = ReservationDto.builder()
                .reservationId(request.getReservationId())
                .reservationNumber(FancardConstants.TestData.TEST_RESERVATION_NUMBER)
                .meetingTitle(FancardConstants.TestData.TEST_MEETING_TITLE)
                .meetingDate(LocalDateTime.now().plusDays(30))
                .venueName(FancardConstants.TestData.TEST_VENUE_NAME)
                .seatNumber(FancardConstants.TestData.TEST_SEAT_NUMBER)
                .build();
        
        LocalDateTime now = LocalDateTime.now();
        return QrCodeResponse.builder()
                .qrCode(qrData)
                .qrCodeUrl(qrCodeUrl)
                .status(FancardConstants.QrCode.STATUS_ACTIVE)
                .generatedAt(now)
                .expiresAt(now.plusSeconds(FancardConstants.QrCode.EXPIRY_SECONDS))
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
