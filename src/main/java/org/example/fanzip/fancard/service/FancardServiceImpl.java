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
        System.out.println("=== getUserFancards START for userId: " + userId + " ===");
        List<Long> membershipIds = fancardMapper.findMembershipIdsByUserId(userId);
        System.out.println("Found membership IDs: " + membershipIds);
        
        List<Fancard> fancards = fancardMapper.findActiveCardsByMembershipIds(membershipIds);
        System.out.println("Found " + fancards.size() + " fancards");
        
        for (Fancard fancard : fancards) {
            System.out.println("Raw fancard from DB - ID: " + fancard.getCardId() + 
                             ", MembershipId: " + fancard.getMembershipId() + 
                             ", DesignUrl: " + fancard.getCardDesignUrl());
        }

        List<FancardListResponse> fancardList = fancards.stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
        
        System.out.println("=== getUserFancards END - returning " + fancardList.size() + " cards ===");
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
        try {
            System.out.println("=== CONVERTING FANCARD " + fancard.getCardId() + " ===");
            System.out.println("Card ID: " + fancard.getCardId());
            System.out.println("Membership ID: " + fancard.getMembershipId());
            System.out.println("Original cardDesignUrl: " + fancard.getCardDesignUrl());
            
            // 실제 데이터베이스에서 관련 정보 조회
            InfluencerDto influencer = null;
            MembershipDto membership = null;
            String influencerName = "Unknown Influencer";
            
            try {
                influencer = fancardMapper.findInfluencerByMembershipId(fancard.getMembershipId());
                System.out.println("Influencer DTO: " + (influencer != null ? influencer.getInfluencerId() : "null"));
            } catch (Exception e) {
                System.out.println("Error finding influencer: " + e.getMessage());
            }
            
            try {
                membership = fancardMapper.findMembershipById(fancard.getMembershipId());
                System.out.println("Membership DTO: " + (membership != null ? membership.getMembershipId() : "null"));
            } catch (Exception e) {
                System.out.println("Error finding membership: " + e.getMessage());
            }
            
            try {
                String name = fancardMapper.findInfluencerNameByMembershipId(fancard.getMembershipId());
                System.out.println("Retrieved influencer name: " + name);
                if (name != null && !name.trim().isEmpty()) {
                    influencerName = name;
                }
            } catch (Exception e) {
                System.out.println("Error finding influencer name: " + e.getMessage());
            }
            
            // 카드 디자인 URL 처리 - /images/ 경로를 /src/assets/로 변경
            String cardDesignUrl = fancard.getCardDesignUrl();
            System.out.println("Original URL from DB: " + cardDesignUrl);
            if (cardDesignUrl != null && cardDesignUrl.startsWith("/images/fancard/")) {
                cardDesignUrl = cardDesignUrl.replace("/images/fancard/", "/src/assets/fancard/");
                System.out.println("Converted URL: " + cardDesignUrl);
            }
            System.out.println("Final cardDesignUrl: " + cardDesignUrl);
            
            FancardListResponse response = FancardListResponse.builder()
                    .cardId(fancard.getCardId())
                    .cardNumber(fancard.getCardNumber())
                    .influencerId(influencer != null ? influencer.getInfluencerId() : 1L)
                    .influencerName(influencerName)
                    .category(influencer != null ? influencer.getCategory() : "UNKNOWN")
                    .membershipGrade(membership != null ? membership.getGrade() : createDefaultGrade())
                    .cardDesignUrl(cardDesignUrl) // null로 만들지 않고 그대로 반환
                    .isActive(fancard.getIsActive())
                    .issueDate(fancard.getIssueDate())
                    .expiryDate(fancard.getExpiryDate())
                    .build();
            
            System.out.println("Final response - CardID: " + response.getCardId() + ", InfluencerName: " + response.getInfluencerName() + ", CardDesignUrl: " + response.getCardDesignUrl());
            System.out.println("=== END CONVERTING FANCARD " + fancard.getCardId() + " ===");
            
            return response;
        } catch (Exception e) {
            System.out.println("Error in convertToListResponse: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private FancardDetailResponse convertToDetailResponse(Fancard fancard) {
        // 실제 데이터베이스에서 관련 정보 조회
        InfluencerDto influencer = fancardMapper.findInfluencerByMembershipId(fancard.getMembershipId());
        MembershipDto membership = fancardMapper.findMembershipById(fancard.getMembershipId());
        
        return FancardDetailResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .cardDesignUrl(fancard.getCardDesignUrl())
                .influencer(influencer != null ? influencer : createDefaultInfluencer())
                .membership(membership != null ? membership : createDefaultMembership(fancard))
                .benefits(FancardConstants.TestData.TEST_BENEFITS) // TODO: 실제 혜택 정보 조회 구현 필요
                .build();
    }

    private MembershipGradeDto createDefaultGrade() {
        return MembershipGradeDto.builder()
                .gradeId(1L)
                .gradeName("White")
                .color("#FFFFFF")
                .benefitsDescription("기본 등급")
                .build();
    }
    
    private InfluencerDto createDefaultInfluencer() {
        return InfluencerDto.builder()
                .influencerId(1L)
                .category("UNKNOWN")
                .profileImage("/src/assets/fancard/default.svg")
                .isVerified(false)
                .build();
    }
    
    private MembershipDto createDefaultMembership(Fancard fancard) {
        return MembershipDto.builder()
                .membershipId(fancard.getMembershipId())
                .subscriptionStart(fancard.getIssueDate())
                .monthlyAmount(BigDecimal.valueOf(5000.00))
                .totalPaidAmount(BigDecimal.valueOf(60000.00))
                .status("ACTIVE")
                .autoRenewal(true)
                .grade(createDefaultGrade())
                .build();
    }
}
