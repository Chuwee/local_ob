package es.onebox.common.datasources.accesscontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class BarcodeInfo implements Serializable {
    private static final long serialVersionUID = 1851315697956153585L;

    private String barcode;
    @JsonProperty("session_id")
    private Long sessionId;
    private TicketType type;
    private ACSeatDTO seat;
    @JsonProperty("update_date")
    private ZonedDateTime updateDate;
    @JsonProperty("related_session")
    private IdNameDTO relatedSession;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public ACSeatDTO getSeat() {
        return seat;
    }

    public void setSeat(ACSeatDTO seat) {
        this.seat = seat;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public IdNameDTO getRelatedSession() {
        return relatedSession;
    }

    public void setRelatedSession(IdNameDTO relatedSession) {
        this.relatedSession = relatedSession;
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
