package es.onebox.mgmt.b2b.balance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.balance.enums.DepositType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class AdditionalInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("deposit_transaction_id")
    private String depositTransactionId;
    @JsonProperty("deposit_type")
    private DepositType depositType;

    public String getDepositTransactionId() {
        return depositTransactionId;
    }

    public void setDepositTransactionId(String depositTransactionId) {
        this.depositTransactionId = depositTransactionId;
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public void setDepositType(DepositType depositType) {
        this.depositType = depositType;
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
