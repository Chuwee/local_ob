package es.onebox.mgmt.b2b.balance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.balance.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class BaseSearchTransactionsFilterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "transaction_date_from can not be null")
    @JsonProperty("transaction_date_from")
    private ZonedDateTime from;
    @NotNull(message = "transaction_date_to can not be null")
    @JsonProperty("transaction_date_to")
    private ZonedDateTime to;
    private TransactionType type;
    private String q;
    @JsonProperty("currency_code")
    private String currencyCode;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

