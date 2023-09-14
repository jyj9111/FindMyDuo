package com.idle.fmd.domain.board.controller;

import com.idle.fmd.domain.board.dto.req.BoardCreateDto;
import com.idle.fmd.domain.board.dto.req.BoardUpdateDto;
import com.idle.fmd.domain.board.dto.req.ReportDto;
import com.idle.fmd.domain.board.dto.res.*;
import com.idle.fmd.domain.board.service.BoardService;
import com.idle.fmd.domain.board.service.BookmarkService;
import com.idle.fmd.domain.board.service.LikeBoardService;
import com.idle.fmd.domain.board.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;
    private final LikeBoardService likeBoardService;
    private final BookmarkService bookmarkService;
    private final ReportService reportService;

    // 게시글 작성
    @PostMapping
    public void boardCreate(@RequestPart(value = "dto") @Validated BoardCreateDto dto,
                            @RequestPart(value = "file", required = false) List<MultipartFile> images,
                            Authentication authentication) {
        boardService.boardCreate(dto, images, authentication.getName());
    }

    // 게시글 단일조회
    @GetMapping("/{boardId}")
    public BoardResponseDto boardRead(@PathVariable Long boardId) {
        boardService.boardView(boardId);
        return boardService.boardRead(boardId);
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public void boardUpdate(@RequestPart(value = "dto") @Validated BoardUpdateDto dto,
                            @RequestPart(value = "file", required = false) List<MultipartFile> images,
                            Authentication authentication,
                            @PathVariable Long boardId) {
        if (images == null) images = new ArrayList<>();
        boardService.boardUpdate(dto, images, authentication.getName(), boardId);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public void boardDelete(Authentication authentication, @PathVariable Long boardId) {
        boardService.boardDelete(authentication.getName(), boardId);
    }

    // 게시글 전체조회 (페이징 처리)
    @GetMapping()
    public Page<BoardAllResponseDto> boardReadAll(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.boardReadAll(pageable);
    }


    // 게시글 검색
    @GetMapping("/search")
    public Page<BoardAllResponseDto> boardSearch(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String searchBy,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.searchBoardAll(query, searchBy, pageable);
    }

    // 좋아요 기능
    @PostMapping("/{boardId}/like")
    public LikeBoardResponseDto likeBoard(Authentication authentication, @PathVariable Long boardId) {
        return likeBoardService.updateLikeOfBoard(authentication.getName(), boardId);
    }

    // 즐겨찾기 기능
    @PostMapping("/{boardId}/bookmark")
    public BookmarkResponseDto bookmarkBoard(Authentication authentication, @PathVariable Long boardId) {
        return bookmarkService.updateOfBookmarkBoard(authentication.getName(), boardId);
    }

    // 신고 기능 현재는 2회 이상 신고이면 게시글 삭제로 해놓음(테스트 용이)
    @PostMapping("/{boardId}/report")
    public ReportResponseDto reportBoard(Authentication authentication, @PathVariable Long boardId, @RequestBody ReportDto dto) {
        return reportService.updateOfReportBoard(authentication.getName(), boardId, dto);
    }

    // 인기순으로 글 조회
    @GetMapping("/best")
    public Page<BoardAllResponseDto> likeSortBoard(@PageableDefault(size = 20, sort = "liked", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.findLikeSortBoards(pageable);
    }
}
