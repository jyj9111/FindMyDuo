package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.ReportDto;
import com.idle.fmd.domain.board.entity.*;
import com.idle.fmd.domain.board.repo.*;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.comment.repo.CommentRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void updateOfReportBoard(String accountId, Long boardId, ReportDto dto) {
        if (!boardRepository.existsById(boardId)) {
            log.info("해당 게시글은 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
        }

        BoardEntity board = boardRepository.findById(boardId).get();
        UserEntity user = userRepository.findByAccountId(accountId).get();

        if (!hasReportBoard(board, user)) {
            board.increaseReportCount();
            createReport(board, user, dto);
        } else {
            board.decreaseReportCount();
            removeReport(board, user);
        }

        // 테스트를 위하여 신고횟수가 2회이상일시 게시글 삭제로 설정함
        if (board.getReported() > 1) {

            List<FileEntity> files = fileRepository.findAllByBoardId(boardId);
            fileRepository.deleteAll(files);

            // 게시글 삭제 전에 해당 게시글의 댓글들을 삭제
            List<CommentEntity> commentsToDelete = commentRepository.findAllByBoardId(boardId);
            commentRepository.deleteAll(commentsToDelete);

            // 게시글 삭제 전에 해당 좋아요 삭제
            List<LikeBoardEntity> likeBoard = likeBoardRepository.findAllByBoardId(boardId);
            likeBoardRepository.deleteAll(likeBoard);


            // 게시글 삭제 전에 해당 즐겨찾기 삭제
            List<BookmarkEntity> bookmarkBoard = bookmarkRepository.findAllByBoardId(boardId);
            bookmarkRepository.deleteAll(bookmarkBoard);


            // 게시글 삭제 전에 신고 삭제
            List<ReportEntity> reports = reportRepository.findAllByBoardId(boardId);
            reportRepository.deleteAll(reports);

            boardRepository.deleteById(boardId);
        }
    }

    private void removeReport(BoardEntity board, UserEntity user) {
        ReportEntity report = reportRepository.findByBoardAndUser(board, user).get();

        reportRepository.delete(report);

        log.info("신고 취소완료");
    }

    private void createReport(BoardEntity board, UserEntity user, ReportDto dto) {
        ReportEntity report = new ReportEntity(board, user, dto.getContent());
        reportRepository.save(report);
        log.info("신고 완료");
    }

    private boolean hasReportBoard(BoardEntity board, UserEntity user) {
        return reportRepository.findByBoardAndUser(board, user).isPresent();
    }
}
