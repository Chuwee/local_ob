package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.io.Serializable;

public class ExternalSessionConfig implements Serializable {

    private static final long serialVersionUID = 6709750497338176876L;

    private Long sessionId;
    private Boolean generalAdmission;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getGeneralAdmission() {
        return generalAdmission;
    }

    public void setGeneralAdmission(Boolean generalAdmission) {
        this.generalAdmission = generalAdmission;
    }
}
