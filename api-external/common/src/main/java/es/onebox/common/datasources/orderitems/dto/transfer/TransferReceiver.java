package es.onebox.common.datasources.orderitems.dto.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class TransferReceiver implements Serializable {

    @Serial
    private static final long serialVersionUID = -5454291710850720398L;

    private String name;
    private String email;
    @JsonProperty("customer_id")
    private String customerId;
    private ZonedDateTime date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
