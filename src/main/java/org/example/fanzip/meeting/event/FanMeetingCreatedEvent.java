package org.example.fanzip.meeting.event;

import java.time.LocalDateTime;

public record FanMeetingCreatedEvent(
        Long meetingId,
        Long influencerId,
        String title,
        LocalDateTime generalOpenTime
) {}
