package org.example.fanzip.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationTokenMapper {
    List<String> findTokensByInfluencerId(@Param("influencerId") Long influencerId);
}
