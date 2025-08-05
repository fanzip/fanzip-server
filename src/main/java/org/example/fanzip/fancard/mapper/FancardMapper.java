package org.example.fanzip.fancard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.fancard.domain.Fancard;

import java.util.List;

@Mapper
public interface FancardMapper {
    List<Fancard> findByMembershipId(@Param("membershipId") Long membershipId);
    
    Fancard findByCardNumber(@Param("cardNumber") String cardNumber);
    
    Fancard findByQrCode(@Param("qrCode") String qrCode);
    
    List<Fancard> findActiveCardsByMembershipIds(@Param("membershipIds") List<Long> membershipIds);
    
    Fancard findActiveCardByMembershipId(@Param("membershipId") Long membershipId);
    
    Fancard findById(@Param("cardId") Long cardId);
    
    void insert(Fancard fancard);
    
    void update(Fancard fancard);
    
    // 인플루언서의 팬카드 디자인 업데이트
    int updateCardDesignByInfluencerId(@Param("influencerId") Long influencerId, 
                                       @Param("cardDesignUrl") String cardDesignUrl);
    
    void delete(@Param("cardId") Long cardId);
}
