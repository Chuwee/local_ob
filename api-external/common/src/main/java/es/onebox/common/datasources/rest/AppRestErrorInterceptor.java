package es.onebox.common.datasources.rest;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AppRestErrorInterceptor implements Interceptor {

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();
    private static final String HEADER_ERROR_CODE = "ob_error_code";

    static {
        ERROR_CODES.put("1000", ApiExternalErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("1001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("1003", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("1008", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("1009", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);

        ERROR_CODES.put("1010", ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("1011", ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("1012", ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("1013", ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        ERROR_CODES.put("1014", ApiExternalErrorCode.FORBIDDEN_RESOURCE);

        ERROR_CODES.put("1015", ApiExternalErrorCode.INVALID_CHANNEL_CONFIG);
        ERROR_CODES.put("1016", ApiExternalErrorCode.INVALID_TERMINAL_CONFIG);
        ERROR_CODES.put("1017", ApiExternalErrorCode.INVALID_POS_CONFIG);
        ERROR_CODES.put("1018", ApiExternalErrorCode.LOGIN_ERROR);
        ERROR_CODES.put("1019", ApiExternalErrorCode.USER_NOT_ACTIVE);

        ERROR_CODES.put("1030", ApiExternalErrorCode.EVENT_NOT_FOUND);
        ERROR_CODES.put("1031", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);
        ERROR_CODES.put("1032", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);
        ERROR_CODES.put("1034", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);
        ERROR_CODES.put("1035", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);
        ERROR_CODES.put("1036", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);
        ERROR_CODES.put("1037", ApiExternalErrorCode.EVENT_NOT_AVAILABLE);

        ERROR_CODES.put("1040", ApiExternalErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("1041", ApiExternalErrorCode.SESSION_NOT_AVAILABLE);
        ERROR_CODES.put("1042", ApiExternalErrorCode.SESSION_NOT_AVAILABLE);
        ERROR_CODES.put("1043", ApiExternalErrorCode.SESSION_NOT_AVAILABLE);

        ERROR_CODES.put("1045", ApiExternalErrorCode.SESSION_SEATMAP_NOT_GRAPHIC);
        ERROR_CODES.put("1046", ApiExternalErrorCode.SESSION_SEATMAP_VIEW_NOT_FOUND);
        ERROR_CODES.put("1047", ApiExternalErrorCode.SESSION_SEAT_NOT_AVAILABLE);
        ERROR_CODES.put("1050", ApiExternalErrorCode.SHOPPING_CART_EXPIRED_OR_NOT_FOUND);
        ERROR_CODES.put("1053", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_SEATS_NOT_AVAILABLE);

        ERROR_CODES.put("1060", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_CODE_NOT_FOUND);
        ERROR_CODES.put("1061", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_CODE_INCORRECT);
        ERROR_CODES.put("1062", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_USER_INVALID);
        ERROR_CODES.put("1063", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_USER_PASSWORD_INVALID);
        ERROR_CODES.put("1064", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_NOT_FOUND);
        ERROR_CODES.put("1065", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_LIMIT_EVENT_EXCEEDED);
        ERROR_CODES.put("1066", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_MIN_LIMIT_NOT_REACHED);
        ERROR_CODES.put("1067", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_GROUP_NEEDED);
        ERROR_CODES.put("1068", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_LIMIT_SESSION_EXCEEDED);
        ERROR_CODES.put("1069", ApiExternalErrorCode.ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_NOT_APPLICABLE);

    }

    @Override
    @NotNull
    public Response intercept(@NotNull Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());

        if (!response.isSuccessful() || response.code() != 200) {
            try {
                String errorCode = response.header("OB_Error_Code");
                if (ERROR_CODES.containsKey(errorCode)) {
                    throw new OneboxRestException(ERROR_CODES.get(errorCode));
                } else {
                    String errorCodeHeader = response.header(HEADER_ERROR_CODE);
                    if (ERROR_CODES.containsKey(errorCodeHeader)) {
                        throw new OneboxRestException(ERROR_CODES.get(errorCodeHeader));
                    }
                }
                throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "Rest error not mapped: " + errorCode, null);
            } finally {
                response.close();
            }
        }

        return response;
    }
}
