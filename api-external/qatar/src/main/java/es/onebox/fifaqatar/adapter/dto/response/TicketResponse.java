package es.onebox.fifaqatar.adapter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class TicketResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6761470750119879511L;

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("plan_name")
    private String planName;
    @JsonProperty("plan_id")
    private Integer planId;
    @JsonProperty("starts_at_iso")
    private ZonedDateTime sessionStart;
    @JsonProperty("ends_at_iso")
    private ZonedDateTime sessionEnd;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("num_tickets")
    private Integer numTickets;
    @JsonProperty("can_validate")
    private Boolean canValidate;
    @JsonProperty("validated_count")
    private Integer validateCount;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("plan_extra")
    private TicketPlanExtra planExtra;
    @JsonProperty("plan_cover_image")
    private String planCoverImage;
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("multiple_places")
    private Boolean multiplePlaces;
    @JsonProperty("order_id")
    private Integer orderId;
    @JsonProperty("order_external_id")
    private String orderExternalId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public ZonedDateTime getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(ZonedDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }

    public ZonedDateTime getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(ZonedDateTime sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getNumTickets() {
        return numTickets;
    }

    public void setNumTickets(Integer numTickets) {
        this.numTickets = numTickets;
    }

    public Boolean getCanValidate() {
        return canValidate;
    }

    public void setCanValidate(Boolean canValidate) {
        this.canValidate = canValidate;
    }

    public Integer getValidateCount() {
        return validateCount;
    }

    public void setValidateCount(Integer validateCount) {
        this.validateCount = validateCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public TicketPlanExtra getPlanExtra() {
        return planExtra;
    }

    public void setPlanExtra(TicketPlanExtra planExtra) {
        this.planExtra = planExtra;
    }

    public String getPlanCoverImage() {
        return planCoverImage;
    }

    public void setPlanCoverImage(String planCoverImage) {
        this.planCoverImage = planCoverImage;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Boolean getMultiplePlaces() {
        return multiplePlaces;
    }

    public void setMultiplePlaces(Boolean multiplePlaces) {
        this.multiplePlaces = multiplePlaces;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderExternalId() {
        return orderExternalId;
    }

    public void setOrderExternalId(String orderExternalId) {
        this.orderExternalId = orderExternalId;
    }
}
