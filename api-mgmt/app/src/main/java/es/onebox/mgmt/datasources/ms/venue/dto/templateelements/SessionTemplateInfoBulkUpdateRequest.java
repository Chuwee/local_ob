package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class SessionTemplateInfoBulkUpdateRequest extends TemplateInfoBulkUpdateBaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5306363187490827168L;

    @NotNull
    private SessionUpdateTemplateInfo sessionTemplateInfo;
    public SessionUpdateTemplateInfo getSessionTemplateInfo() {
        return sessionTemplateInfo;
    }

    public void setSessionTemplateInfo(SessionUpdateTemplateInfo sessionTemplateInfo) {
        this.sessionTemplateInfo = sessionTemplateInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
