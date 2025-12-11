package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class TemplateElementInfo extends TemplateElementInfoBase implements Serializable {

    @Serial
    private static final long serialVersionUID = -7596147537811954958L;

    private TemplateInfoBaseResponse templateInfo;

    public TemplateInfoBaseResponse getTemplateInfo() {
        return templateInfo;
    }

    public void setTemplateInfo(TemplateInfoBaseResponse templateInfo) {
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
