package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -4085653265640885751L;
    private Boolean enabled;

    private Long whatsappTemplate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getWhatsappTemplate() {
        return whatsappTemplate;
    }

    public void setWhatsappTemplate(Long whatsappTemplate) {
        this.whatsappTemplate = whatsappTemplate;
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
