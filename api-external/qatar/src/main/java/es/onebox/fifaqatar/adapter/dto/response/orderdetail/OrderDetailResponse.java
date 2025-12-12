package es.onebox.fifaqatar.adapter.dto.response.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderDetailResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5545473369224186538L;

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("payment_gateway")
    private String paymentGateway;
    @JsonProperty("tickets")
    private List<OrderTicket> tickets;
    @JsonProperty("plan_id")
    private Integer planId;
    @JsonProperty("plan_name")
    private String planName;
    @JsonProperty("plant_type")
    private String planType;
    @JsonProperty("plan_cover_image")
    private String planCoverImage;
    @JsonProperty("price_breakdown_items")
    private List<OrderPriceBreakdown> priceBreakdownItems;
    @JsonProperty("price_breakdown_items_footer")
    private List<OrderPriceBreakdown> priceBreakdownItemsFooter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public List<OrderTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<OrderTicket> tickets) {
        this.tickets = tickets;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getPlanCoverImage() {
        return planCoverImage;
    }

    public void setPlanCoverImage(String planCoverImage) {
        this.planCoverImage = planCoverImage;
    }

    public List<OrderPriceBreakdown> getPriceBreakdownItems() {
        return priceBreakdownItems;
    }

    public void setPriceBreakdownItems(List<OrderPriceBreakdown> priceBreakdownItems) {
        this.priceBreakdownItems = priceBreakdownItems;
    }

    public List<OrderPriceBreakdown> getPriceBreakdownItemsFooter() {
        return priceBreakdownItemsFooter;
    }

    public void setPriceBreakdownItemsFooter(List<OrderPriceBreakdown> priceBreakdownItemsFooter) {
        this.priceBreakdownItemsFooter = priceBreakdownItemsFooter;
    }

}
