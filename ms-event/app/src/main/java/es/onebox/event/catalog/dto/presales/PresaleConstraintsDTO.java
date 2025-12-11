package es.onebox.event.catalog.dto.presales;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PresaleConstraintsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -658490130041481336L;

    private List<Long> customerTypes;
    private Long points;

    public List<Long> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<Long> customerTypes) {
        this.customerTypes = customerTypes;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
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
