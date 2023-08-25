package com.idle.fmd.domain.board.service;

import com.idle.fmd.domain.board.dto.ReportDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.ReportEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.board.repo.ReportRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

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

        if (board.getReported() > 1) {
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
