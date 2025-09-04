package org.example.fanzip.fancard.service;

import org.springframework.stereotype.Service;

@Service
public class LocationService {
    
    // 행사장 반경 (미터 단위) - 1km
    private static final double VENUE_RADIUS_METERS = 1000.0;
    
    // 테스트용 행사장 좌표 (현재 위치 기준)
    private static final double VENUE_LATITUDE = 37.552128;
    private static final double VENUE_LONGITUDE = 127.0710272;
    
    /**
     * 사용자 위치가 행사장 범위 내에 있는지 확인
     */
    public boolean isWithinVenueRange(double userLatitude, double userLongitude, Long fanMeetingId) {
        // TODO: fanMeetingId로 실제 행사장 좌표를 조회하는 로직 구현
        // 테스트용: 모든 행사장 좌표를 세종대학교 감원관으로 고정
        
        double distance = calculateDistance(userLatitude, userLongitude, VENUE_LATITUDE, VENUE_LONGITUDE);
        
        System.out.println("=== 위치 검증 디버깅 ===");
        System.out.println("사용자 위치: " + userLatitude + ", " + userLongitude);
        System.out.println("행사장 위치: " + VENUE_LATITUDE + ", " + VENUE_LONGITUDE);
        System.out.println("거리: " + distance + "m");
        System.out.println("허용 반경: " + VENUE_RADIUS_METERS + "m");
        System.out.println("결과: " + (distance <= VENUE_RADIUS_METERS));
        System.out.println("=====================");
        
        return distance <= VENUE_RADIUS_METERS;
    }
    
    /**
     * 두 지점 간의 거리를 계산 (미터 단위)
     * Haversine formula 사용
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371000; // 지구 반지름 (미터)
        
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLonRad = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}