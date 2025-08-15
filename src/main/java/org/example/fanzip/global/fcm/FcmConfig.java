package org.example.fanzip.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;

// FCM 초기화 클래스

@Configuration
public class FcmConfig {

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @Value("${firebase.project-id}")
    private String projectId;

    @PostConstruct
    public void init() throws Exception {
        // 이미 초기화돼 있으면 패스
        if (!FirebaseApp.getApps().isEmpty()) return;

        InputStream is = null;
        
        // Firebase credentials JSON이 환경변수로 제공된 경우 우선 사용
        String credentialsJson = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            is = new java.io.ByteArrayInputStream(credentialsJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } else {
            // 파일 경로 방식 (기존 방식 유지)
            if (credentialsPath.startsWith("classpath:")) {
                String path = credentialsPath.replace("classpath:", "");
                is = FcmConfig.class.getClassLoader().getResourceAsStream(path);
            } else {
                is = new java.io.FileInputStream(credentialsPath);
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .setProjectId(projectId)
                .build();

        FirebaseApp.initializeApp(options);
    }
}
