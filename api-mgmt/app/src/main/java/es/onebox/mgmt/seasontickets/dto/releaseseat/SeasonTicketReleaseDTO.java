package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.customers.dto.CustomerDTO;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketReleaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3655365938504131571L;

    @JsonProperty("order_code")
    private String orderCode;
    @JsonProperty("product_id")
    private Long productId;
    private ReleaseStatus status;
    @JsonProperty("release_date")
    private ZonedDateTime releaseDate;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("percentage")
    private Double percentage;
    @JsonProperty("ticket_data")
    private SeasonTicketReleaseTicketDataDTO ticketData;
    private SeasonTicketReleaseSessionDTO session;
    private String userId;
    private CustomerDTO customer;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public void setStatus(ReleaseStatus status) {
        this.status = status;
    }

    public ZonedDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public SeasonTicketReleaseTicketDataDTO getTicketData() {
        return ticketData;
    }

    public void setTicketData(SeasonTicketReleaseTicketDataDTO ticketData) {
        this.ticketData = ticketData;
    }

    public SeasonTicketReleaseSessionDTO getSession() {
        return session;
    }

    public void setSession(SeasonTicketReleaseSessionDTO session) {
        this.session = session;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }
}
