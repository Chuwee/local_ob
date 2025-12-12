package es.onebox.common.datasources.ms.order.exception;

public class PaginationSizeTooLargeException extends RuntimeException{
    public PaginationSizeTooLargeException(String message) {
        super(message);
    }
}
