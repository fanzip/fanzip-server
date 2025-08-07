package org.example.fanzip.global.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;


@Configuration
public class AwsS3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;


    @PostConstruct
    public void checkKeys() {
        System.out.println("✅ Access Key: " + accessKey);
        System.out.println("✅ Secret Key: " + secretKey);
        System.out.println("✅ Region: " + region);
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        System.out.println("== AWS S3 설정 확인 ==");
        System.out.println("Region: " + region);
        System.out.println("Access Key: " + accessKey);
        System.out.println("Secret Key: " + secretKey);



        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}
