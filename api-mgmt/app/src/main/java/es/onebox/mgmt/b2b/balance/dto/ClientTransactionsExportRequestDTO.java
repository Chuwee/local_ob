package es.onebox.mgmt.b2b.balance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.export.dto.ExportRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ClientTransactionsExportRequestDTO extends ExportRequest<ClientTransactionsExportFileFieldDTO> {

    private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    private Long entityId;
    @Valid
    @NotNull(message = "filter can not be null")
    private BaseSearchTransactionsFilterDTO filter;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public BaseSearchTransactionsFilterDTO getFilter() {
        return filter;
    }

    public void setFilter(BaseSearchTransactionsFilterDTO filter) {
        this.filter = filter;
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