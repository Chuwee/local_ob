package es.onebox.mgmt.datasources.ms.event.dto.session;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionExternalSessions implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean generalAdmission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getGeneralAdmission() {
        return generalAdmission;
    }

    public void setGeneralAdmission(Boolean generalAdmission) {
        this.generalAdmission = generalAdmission;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
