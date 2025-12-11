package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class TemplateInfoBulkUpdateRequest extends TemplateInfoBulkUpdateBaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6899231396827609291L;

    @NotNull
    private UpdateTemplateInfoDefault templateInfo;

    public UpdateTemplateInfoDefault getTemplateInfo() {
        return templateInfo;
    }

    public void setTemplateInfo(UpdateTemplateInfoDefault templateInfo) {
        this.templateInfo = templateInfo;
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
