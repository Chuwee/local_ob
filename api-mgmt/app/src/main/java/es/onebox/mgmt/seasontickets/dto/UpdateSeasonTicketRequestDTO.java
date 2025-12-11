package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.events.dto.EventContactDTO;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZoneId;

public class UpdateSeasonTicketRequestDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private static final String UTC = "UTC";

    private Long id;

    private String name;

    private SeasonTicketStatusDTO status;

    private String reference;

    private EventContactDTO contact;

    @Valid
    private UpdateSeasonTicketsSettingsDTO settings;

    @JsonProperty("currency_code")
    private String currencyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public EventContactDTO getContact() {
        return contact;
    }

    public void setContact(EventContactDTO contact) {
        this.contact = contact;
    }

    public String getCurrencyCode() { return currencyCode; }

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public UpdateSeasonTicketsSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(UpdateSeasonTicketsSettingsDTO settings) {
        this.settings = settings;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public void convertDates() {
        if (settings != null && settings.getOperative() != null) {
            String timezone = settings.getZoneId() == null ? UTC : settings.getZoneId();
            convertReleaseDates(timezone);
            convertSalesDates(timezone);
            convertBookingsDates(timezone);
        }
    }

    private void convertReleaseDates(String timezone) {
        if (settings.getOperative().getRelease() != null && settings.getOperative().getRelease().getDate() != null) {
            settings.getOperative().getRelease()
                    .setDate(this.settings.getOperative().getRelease().getDate().withZoneSameInstant(ZoneId.of(timezone)));
        }
    }

    private void convertBookingsDates(String timezone) {
        if (settings.getOperative().getBooking() != null) {
            if (settings.getOperative().getBooking().getStartDate() != null) {
                settings.getOperative().getBooking()
                        .setStartDate(this.settings.getOperative().getBooking().getStartDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
            if (settings.getOperative().getBooking().getEndDate() != null) {
                settings.getOperative().getBooking()
                        .setEndDate(this.settings.getOperative().getBooking().getEndDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
        }
    }

    private void convertSalesDates(String timezone) {
        if (settings.getOperative().getSale() != null) {
            if (settings.getOperative().getSale().getStartDate() != null) {
                settings.getOperative().getSale()
                        .setStartDate(this.settings.getOperative().getSale().getStartDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
            if (settings.getOperative().getSale().getEndDate() != null) {
                settings.getOperative().getSale()
                        .setEndDate(this.settings.getOperative().getSale().getEndDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
        }
    }
}
