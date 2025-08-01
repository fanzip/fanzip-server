package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingVO;
import org.example.fanzip.meeting.domain.UserGrade;
import org.example.fanzip.meeting.dto.FanMeetingDetailDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;
import org.example.fanzip.meeting.mapper.FanMeetingMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FanMeetingServiceImpl implements FanMeetingService {
    private final FanMeetingMapper fanMeetingMapper;

    @Override
    public List<FanMeetingResponseDTO> getOpenMeetings(String userGradeStr) {
        if (userGradeStr == null) {
            throw new IllegalArgumentException("회원 등급은 필수입니다.");
        }

        UserGrade userGrade = UserGrade.from(userGradeStr);
        List<FanMeetingVO> meetings = fanMeetingMapper.findAllOpenMeetings();

        return meetings.stream().map(meeting -> {
            FanMeetingResponseDTO dto = new FanMeetingResponseDTO();
            dto.setMeetingId(meeting.getMeetingId());
            dto.setTitle(meeting.getTitle());
            dto.setVenueName(meeting.getVenueName());
            dto.setVenueAddress(meeting.getVenueAddress());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setAvailableSeats(meeting.getAvailableSeats());
            dto.setStatus(meeting.getStatus());
            dto.setOpenTime(extractOpenTime(meeting, userGrade));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public FanMeetingDetailDTO getMeetingDetail(Long meetingId) {
        FanMeetingVO vo = fanMeetingMapper.findById(meetingId);

        return FanMeetingDetailDTO.builder()
                .meetingId(vo.getMeetingId())
                .title(vo.getTitle())
                .description(vo.getDescription())
                .venueName(vo.getVenueName())
                .venueAddress(vo.getVenueAddress())
                .meetingDate(vo.getMeetingDate())
                .totalSeats(vo.getTotalSeats())
                .availableSeats(vo.getAvailableSeats())
                .status(vo.getStatus())
                .vipOpenTime(vo.getVipOpenTime())
                .goldOpenTime(vo.getGoldOpenTime())
                .silverOpenTime(vo.getSilverOpenTime())
                .whiteOpenTime(vo.getWhiteOpenTime())
                .generalOpenTime(vo.getGeneralOpenTime())
                .build();
    }


    private LocalDateTime extractOpenTime(FanMeetingVO meeting, UserGrade grade) {
        return switch (grade) {
            case VIP -> meeting.getVipOpenTime();
            case GOLD -> meeting.getGoldOpenTime();
            case SILVER -> meeting.getSilverOpenTime();
            case WHITE -> meeting.getWhiteOpenTime();
            case GENERAL -> meeting.getGeneralOpenTime();
        };
    }

}
