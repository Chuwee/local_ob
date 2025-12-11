package es.onebox.mgmt.oneboxinvoicing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class GenerateOneboxInvoiceRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @JsonProperty("user_id")
    @NotNull(message = "userId can not be null")
    private Long userId;
    @JsonProperty("operator_id")
    @NotNull(message = "operatorId can not be null")
    private Long operatorId;
    @JsonProperty("entities_id")
    private List<Long> entitiesId;
    @JsonProperty("event_ids")
    private List<Long> eventIds;
    @JsonProperty("entity_code")
    private String entityCode;
    @NotNull(message = "email can not be null")
    private String email;
    @NotNull(message = "from can not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime from;
    @NotNull(message = "to can not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime to;
    @JsonProperty("order_perspective")
    @NotNull(message = "orderPerspective can not be null")
    private OneboxInvoiceType orderPerspective;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<Long> getEntitiesId() {
        return entitiesId;
    }

    public void setEntitiesId(List<Long> entitiesId) {
        this.entitiesId = entitiesId;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public OneboxInvoiceType getOrderPerspective() {
        return orderPerspective;
    }

    public void setOrderPerspective(OneboxInvoiceType orderPerspective) {
        this.orderPerspective = orderPerspective;
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
