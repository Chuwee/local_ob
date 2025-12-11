package es.onebox.event.seasontickets.amqp.renewals.commit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.onebox.core.serializer.json.CustomZonedDateTimeDeserializer;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CommitRenewalsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private String userId;
    private String renewalId;
    private String orderCode;
    private Long rateId;
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime purchaseDate;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(String renewalId) {
        this.renewalId = renewalId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
