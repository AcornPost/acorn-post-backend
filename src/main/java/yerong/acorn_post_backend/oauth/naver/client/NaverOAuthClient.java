package yerong.acorn_post_backend.oauth.naver.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import yerong.acorn_post_backend.oauth.naver.dto.NaverProfileResponse;
import yerong.acorn_post_backend.oauth.naver.dto.NaverTokenResponse;

@Component
public class NaverOAuthClient {
    private final RestClient restClient = RestClient.create();

    public NaverTokenResponse getToken(
            String tokenUri,
            String clientId,
            String clientSecret,
            String code,
            String state,
            String redirectUri
    ) {
        return restClient.post()
                .uri(tokenUri) 
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(new LinkedMultiValueMap<String, String>() {{
                    add("grant_type", "authorization_code");
                    add("client_id", clientId);
                    add("client_secret", clientSecret);
                    add("code", code);
                    add("state", state);
                    add("redirect_uri", redirectUri);
                }})
                .retrieve()
                .body(NaverTokenResponse.class);
    }

    public NaverProfileResponse getProfile(String profileUri, String accessToken) {
        return restClient.get()
                .uri(profileUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(NaverProfileResponse.class);
    }
}
