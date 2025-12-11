package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;

public class CreatePassbookTemplate implements Serializable {

    private Long entityId;
    private Long originEntityId;
    private Long operatorId;
    private String name;
    private String templateCodeToCopy;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateCodeToCopy() {
        return templateCodeToCopy;
    }

    public void setTemplateCodeToCopy(String templateCodeToCopy) {
        this.templateCodeToCopy = templateCodeToCopy;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getOriginEntityId() {
        return originEntityId;
    }

    public void setOriginEntityId(Long originEntityId) {
        this.originEntityId = originEntityId;
    }
}
