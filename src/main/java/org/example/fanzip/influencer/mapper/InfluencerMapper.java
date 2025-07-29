package org.example.fanzip.influencer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.influencer.domain.InfluencerVO;
import org.example.fanzip.influencer.domain.enums.InfluencerCategory;

import java.util.List;

@Mapper
public interface InfluencerMapper {

    List<InfluencerVO> findAllFiltered(@Param("userId") Long userId,
                                       @Param("category") InfluencerCategory category);
}
