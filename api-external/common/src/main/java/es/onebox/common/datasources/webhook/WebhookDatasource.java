package es.onebox.common.datasources.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthConsultResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthLoginResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthSeatManagementResponseDTO;
import es.onebox.common.datasources.webhook.dto.atm.OAuth2TokenResponse;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.WebhookUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.exception.HttpErrorException;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.status.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebhookDatasource {

    private static final String ATM_IGNORED_CHANNEL_MESSAGE = "\"Canal no valido\"";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookDatasource.class);

    private final HttpClient httpClient;

    @Autowired
    public WebhookDatasource(ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl("")
                .jacksonMapper(jacksonMapper)
                .build();
    }

    @Cached(key = "accessTokenATM", expires = 5, timeUnit = TimeUnit.MINUTES)
    public OAuth2TokenResponse getATMAccessToken(@CachedArg String url, @CachedArg String client_id,
                                                 @CachedArg String client_secret, @CachedArg String username,
                                                 @CachedArg String password) {
        OAuth2TokenResponse response;
        try {
            response = httpClient.buildRequest(HttpMethod.POST, url)
                    .params(new QueryParameters.Builder()
                            .addQueryParameter("client_id", client_id)
                            .addQueryParameter("client_secret", client_secret)
                            .addQueryParameter("grant_type", "password")
                            .addQueryParameter("username", username)
                            .addQueryParameter("password", password)
                            .build())
                    .execute(OAuth2TokenResponse.class);
        } catch (Exception e) {
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] error while obtaining salesforce token: {}", e.getMessage(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
        return response;
    }

    public void sendATMNotification(OAuth2TokenResponse atmAuthResponse, OrderNotificationMessageDTO messageDTO, String orderCode) {
        try {
            httpClient.buildRequest(HttpMethod.POST, atmAuthResponse.getInstanceURL() + "/services/apexrest/orders")
                    .headers(new RequestHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + atmAuthResponse.getAccessToken())
                            .addHeader("ob-action", messageDTO.getHeaders().get("ob-action"))
                            .addHeader("ob-delivery-id", messageDTO.getHeaders().get("ob-delivery-id"))
                            .addHeader("ob-event", messageDTO.getHeaders().get("ob-event"))
                            .addHeader("ob-signature", messageDTO.getSignature())
                            .build())
                    .body(new ClientRequestBody(messageDTO.getPayload()))
                    .execute();
        } catch (HttpErrorException e) {
            if (HttpStatus.INTERNAL_SERVER_ERROR.equals(e.getHttpStatus()) && ATM_IGNORED_CHANNEL_MESSAGE.equals(e.getResponseBody())) {
                LOGGER.info("[ATM SALESFORCE NOTIFICATION] [{}] Order channel ignored by salesforce", orderCode);
                return;
            }
            LOGGER.error("[ATM SALESFORCE NOTIFICATION] [{}] Error while calling salesforce. code: {}, body: {}", orderCode,
                    e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }

    public AthLoginResponseDTO loginATH(String url, OrderNotificationMessageDTO messageDTO) {
        try {
            return httpClient.buildRequest(HttpMethod.POST, url)
                    .headers(new RequestHeaders.Builder()
                            .addHeader("Authorization", "Basic " + messageDTO.getHeaders().get("auth"))
                            .build())
                    .body(new ClientRequestBody(messageDTO.getPayload()))
                    .execute(AthLoginResponseDTO.class);
        } catch (HttpErrorException e) {
            LOGGER.error("[ATH WEBHOOK] error while calling create/modify. code: {}, body: {}", e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }

    public AthSeatManagementResponseDTO sendATHCreateModifyNotification(String url, OrderNotificationMessageDTO messageDTO) {
        try {
            return httpClient.buildRequest(HttpMethod.POST, url)
                    .headers(new RequestHeaders.Builder()
                            .addHeader("Authorization", "Basic " + messageDTO.getHeaders().get("auth"))
                            .build())
                    .body(new ClientRequestBody(messageDTO.getPayload()))
                    .execute(AthSeatManagementResponseDTO.class);
        } catch (HttpErrorException e) {
            LOGGER.error("[ATH WEBHOOK] error while calling create/modify. code: {}, body: {}", e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }

    public AthConsultResponseDTO getATHCessionsList(String url, OrderNotificationMessageDTO messageDTO) {
        try {
            return httpClient.buildRequest(HttpMethod.POST, url)
                    .headers(new RequestHeaders.Builder()
                            .addHeader("Authorization", "Basic " + messageDTO.getHeaders().get("auth"))
                            .build())
                    .body(new ClientRequestBody(messageDTO.getPayload()))
                    .execute(AthConsultResponseDTO.class);
        } catch (HttpErrorException e) {
            LOGGER.error("[ATH WEBHOOK] error while calling consult. code: {}, body: {}", e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }

    public void sendFeverDefaultMessage(WebhookFeverDTO webhookFever, RequestHeaders requestHeaders, String feverUrl) {
        LOGGER.info(WebhookUtils.buildSendingMessage(webhookFever));
        try {
            httpClient.buildRequest(HttpMethod.POST, feverUrl)
                    .headers(requestHeaders)
                    .body(new ClientRequestBody(webhookFever.getFeverMessage()))
                    .execute();
        } catch (HttpErrorException e) {
            LOGGER.error("[FEVER WEBHOOK] - Error sending webhook. code: {}, body: {}", e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }

    public void sendFeverMessage(WebhookFeverDTO webhookFever, RequestHeaders requestHeaders, String feverUrl) {
        LOGGER.info(WebhookUtils.buildSendingMessage(webhookFever));
        try {
            httpClient.buildRequest(HttpMethod.POST, feverUrl)
                    .headers(requestHeaders)
                    .body(new ClientRequestBody(webhookFever.getFeverMessage()))
                    .execute();
        } catch (HttpErrorException e) {
            LOGGER.error("[FEVER WEBHOOK] - Error sending webhook. code: {}, body: {}", e.getHttpStatus(), e.getResponseBody(), e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.HTTP_NOTIFICATION_CALL_ERROR);
        }
    }


}
