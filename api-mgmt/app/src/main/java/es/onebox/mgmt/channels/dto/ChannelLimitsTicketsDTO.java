package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelLimitsTicketsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("purchase_max")
    @Min(value = 1, message = "purchase_max must be above 0")
    @Max(value = 150, message = "purchase_max must be below 150")
    private Integer purchaseMax;
    @JsonProperty("booking_max")
    @Min(value = 1, message = "booking_max must be above 0")
    @Max(value = 150, message = "booking_max must be below 150")
    private Integer bookingMax;
    @JsonProperty("issue_max")
    @Min(value = 1, message = "issue_max must be above 0")
    @Max(value = 150, message = "issue_max must be below 150")
    private Integer issueMax;

    public Integer getPurchaseMax() {
        return purchaseMax;
    }

    public void setPurchaseMax(Integer purchaseMax) {
        this.purchaseMax = purchaseMax;
    }

    public Integer getBookingMax() {
        return bookingMax;
    }

    public void setBookingMax(Integer bookingMax) {
        this.bookingMax = bookingMax;
    }

    public Integer getIssueMax() {
        return issueMax;
    }

    public void setIssueMax(Integer issueMax) {
        this.issueMax = issueMax;
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
