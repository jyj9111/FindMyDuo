package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.BoardCreateDto;
import com.idle.fmd.domain.board.dto.BoardAllResponseDto;
import com.idle.fmd.domain.board.dto.BoardResponseDto;
import com.idle.fmd.domain.board.dto.BoardUpdateDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.BookmarkEntity;
import com.idle.fmd.domain.board.entity.LikeBoardEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.board.entity.FileEntity;
import com.idle.fmd.domain.board.repo.BookmarkRepository;
import com.idle.fmd.domain.board.repo.FileRepository;
import com.idle.fmd.domain.board.repo.LikeBoardRepository;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.comment.repo.CommentRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.utils.FileHandler;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

                if (file.isDeleted() == false) fileRepository.deleteById(file.getId());
            }

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

        for (FileEntity file : boardEntity.getFiles()) {
            fileRepository.deleteById(file.getId());
        }


        // 게시글 삭제 전에 해당 게시글의 댓글들을 삭제
        List<CommentEntity> commentsToDelete = commentRepository.findAllByBoardId(boardId);
        commentRepository.deleteAll(commentsToDelete);

        // 게시글 삭제 전에 해당 좋아요 삭제
        List<LikeBoardEntity> likeBoard = likeBoardRepository.findAllByBoardId(boardId);
        likeBoardRepository.deleteAll(likeBoard);
        boardEntity.clearLikeCount();

        // 게시글 삭제 전에 해당 즐겨찾기 삭제
        List<BookmarkEntity> bookmarkBoard = bookmarkRepository.findAllByBoardId(boardId);
        bookmarkRepository.deleteAll(bookmarkBoard);
        boardEntity.clearBookmarkCount();

        log.info("게시글이 삭제되었습니다.");
        boardRepository.deleteById(boardId);
    }

    public Page<BoardAllResponseDto> boardReadAll(Pageable pageable) {
        Page<BoardEntity> boardPage = boardRepository.findAll(pageable);
        Page<BoardAllResponseDto> boardResponseDtoPage = boardPage.map(BoardAllResponseDto::fromBoardEntity);

        return boardResponseDtoPage;
    }

    @Transactional
    public String updateLikeOfBoard(String accountId, Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity board = boardRepository.findById(boardId).get();
        UserEntity user = userRepository.findByAccountId(accountId).get();

        if (!hasLikeBoard(board, user)) {
            board.increaseLikeCount();
            return createLikeBoard(board, user);
        }

        board.decreaseLikeCount();
        return removeLikeBoard(board, user);
    }

    private String removeLikeBoard(final BoardEntity board, final UserEntity user) {
        LikeBoardEntity likeBoard = likeBoardRepository.findByBoardAndUser(board, user).get();

        likeBoardRepository.delete(likeBoard);

        return "좋아요 취소 완료";
    }

    private String createLikeBoard(final BoardEntity board, final UserEntity user) {
        LikeBoardEntity likeBoard = new LikeBoardEntity(board, user);
        likeBoardRepository.save(likeBoard);
        return "좋아요 처리 완료";
    }

    private boolean hasLikeBoard(final BoardEntity board, final UserEntity user) {
        return likeBoardRepository.findByBoardAndUser(board, user).isPresent();
    }

    @Transactional
    public String updateOfBookmarkBoard(String accountId, Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity board = boardRepository.findById(boardId).get();
        UserEntity user = userRepository.findByAccountId(accountId).get();

        if (!hasBookmarkBoard(board, user)) {
            board.increaseBookmarkCount();
            return createBookmark(board, user);
        }

        board.decreaseBookmarkCount();
        return removeBookmark(board, user);
    }

    private String removeBookmark(BoardEntity board, UserEntity user) {
        BookmarkEntity bookmark = bookmarkRepository.findByBoardAndUser(board, user).get();

        bookmarkRepository.delete(bookmark);

        return "즐겨찾기 취소완료";
    }

    private String createBookmark(final BoardEntity board, final UserEntity user) {
        BookmarkEntity bookmark = new BookmarkEntity(board, user);
        bookmarkRepository.save(bookmark);
        return "즐겨찾기 처리완료";
    }

    private boolean hasBookmarkBoard(BoardEntity board, UserEntity user) {
        return bookmarkRepository.findByBoardAndUser(board, user).isPresent();
    }
}
