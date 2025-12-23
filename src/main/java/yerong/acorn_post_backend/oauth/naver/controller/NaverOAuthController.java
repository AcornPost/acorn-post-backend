package yerong.acorn_post_backend.oauth.naver.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.member.domain.SocialProvider;
import yerong.acorn_post_backend.member.dto.login.LoginResponse;
import yerong.acorn_post_backend.member.service.MemberService;
import yerong.acorn_post_backend.oauth.naver.dto.NaverUserInfo;
import yerong.acorn_post_backend.oauth.naver.service.NaverOAuthService;
import yerong.acorn_post_backend.oauth.token.TokenPair;
import yerong.acorn_post_backend.oauth.token.service.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/naver")
public class NaverOAuthController {
    private final NaverOAuthService naverOAuthService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;


    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();

        String url = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + state;

        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse response) throws Exception {

        NaverUserInfo userInfo = naverOAuthService.login(code, state);

        LoginResponse login = memberService.findOrCreateSocialMember(
                SocialProvider.NAVER,
                userInfo.socialId(),
                userInfo.email(),
                userInfo.username()
        );

        TokenPair tokenPair = tokenService.issue(login.memberId(), login.role().getKey());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(false)      // 운영 HTTPS면 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        response.sendRedirect("http://localhost:5173/oauth/redirect");
    }
}
