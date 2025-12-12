package es.onebox.ms.notification.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Created by rfontecha on 4/21/15.
 */
public enum MsNotificationErrorCode implements ErrorCode {

    GENERIC_REST_EXCEPTION("500MN0001", HttpStatus.INTERNAL_SERVER_ERROR, "Generic error, something really wrong happened"),
    RABBITMQ_EXCEPTION("500MN0002", HttpStatus.INTERNAL_SERVER_ERROR, "RabbitMQ generic error"),
    INVALID_ENTITY_ID("INVALID_ENTITY_ID", CONFLICT, "Entity id is invalid"),
    INVALID_CHANNEL_ID("INVALID_CHANNEL_ID", CONFLICT,"Entity id is invalid"),
    ENTITY_CONFIG_NOT_FOUND("ENTITY_CONFIG_NOT_FOUND", HttpStatus.NOT_FOUND, "Entity has no configuration to update"),
    ORDER_ACTION_NOT_FOUND("ORDER_ACTION_NOT_FOUND", HttpStatus.NOT_FOUND, "No action found for order type and status"),
    MEMBER_ORDER_ACTION_NOT_FOUND("MEMBER_ORDER_ACTION_NOT_FOUND", HttpStatus.NOT_FOUND, "No action found for member order type and status"),
    PREORDER_ACTION_NOT_FOUND("PREORDER_ACTION_NOT_FOUND", HttpStatus.NOT_FOUND, "No action found for abandoned preorder code"),
    EVENT_NOT_FOUND("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND, "No event found for message received"),
    INCORRECT_EVENT_ACTION_REL("INCORRECT_EVENT_ACTION_REL", HttpStatus.BAD_REQUEST, "Invalid event-action relationship"),
    CHANNEL_NOT_FOUND("CHANNEL_NOT_FOUND", HttpStatus.NOT_FOUND, "No channel found for message received"),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "No product found for message received");

    private final HttpStatus httpStatus;
    private final String message;
    private final String errorCode;

    MsNotificationErrorCode(String errorCode, HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }
}
