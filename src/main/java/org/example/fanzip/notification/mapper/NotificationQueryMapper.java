package org.example.fanzip.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationQueryMapper {
    String findInfluencerNameById(@Param("influencerId") Long influencerId);
}
