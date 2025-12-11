package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class TemplateInfoDefault extends TemplateInfoBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 3625293969110381805L;

    private AggregatedInfo defaultInfo;

    public AggregatedInfo getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(AggregatedInfo defaultInfo) {
        this.defaultInfo = defaultInfo;
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
