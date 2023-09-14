package com.idle.fmd.global.utils;

import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileHandler {
    private final AwsS3Service awsS3Service;

    public String getBoardFilePath(Long id, MultipartFile image) {
        String imgName = LocalDateTime.now().toString()
                .replace(":", "").replace("-", "").replace(".", "");
        String extension = "." + image.getOriginalFilename().split("\\.")[1];
        String filename = imgName + extension;
        String imgDir = String.format("/images/board/%d/%s", id, filename);

        String imageUrl = "";
        try {
            imageUrl = awsS3Service.upload(imgDir, image);
        } catch (Exception error) {
            log.error(error.getMessage());
            throw new BusinessException(BusinessExceptionCode.CANNOT_SAVE_BOARD_IMAGE_ERROR);
        }

        log.debug("board_id: [{}], message: 게시글 이미지업로드 성공", id);
        return imageUrl;
    }

    public String getBasicProfilePath(){
        String url = "";
        try {
            url = awsS3Service.download("/images/profile/basic.png");
        } catch (Exception error) {
            log.error(error.getMessage());
            error.printStackTrace();
            throw new BusinessException(BusinessExceptionCode.CANNOT_BRING_BASIC_IMAGE_ERROR);
        }
        return url;
    }

    public String getProfileFilePath(String accountId, MultipartFile image) {
        // 이미지 이름 생성
        String originalImageName = image.getOriginalFilename();
        String extension = originalImageName.substring(originalImageName.lastIndexOf(".") + 1);
        String filename = "profile." + extension;

        // 유저 ID를 프로필 디렉토리명으로 설정
        String imgDir = String.format("/images/profile/%s/%s", accountId, filename);
        String imageUrl = "";
        try {
            imageUrl = awsS3Service.upload(imgDir, image);

        } catch (Exception error) {
            log.error(error.getMessage());
            throw new BusinessException(BusinessExceptionCode.CANNOT_SAVE_IMAGE_ERROR);
        }

        log.debug("accountId: [{}], message: 프로필 이미지 업로드 성공", accountId);
        return imageUrl;
    }

    public void deleteFolder(String imgDir, String type) {
        try {
            // S3 - imgDir 경로에 있는 이미지 삭제
            awsS3Service.delete(imgDir);
        } catch (Exception error) {
            log.error(error.getMessage());
            if (type.equals("board")) {
                throw new BusinessException(BusinessExceptionCode.CANNOT_DELETE_BOARD_DIRECTORY_ERROR);
            } else if (type.equals("profile")) {
                throw new BusinessException(BusinessExceptionCode.CANNOT_DELETE_DIRECTORY_ERROR);
            }
        }
    }

    // 챔피언 이름을 입력받아 해당 챔피언 이미지 url경로 반환 메서드
    public String getChampionImgPath(String champ) {
        String path = "/images/lol/champion/" + champ + ".png";
        String url = "";
        try {
            url = awsS3Service.download(path);
        } catch (Exception error) {
            log.error(error.getMessage());
            error.printStackTrace();
            throw new BusinessException(BusinessExceptionCode.CANNOT_BRING_CHAMPION_IMAGE_ERROR);
        }
        return url;
    }

    // 티어 이름을 입력받아 해당 티어 이미지 url경로 반환 메서드
    public String getTierImgPath(String tier) {
        String path = "/images/lol/tier/" + tier + ".png";
        String url = "";
        try {
            url = awsS3Service.download(path);
        } catch (Exception error) {
            log.error(error.getMessage());
            error.printStackTrace();
            throw new BusinessException(BusinessExceptionCode.CANNOT_BRING_TIER_IMAGE_ERROR);
        }
        return url;
    }
}
