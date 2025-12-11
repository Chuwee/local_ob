package es.onebox.mgmt.datasources.ms.accesscontrol.dto;

import java.io.Serializable;

public class StartImportProcessRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Integer importProcess;

    public StartImportProcessRequest() {
    }

    public StartImportProcessRequest(Long sessionId, Integer importProcess) {
        this.sessionId = sessionId;
        this.importProcess = importProcess;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getImportProcess() {
        return importProcess;
    }

    public void setImportProcess(Integer importProcess) {
        this.importProcess = importProcess;
    }
}
