package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.res.LikeBoardResponseDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.LikeBoardEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.board.repo.LikeBoardRepository;
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
public class LikeBoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeBoardRepository likeBoardRepository;

    @Transactional
    public LikeBoardResponseDto updateLikeOfBoard(String accountId, Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity board = boardRepository.findById(boardId).get();
        UserEntity user = userRepository.findByAccountId(accountId).get();

        String message = "";
        if (!hasLikeBoard(board, user)) {
            board.increaseLikeCount();
            message = createLikeBoard(board, user);
        } else {
            board.decreaseLikeCount();
            message = removeLikeBoard(board, user);
        }

        return LikeBoardResponseDto.fromEntity(board, message);
    }

    private String removeLikeBoard(final BoardEntity board, final UserEntity user) {
        LikeBoardEntity likeBoard = likeBoardRepository.findByBoardAndUser(board, user).get();

        likeBoardRepository.delete(likeBoard);
        log.info("좋아요 취소 완료");
        String message = "좋아요 취소 완료";
        return message;
    }

    private String createLikeBoard(final BoardEntity board, final UserEntity user) {
        LikeBoardEntity likeBoard = new LikeBoardEntity(board, user);
        likeBoardRepository.save(likeBoard);
        log.info("좋아요 처리 완료");
        String message = "좋아요 처리 완료";
        return message;
    }

    private boolean hasLikeBoard(final BoardEntity board, final UserEntity user) {
        return likeBoardRepository.findByBoardAndUser(board, user).isPresent();
    }
}
