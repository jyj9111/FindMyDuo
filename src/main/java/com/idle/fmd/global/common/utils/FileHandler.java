package com.idle.fmd.global.common.utils;

import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Component
public class FileHandler {

    public String getBoardFilePath(Long id, MultipartFile boardImage) {
        String imgName = LocalDateTime.now().toString()
                .replace(":", "").replace("-", "").replace(".", "");

        String extension = "." + boardImage.getOriginalFilename().split("\\.")[1];
        String filename = imgName + extension;
        String imgDir = String.format("./images/board/%d", id);

        try {
            Files.createDirectories(Paths.get(imgDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(BusinessExceptionCode.CANNOT_SAVE_BOARD_IMAGE_ERROR);
        }

        File file = new File(Path.of(imgDir, filename).toUri());

        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(boardImage.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(BusinessExceptionCode.CANNOT_SAVE_BOARD_IMAGE_ERROR);
        }

        log.debug("board_id: [{}], message: 게시글 이미지등록 성공", id);
        return String.format("/static/board/%d/%s", id, filename);
    }
}
