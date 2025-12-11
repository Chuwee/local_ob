package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SessionPreSaleCustomerTypeDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean selected;

    public SessionPreSaleCustomerTypeDTO() {
    }

    public SessionPreSaleCustomerTypeDTO(Long id) {
        super(id);
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
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
