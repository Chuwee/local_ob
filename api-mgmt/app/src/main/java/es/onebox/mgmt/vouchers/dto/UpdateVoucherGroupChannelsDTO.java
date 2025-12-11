package es.onebox.mgmt.vouchers.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public class UpdateVoucherGroupChannelsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private ChannelsScopeDTO scope;
    private Set<IdNameDTO> items;
    private Set<@Min(value = 1 , message = "The ids must be above 0") Long> ids;

    public ChannelsScopeDTO getScope() {
        return scope;
    }

    public void setScope(ChannelsScopeDTO scope) {
        this.scope = scope;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Set<IdNameDTO> getItems() {
        return items;
    }

    public void setItems(Set<IdNameDTO> items) {
        this.items = items;
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
