package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.events.dto.EventContactDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZoneId;

public class SeasonTicketDTO extends BaseSeasonTicketDTO implements Serializable, DateConvertible {

    private static final String UTC = "UTC";
    private static final long serialVersionUID = 1L;

    private EventContactDTO contact;

    private SeasonTicketsSettingsDTO settings;

    @JsonProperty("has_sales")
    private Boolean hasSales;

    @JsonProperty("has_sales_request")
    private Boolean hasSalesRequest;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    public SeasonTicketsSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(SeasonTicketsSettingsDTO settings) {
        this.settings = settings;
    }

    public EventContactDTO getContact() {
        return contact;
    }

    public void setContact(EventContactDTO contact) {
        this.contact = contact;
    }

    public Boolean getHasSales() {
        return hasSales;
    }

    public Boolean getHasSalesRequest() {
        return hasSalesRequest;
    }

    public void setHasSalesRequest(Boolean hasSalesRequest) {
        this.hasSalesRequest = hasSalesRequest;
    }

    public void setHasSales(Boolean hasSales) {
        this.hasSales = hasSales;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
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
        super.convertDates();
        if(settings != null && settings.getOperative() != null) {
            String timezone = settings.getZoneId() == null ? UTC : settings.getZoneId();
            convertReleaseDates(timezone);
            convertSalesDates(timezone);
            convertBookingsDates(timezone);
            if(settings.getOperative().getRenewal() != null) {
                settings.getOperative().getRenewal().convertDates();
            }
        }
    }

    private void convertReleaseDates(String timezone) {
        if (settings.getOperative().getRelease() != null && settings.getOperative().getRelease().getDate() != null) {
            settings.getOperative().getRelease().setDate(this.settings.getOperative().getRelease().getDate().withZoneSameInstant(ZoneId.of(timezone)));
        }
    }

    private void convertBookingsDates(String timezone) {
        if (settings.getOperative().getBooking() != null) {
            if (settings.getOperative().getBooking().getStartDate() != null) {
                settings.getOperative().getBooking().setStartDate(this.settings.getOperative().getBooking().getStartDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
            if (settings.getOperative().getBooking().getEndDate() != null) {
                settings.getOperative().getBooking().setEndDate(this.settings.getOperative().getBooking().getEndDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
        }
    }

    private void convertSalesDates(String timezone) {
        if (settings.getOperative().getSale() != null) {
            if (settings.getOperative().getSale().getStartDate() != null) {
                settings.getOperative().getSale().setStartDate(this.settings.getOperative().getSale().getStartDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
            if (settings.getOperative().getSale().getEndDate() != null) {
                settings.getOperative().getSale().setEndDate(this.settings.getOperative().getSale().getEndDate().withZoneSameInstant(ZoneId.of(timezone)));
            }
        }
    }
}
