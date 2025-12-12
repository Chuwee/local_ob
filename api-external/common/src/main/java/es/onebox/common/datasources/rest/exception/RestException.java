package es.onebox.common.datasources.rest.exception;

import java.io.Serializable;

public class RestException implements Serializable {

    private String msg;
    private Integer errorCode;


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
