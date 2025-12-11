package es.onebox.mgmt.products.dto;

import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ProductPublishingSessionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2541117156268234758L;

    private SelectionType type;

    private Set<ProductSessionDTO> sessions;

    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public Set<ProductSessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ProductSessionDTO> sessions) {
        this.sessions = sessions;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
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
