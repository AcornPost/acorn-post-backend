package yerong.acorn_post_backend.oauth.naver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.oauth.naver.client.NaverOAuthClient;
import yerong.acorn_post_backend.oauth.naver.dto.NaverProfileResponse;
import yerong.acorn_post_backend.oauth.naver.dto.NaverTokenResponse;
import yerong.acorn_post_backend.oauth.naver.dto.NaverUserInfo;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {

    private final NaverOAuthClient naverOAuthClient;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String profileUri;

    public NaverUserInfo login(String code, String state) {
        NaverTokenResponse token;
        try {
            token = naverOAuthClient.getToken(
                    tokenUri, clientId, clientSecret, code, state, redirectUri
            );
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NAVER_TOKEN_REQUEST_FAILED);
        }

        if (token == null || token.accessToken() == null || token.accessToken().isBlank()) {
            String msg = "네이버 토큰 응답이 유효하지 않습니다."
                    + (token == null ? "" : " (" + safe(token.errorDescription()) + ")");
            throw new ApiException(ErrorCode.NAVER_TOKEN_REQUEST_FAILED, msg);
        }

        NaverProfileResponse profile;
        try {
            profile = naverOAuthClient.getProfile(profileUri, token.accessToken());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.NAVER_PROFILE_REQUEST_FAILED);
        }

        if (profile == null || profile.response() == null || profile.response().id() == null) {
            throw new ApiException(ErrorCode.NAVER_PROFILE_REQUEST_FAILED, "네이버 프로필 응답이 유효하지 않습니다.");
        }

        return new NaverUserInfo(
                profile.response().id(),
                profile.response().email(),
                profile.response().username()
        );
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
