package com.idle.fmd.domain.user.service;


import com.idle.fmd.domain.board.dto.res.BoardAllResponseDto;
import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.BookmarkEntity;
import com.idle.fmd.domain.board.repo.BoardRepository;
import com.idle.fmd.domain.board.repo.BookmarkRepository;
import com.idle.fmd.domain.board.service.BoardService;
import com.idle.fmd.domain.user.dto.req.*;
import com.idle.fmd.domain.user.dto.res.UserLoginResponseDto;
import com.idle.fmd.domain.user.dto.res.UserMyPageResponseDto;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import com.idle.fmd.global.utils.FileHandler;
import com.idle.fmd.global.utils.RedisUtil;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.idle.fmd.domain.user.entity.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final CustomUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final UserRepository repository;
    private final BookmarkRepository bookmarkRepository;
    private final FileHandler fileHandler;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final UserRepository userRepository;

    // 회원가입 메서드
    public void signup(SignupDto dto){
        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 데이터가 다르면 예외 발생
        if(!password.equals(passwordCheck))
            throw new BusinessException(BusinessExceptionCode.PASSWORD_CHECK_ERROR);

        // 이미 존재하는 이메일로 회원가입을 시도하면 예외 발생
        if(manager.existByEmail(dto.getEmail()))
            throw new BusinessException(BusinessExceptionCode.DUPLICATED_EMAIL_ERROR);

        // 등록하려는 이메일에 해당되는 인증코드를 가져온다.
        Object emailAuthObject = redisUtil.getAuthCode(dto.getEmail());

        // 해당하는 이메일의 인증코드가 없다면 이메일 요청을 보내지 않았음을 알리는 예외발생
        if(emailAuthObject == null)
            throw new BusinessException(BusinessExceptionCode.NO_EMAIL_AUTH_REQUEST_ERROR);

        // Object 형태의 인증코드를 문자열 타입으로 변환한다.
        String emailAuthCode = emailAuthObject.toString();

        // 이메일로 보낸 인증코드와 입력한 인증코드가 같은지 비교해서 같지 않으면 유효한 인증코드가 아님을 알리는 예외발생
        if(!emailAuthCode.equals(dto.getEmailAuthCode()))
            throw new BusinessException(BusinessExceptionCode.NOT_VALID_EMAIL_AUTH_CODE_ERROR);

        // CustomUserDetailsManager 의 createUser 메서드를 호출해서 유저를 등록 ( UserDetails 객체 전달 필요 )
        manager.createUser(
                CustomUserDetails.builder()
                        .accountId(dto.getAccountId())
                        .email(dto.getEmail())
                        .nickname(dto.getNickname())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .profileImage(fileHandler.getBasicProfilePath())
                        .build()
        );

        redisUtil.delete(dto.getEmail());
    }

    // 로그인 메서드
    public UserLoginResponseDto loginUser(UserLoginRequestDto dto) {

        log.info("로그인 : " + manager.userExists(dto.getAccountId()));

        if (!manager.userExists(dto.getAccountId()))
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);

        CustomUserDetails userDetails = (CustomUserDetails)manager.loadUserByUsername(dto.getAccountId());

        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new BusinessException(BusinessExceptionCode.LOGIN_PASSWORD_CHECK_ERROR);

        // 새로운 액세스 토큰과 리프레쉬 토큰 생성
        String token = jwtTokenUtils.generateToken(userDetails);
        redisUtil.issueRefreshToken(token);

        return new UserLoginResponseDto(token, userDetails.getNickname(), userDetails.getProfileImage());
    }

    // Oauth 로그인 유저를 위한 메서드
    public UserLoginResponseDto loginForOauthUser(String token) {
        // 전달 받은 jwt Token 유효성 검사
        if (!jwtTokenUtils.validate(token)) {
            throw new BusinessException(BusinessExceptionCode.TOKEN_ACCOUNT_MISMATCH_ERROR);
        }
        // 전달 받은 jwt 토큰에서 accountId 값 추출
        String accountId = jwtTokenUtils.parseClaims(token).getSubject();
        // UserDetails에서 nickname 추출
        CustomUserDetails userDetails = (CustomUserDetails)manager.loadUserByUsername(accountId);
        return new UserLoginResponseDto(token, userDetails.getNickname(), userDetails.getProfileImage());
    }

    // 이메일 인증 메일을 보내는 메서드
    public void sendEmail(EmailAuthRequestDto dto){
        // 이메일 전송 시 내용 구성 설정객체 생성
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 어디로 보낼 것인지 설정
        simpleMailMessage.setTo(dto.getEmail());
        // 제목 설정
        simpleMailMessage.setSubject("[구해듀오] 이메일 인증 요청메일입니다.");
        // 랜덤한 6자리의 난수를 인증코드로 생성 후 이메일 내용에 포함
        int authCode = (int)(Math.random() * 1000000);
        simpleMailMessage.setText("아래의 인증코드를 입력해주세요.\n" + authCode);

        // 이메일 전송
        mailSender.send(simpleMailMessage);

        // redisUtil 클래스의 setEmailAuthCode() 메서드를 이용해서 해당 이메일로 보내진 인증코드를 저장
        redisUtil.setEmailAuthCode(dto.getEmail(), authCode);
    }

    // 로그아웃 메서드
    public void logout(String token){
        // redisUtil 의 setBlackListToken() 메서드를 이용해서 해당 토큰을 블랙리스트로 등록
        redisUtil.setBlackListToken(token);
        // 로그아웃을 하면 리프레쉬 토큰을 제거함
        redisUtil.removeRefreshToken(token);
    }

    // 유저 조회 메서드
    public UserMyPageResponseDto profile(String accountId) {
        Optional<UserEntity> entity = repository.findByAccountId(accountId);

        if(entity.isPresent()) {
            return UserMyPageResponseDto.fromEntity(entity.get());
        } else throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
    }

    // 유저 정보 수정 메서드
    public UserMyPageRequestDto update(String accountId, UserMyPageRequestDto dto) {
        String checkAccountId = dto.getAccountId();

        // 토큰에 있는 accountId 와 현재 바디에 담긴 accountId 정보가 다를 때 예외 발생
        if(!accountId.equals(checkAccountId))
            throw new BusinessException(BusinessExceptionCode.TOKEN_ACCOUNT_MISMATCH_ERROR);

        CustomUserDetails updateUserDetails =
                CustomUserDetails.builder()
                        .email(dto.getEmail())
                        .nickname(dto.getNickname())
                        .build();

        // CustomUserDetailsManager 의 updateUser 메서드를 호출해서 유저를 등록 (UserDetails 객체 전달 필요)
        manager.updateUser(updateUserDetails, dto.getAccountId());

        return dto;
    }

    // 비밀번호 변경 메서드
    public void changePassword(String accountId, ChangePasswordRequestDto dto) {
        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 데이터가 다르면 예외 발생 (회원가입에 사용한 에러 사용)
        if(!password.equals(passwordCheck))
            throw new BusinessException(BusinessExceptionCode.PASSWORD_CHECK_ERROR);

        // 비밀번호 값이 null 일 경우 (입력하지 않고 요청을 보냈을 경우)
        if(password == null || password.trim().isEmpty()) {
            throw new BusinessException(BusinessExceptionCode.EMPTY_PASSWORD_ERROR);
        }

        // 비밀번호 길이 검증 후 8자리 미만 시 예외 발생
        if(password.length() < 8) {
            throw new BusinessException(BusinessExceptionCode.PASSWORD_LENGTH_ERROR);
        }

        CustomUserDetails userDetails =
                CustomUserDetails.builder()
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .build();

        // CustomUserDetailsManager 의 updateUser 메서드를 호출해서 유저를 등록 (UserDetails 객체 전달 필요)
        manager.changePassword(accountId, userDetails.getPassword());
    }

    // User 삭제 메서드
    public void delete(String accountId) {
        // 이미 탈퇴를 해서 없는 회원일 경우 존재하지 않는 아이디 예외 발생 (로그인에 처리한 예외 사용)
        if(!manager.userExists(accountId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        // 회원 탈퇴시 게시글에 포함된 이미지 저장한 디렉토리 삭제
        UserEntity userEntity = userRepository.findByAccountId(accountId).get();
        List<BoardEntity> boards = boardRepository.findAllByUser(userEntity);

        for (BoardEntity board: boards) {
            Long boardId = board.getId();
            log.info("회원 탈퇴 디렉토리 삭제 테스트 : " + boardId);
            boardService.deleteBoardImageDirectory(boardId);
        }

        // UserDetailsManager 의 deleteUser 메소드를 호출하여 유저 정보 삭제
        manager.deleteUser(accountId);

        // 프로필 이미지 저장한 디렉토리 삭제
        deleteProfileImageDirectory(accountId);
    }

    // 프로필 이미지 변경 메서드
    public String uploadProfileImage(String accountId, MultipartFile image) {
        String imageUrl = fileHandler.getProfileFilePath(accountId, image);
        manager.updateProfileImage(accountId, imageUrl);
        return imageUrl;
    }

    // 회원 탈퇴 시 프로필 이미지 디렉토리 삭제 메서드
    public void deleteProfileImageDirectory(String accountId) {
        // 프로필 이미지 저장 폴더 경로 세팅
        String profileDir = String.format("images/profile/%s", accountId);
        // 해당 폴더의 이미지 삭제
        fileHandler.deleteFolder(profileDir, "profile");
    }

    public Page<BoardAllResponseDto> findBookmark(String accountId, Pageable pageable) {

        UserEntity user = repository.findByAccountId(accountId).get();

        Page<BookmarkEntity> board = bookmarkRepository.findAllByUser(user, pageable);
        Page<BoardAllResponseDto> boardDto = board.map(BoardAllResponseDto::fromBookmarkEntity);

        return boardDto;
    }

    // 아이디 중복 확인
    public boolean existsByAccountId(String accountId) {
        return repository.existsByAccountId(accountId);
    }

    // 닉네임 중복 확인
    public boolean existsByNickname(String nickname) {
        return repository.existsByNickname(nickname);
    }

    // 이메일 중복 확인
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
