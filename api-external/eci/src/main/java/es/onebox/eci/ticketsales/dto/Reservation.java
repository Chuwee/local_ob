package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Reservation implements Serializable {

    @Serial
    private static final long serialVersionUID = 8657800646931777609L;

    private String identifier;
    @JsonProperty("related_reservation")
    private RelatedReservation relatedReservation;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public RelatedReservation getRelatedReservation() {
        return relatedReservation;
    }

    public void setRelatedReservation(RelatedReservation relatedReservation) {
        this.relatedReservation = relatedReservation;
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
