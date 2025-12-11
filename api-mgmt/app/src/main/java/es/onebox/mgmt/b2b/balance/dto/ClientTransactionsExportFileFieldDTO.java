package es.onebox.mgmt.b2b.balance.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.mgmt.b2b.balance.enums.ClientTransactionField;
import es.onebox.mgmt.export.deserializer.ClientTransactionsExportFieldDeserializer;
import es.onebox.mgmt.export.dto.ExportFileField;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ClientTransactionsExportFileFieldDTO extends ExportFileField<ClientTransactionField> {

    @JsonDeserialize(using = ClientTransactionsExportFieldDeserializer.class)
    @NotNull(message = "field can not be null")
    private ClientTransactionField field;

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
