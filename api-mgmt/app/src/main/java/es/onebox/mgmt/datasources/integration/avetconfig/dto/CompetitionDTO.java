package es.onebox.mgmt.datasources.integration.avetconfig.dto;

import java.io.Serial;
import java.io.Serializable;

public class CompetitionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

    private Integer id;
    private String description;
    private Integer code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
