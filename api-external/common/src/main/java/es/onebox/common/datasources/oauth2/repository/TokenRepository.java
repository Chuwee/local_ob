package es.onebox.common.datasources.oauth2.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
import es.onebox.common.datasources.oauth2.ApiOAuth2Datasource;
import es.onebox.common.datasources.oauth2.dto.TokenRequest;
import es.onebox.common.datasources.oauth2.dto.TokenResponse;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepository {
    private static final String ONEBOX_CLIENT_ID = "onebox-client";
    private static final String ONEBOX_CLIENT_SECRET = "oceantech";
    private static final String SELLER_CHANNEL_CLIENT_ID = "seller-channel-client";
    private static final String SELLER_CHANNEL_SECRET = "s3ll3rBoX";
    private static final String CUSTOMER_CLIENT_ID = "customer-client";

    private static final String GRANT_TYPE_PASS = "password";
    private static final String GRANT_TYPE_CREDENTIALS = "client_credentials";


    private final ApiOAuth2Datasource apiOAuth2Datasource;

    public TokenRepository(ApiOAuth2Datasource apiOAuth2Datasource) {
        this.apiOAuth2Datasource = apiOAuth2Datasource;
    }

    @Cached(key = "onebox_client_token_password", expires = 21600)
    public String getApiOrdersToken(@CachedArg String username, @CachedArg String password) {

        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(ONEBOX_CLIENT_ID, ONEBOX_CLIENT_SECRET)
                .withGrantType(GRANT_TYPE_PASS)
                .withUserPass(username, password, true)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }

    @Cached(key = "onebox_client_token", expires = 21600)
    public String getOneboxClientToken(@CachedArg Long userId, @SkippedCachedArg String apiKey) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(ONEBOX_CLIENT_ID, apiKey)
                .withGrantType(GRANT_TYPE_CREDENTIALS)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }

    @Cached(key = "onebox_client_apikey", expires = 21600)
    public String getOneboxClientToken(@CachedArg String apiKey) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(ONEBOX_CLIENT_ID, apiKey)
                .withGrantType(GRANT_TYPE_CREDENTIALS)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }


    @Cached(key = "api_catalog_token", expires = 21600)
    public String getSellerChannelToken(@CachedArg String username, @CachedArg String password, @CachedArg Long channelId) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(SELLER_CHANNEL_CLIENT_ID, SELLER_CHANNEL_SECRET)
                .withGrantType(GRANT_TYPE_PASS)
                .withUserPass(username, password, true)
                .withChannelId(channelId)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }

    @Cached(key = "seller_channel_token", expires = 21600)
    public String getSellerChannelToken(@CachedArg Long channelId, @CachedArg String apiKey) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(SELLER_CHANNEL_CLIENT_ID, apiKey)
                .withGrantType(GRANT_TYPE_CREDENTIALS)
                .withChannelId(channelId)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }

    @Cached(key = "customer_token", expires = 21600)
    public String getCustomerToken(@CachedArg String apiKey, @CachedArg Long entityId) {
        TokenRequest tokenRequest = TokenRequest.builder()
                .withClientIdAndSecret(CUSTOMER_CLIENT_ID, apiKey)
                .withGrantType(GRANT_TYPE_CREDENTIALS)
                .withEntityId(entityId)
                .build();
        TokenResponse response = apiOAuth2Datasource.getToken(tokenRequest);
        return response.getAccessToken();
    }

}
