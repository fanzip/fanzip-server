package org.example.fanzip.fancard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.fancard.domain.Fancard;
import org.example.fanzip.fancard.dto.response.InfluencerDto;
import org.example.fanzip.fancard.dto.response.MembershipDto;
import org.example.fanzip.fancard.dto.response.MembershipGradeDto;
import org.example.fanzip.fancard.dto.response.PaymentHistoryDto;

import java.util.List;

@Mapper
public interface FancardMapper {
    List<Fancard> findByMembershipId(@Param("membershipId") Long membershipId);
    
    Fancard findByCardNumber(@Param("cardNumber") String cardNumber);
    
    Fancard findByQrCode(@Param("qrCode") String qrCode);
    
    List<Fancard> findActiveCardsByMembershipIds(@Param("membershipIds") List<Long> membershipIds);
    
    Fancard findActiveCardByMembershipId(@Param("membershipId") Long membershipId);
    
    // 멤버십 ID로 팬카드 조회 (활성/비활성 모두)
    Fancard findCardByMembershipId(@Param("membershipId") Long membershipId);
    
    Fancard findById(@Param("cardId") Long cardId);
    
    void insert(Fancard fancard);
    
    void update(Fancard fancard);
    
    // 인플루언서의 팬카드 디자인 업데이트
    int updateCardDesignByInfluencerId(@Param("influencerId") Long influencerId, 
                                       @Param("cardDesignUrl") String cardDesignUrl);
    
    void delete(@Param("cardId") Long cardId);
    
    // 팬카드 상태 변경 메소드들
    void activateCard(@Param("cardId") Long cardId);
    void deactivateCard(@Param("cardId") Long cardId);
    void deactivateCardByMembershipId(@Param("membershipId") Long membershipId);
    
    // 추가된 메서드들 - 관련 정보 조회
    List<Long> findMembershipIdsByUserId(@Param("userId") Long userId);
    
    InfluencerDto findInfluencerByMembershipId(@Param("membershipId") Long membershipId);
    
    InfluencerDto findInfluencerById(@Param("influencerId") Long influencerId);
    
    MembershipDto findMembershipById(@Param("membershipId") Long membershipId);
    
    MembershipGradeDto findMembershipGradeById(@Param("gradeId") Long gradeId);
    
    String findInfluencerNameByMembershipId(@Param("membershipId") Long membershipId);
    
    // 결제 히스토리 조회
    List<PaymentHistoryDto> findPaymentHistoryByMembershipId(@Param("membershipId") Long membershipId);
}
