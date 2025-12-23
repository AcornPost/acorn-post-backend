package yerong.acorn_post_backend.oauth.naver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yerong.acorn_post_backend.oauth.naver.client.NaverOAuthClient;
import yerong.acorn_post_backend.oauth.naver.dto.NaverProfileResponse;
import yerong.acorn_post_backend.oauth.naver.dto.NaverTokenResponse;
import yerong.acorn_post_backend.oauth.naver.dto.NaverUserInfo;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {
    private final NaverOAuthClient naverOAuthClient;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}") private String clientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}") private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}") private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.naver.tok"
            + "en-uri}") private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}") private String profileUri;

    public NaverUserInfo login(String code, String state) throws IllegalAccessException {
        NaverTokenResponse token = naverOAuthClient.getToken(
                tokenUri, clientId, clientSecret, code, state, redirectUri
        );
        if (token == null || token.accessToken() == null) {
            throw new IllegalAccessException("NAVER_TOKEN_FAILED: " + (token == null ? "null" : token.errorDescription()));
        }

        NaverProfileResponse profile = naverOAuthClient.getProfile(profileUri, token.accessToken());
        if (profile == null || profile.response() == null || profile.response().id() == null) {
            throw new IllegalAccessException("NAVER_PROFILE_FAILED");
        }

        return new NaverUserInfo(
                profile.response().id(),
                profile.response().email(),
                profile.response().username()
        );
    }
}
