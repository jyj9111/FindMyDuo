package com.idle.fmd.global.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.dir}")
    private String dir;

    private final AmazonS3 amazonS3;

    public String upload(String imgDir, MultipartFile multipartFile) {
        String s3FileName = "";
        try {
            // 파일 이름 포함 저장할 경로 세팅
            s3FileName = dir + imgDir;
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);
        } catch (Exception error) {
            log.error(error.getMessage());
        }
        // 업로드한 파일 반환 받을 수 있는 url 반환
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    public void delete(String imgDir) {
        try {
            // S3의 경로 구성에 맞게 설정
            String s3FolderName = dir + "/" + imgDir + "/";
            // 위 경로의 파일들을 가져온다.
            ObjectListing objectListing = amazonS3.listObjects(bucket, s3FolderName);
            // 가져온 Object의 key값을 받아와서 해당 파일 삭제
            // 해당 폴더의 모든 파일 삭제
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                amazonS3.deleteObject(bucket, objectSummary.getKey());
            }
        } catch (Exception error) {
            log.error(error.getMessage());
        }
    }

    // 경로를 가지고 해당 파일 url 반환 메서드
    public String download(String imgDir) {
        String path = dir + imgDir;
        String url = "";
        try {
            url = amazonS3.getUrl(bucket, path).toString();
        } catch (Exception error) {
            log.error(error.getMessage());
            error.printStackTrace();
        }
        return url;
    }
}
