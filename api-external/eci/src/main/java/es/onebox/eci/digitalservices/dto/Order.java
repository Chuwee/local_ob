package es.onebox.eci.digitalservices.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class Order implements Serializable {

    private static final long serialVersionUID = 7478335356587088674L;

    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("promoter_cif")
    private String promoterCIF;
    @JsonProperty("promoter_name")
    private String promoterName;
    @JsonProperty("promoter_address")
    private String promoterAddress;
    @JsonProperty("promoter_country")
    private String promoterCountry;
    @JsonProperty("platform_cif")
    private String platformCIF;
    @JsonProperty("platform_name")
    private String platformName;
    @JsonProperty("platform_server_country")
    private String platformServerCountry;
    private String code;
    @JsonProperty("refund_related_code")
    private String refundRelatedCode;
    @JsonProperty("event_name")
    private String eventName;
    @JsonProperty("event_date")
    private String eventDate;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPromoterCIF() {
        return promoterCIF;
    }

    public void setPromoterCIF(String promoterCIF) {
        this.promoterCIF = promoterCIF;
    }

    public String getPromoterName() {
        return promoterName;
    }

    public void setPromoterName(String promoterName) {
        this.promoterName = promoterName;
    }

    public String getPromoterAddress() {
        return promoterAddress;
    }

    public void setPromoterAddress(String promoterAddress) {
        this.promoterAddress = promoterAddress;
    }

    public String getPromoterCountry() {
        return promoterCountry;
    }

    public void setPromoterCountry(String promoterCountry) {
        this.promoterCountry = promoterCountry;
    }

    public String getPlatformCIF() {
        return platformCIF;
    }

    public void setPlatformCIF(String platformCIF) {
        this.platformCIF = platformCIF;
    }

    public String getPlatformServerCountry() {
        return platformServerCountry;
    }

    public void setPlatformServerCountry(String platformServerCountry) {
        this.platformServerCountry = platformServerCountry;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRefundRelatedCode() {
        return refundRelatedCode;
    }

    public void setRefundRelatedCode(String refundRelatedCode) {
        this.refundRelatedCode = refundRelatedCode;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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
