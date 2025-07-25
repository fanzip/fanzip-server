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
    
    void delete(@Param("cardId") Long cardId);
}
