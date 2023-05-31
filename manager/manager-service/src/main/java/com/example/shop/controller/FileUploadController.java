package com.example.shop.controller;

import com.example.shop.base.BaseManager;
import com.example.shop.entity.R;

import com.example.shop.properties.MinIOProperties;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author: William
 * @date: 2023-05-31 00:54
 **/

@RestController
@RequiredArgsConstructor
public class FileUploadController extends BaseManager {

    private final MinIOProperties minIOProperties;

    @PostMapping("/admin/file/upload/element")
    public R<String> uploadElement(@RequestParam("file") MultipartFile multipartFile) {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(minIOProperties.getEndpoint())
                            .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(minIOProperties.getBucketName()).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minIOProperties.getBucketName()).build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
//            ObjectWriteResponse response = minioClient.uploadObject(
//                    UploadObjectArgs.builder()
//                            .bucket("sz2212")
//                            .object("asiaphotos-2015.jpg")
//                            .filename("D:\\googleDownload\\123.jpg")
//                            .build());

            String suffix = multipartFile.getOriginalFilename().substring(
                    multipartFile.getOriginalFilename().lastIndexOf(".")
            );

            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder().bucket(minIOProperties.getBucketName()).object(UUID.randomUUID().toString() + suffix).stream(
                                    new ByteArrayInputStream(multipartFile.getBytes()), multipartFile.getSize(), -1)
                            .build());

            return ok(
                    minIOProperties.getEndpoint() + "/" + minIOProperties.getBucketName() + "/" + response.object()
            );

        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
            e.printStackTrace();
        }
        return null;
    }





}
