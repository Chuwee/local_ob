package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ProductPublishingSessions implements Serializable {

    @Serial
    private static final long serialVersionUID = 2541117156268234758L;

    private SelectionType type;
    private Set<ProductSessionBase> sessions;


    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public Set<ProductSessionBase> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ProductSessionBase> sessions) {
        this.sessions = sessions;
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

