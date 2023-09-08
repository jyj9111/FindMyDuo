package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.*;
import com.idle.fmd.domain.board.entity.*;
import com.idle.fmd.domain.board.repo.*;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.comment.repo.CommentRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.utils.FileHandler;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final CommentRepository commentRepository;
    private final FileHandler fileHandler;
    private final FileRepository fileRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReportRepository reportRepository;

    public void boardCreate(BoardCreateDto dto, List<MultipartFile> images, String accountId) {

        if (!userRepository.existsByAccountId(accountId)) {
            log.info("글을 작성하실 수 없습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        UserEntity userEntity = userRepository.findByAccountId(accountId).get();

        BoardEntity boardEntity = BoardEntity.createBoard(dto, userEntity);

        boardRepository.save(boardEntity);

        log.info("images : " + images);
        List<FileEntity> files = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String imgUrl = fileHandler.getBoardFilePath(boardEntity.getId(), image);
                files.add(fileRepository.save(FileEntity.createFile(boardEntity, imgUrl)));
            }
        }

        boardEntity.changeImageBoard(files);
        boardRepository.save(boardEntity);
    }

    public BoardResponseDto boardRead(Long boardId) {

        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity boardEntity = boardRepository.findById(boardId).get();

        return BoardResponseDto.fromEntity(boardEntity);
    }

    public void boardUpdate(BoardUpdateDto dto, List<MultipartFile> images, String accountId, Long boardId) {

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

        log.info("이미지 있는가 :" + images);
        // 이미지파일을 입력받을 경우 기존 이미지 삭제 후 입력받은 이미지를 추가해준다.
        List<FileEntity> files = new ArrayList<>();
        if (!images.isEmpty()) {

            log.info("게시판 이미지 수정 전 삭제");
            for (FileEntity file : boardEntity.getFiles()) {
                fileRepository.deleteById(file.getId());
            }

            // 경로에 있는 파일 삭제
            deleteBoardImageDirectory(boardId);

            log.info("게시판 이미지 수정 추가");
            for (MultipartFile image : images) {
                String imgUrl = fileHandler.getBoardFilePath(boardEntity.getId(), image);
                files.add(fileRepository.save(FileEntity.createFile(boardEntity, imgUrl)));
            }

            boardEntity.changeImageBoard(files);
        }

        boardEntity.updateBoard(dto.getTitle(), dto.getContent());
        boardRepository.save(boardEntity);

        BoardAllResponseDto.fromBoardEntity(boardEntity);
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

        // 경로에 있는 파일 삭제
        deleteBoardImageDirectory(boardId);
        log.info("게시글이 삭제되었습니다.");
        boardRepository.deleteById(boardId);
    }

    // 검색 기능
    // 제목으로 검색
    public Page<BoardAllResponseDto> searchBoardsTitle(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(BusinessExceptionCode.NO_SEARCH_QUERY_PARAMETER);
        }
        Page<BoardEntity> searchResult = boardRepository.findByTitleContainingIgnoreCase(query, pageable);
        return searchResult.map(BoardAllResponseDto::fromBoardEntity);
    }

    // 작성자로 검색
    public Page<BoardAllResponseDto> searchBoardsUser(String nickname, Pageable pageable) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(BusinessExceptionCode.NO_SEARCH_QUERY_PARAMETER);
        }
        UserEntity user = userRepository.findByNickname(nickname);
        if (user == null) {
            log.info("존재 하지 않는 사용자 입니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }
        Page<BoardEntity> searchUserResult = boardRepository.findByUser(user, pageable);
        return searchUserResult.map(BoardAllResponseDto::fromBoardEntity);
    }

    // 내용으로 검색
    public Page<BoardAllResponseDto> searchBoardsContent(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            throw new BusinessException(BusinessExceptionCode.NO_SEARCH_QUERY_PARAMETER);
        }
        Page<BoardEntity> searchContentResult = boardRepository.findByContentContainingIgnoreCase(query, pageable);
        return searchContentResult.map(BoardAllResponseDto::fromBoardEntity);
    }

    // 검색 기능 통합
    public Page<BoardAllResponseDto> searchBoardAll(String query, String searchBy, Pageable pageable) {
        if ("user".equals(searchBy)) {
            return searchBoardsUser(query, pageable);
        } else if ("content".equals(searchBy)) {
            return searchBoardsContent(query, pageable);
        } else if ("title".equals(searchBy)) {
            return searchBoardsTitle(query, pageable);
        } else {
            throw new BusinessException(BusinessExceptionCode.SEARCH_STANDARD_ERROR);
        }
    }


    // 게시글 전체 조회
    public Page<BoardAllResponseDto> boardReadAll(Pageable pageable) {
        Page<BoardEntity> boardPage = boardRepository.findAll(pageable);
        Page<BoardAllResponseDto> boardResponseDtoPage = boardPage.map(BoardAllResponseDto::fromBoardEntity);

        return boardResponseDtoPage;
    }

    private void deleteBoardImageDirectory (Long boardId){
        String boardImgDir = String.format("./images/board/%s", boardId);
        try {
            FileUtils.deleteDirectory(new File(boardImgDir));
        } catch (IOException e) {
            log.error("게시판 이미지 디렉토리 삭제 중 오류 발생");
            throw new BusinessException(BusinessExceptionCode.CANNOT_DELETE_DIRECTORY_ERROR);
        }
    }

    // 조회수 카운팅
    public void boardView(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }
        BoardEntity boardEntity = boardRepository.findById(id).get();
        boardRepository.updateViewCount(boardEntity.getView() + 1, boardEntity.getId());
    }

    // 인기순으로 정렬 기능
    @Transactional(readOnly = true)
    public Page<BoardAllResponseDto> findLikeSortBoards(Pageable pageable) {
        Page<BoardEntity> boardEntities = boardRepository.findAll(pageable);
        Page<BoardAllResponseDto> boardAllResponseDtos = boardEntities.map(BoardAllResponseDto::fromBoardEntity);
        return boardAllResponseDtos;
    }




}