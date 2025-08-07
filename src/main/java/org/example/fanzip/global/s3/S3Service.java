package org.example.fanzip.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.influencer.mapper.InfluencerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.net.URL;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    private final InfluencerMapper influencerMapper;

    private static final String BUCKET_NAME = "fanzip"; // S3 버킷 이름
    private static final String PROFILE_IMAGE_PATH = "influencer_profile";  // S3 버킷에 프로필 이미지가 저장될 경로
    private static final String FANCARD_IMAGE_PATH = "fancard_image"; // S3 버킷에 팬카드 이미지가 저장될 경로


    /**
     * 1. 인플루언서 프로필 이미지 업로드
     */

    @Transactional
    public String uploadProfileImage(MultipartFile file, Long influencerId) {
        System.out.println("🔥 uploadProfileImage() 진입");
        System.out.println("🔥 influencerId = " + influencerId);
        System.out.println("🔥 file.isEmpty() = " + file.isEmpty());
        System.out.println("🔥 file.originalName = " + file.getOriginalFilename());

        String existingImageUrl = getCurrentProfileImageUrl(influencerId);
        System.out.println("🔥 기존 이미지 URL: " + existingImageUrl);

         // ✅ 기존 이미지가 있다면 삭제
//        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
//            deleteS3Image(existingImageUrl);
//            System.out.println("🗑 기존 이미지 삭제 완료");
//        }

        String newImageUrl = uploadImage(file, PROFILE_IMAGE_PATH);
        System.out.println("✅ S3 업로드 성공: " + newImageUrl);

        updateProfileImageUrl(influencerId, newImageUrl);
        System.out.println("✅ DB 업데이트 완료");

        // ✅ 업데이트된 URL 직접 조회해서 확인해보기
        String updatedImageUrl = getCurrentProfileImageUrl(influencerId);
        System.out.println("🎯 업데이트 후 DB 조회한 이미지 URL: " + updatedImageUrl);

        return newImageUrl;
    }

    /**
     * 2. 팬카드 이미지 업로드
     */
    @Transactional
    public String uploadFanCardImage(MultipartFile file, Long influencerId) {
        System.out.println("🔥 uploadFanCardImage() 진입");
        System.out.println("🔥 influencerId = " + influencerId);
        System.out.println("🔥 file.isEmpty() = " + file.isEmpty());
        System.out.println("🔥 file.originalName = " + file.getOriginalFilename());

        // 기존 팬카드 이미지 URL 조회
        String existingCardUrl = getCurrentFanCardImageUrl(influencerId);
        System.out.println("🔥 기존 팬카드 이미지 URL: " + existingCardUrl);

        // 기존 이미지가 있으면 삭제
//        if (existingCardUrl != null && !existingCardUrl.isEmpty()) {
//            deleteS3Image(existingCardUrl);
//            System.out.println("🗑 기존 이미지 삭제 완료");
//        }

        // 새 이미지 업로드
        String newCardUrl = uploadImage(file, FANCARD_IMAGE_PATH);
        System.out.println("✅ S3 팬카드 업로드 성공: " + newCardUrl);

        // DB 업데이트
        updateFanCardImageUrl(influencerId, newCardUrl);
        System.out.println("✅ DB 업데이트 완료");

        return newCardUrl;
    }
    /**
     * 3. 실제 S3에 이미지 업로드 처리
     */
    private String uploadImage(MultipartFile file, String directory) {
        String fileName = createFileName(file.getOriginalFilename());
        String key = directory + "/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());



        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, key, inputStream, metadata));
            return amazonS3.getUrl(BUCKET_NAME, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 이미지 업로드 실패", e);
        }
    }

    /**
     * 4. 랜덤 파일명 생성
     */
    private String createFileName(String originalName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalName));
    }

    /**
     * 5. 파일 확장자 유효성 검사
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 파일명입니다.");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // 허용할 확장자 목록 (소문자 기준)
        List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".heic");

        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 확장자입니다: " + extension);
        }

        return extension;
    }

    /**
     * 6) 현재 인플루언서의 프로필 이미지 URL 조회 (DB에서)
     */
    public String getCurrentProfileImageUrl(Long influencerId) {
        return influencerMapper.selectProfileImageUrl(influencerId);
    }

    /**
     * 7) S3에 저장된 이미지를 삭제하는 메서드
     * @param imageUrl 삭제할 이미지의 S3 URL
     */
//    public void deleteS3Image(final String imageUrl) {
//        if (imageUrl == null || imageUrl.isEmpty()) return;
//
//        try {
//            URL url = new URL(imageUrl);
//            String imageKey = url.getPath().substring(1); // "/influencer_profile/abc.jpg" → "influencer_profile/abc.jpg"
//
//            amazonS3.deleteObject(BUCKET_NAME, imageKey);
//
//            if (amazonS3.doesObjectExist(BUCKET_NAME, imageKey)) {
//                throw new RuntimeException("S3 이미지 삭제 실패: " + imageKey);
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("잘못된 S3 이미지 URL입니다: " + imageUrl);
//        }
//    }

    /**
     * 8) DB에서 프로필 이미지 URL null 처리
     */

    public void updateProfileImageUrl(Long influencerId, String newUrl) {
        influencerMapper.updateProfileImage(influencerId, newUrl);
    }


    /*
    * 팬카드 이미지 조회
    * */
    public String getCurrentFanCardImageUrl(Long influencerId) {
        return influencerMapper.selectFanCardImageUrl(influencerId);
    }

    /*
    * 팬카드 이미지 업데이트
    * */
    public void updateFanCardImageUrl(Long influencerId, String newUrl) {
        influencerMapper.updateFanCardImageUrl(influencerId, newUrl);
    }

}