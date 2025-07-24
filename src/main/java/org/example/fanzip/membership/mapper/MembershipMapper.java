package org.example.fanzip.membership.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.membership.domain.MembershipVO;


@Mapper
public interface MembershipMapper {

    void insertMembership(MembershipVO membershipVO);

    void deleteByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);


    MembershipVO findByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);
}
