    package org.example.fanzip.membership.mapper;


    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;
    import org.example.fanzip.membership.domain.MembershipVO;
    import org.example.fanzip.membership.dto.MembershipGradeDTO;

    import java.util.List;


    @Mapper
    public interface MembershipMapper {

        void insertMembership(MembershipVO membershipVO);

        void deleteByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);


        MembershipVO findByUserIdAndInfluencerId(@Param("userId") long userId, @Param("influencerId") long influencerId);

        // 매퍼 수정하기
        List<MembershipGradeDTO> findGradesByInfluencerId(Long influencerId);
    }
