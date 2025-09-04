package org.example.fanzip.notification.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.fanzip.notification.domain.NotificationVO;

@Mapper
public interface NotificationMapper {
    int insert(NotificationVO vo);
}
