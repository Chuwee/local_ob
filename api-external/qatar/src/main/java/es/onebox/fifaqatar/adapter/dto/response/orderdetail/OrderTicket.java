package es.onebox.fifaqatar.adapter.dto.response.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class OrderTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = 6037477983640300287L;

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ticket_status")
    private Integer status;
    @JsonProperty("ticket_number")
    private Integer quantity;
    @JsonProperty("instructions")
    private String instructions;
    @JsonProperty("codes")
    private List<String> codes;
    @JsonProperty("is_exchangeable")
    private Boolean exchangeable;
    @JsonProperty("is_express_exchangeable")
    private Boolean expressExchangeable;
    @JsonProperty("is_transferable")
    private Boolean transferable;
    @JsonProperty("is_upgradeable")
    private Boolean upgradeable;

    @JsonProperty("session_id")
    private Integer sessionId;
    @JsonProperty("session_label")
    private String sessionLabel;
    @JsonProperty("session_place_name")
    private String sessionPlaceName;
    @JsonProperty("session_google_place_id")
    private String sessionGooglePlaceId;
    @JsonProperty("session_city_code")
    private String sessionCityCode;

    @JsonProperty("starts_at")
    private ZonedDateTime starts;
    @JsonProperty("ends_at")
    private ZonedDateTime end;
    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("total_price")
    private OrderPrice totalPrice;
    @JsonProperty("unitPrice")
    private OrderPrice unitPrice;
    @JsonProperty("surcharge_applied")
    private BigDecimal surchargeApplied;
    @JsonProperty("strikethrough_price")
    private OrderPrice strikethroughPrice;
    @JsonProperty("strikethrough_percentage")
    private String strikethroughPercentage;
    @JsonProperty("price_breakdown_items")
    private List<OrderPriceBreakdown> priceBreakdownItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Boolean getExchangeable() {
        return exchangeable;
    }

    public void setExchangeable(Boolean exchangeable) {
        this.exchangeable = exchangeable;
    }

    public Boolean getExpressExchangeable() {
        return expressExchangeable;
    }

    public void setExpressExchangeable(Boolean expressExchangeable) {
        this.expressExchangeable = expressExchangeable;
    }

    public Boolean getTransferable() {
        return transferable;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public Boolean getUpgradeable() {
        return upgradeable;
    }

    public void setUpgradeable(Boolean upgradeable) {
        this.upgradeable = upgradeable;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionLabel() {
        return sessionLabel;
    }

    public void setSessionLabel(String sessionLabel) {
        this.sessionLabel = sessionLabel;
    }

    public String getSessionPlaceName() {
        return sessionPlaceName;
    }

    public void setSessionPlaceName(String sessionPlaceName) {
        this.sessionPlaceName = sessionPlaceName;
    }

    public String getSessionGooglePlaceId() {
        return sessionGooglePlaceId;
    }

    public void setSessionGooglePlaceId(String sessionGooglePlaceId) {
        this.sessionGooglePlaceId = sessionGooglePlaceId;
    }

    public String getSessionCityCode() {
        return sessionCityCode;
    }

    public void setSessionCityCode(String sessionCityCode) {
        this.sessionCityCode = sessionCityCode;
    }

    public ZonedDateTime getStarts() {
        return starts;
    }

    public void setStarts(ZonedDateTime starts) {
        this.starts = starts;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderPrice getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(OrderPrice totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderPrice getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(OrderPrice unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSurchargeApplied() {
        return surchargeApplied;
    }

    public void setSurchargeApplied(BigDecimal surchargeApplied) {
        this.surchargeApplied = surchargeApplied;
    }

    public OrderPrice getStrikethroughPrice() {
        return strikethroughPrice;
    }

    public void setStrikethroughPrice(OrderPrice strikethroughPrice) {
        this.strikethroughPrice = strikethroughPrice;
    }

    public String getStrikethroughPercentage() {
        return strikethroughPercentage;
    }

    public void setStrikethroughPercentage(String strikethroughPercentage) {
        this.strikethroughPercentage = strikethroughPercentage;
    }

    public List<OrderPriceBreakdown> getPriceBreakdownItems() {
        return priceBreakdownItems;
    }

    public void setPriceBreakdownItems(List<OrderPriceBreakdown> priceBreakdownItems) {
        this.priceBreakdownItems = priceBreakdownItems;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
