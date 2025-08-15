    package org.example.fanzip.membership.mapper;


    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;
    import org.example.fanzip.membership.domain.MembershipVO;
    import org.example.fanzip.membership.dto.MembershipGradeDTO;
    import org.example.fanzip.membership.dto.UserMembershipInfoDTO;

    import java.math.BigDecimal;
    import java.util.List;
    import java.util.Map;


    @Mapper
    public interface MembershipMapper {

        void insertMembership(MembershipVO membershipVO);

        void deleteByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);


        MembershipVO findByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);

        // 매퍼 수정하기
        List<MembershipGradeDTO> findGradesByInfluencerId(Long influencerId);

        List<Long> findSubscribedInfluencerIdsByUserId(@Param("userId") Long userId);
        
        String findHighestGradeByUserId(@Param("userId") Long userId);

        UserMembershipInfoDTO.UserMembershipSubscriptionDTO findUserSubscriptionByInfluencerId(@Param("userId") Long userId, @Param("influencerId") Long influencerId);

        BigDecimal findMonthlyAmountByGradeId(@Param("gradeId") Integer gradeId);

        MembershipVO findByUserIdAndInfluencerIdForUpdate(
                @Param("userId") long userId,
                @Param("influencerId") long influencerId
        );

        int insertPending(@Param("userId") long userId,
                          @Param("influencerId") long influencerId,
                          @Param("gradeId") int gradeId,
                          @Param("amount") BigDecimal amount);

        int updateToPending(@Param("userId") long userId,
                            @Param("influencerId") long influencerId,
                            @Param("gradeId") int gradeId,
                            @Param("amount") BigDecimal amount);

        // 결제를 위한 멤버십 정보 조회
        Map<String, Object> selectMembershipForPayment(@Param("membershipId") Long membershipId);

        // 멤버십 상태 업데이트
        int updateMembershipStatus(@Param("membershipId") Long membershipId, @Param("status") String status);

        int updateToActive(@Param("membershipId") long membershipId);

        MembershipVO findByMembershipId(@Param("membershipId") long membershipId);

        int updateTotalPaidAmount(@Param("membershipId") long membershipId, @Param("amount") BigDecimal amount);

        int cancelMembership(@Param("membershipId") long membershipId, @Param("userId") long userId);
    }
