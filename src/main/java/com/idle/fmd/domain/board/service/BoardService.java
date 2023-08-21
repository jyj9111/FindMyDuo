package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.BoardCreateDto;
import com.idle.fmd.domain.board.dto.BoardResponseDto;
import com.idle.fmd.domain.board.dto.BoardUpdateDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.file.entity.FileEntity;
import com.idle.fmd.domain.file.repo.FileRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.common.utils.FileHandler;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final FileHandler fileHandler;
    private final FileRepository fileRepository;

    public BoardResponseDto boardCreate(BoardCreateDto dto, List<MultipartFile> images, String accountId) {

        if (!userRepository.existsByAccountId(accountId)) {
            log.info("글을 작성하실 수 없습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        UserEntity userEntity = userRepository.findByAccountId(accountId).get();

        BoardEntity boardEntity = BoardEntity.createBoard(dto, userEntity);

        boardRepository.save(boardEntity);

        List<FileEntity> files = new ArrayList<>();
        if (images.get(0).getContentType() != null) {
            for (MultipartFile image : images) {
                String imgUrl = fileHandler.getBoardFilePath(boardEntity.getId(), image);
                files.add(fileRepository.save(FileEntity.createFile(boardEntity, imgUrl)));
            }
        }

        boardEntity.createImageBoard(files);
        boardRepository.save(boardEntity);

        return BoardResponseDto.fromEntity(boardEntity);
    }

    public BoardResponseDto boardRead(Long boardId) {

        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity boardEntity = boardRepository.findById(boardId).get();

        return BoardResponseDto.fromEntity(boardEntity);
    }

    public BoardResponseDto boardUpdate(BoardUpdateDto dto, String accountId, Long boardId) {

        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        String dbId = boardEntity.getUser().getAccountId();

        if (!dbId.equals(accountId)) {
            log.info("게시글 작성자와 수정하는 작성자가 일치하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_MATCHES_USER_ERROR);
        }

        boardEntity.updateBoard(dto.getTitle(), dto.getContent());
        boardRepository.save(boardEntity);

        return BoardResponseDto.fromEntity(boardEntity);
    }

    public void boardDelete(String accountId, Long boardId) {

        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        String dbId = boardEntity.getUser().getAccountId();

        if (!dbId.equals(accountId)) {
            log.info("게시글 작성자와 수정하는 작성자가 일치하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_MATCHES_USER_ERROR);
        }

        for (FileEntity file : boardEntity.getFiles()) {
            fileRepository.deleteById(file.getId());
        }

        deleteBoardImageDirectory(boardId);
        log.info("게시글이 삭제되었습니다.");
        boardRepository.deleteById(boardId);
    }

    private void deleteBoardImageDirectory(Long boardId) {
        String boardImgDir = String.format("./images/board/%s", boardId);
        try {
            FileUtils.deleteDirectory(new File(boardImgDir));
        } catch (IOException e) {
            log.error("게시판 이미지 디렉토리 삭제 중 오류 발생");
            throw new BusinessException(BusinessExceptionCode.CANNOT_DELETE_DIRECTORY_ERROR);
        }
    }

    public Page<BoardResponseDto> boardReadAll(Pageable pageable) {
        Page<BoardEntity> boardPage = boardRepository.findAll(pageable);
        Page<BoardResponseDto> boardResponseDtoPage = boardPage.map(BoardResponseDto::fromEntity);

        return boardResponseDtoPage;
    }
}
