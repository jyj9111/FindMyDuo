package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.res.BookmarkResponseDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.BookmarkEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.board.repo.BookmarkRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public BookmarkResponseDto updateOfBookmarkBoard(String accountId, Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity board = boardRepository.findById(boardId).get();
        UserEntity user = userRepository.findByAccountId(accountId).get();

        String message = "";
        if (!hasBookmarkBoard(board, user)) {
            board.increaseBookmarkCount();
            message = createBookmark(board, user);
        } else {
            board.decreaseBookmarkCount();
            message = removeBookmark(board, user);
        }

        return BookmarkResponseDto.fromEntity(board, message);
    }

    private String removeBookmark(BoardEntity board, UserEntity user) {
        BookmarkEntity bookmark = bookmarkRepository.findByBoardAndUser(board, user).get();

        bookmarkRepository.delete(bookmark);
        log.info("즐겨찾기 취소완료");
        String message = "즐겨찾기 취소완료";
        return message;
    }

    private String createBookmark(final BoardEntity board, final UserEntity user) {
        BookmarkEntity bookmark = new BookmarkEntity(board, user);
        bookmarkRepository.save(bookmark);
        log.info("즐겨찾기 처리완료");
        String message = "즐겨찾기 처리완료";
        return message;
    }

    private boolean hasBookmarkBoard(BoardEntity board, UserEntity user) {
        return bookmarkRepository.findByBoardAndUser(board, user).isPresent();
    }
}
