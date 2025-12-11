package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;

public interface FormattableErrorCode extends ErrorCode {
    String formatMessage(Object... args);
}
