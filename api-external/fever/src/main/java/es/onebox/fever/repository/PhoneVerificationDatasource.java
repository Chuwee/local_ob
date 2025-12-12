package es.onebox.fever.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.fever.dto.RegisterPhoneRequest;
import es.onebox.fever.dto.UserPhoneInfoDTO;
import es.onebox.fever.dto.VerifyPhoneRequest;
import es.onebox.fever.exception.FeverExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PhoneVerificationDatasource {

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("SMS_FAILED", ApiExternalErrorCode.FEVER_OTP_SMS_FAILED);
        ERROR_CODES.put("VERIFICATION_FAILED", ApiExternalErrorCode.FEVER_OTP_VERIFICATION_FAILED);
        ERROR_CODES.put("BLACKLISTED_IP", ApiExternalErrorCode.FEVER_OTP_BLACKLISTED_IP);
        ERROR_CODES.put("MAX_ATTEMPTS", ApiExternalErrorCode.FEVER_OTP_MAX_ATTEMPTS);
        ERROR_CODES.put("INVALID_FORMAT", ApiExternalErrorCode.FEVER_OTP_INVALID_FORMAT);
        ERROR_CODES.put("INVALID_PHONE_NUMBER", ApiExternalErrorCode.FEVER_OTP_INVALID_PHONE_NUMBER);
        ERROR_CODES.put("INVALID_PARAMETER", ApiExternalErrorCode.FEVER_OTP_INVALID_PARAMETER);
        ERROR_CODES.put("UNKNOWN_ERROR", ApiExternalErrorCode.FEVER_OTP_UNKNOWN_ERROR);
        ERROR_CODES.put("INVALID_TOKEN", ApiExternalErrorCode.FEVER_TOKEN_EXPIRED);
        ERROR_CODES.put("FORBIDDEN_REQUEST", ApiExternalErrorCode.FORBIDDEN_RESOURCE);

    }

    private final HttpClient httpClient;

    @Autowired
    public PhoneVerificationDatasource(@Value("${fever.phone-verification.url}") String baseUrl,
                                       TracingInterceptor tracingInterceptor) {
        ObjectMapper jacksonMapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                                                  .baseUrl(baseUrl)
                                                  .jacksonMapper(jacksonMapper)
                                                  .interceptors(tracingInterceptor)
                                                  .exceptionBuilder(new FeverExceptionBuilder(ERROR_CODES,
                                                                                              jacksonMapper))
                                                  .build();
    }

    public UserPhoneInfoDTO getUserPhoneInfo(String userId, String token) {
        return httpClient.buildRequest(HttpMethod.GET, "/{user_id}/phone/")
                         .pathParams(userId)
                         .headers(DatasourceUtils.prepareAuthHeader(token))
                         .execute(UserPhoneInfoDTO.class);
    }

    public void registerPhone(RegisterPhoneRequest request, String userId, String token) {
        httpClient.buildRequest(HttpMethod.POST, "/{user_id}/register-phone/")
                  .pathParams(userId)
                  .body(new ClientRequestBody(request))
                  .headers(DatasourceUtils.prepareAuthHeader(token))
                  .execute();
    }

    public void requestPhoneVerificationCode(String userId, String token) {
        httpClient.buildRequest(HttpMethod.POST, "/{user_id}/request-phone-otp/")
                  .pathParams(userId)
                  .headers(DatasourceUtils.prepareAuthHeader(token))
                  .execute();
    }

    public void verifyPhone(VerifyPhoneRequest request, String userId, String token) {
        httpClient.buildRequest(HttpMethod.POST, "/{user_id}/verify-phone-otp/")
                  .pathParams(userId)
                  .body(new ClientRequestBody(request))
                  .headers(DatasourceUtils.prepareAuthHeader(token))
                  .execute();
    }

}
