package org.example.fanzip.fancard.service;

import org.example.fanzip.fancard.dto.request.QrCodeRequest;
import org.example.fanzip.fancard.dto.request.QrCodeValidationRequest;
import org.example.fanzip.fancard.dto.response.*;
import org.example.fanzip.fancard.domain.Fancard;
import org.example.fanzip.fancard.exception.FancardNotFoundException;
import org.example.fanzip.fancard.mapper.FancardMapper;
import org.example.fanzip.fancard.constants.FancardConstants;
import org.example.fanzip.user.service.UserService;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.meeting.mapper.FanMeetingMapper;
import org.example.fanzip.meeting.mapper.FanMeetingReservationMapper;
import org.example.fanzip.meeting.domain.FanMeetingVO;
import org.example.fanzip.meeting.domain.FanMeetingReservationVO;
import org.example.fanzip.notification.mapper.PushTokenMapper;
import org.example.fanzip.global.fcm.FcmService;
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
    private final UserService userService;
    private final FanMeetingMapper fanMeetingMapper;
    private final FanMeetingReservationMapper reservationMapper;
    private final PushTokenMapper pushTokenMapper;
    private final FcmService fcmService;

    public FancardServiceImpl(FancardMapper fancardMapper, LocationService locationService, 
                             QrCodeGeneratorService qrCodeGeneratorService, UserService userService,
                             FanMeetingMapper fanMeetingMapper, FanMeetingReservationMapper reservationMapper,
                             PushTokenMapper pushTokenMapper, FcmService fcmService) {
        this.fancardMapper = fancardMapper;
        this.locationService = locationService;
        this.qrCodeGeneratorService = qrCodeGeneratorService;
        this.userService = userService;
        this.fanMeetingMapper = fanMeetingMapper;
        this.reservationMapper = reservationMapper;
        this.pushTokenMapper = pushTokenMapper;
        this.fcmService = fcmService;
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
        
        // 사용자의 FCM 토큰 조회
        String fcmToken = pushTokenMapper.findTokenByUserId(request.getUserId());
        System.out.println("🔍 FCM 토큰 조회: userId=" + request.getUserId() + ", fcmToken=" + fcmToken);
        
        // QR 코드 데이터 생성 (FCM 토큰 포함)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String qrData = qrCodeGeneratorService.generateQrDataString(
                request.getUserId(), 
                request.getFanMeetingId(), 
                request.getReservationId(), 
                timestamp,
                fcmToken
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
    
    @Override
    public QrCodeValidationResponse validateQrCode(QrCodeValidationRequest request) {
        LocalDateTime validatedAt = LocalDateTime.now();
        System.out.println("🔍 QR 검증 요청 시작: " + request.getQrData() + " at " + validatedAt);
        
        try {
            // 1. QR 코드 형식 검증
            String qrData = request.getQrData();
            if (qrData == null || !qrData.startsWith(FancardConstants.QrCode.FANZIP_PREFIX)) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_FORMAT,
                        FancardConstants.QrCode.INVALID_FORMAT_MESSAGE, null, null, null, null, null, 
                        validatedAt, "QR_FORMAT_001", "QR 데이터가 FANZIP_ 형식이 아닙니다.");
            }
            
            // 2. QR 데이터 파싱 (4부분 또는 5부분 지원)
            String[] parts = qrData.substring(FancardConstants.QrCode.FANZIP_PREFIX.length()).split("_");
            if (parts.length < 4 || parts.length > 5) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_FORMAT,
                        FancardConstants.QrCode.INVALID_FORMAT_MESSAGE, null, null, null, null, null,
                        validatedAt, "QR_FORMAT_002", "QR 데이터 파트 수가 올바르지 않습니다. (4-5개 파트 필요)");
            }
            
            Long userId, fanMeetingId, reservationId;
            String timestamp, fcmToken;
            try {
                userId = Long.parseLong(parts[0]);
                fanMeetingId = Long.parseLong(parts[1]);
                reservationId = Long.parseLong(parts[2]);
                timestamp = parts[3];
                fcmToken = parts.length > 4 ? parts[4] : "NO_TOKEN"; // 호환성을 위해 옵셔널 처리
            } catch (NumberFormatException e) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_FORMAT,
                        FancardConstants.QrCode.INVALID_FORMAT_MESSAGE, null, null, null, null, null,
                        validatedAt, "QR_FORMAT_003", "QR 데이터의 숫자 형식이 올바르지 않습니다.");
            }
            
            // 3. QR 코드 만료 검증
            LocalDateTime qrGeneratedTime;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                qrGeneratedTime = LocalDateTime.parse(timestamp, formatter);
            } catch (Exception e) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_FORMAT,
                        FancardConstants.QrCode.INVALID_FORMAT_MESSAGE, userId, fanMeetingId, reservationId, null, null,
                        validatedAt, "QR_FORMAT_004", "타임스탬프 형식이 올바르지 않습니다.");
            }
            
            if (qrGeneratedTime.plusSeconds(FancardConstants.QrCode.EXPIRY_SECONDS).isBefore(validatedAt)) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_EXPIRED,
                        FancardConstants.QrCode.EXPIRED_MESSAGE, userId, fanMeetingId, reservationId, null, null,
                        validatedAt, "QR_EXPIRED_001", "QR 코드 유효 시간이 만료되었습니다.");
            }
            
            // 4. 사용자 검증
            UserDTO user = userService.getUser(userId);
            if (user == null) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_USER,
                        FancardConstants.QrCode.INVALID_USER_MESSAGE, userId, fanMeetingId, reservationId, null, null,
                        validatedAt, "USER_001", "존재하지 않는 사용자입니다.");
            }
            
            // 5. 팬미팅 검증
            FanMeetingVO meeting = fanMeetingMapper.findById(fanMeetingId);
            if (meeting == null) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_MEETING,
                        FancardConstants.QrCode.INVALID_MEETING_MESSAGE, userId, fanMeetingId, reservationId, 
                        user.getName(), user.getEmail(), validatedAt, "MEETING_001", "존재하지 않는 팬미팅입니다.");
            }
            
            // 6. 예약 검증
            FanMeetingReservationVO reservation = reservationMapper.findByUserAndMeeting(userId, fanMeetingId);
            if (reservation == null || !reservation.getReservationId().equals(reservationId)) {
                return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_INVALID_RESERVATION,
                        FancardConstants.QrCode.INVALID_RESERVATION_MESSAGE, userId, fanMeetingId, reservationId,
                        user.getName(), user.getEmail(), validatedAt, "RESERVATION_001", "유효하지 않은 예약 정보입니다.");
            }
            
            // 6-1. 중복 입장 방지 (이미 사용된 예약인지 확인)
            if ("USED".equals(reservation.getStatus())) {
                return buildValidationResponse(false, "ALREADY_USED",
                        "이미 입장 처리된 예약입니다.", userId, fanMeetingId, reservationId,
                        user.getName(), user.getEmail(), validatedAt, "RESERVATION_002", "이미 입장 처리된 예약입니다.");
            }
            
            // 7. 위치 검증 (optional - if latitude/longitude provided)
            if (request.getLatitude() != null && request.getLongitude() != null) {
                if (!locationService.isWithinVenueRange(request.getLatitude(), request.getLongitude(), fanMeetingId)) {
                    return buildValidationResponse(false, FancardConstants.QrCode.VALIDATION_LOCATION_ERROR,
                            FancardConstants.QrCode.LOCATION_ERROR_MESSAGE, userId, fanMeetingId, reservationId,
                            user.getName(), user.getEmail(), validatedAt, "LOCATION_001", "행사장 범위를 벗어났습니다.");
                }
            }
            
            // 8. 예약 상태를 USED로 업데이트 (중복 입장 방지)
            try {
                reservationMapper.updateReservationStatus(reservationId, "USED", validatedAt);
                System.out.println("예약 상태를 USED로 업데이트 완료: reservationId=" + reservationId);
            } catch (Exception e) {
                System.err.println("예약 상태 업데이트 실패: " + e.getMessage());
                // 상태 업데이트 실패 시 검증 실패로 처리
                return buildValidationResponse(false, "SYSTEM_ERROR",
                        "시스템 오류가 발생했습니다.", userId, fanMeetingId, reservationId,
                        user.getName(), user.getEmail(), validatedAt, "SYSTEM_003", "예약 상태 업데이트 실패");
            }
            
            // 9. FCM 알림 전송 (QR 코드에서 추출한 FCM 토큰 사용)
            if (fcmToken != null && !fcmToken.equals("NO_TOKEN")) {
                try {
                    String notificationTitle = "✅ 입장 확인";
                    String notificationBody = String.format("%s 팬미팅 입장이 확인되었습니다! 🎉", meeting.getTitle());
                    String targetUrl = "/fancard/mobile-ticket/" + reservationId + "/" + reservation.getSeatId() + "/" + fanMeetingId;
                    
                    fcmService.sendToToken(fcmToken, notificationTitle, notificationBody, targetUrl);
                    System.out.println("QR 검증 성공 알림 전송 완료: " + fcmToken);
                } catch (Exception e) {
                    System.err.println("FCM 알림 전송 실패: " + e.getMessage());
                    // 알림 전송 실패해도 QR 검증은 성공으로 처리
                }
            }
            
            // 9. 성공 응답 - 예약 정보 구성
            ReservationDto reservationDto = ReservationDto.builder()
                    .reservationId(reservation.getReservationId())
                    .reservationNumber("FM" + reservation.getReservationId().toString())
                    .meetingTitle(meeting.getTitle())
                    .meetingDate(meeting.getMeetingDate())
                    .venueName(meeting.getVenueName())
                    .seatNumber("A-" + reservation.getReservationId() % 100) // 임시 좌석 번호
                    .build();
            
            QrCodeValidationResponse successResponse = buildValidationResponse(true, FancardConstants.QrCode.VALIDATION_SUCCESS,
                    FancardConstants.QrCode.SUCCESS_MESSAGE, userId, fanMeetingId, reservationId,
                    user.getName(), user.getEmail(), validatedAt, null, null);
            
            return QrCodeValidationResponse.builder()
                    .isValid(successResponse.isValid())
                    .status(successResponse.getStatus())
                    .message(successResponse.getMessage())
                    .userId(successResponse.getUserId())
                    .fanMeetingId(successResponse.getFanMeetingId())
                    .reservationId(successResponse.getReservationId())
                    .userName(successResponse.getUserName())
                    .userEmail(successResponse.getUserEmail())
                    .validatedAt(successResponse.getValidatedAt())
                    .errorCode(successResponse.getErrorCode())
                    .errorDetails(successResponse.getErrorDetails())
                    .reservation(reservationDto)
                    .build();
                    
        } catch (Exception e) {
            return buildValidationResponse(false, "SYSTEM_ERROR", "시스템 오류가 발생했습니다.", 
                    null, null, null, null, null, validatedAt, "SYSTEM_001", e.getMessage());
        }
    }
    
    @Override
    public QrCodeResponse getMobileTicketData(Long userId, Long reservationId, Long seatId, Long meetingId) {
        try {
            // 예약 정보 검증 (데이터가 없으면 테스트 데이터로 대체)
            FanMeetingReservationVO reservation = reservationMapper.findByUserAndMeeting(userId, meetingId);
            
            // 팬미팅 정보 조회 (데이터가 없으면 테스트 데이터로 대체)
            FanMeetingVO meeting = fanMeetingMapper.findById(meetingId);
            
            // 좌석 정보는 현재 간단히 처리
            String seatNumber = "A-" + (seatId % 100); // 임시 좌석 번호 생성
            
            // 예약 정보 구성 (실제 데이터가 없으면 테스트 데이터 사용)
            ReservationDto reservationDto;
            if (reservation != null && meeting != null) {
                // 실제 데이터 사용
                reservationDto = ReservationDto.builder()
                        .reservationId(reservation.getReservationId())
                        .reservationNumber("FM" + reservation.getReservationId().toString())
                        .meetingTitle(meeting.getTitle())
                        .meetingDate(meeting.getMeetingDate())
                        .venueName(meeting.getVenueName())
                        .seatNumber(seatNumber)
                        .build();
            } else {
                // 테스트 데이터 사용
                reservationDto = ReservationDto.builder()
                        .reservationId(reservationId)
                        .reservationNumber("FM" + reservationId.toString())
                        .meetingTitle(FancardConstants.TestData.TEST_MEETING_TITLE)
                        .meetingDate(LocalDateTime.now().plusDays(30))
                        .venueName(FancardConstants.TestData.TEST_VENUE_NAME)
                        .seatNumber(seatNumber)
                        .build();
                
                System.out.println("⚠️ 실제 예약/팬미팅 데이터가 없어서 테스트 데이터를 사용합니다.");
                System.out.println("   - userId: " + userId + ", meetingId: " + meetingId);
                System.out.println("   - reservationId: " + reservationId + ", seatId: " + seatId);
            }
            
            // 사용자의 FCM 토큰 조회
            String fcmToken = pushTokenMapper.findTokenByUserId(userId);
            
            // 기본 QR 코드 응답 (생성되지 않은 상태)
            LocalDateTime now = LocalDateTime.now();
            return QrCodeResponse.builder()
                    .qrCode(null) // QR 코드는 사용자가 생성 버튼을 눌렀을 때 생성
                    .qrCodeUrl(null)
                    .status("READY") // 생성 준비 상태
                    .generatedAt(now)
                    .expiresAt(null)
                    .reservation(reservationDto)
                    .fcmToken(fcmToken) // FCM 토큰 포함
                    .build();
                    
        } catch (Exception e) {
            System.err.println("모바일 티켓 데이터 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("모바일 티켓 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    private QrCodeValidationResponse buildValidationResponse(boolean isValid, String status, String message,
                                                           Long userId, Long fanMeetingId, Long reservationId,
                                                           String userName, String userEmail, LocalDateTime validatedAt,
                                                           String errorCode, String errorDetails) {
        return QrCodeValidationResponse.builder()
                .isValid(isValid)
                .status(status)
                .message(message)
                .userId(userId)
                .fanMeetingId(fanMeetingId)
                .reservationId(reservationId)
                .userName(userName)
                .userEmail(userEmail)
                .validatedAt(validatedAt)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
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
            
            // 카드 디자인 URL 처리 - S3 URL은 그대로 사용, 로컬 경로만 변환
            String cardDesignUrl = fancard.getCardDesignUrl();
            System.out.println("Original URL from DB: " + cardDesignUrl);
            
            // S3 URL (https://)은 그대로 사용
            if (cardDesignUrl != null && cardDesignUrl.startsWith("https://")) {
                System.out.println("Using S3 URL as-is: " + cardDesignUrl);
            } 
            // 레거시 로컬 경로는 assets로 변환 (호환성 유지)
            else if (cardDesignUrl != null && cardDesignUrl.startsWith("/images/fancard/")) {
                cardDesignUrl = cardDesignUrl.replace("/images/fancard/", "/src/assets/fancard/");
                System.out.println("Converted local URL: " + cardDesignUrl);
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
        
        // 결제 히스토리 조회
        List<PaymentHistoryDto> paymentHistory = getPaymentHistory(fancard.getMembershipId());
        
        return FancardDetailResponse.builder()
                .cardId(fancard.getCardId())
                .cardNumber(fancard.getCardNumber())
                .cardDesignUrl(fancard.getCardDesignUrl())
                .influencer(influencer != null ? influencer : createDefaultInfluencer())
                .membership(membership != null ? membership : createDefaultMembership(fancard))
                .benefits(FancardConstants.TestData.TEST_BENEFITS) // TODO: 실제 혜택 정보 조회 구현 필요
                .paymentHistory(paymentHistory) // 결제 히스토리 추가
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
                .subscriptionStart(java.time.LocalDate.now())
                .monthlyAmount(BigDecimal.valueOf(5000.00))
                .totalPaidAmount(BigDecimal.valueOf(60000.00))
                .status("ACTIVE")
                .autoRenewal(true)
                .grade(createDefaultGrade())
                .build();
    }

    @Override
    @Transactional
    public void createFancardForMembership(Long membershipId, Long influencerId) {
        try {
            // 이미 팬카드가 있는지 확인
            Fancard existingCard = fancardMapper.findActiveCardByMembershipId(membershipId);
            if (existingCard != null) {
                System.out.println("이미 팬카드가 존재합니다. membershipId: " + membershipId);
                return;
            }

            // 인플루언서 정보 조회
            InfluencerDto influencer = fancardMapper.findInfluencerByMembershipId(membershipId);
            String influencerName = influencer != null ? influencer.getInfluencerName() : "Unknown";

            // 카드 번호 생성 (예: FC + membershipId + 타임스탬프 기반 4자리) - 중복 방지
            long timestamp = System.currentTimeMillis() % 10000;
            String cardNumber = String.format("FC%06d%04d", membershipId, timestamp);

            // 실제 인플루언서의 팬카드 이미지 URL 사용 (S3)
            String defaultCardDesignUrl = influencer != null && influencer.getFancardImage() != null
                    ? influencer.getFancardImage()
                    : "/images/fancard/default.svg"; // 기본값

            // 팬카드 생성
            Fancard fancard = new Fancard(membershipId, cardNumber, defaultCardDesignUrl);

            fancardMapper.insert(fancard);
            System.out.println("팬카드 생성 완료. membershipId: " + membershipId + ", cardId: " + fancard.getCardId());
            
        } catch (Exception e) {
            System.err.println("팬카드 생성 실패: membershipId=" + membershipId + ", error=" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("팬카드 생성 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public List<PaymentHistoryDto> getPaymentHistory(Long membershipId) {
        try {
            List<PaymentHistoryDto> history = fancardMapper.findPaymentHistoryByMembershipId(membershipId);
            System.out.println("결제 히스토리 조회 완료: membershipId=" + membershipId + ", count=" + history.size());
            return history;
        } catch (Exception e) {
            System.err.println("결제 히스토리 조회 실패: membershipId=" + membershipId + ", error=" + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
