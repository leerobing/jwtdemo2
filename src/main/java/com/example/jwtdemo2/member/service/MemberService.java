package com.example.jwtdemo2.member.service;

import com.example.jwtdemo2.core.entity.Member;
import com.example.jwtdemo2.jwt.JwtTokenProvider;
import com.example.jwtdemo2.jwt.TokenInfo;
import com.example.jwtdemo2.member.repository.MemberRepository;
import com.example.jwtdemo2.sec.EncrypterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MemberService {

    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Optional<TokenInfo> login(String memberId, String password) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("회원없음"));


        if (encoder.matches(password,member.getPassword())==true) {
            // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
            // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, member.getPassword());

            // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            return Optional.ofNullable(tokenInfo);
        } else {
            return null;
        }
    }

    public Member save(Member member) {

        String encode = encoder.encode(member.getPassword());

        Member saveMember = Member.builder()
                .memberId(member.getMemberId())
                .password(encode)
                .roles(Collections.singletonList("USER"))
                .build();
        return memberRepository.save(saveMember);
    }
}
