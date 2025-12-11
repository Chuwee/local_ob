package es.onebox.mgmt.passbook.dto;

import java.io.Serializable;

public class CodeDTO implements Serializable {

    private String code;

    public CodeDTO() {
    }

    public CodeDTO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
