package com.idle.fmd.domain.comment.service;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.comment.dto.CommentRequestDto;
import com.idle.fmd.domain.comment.dto.CommentResponseDto;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.comment.repo.CommentRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

        //댓글 생성
        public CommentResponseDto createComment(Long boardId, Authentication authentication, CommentRequestDto dto){
            String accountId = authentication.getName();

            // 댓글을 달기 전 게시물이 있는지 확인
            Optional<BoardEntity> boardEntity = boardRepository.findById(boardId);
            if(!boardEntity.isPresent()){
                throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
            }


            // 댓글 작성은 로그인 한 사람만 가능
            Optional<UserEntity> userEntity = userRepository.findByAccountId(accountId);
            UserEntity user = userEntity.get();
            if (!user.getAccountId().equals(accountId)){
                log.info("댓글을 작성하시려면 로그인 하십시오.");
                throw new BusinessException(BusinessExceptionCode.UNAUTHORIZED_USER);
            }
            BoardEntity newboardEntity = boardEntity.get();

            CommentEntity newComment = CommentEntity.builder()
                    .user(user)
                    .board(newboardEntity)
                    .content(dto.getContent())
                    .build();

            commentRepository.save(newComment);
            return CommentResponseDto.fromEntity(newComment);
        }


        // 게시물에 달린 댓글 전체 조회
        public List<CommentResponseDto> readCommentAll(Long boardId) {
            List<CommentEntity> commentEntities
                    = commentRepository.findAllByBoardId(boardId);
            List<CommentResponseDto> commentDtoList = new ArrayList<>();
            for (CommentEntity entity : commentEntities) {
                commentDtoList.add(CommentResponseDto.fromEntity(entity));
            }
            return commentDtoList;
        }


        // 댓글 업데이트
        public CommentResponseDto updateComment(
                Long boardId,
                Long commentId,
                Authentication authentication,
                CommentRequestDto dto){

            String username = authentication.getName();

            // 수정하려고 하는 댓글이 존재하는지 확인
            Optional<CommentEntity> optionalComment
                    = commentRepository.findById(commentId);
            if(optionalComment.isEmpty()){
                log.info("해당 댓글이 존재하지 않습니다.");
                throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
            }

            CommentEntity comment = optionalComment.get();

            // 해당 피드에 작성된 댓글을 수정하려고 하는 것인지 확인
            if(!boardId.equals(comment.getBoard().getId())){
                log.info("해당 게시글이 존재하지 않습니다.");
                throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
            }

            // 수정하려는 댓글의 사용자와 일치하는지 확인
            if(!comment.getUser().getAccountId().equals(username)){
                log.info("사용자와 일치하지 않습니다.");
                throw new BusinessException(BusinessExceptionCode.NOT_MATCHES_USER_ERROR);
            }

            // 댓글 수정
            CommentEntity updatedComment = CommentEntity.builder()
                    .id(comment.getId())
                    .content(dto.getContent())
                    .user(comment.getUser())
                    .board(comment.getBoard())
                    .build();

            CommentEntity savedComment = commentRepository.save(updatedComment);

            return CommentResponseDto.fromEntity(savedComment);
        }


        // 댓글 삭제
        public void deleteComment(Long boardId,
                                  Long commentId,
                                  Authentication authentication){

            String accountId = authentication.getName();

            // 삭제하려는 댓글 존재 확인
            Optional<CommentEntity> optionalComment
                    = commentRepository.findById(commentId);
            if(optionalComment.isEmpty()) {
                log.info("해당 댓글이 존재하지 않습니다.");
                throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
            }

            CommentEntity comment = optionalComment.get();

            // 삭제하려고 하는 댓글이 해당 피드의 달린 댓글인지 확인
            if(!boardId.equals(comment.getBoard().getId())){
                log.info("해당 게시글이 존재하지 않습니다.");
            throw new BusinessException(BusinessExceptionCode.NOT_EXISTS_BOARD_ERROR);
            }

            // 현재 사용자가 등록한 댓글인지 확인
            if(!comment.getUser().getAccountId().equals(accountId)){
                log.info("게시글을 작성한 사용자가 아닙니다.");
                throw new BusinessException(BusinessExceptionCode.UNAUTHORIZED_USER);
            }

            log.info("삭제되었습니다.");
            commentRepository.deleteById(commentId);

        }

    }
