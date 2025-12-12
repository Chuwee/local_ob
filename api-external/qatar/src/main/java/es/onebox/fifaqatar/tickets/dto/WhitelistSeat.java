package es.onebox.fifaqatar.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class WhitelistSeat implements Serializable {

    private static final long serialVersionUID = -1225771862432811235L;

    private String barcode;
    private String type;
    private Seat seat;
    @JsonProperty("update_date")
    private ZonedDateTime updateDate;
    @JsonProperty("related_session")
    private IdNameDTO relatedSession;
    private Session session;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
