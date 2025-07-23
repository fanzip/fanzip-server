package org.example.fanzip.membership.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.membership.domain.MembershipVO;

import java.math.BigInteger;

@Mapper
public interface MembershipMapper {

    void insertMembership(MembershipVO membershipVO);

    MembershipVO findByUserIdAndInfluencerId(@Param("userId") BigInteger userId, @Param("influencerId") long influencerId);
}
