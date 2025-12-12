package es.onebox.flc.orders.dto.groups;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AttributeValueDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3696279462639128613L;

    private Object userInputValue;
    private DomainValueDTO domainValue;

    public Object getUserInputValue() {
        return userInputValue;
    }

    public void setUserInputValue(Object userInputValue) {
        this.userInputValue = userInputValue;
    }

    public DomainValueDTO getDomainValue() {
        return domainValue;
    }

    public void setDomainValue(DomainValueDTO domainValue) {
        this.domainValue = domainValue;
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
