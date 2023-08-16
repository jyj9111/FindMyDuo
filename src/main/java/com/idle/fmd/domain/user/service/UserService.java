package com.idle.fmd.domain.user.service;


import com.idle.fmd.domain.user.dto.*;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import com.idle.fmd.global.common.utils.RedisUtil;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.idle.fmd.domain.user.entity.CustomUserDetails;

import java.util.Optional;
import java.util.Random;

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
                        .build()
        );

        redisUtil.delete(dto.getEmail());
    }

    public UserLoginResponseDto loginUser(UserLoginRequestDto dto) {

        log.info("로그인 : " + manager.userExists(dto.getAccountId()));

        if (!manager.userExists(dto.getAccountId())) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        UserDetails userDetails = manager.loadUserByUsername(dto.getAccountId());

        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            throw new BusinessException(BusinessExceptionCode.LOGIN_PASSWORD_CHECK_ERROR);
        }

        return new UserLoginResponseDto(jwtTokenUtils.generateToken(userDetails));
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
        Random random = new Random();
        int authCode = random.nextInt(100000, 1000000);
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
    }

    // 유저 조회 메서드
    public UserMyPageResponseDto profile(String accountId) {
        Optional<UserEntity> entity = repository.findByAccountId(accountId);

        if(entity.isPresent()) {
            return UserMyPageResponseDto.fromEntity(entity.get());
        } else throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
    }
}
