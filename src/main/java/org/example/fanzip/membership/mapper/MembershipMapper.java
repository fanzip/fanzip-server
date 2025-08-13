    package org.example.fanzip.membership.mapper;


    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;
    import org.example.fanzip.membership.domain.MembershipVO;
    import org.example.fanzip.membership.dto.MembershipGradeDTO;
    import org.example.fanzip.membership.dto.UserMembershipInfoDTO;

    import java.util.List;


    @Mapper
    public interface MembershipMapper {

        void insertMembership(MembershipVO membershipVO);

        void deleteByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);


        MembershipVO findByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);

        // 매퍼 수정하기
        List<MembershipGradeDTO> findGradesByInfluencerId(Long influencerId);

        /**
         * 사용자가 구독중인 인플루언서 ID 목록 조회
         */
        List<Long> findSubscribedInfluencerIdsByUserId(@Param("userId") Long userId);
        
        /**
         * 사용자의 최고 등급 조회 (구독중인 멤버십 중 가장 높은 등급)
         */
        String findHighestGradeByUserId(@Param("userId") Long userId);
        
        /**
         * 특정 인플루언서에 대한 사용자의 구독 정보 조회
         */
        UserMembershipInfoDTO.UserMembershipSubscriptionDTO findUserSubscriptionByInfluencerId(@Param("userId") Long userId, @Param("influencerId") Long influencerId);
    }
