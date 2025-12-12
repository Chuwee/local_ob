package es.onebox.common.exception;

public class ApiException extends RuntimeException {
    private Integer id;
    private String code;
    private String message;

    public ApiException() {

    }

    public ApiException(String message, Throwable cause, Integer id, String code, String message1) {
        super(message, cause);
        this.id = id;
        this.code = code;
        this.message = message1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
