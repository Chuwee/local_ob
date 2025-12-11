package es.onebox.mgmt.users.dto;

import es.onebox.mgmt.entities.dto.EntityDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UserSelfDTO extends UserDTO implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private UserReportsDTO reports;
    private EntityDTO entity;

    public UserReportsDTO getReports() {
        return reports;
    }
    public void setReports(UserReportsDTO reports) {
        this.reports = reports;
    }

    public EntityDTO getEntity() {
        return entity;
    }

    public void setEntity(EntityDTO entity) {
        this.entity = entity;
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
