package org.example.fanzip.fancard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.fancard.domain.Fancard;
import org.example.fanzip.fancard.dto.response.InfluencerDto;
import org.example.fanzip.fancard.dto.response.MembershipDto;
import org.example.fanzip.fancard.dto.response.MembershipGradeDto;

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
    
    void delete(@Param("cardId") Long cardId);
    
    // 추가된 메서드들 - 관련 정보 조회
    List<Long> findMembershipIdsByUserId(@Param("userId") Long userId);
    
    InfluencerDto findInfluencerByMembershipId(@Param("membershipId") Long membershipId);
    
    MembershipDto findMembershipById(@Param("membershipId") Long membershipId);
    
    MembershipGradeDto findMembershipGradeById(@Param("gradeId") Long gradeId);
    
    String findInfluencerNameByMembershipId(@Param("membershipId") Long membershipId);
}
