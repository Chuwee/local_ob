package es.onebox.fever.dto;

import java.io.Serial;
import java.io.Serializable;

public class UserPhoneInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UserPhoneInfoDataDTO data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UserPhoneInfoDataDTO getData() {
        return data;
    }

    public void setData(UserPhoneInfoDataDTO data) {
        this.data = data;
    }
}
