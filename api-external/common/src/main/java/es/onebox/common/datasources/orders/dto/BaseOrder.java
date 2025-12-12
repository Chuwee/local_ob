package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class BaseOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String code;
    @JsonProperty("external_code")
    private String externalCode;
    private ZonedDateTime date;
    @JsonProperty("payment_data")
    private List<OrderPayment> paymentsData;
    @JsonProperty("last_modified")
    private ZonedDateTime lastModified;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public List<OrderPayment> getPaymentsData() {
        return paymentsData;
    }

    public void setPaymentsData(List<OrderPayment> paymentsData) {
        this.paymentsData = paymentsData;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
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
