package com.example.jwtdemo2.member.controller;

import com.example.jwtdemo2.core.entity.Member;
import com.example.jwtdemo2.jwt.TokenInfo;
import com.example.jwtdemo2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public String join(@RequestBody MemberLoginRequestDto memberLoginRequestDto){
        log.info("id={}",memberLoginRequestDto.getMemberId());

        Member member = Member.builder()
                .memberId(memberLoginRequestDto.getMemberId())
                .password(memberLoginRequestDto.getPassword())
                .roles(Collections.singletonList("USER"))
                .build();


        Member saved = memberService.save(member);

        return saved.toString();

    }

    @PostMapping("/login")
    public Optional<TokenInfo> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        String memberId = memberLoginRequestDto.getMemberId();
        String password = memberLoginRequestDto.getPassword();
        TokenInfo tokenInfo = memberService.login(memberId, password).orElseThrow(() -> new IllegalArgumentException("회원없음"));
        return Optional.ofNullable(tokenInfo);
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}