package es.onebox.common.datasources.ms.collective.dto;

import java.io.Serializable;

public class ResponseCollectiveDTO implements Serializable {
    private long id;
    private String name;
    private String externalValidator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalValidator() {
        return externalValidator;
    }

    public void setExternalValidator(String externalValidator) {
        this.externalValidator = externalValidator;
    }
}
