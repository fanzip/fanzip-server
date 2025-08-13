package org.example.fanzip.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.meeting.domain.FanMeetingSeatVO;
import org.example.fanzip.meeting.domain.FanMeetingStatus;
import org.example.fanzip.meeting.domain.FanMeetingVO;
import org.example.fanzip.meeting.domain.UserGrade;
import org.example.fanzip.meeting.dto.FanMeetingDetailResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingRequestDTO;
import org.example.fanzip.meeting.dto.FanMeetingResponseDTO;
import org.example.fanzip.meeting.dto.FanMeetingSeatResponseDTO;
import org.example.fanzip.meeting.mapper.FanMeetingMapper;
import org.example.fanzip.meeting.mapper.FanMeetingSeatMapper;
import org.example.fanzip.membership.mapper.MembershipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FanMeetingServiceImpl implements FanMeetingService {
    private final FanMeetingMapper fanMeetingMapper;
    private final FanMeetingSeatMapper fanMeetingSeatMapper;
    private final MembershipMapper membershipMapper;

    @Autowired
    private FanMeetingSeatMapper seatMapper;

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
            dto.setProfileImageUrl(meeting.getProfileImageUrl());
            dto.setOpenTime(extractOpenTime(meeting, userGrade));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public FanMeetingDetailResponseDTO getMeetingDetail(Long meetingId) {
        FanMeetingDetailResponseDTO dto = fanMeetingMapper.findDetailById(meetingId);

        return FanMeetingDetailResponseDTO.builder()
                .meetingId(dto.getMeetingId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .venueName(dto.getVenueName())
                .venueAddress(dto.getVenueAddress())
                .meetingDate(dto.getMeetingDate())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getAvailableSeats())
                .status(dto.getStatus())
                .vipOpenTime(dto.getVipOpenTime())
                .goldOpenTime(dto.getGoldOpenTime())
                .silverOpenTime(dto.getSilverOpenTime())
                .whiteOpenTime(dto.getWhiteOpenTime())
                .generalOpenTime(dto.getGeneralOpenTime())
                .profileImageUrl(dto.getProfileImageUrl())
                .posterImageUrl(dto.getPosterImageUrl())
                .influencerName(dto.getInfluencerName())
                .influencerId(dto.getInfluencerId())
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

    public List<FanMeetingSeatResponseDTO> getSeats(Long meetingId) {
        return fanMeetingSeatMapper.findSeatsByMeetingId(meetingId);
    }

    public void createFanMeeting(FanMeetingVO meeting) {
        // 팬미팅 insert
        fanMeetingMapper.insertFanMeeting(meeting);

        // 좌석 자동 생성
        List<FanMeetingSeatVO> seats = generateSeats(meeting.getMeetingId());
        seatMapper.insertSeatList(seats);
    }

    private List<FanMeetingSeatVO> generateSeats(Long meetingId) {
        List<FanMeetingSeatVO> seats = new ArrayList<>();
        String[] rows = "ABCDEFGHIJKLMNO".split("");  // 15행

        for (String row : rows) {
            for (int col = 1; col <= 11; col++) {
                FanMeetingSeatVO seat = new FanMeetingSeatVO();
                seat.setMeetingId(meetingId);
                seat.setSeatNumber(row + col); // A1 ~ O11
                seat.setPrice(BigDecimal.valueOf(36000));
                seat.setReserved(false);
                seat.setVersion(0);
                seat.setCreatedAt(LocalDateTime.now());

                seats.add(seat);
            }
        }

        return seats;
    }


    @Override
    @Transactional
    public FanMeetingDetailResponseDTO createFanMeeting(FanMeetingRequestDTO request) {
        if (request.getInfluencerId() == null) {
            throw new IllegalArgumentException("influencerId가 null입니다.");
        }

        int rows = 15;
        int cols = 11;
        int total = rows * cols;

        FanMeetingVO meeting = new FanMeetingVO();
        meeting.setInfluencerId(request.getInfluencerId());
        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setVenueName(request.getVenueName());
        meeting.setVenueAddress(request.getVenueAddress());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setVipOpenTime(request.getVipOpenTime());
        meeting.setGoldOpenTime(request.getGoldOpenTime());
        meeting.setSilverOpenTime(request.getSilverOpenTime());
        meeting.setWhiteOpenTime(request.getWhiteOpenTime());
        meeting.setGeneralOpenTime(request.getGeneralOpenTime());
        meeting.setStatus(FanMeetingStatus.PLANNED);
        meeting.setProfileImageUrl(request.getProfileImageUrl());
        meeting.setTotalSeats(total);
        meeting.setAvailableSeats(total);
        fanMeetingMapper.insertFanMeeting(meeting); // Auto-increment ID 채워짐


        // 좌석 165개 자동 생성
        List<FanMeetingSeatVO> seats = new ArrayList<>();
        char[] rowArr = "ABCDEFGHIJKLMNO".toCharArray();
        for (char r : rowArr) {
            for (int c = 1; c <= cols; c++) {
                FanMeetingSeatVO seat = new FanMeetingSeatVO();
                seat.setMeetingId(meeting.getMeetingId());
                seat.setSeatNumber("" + r + c);
                seat.setReserved(false);
                seat.setPrice(new BigDecimal("20000"));
                seat.setVersion(0);
                seat.setCreatedAt(LocalDateTime.now());
                seats.add(seat);
            }
        }
        fanMeetingSeatMapper.insertSeatList(seats);

        return fanMeetingMapper.findDetailById(meeting.getMeetingId());
    }

    @Override
    public List<FanMeetingResponseDTO> getSubscribedInfluencerMeetings(String userGradeStr, Long userId) {
        if (userGradeStr == null) {
            throw new IllegalArgumentException("회원 등급은 필수입니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        UserGrade userGrade = UserGrade.from(userGradeStr);
        List<Long> subscribedInfluencerIds = membershipMapper.findSubscribedInfluencerIdsByUserId(userId);

        if (subscribedInfluencerIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<FanMeetingVO> meetings = fanMeetingMapper.findOpenMeetingsByInfluencerIds(subscribedInfluencerIds);

        return meetings.stream().map(meeting -> {
            FanMeetingResponseDTO dto = new FanMeetingResponseDTO();
            dto.setMeetingId(meeting.getMeetingId());
            dto.setTitle(meeting.getTitle());
            dto.setVenueName(meeting.getVenueName());
            dto.setVenueAddress(meeting.getVenueAddress());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setAvailableSeats(meeting.getAvailableSeats());
            dto.setStatus(meeting.getStatus());
            dto.setProfileImageUrl(meeting.getProfileImageUrl());
            dto.setOpenTime(extractOpenTime(meeting, userGrade));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FanMeetingResponseDTO> getNonSubscribedInfluencerMeetings(String userGradeStr, Long userId) {
        if (userGradeStr == null) {
            throw new IllegalArgumentException("회원 등급은 필수입니다.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        UserGrade userGrade = UserGrade.from(userGradeStr);
        List<Long> subscribedInfluencerIds = membershipMapper.findSubscribedInfluencerIdsByUserId(userId);

        List<FanMeetingVO> meetings;
        if (subscribedInfluencerIds.isEmpty()) {
            meetings = fanMeetingMapper.findAllOpenMeetings();
        } else {
            meetings = fanMeetingMapper.findOpenMeetingsExcludingInfluencerIds(subscribedInfluencerIds);
        }

        return meetings.stream().map(meeting -> {
            FanMeetingResponseDTO dto = new FanMeetingResponseDTO();
            dto.setMeetingId(meeting.getMeetingId());
            dto.setTitle(meeting.getTitle());
            dto.setVenueName(meeting.getVenueName());
            dto.setVenueAddress(meeting.getVenueAddress());
            dto.setMeetingDate(meeting.getMeetingDate());
            dto.setAvailableSeats(meeting.getAvailableSeats());
            dto.setStatus(meeting.getStatus());
            dto.setProfileImageUrl(meeting.getProfileImageUrl());
            dto.setOpenTime(extractOpenTime(meeting, userGrade));
            return dto;
        }).collect(Collectors.toList());
    }

}
