package org.example.fanzip.membership.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.UserGrade;
import org.example.fanzip.membership.domain.MembershipVO;
import org.example.fanzip.membership.domain.enums.MembershipStatus;
import org.example.fanzip.membership.dto.MembershipGradeDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.dto.UserMembershipInfoDTO;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.example.fanzip.payment.domain.enums.PaymentMethod;
import org.example.fanzip.payment.domain.enums.PaymentType;
import org.example.fanzip.payment.dto.PaymentRequestDto;
import org.example.fanzip.payment.dto.PaymentResponseDto;
import org.example.fanzip.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipServiceImpl implements MembershipService {

    private final MembershipMapper membershipMapper;
    private final PaymentService paymentService;

    @Value("${app.web-base-url:http://localhost:5173}")
    private String webBaseUrl;

    @Override
    public MembershipSubscribeResponseDTO subscribe(MembershipSubscribeRequestDTO requestDTO, long userId) {

        final Long influencerId = requestDTO.getInfluencerId();
        final Integer gradeId   = requestDTO.getGradeId();

        // 0) 서버 기준 금액 조회
        BigDecimal amount = membershipMapper.findMonthlyAmountByGradeId(gradeId);
        if (amount == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 gradeId");
        }

        // 1) (userId, influencerId) 잠금 - 동시요청 대비
        MembershipVO locked = membershipMapper.findByUserIdAndInfluencerIdForUpdate(userId, influencerId);

        if (locked != null) {
            boolean active = locked.getStatus() == MembershipStatus.ACTIVE; // enum 직접 비교
            boolean notExpired = locked.getSubscriptionEnd() != null
                    && locked.getSubscriptionEnd().isAfter(LocalDate.now());
            if (active && notExpired) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 구독 중입니다.");
            }
            membershipMapper.updateToPending(userId, influencerId, gradeId, amount);
        } else {
            try {
                membershipMapper.insertPending(userId, influencerId, gradeId, amount);
            } catch (org.springframework.dao.DuplicateKeyException ignore) {
                locked = membershipMapper.findByUserIdAndInfluencerIdForUpdate(userId, influencerId);
                boolean active = locked != null
                        && locked.getStatus() == MembershipStatus.ACTIVE
                        && locked.getSubscriptionEnd() != null
                        && locked.getSubscriptionEnd().isAfter(LocalDate.now());
                if (active) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 구독 중입니다.");
                }
                membershipMapper.updateToPending(userId, influencerId, gradeId, amount);
            }
        }

        // 2) 방금 업데이트된 구독 행 재조회 → membershipId 확보
        MembershipVO current = membershipMapper.findByUserIdAndInfluencerId(userId, influencerId);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "구독 행 조회 실패");
        }
        Long membershipId = current.getMembershipId();

        // 3) 결제 세션 생성 (Payment DTO 시그니처에 정확히 맞춤)
        PaymentRequestDto payReq = PaymentRequestDto.builder()
                .userId(userId)
                .membershipId(membershipId)
                .paymentType(PaymentType.MEMBERSHIP)
                .paymentMethod(PaymentMethod.TOSSPAY) // ✅ 고정
                .amount(amount)
                .build();

        PaymentResponseDto payRes = paymentService.createPayment(payReq);

        String paymentPageUrl = "/payments/request?paymentId=" + payRes.getPaymentId();


        // 4) 응답 구성 (현재 상태는 PENDING)
        return MembershipSubscribeResponseDTO.builder()
                .membershipId(membershipId)
                .influencerId(influencerId)
                .gradeId(gradeId)
                .status(MembershipStatus.PENDING)
                .monthlyAmount(amount)
                .paymentId(payRes.getPaymentId())
                .paymentPageUrl(paymentPageUrl)
                .build();
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
    
    @Override
    @Transactional
    public boolean cancelMembership(Long membershipId, Long userId) {
        try {
            // 멤버십 정보 조회 (해지 전에 정보 필요)
            MembershipVO membership = membershipMapper.findByMembershipId(membershipId);
            if (membership == null || !membership.getUserId().equals(userId)) {
                System.err.println("구독 취소 실패: 유효하지 않은 멤버십 - membershipId=" + membershipId + ", userId=" + userId);
                return false;
            }
            
            // 구독 취소 실행 (memberships 테이블 상태 변경)
            int result = membershipMapper.cancelMembership(membershipId, userId);
            
            if (result > 0) {
                // 해지 이벤트를 payments 테이블에도 기록 (추억 탭에서 시간순 표시를 위해)
                try {
                    PaymentRequestDto cancellationRecord = PaymentRequestDto.builder()
                            .userId(userId)
                            .membershipId(membershipId)
                            .paymentType(PaymentType.MEMBERSHIP)
                            .paymentMethod(PaymentMethod.TOSSPAY)  // 해지 이벤트 기록용
                            .amount(BigDecimal.ZERO)  // 환불이 아닌 단순 해지
                            .transactionId("CANCEL_" + membershipId + "_" + System.currentTimeMillis())
                            .build();
                    
                    PaymentResponseDto cancellationPayment = paymentService.createPayment(cancellationRecord);
                    System.out.println("해지 이벤트 payments 테이블 기록 완료: paymentId=" + cancellationPayment.getPaymentId());
                    
                    // 해지 결제 상태를 CANCELLED로 즉시 변경
                    paymentService.cancelledPaymentById(cancellationPayment.getPaymentId());
                    
                } catch (Exception e) {
                    System.err.println("해지 이벤트 payments 기록 실패 (멤버십 해지는 성공): " + e.getMessage());
                    // payments 기록 실패해도 멤버십 해지는 성공으로 처리
                }
                
                System.out.println("구독 취소 성공: membershipId=" + membershipId + ", userId=" + userId);
                return true;
            } else {
                System.err.println("구독 취소 실패: membershipId=" + membershipId + ", userId=" + userId + " (해당 가능한 멤버십이 없음)");
                return false;
            }
        } catch (Exception e) {
            System.err.println("구독 취소 중 오류 발생: membershipId=" + membershipId + ", userId=" + userId + ", error=" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
