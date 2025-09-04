package org.example.fanzip.global.s3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;


@Api(
        value = "S3 이미지 업로드",
        description = "AWS S3에 프로필/팬카드/팬미팅 포스터/마켓 이미지를 업로드/삭제하는 API 집합입니다."
)
@RestController
@RequestMapping("/api/influencers")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * 1) 인플루언서 프로필 이미지 업로드
     * 예) POST /api/{influencerId}/profile/image
     */

    @ApiOperation(
            value = "인플루언서 프로필 이미지 업로드",
            notes = """
                        인플루언서 프로필 이미지를 업로드합니다.
                        - `influencerId`는 URL 경로에 포함됩니다.
                        - 업로드된 이미지는 S3 버킷 `fanzip`의 `influencer_profile/` 경로에 저장됩니다.
                    
                        [요청 예시]
                        POST /api/influencers/5/profile/image
                        Content-Type: application/json
                    
                        {
                            "imageUrl": "https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/uuid-or-filename.jpg"
                        }
                    
                        [응답 예시]
                        200 OK
                    """)

    @ApiResponses({
            @ApiResponse(code = 200, message = "업로드 성공 (이미지 URL 반환)"),
            @ApiResponse(code = 400, message = "잘못된 요청(DTO 누락 또는 형식 오류)")
    })

    @PostMapping("/{influencerId}/profile/image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long influencerId,
            @RequestParam("file") MultipartFile file
    ) {
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("5MB 이하의 파일만 업로드할 수 있습니다.");
        }

        String imageUrl = s3Service.uploadProfileImage(file, influencerId);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 2) 인플루언서 팬카드 이미지 업로드
     * 예) POST /api/{influencerId}/fancard/image
     */
    @ApiOperation(
            value = "인플루언서 팬카드 이미지 업로드",
            notes = """
                        인플루언서 팬카드 이미지를 업로드합니다.
                        이 이미지는 팬카드 디자인 전반에 반영됩니다.
                        - `influencerId`는 URL 경로에 포함됩니다.
                        - 요청 바디에는 팬카드 이미지 URL 또는 관련 설정이 담긴 DTO를 전송합니다.

                        [요청 예시]
                        POST /api/influencers/5/fancard/image
                        Content-Type: application/json

                        {
                             "imageUrl": "https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/1234abcd.jpg"
                        }

                        [응답 예시]
                        200 OK
                    """)
    @ApiResponses({
            @ApiResponse(code = 200, message = "업로드 성공 (이미지 URL 반환)"),
            @ApiResponse(code = 400, message = "잘못된 요청(DTO 누락 또는 형식 오류)")
    })


    @PostMapping("/{influencerId}/fancard/image")
    public ResponseEntity<String> uploadFanCardImage(
            @PathVariable Long influencerId,
            @RequestParam("file") MultipartFile file
    ) {
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("5MB 이하의 파일만 업로드할 수 있습니다.");
        }

        String imageUrl = s3Service.uploadFanCardImage(file, influencerId);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 3) 인플루언서 프로필 이미지 삭제
     * 예) DELETE /api/influencers/{influencerId}/profile/image
     */
    @ApiOperation(
            value = "인플루언서 프로필 이미지 삭제",
            notes = """
                    (현재 S3 삭제는 제외됨) 인플루언서의 현재 프로필 이미지를 삭제합니다.
                    - `influencerId`를 통해 해당 인플루언서의 기존 이미지 URL을 조회 후, S3에서 삭제합니다.
                    - 삭제 후 DB에서도 해당 이미지 URL 정보를 제거합니다.
                    
                    [요청 예시]
                    DELETE /api/influencers/7/profile/image
                    
                    [응답 예시]
                    204 No Content
                    """
    )
    @ApiResponses({
            @ApiResponse(code = 204, message = "삭제 성공 (No Content)"),
            @ApiResponse(code = 404, message = "이미지가 존재하지 않음")
    })
    @DeleteMapping("/{influencerId}/profile/image")
    public ResponseEntity<Void> deleteProfileImage(
            @PathVariable Long influencerId
    ) {
        // 1. 현재 이미지 URL 조회
        String existingImageUrl = s3Service.getCurrentProfileImageUrl(influencerId);

        if (existingImageUrl == null || existingImageUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 2. S3에서 삭제
//        s3Service.deleteS3Image(existingImageUrl);

        // 3. DB URL 제거 (null 처리)
        s3Service.updateProfileImageUrl(influencerId, null);

        return ResponseEntity.noContent().build();
    }

    /**
     * 4) 인플루언서 팬카드 이미지 삭제
     * 예) DELETE /api/influencers/{influencerId}/fancard/image
     */
    @ApiOperation(
            value = "인플루언서 팬카드 이미지 삭제",
            notes = """
                (현재 S3 삭제는 제외됨) 인플루언서의 현재 팬카드 이미지를 삭제합니다.
                - `influencerId`를 통해 해당 인플루언서의 기존 팬카드 이미지 URL을 조회 후, S3에서 삭제합니다.
                - 삭제 후 DB에서도 해당 팬카드 이미지 URL 정보를 제거합니다.
                
                [요청 예시]
                DELETE /api/influencers/7/fancard/image
                
                [응답 예시]
                204 No Content
                """
    )
    @ApiResponses({
            @ApiResponse(code = 204, message = "삭제 성공 (No Content)"),
            @ApiResponse(code = 404, message = "이미지가 존재하지 않음")
    })
    @DeleteMapping("/{influencerId}/fancard/image")


    public ResponseEntity<Void> deleteFanCardImage(
            @PathVariable Long influencerId
    ) {
        // 1. 현재 팬카드 이미지 URL 조회
        String existingImageUrl = s3Service.getCurrentFanCardImageUrl(influencerId);

        if (existingImageUrl == null || existingImageUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 2. S3에서 삭제
//        s3Service.deleteS3Image(existingImageUrl);

        // 3. DB URL 제거 (null 처리)
        s3Service.updateFanCardImageUrl(influencerId, null);

        return ResponseEntity.noContent().build();
    }

    /* =======================================================
     * ✅ 추가: 5) 팬미팅 포스터 업로드
     *    POST /api/influencers/{influencerId}/fanmeeting/poster
     * ======================================================= */
    @ApiOperation(
            value = "팬미팅 포스터 업로드",
            notes = """
                    팬미팅 포스터 이미지를 업로드합니다.
                    
                    [요청 예시]
                    POST /api/influencers/5/fanmeeting/poster
                    Content-Type: multipart/form-data
                    (file: <이미지 파일>)
                    
                    [응답 예시]
                    200 OK, body: "https://.../fanmeeting_poster/5/xxx.jpg"
                    """
    )
    @PostMapping(value = "/{influencerId}/fanmeeting/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFanmeetingPoster(
            @PathVariable Long influencerId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = s3Service.uploadFanMeetingPoster(file, influencerId);
        return ResponseEntity.ok(url);
    }

    /* =======================================================
     * ✅ 추가: 6) 마켓 이미지 업로드(썸네일/슬라이드/상세)
     *    - 썸네일: POST /api/influencers/{influencerId}/market/images/thumbnail (file 1장)
     *    - 슬라이드: POST /api/influencers/{influencerId}/market/images/slide     (files 여러 장)
     *    - 상세:   POST /api/influencers/{influencerId}/market/images/detail    (files 여러 장)
     * ======================================================= */

    @ApiOperation(
            value = "마켓 썸네일 업로드",
            notes = """
                    공구 마켓 썸네일 이미지를 업로드합니다.
                    
                    [요청 예시]
                    POST /api/influencers/5/market/images/thumbnail
                    Content-Type: multipart/form-data
                    (file: <이미지 파일>)
                    
                    [응답 예시]
                    200 OK, body: {"url":"https://.../market/5/thumbnail/xxx.jpg"}
                    """
    )
    @PostMapping(value = "/{influencerId}/market/images/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadMarketThumbnail(
            @PathVariable Long influencerId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = s3Service.uploadMarketThumbnail(file, influencerId);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @ApiOperation(
            value = "마켓 슬라이드 이미지 업로드(여러 장)",
            notes = """
                    공구 마켓 슬라이드 이미지를 여러 장 업로드합니다.
                    
                    [요청 예시]
                    POST /api/influencers/5/market/images/slide
                    Content-Type: multipart/form-data
                    (files: <이미지 파일들>)
                    
                    [응답 예시]
                    200 OK, body: {"urls":["https://.../market/5/slide/a.jpg","..."]}
                    """
    )
    @PostMapping(value = "/{influencerId}/market/images/slide", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, List<String>>> uploadMarketSlide(
            @PathVariable Long influencerId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> urls = s3Service.uploadMarketSlideImages(files, influencerId);
        return ResponseEntity.ok(Map.of("urls", urls));
    }

    @ApiOperation(
            value = "마켓 상세 이미지 업로드(여러 장)",
            notes = """
                    공구 마켓 상세 이미지를 여러 장 업로드합니다.
                    
                    [요청 예시]
                    POST /api/influencers/5/market/images/detail
                    Content-Type: multipart/form-data
                    (files: <이미지 파일들>)
                    
                    [응답 예시]
                    200 OK, body: {"urls":["https://.../market/5/detail/a.jpg","..."]}
                    """
    )
    @PostMapping(value = "/{influencerId}/market/images/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, List<String>>> uploadMarketDetail(
            @PathVariable Long influencerId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> urls = s3Service.uploadMarketDetailImages(files, influencerId);
        return ResponseEntity.ok(Map.of("urls", urls));
    }

}
