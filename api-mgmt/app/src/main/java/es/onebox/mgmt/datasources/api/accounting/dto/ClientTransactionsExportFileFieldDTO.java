package es.onebox.mgmt.datasources.api.accounting.dto;

import es.onebox.mgmt.datasources.api.accounting.enums.ClientTransactionField;
import es.onebox.mgmt.export.dto.ExportFileField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClientTransactionsExportFileFieldDTO extends ExportFileField<ClientTransactionField> {

    private static final long serialVersionUID = 1L;

    private ClientTransactionField field;

    public ClientTransactionsExportFileFieldDTO(ClientTransactionField field) {
        this.field = field;
    }

    @Override
    public ClientTransactionField getField() {
        return field;
    }

    @Override
    public void setField(ClientTransactionField field) {
        this.field = field;
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
