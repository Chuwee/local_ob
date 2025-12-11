package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
public final class ApiErrorDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    public final ErrorCode code;

    private String message = null;

    public ApiErrorDTO(ApiMgmtErrorCode code) {
        this.code = code;
    }

    public ApiErrorDTO(ErrorCode code) {
        this.code = code;
    }

    public ApiErrorDTO(ErrorCode code, String message) {
        this.code = code;
        if (StringUtils.isNotBlank(message)) {
            this.message = message;
        }
    }

    public String getCode() {
        return code.getErrorCode();
    }

    public String getMessage() {
        if (message != null) {
            return message;
        }
        return code.getMessage();
    }
}
