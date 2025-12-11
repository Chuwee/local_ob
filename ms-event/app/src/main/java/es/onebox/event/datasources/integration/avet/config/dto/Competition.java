package es.onebox.event.datasources.integration.avet.config.dto;

import java.io.Serial;
import java.io.Serializable;

public class Competition implements Serializable {

    @Serial
    private static final long serialVersionUID = -4853518625246270471L;

    private String description;
    private Integer code;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
