package es.onebox.event.sessions.domain;

import java.util.Objects;

public class ExternalSessionConfig {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExternalSessionConfig that = (ExternalSessionConfig) o;
        return Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(generalAdmission, that.generalAdmission);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sessionId, generalAdmission);
    }
}
