package es.onebox.mgmt.datasources.integration.avetsocketconnector.exception;


import es.onebox.datasource.http.error.ExceptionBuilder;
import es.onebox.datasource.http.status.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

public class ClientHttpExceptionBuilder implements ExceptionBuilder<ResponseStatusException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHttpExceptionBuilder.class);

    public static final String OB_ERROR_CODE = "OB_Error_Code";
    public static final String OB_ERROR_VALUE = "OB_Error_Value";

    @Override
    public ResponseStatusException build(HttpStatus status, Request request, Response response, String responseBody) {

        String errorMsg = "Error (" + status + ") in call: " + request.url();

        int statusCode = status.value();
        String socketConnectorErrorCode = response.header(OB_ERROR_CODE);
        String socketConnectorErrorValue;
        if (socketConnectorErrorCode != null) {
            statusCode = Integer.valueOf(socketConnectorErrorCode);

            errorMsg += " [AVET_ERROR: " + socketConnectorErrorCode;
            socketConnectorErrorValue = response.header(OB_ERROR_VALUE);
            if (socketConnectorErrorValue != null && !socketConnectorErrorValue.isEmpty()) {
                errorMsg += " " + socketConnectorErrorValue;
            }
            errorMsg += "]";
        }

        LOGGER.error(errorMsg);

        return new ResponseStatusException(statusCode, errorMsg, null);
    }

}
