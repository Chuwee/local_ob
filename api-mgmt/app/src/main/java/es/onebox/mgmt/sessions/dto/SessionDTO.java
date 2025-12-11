package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.ZoneId;
import java.util.Map;

public class SessionDTO extends BaseSessionDTO implements DateConvertible {

    @Serial
    private static final long serialVersionUID = 2727160492222421445L;

    private SessionSettingsDTO settings;

    @JsonProperty("has_sales")
    private Boolean hasSales;

    @JsonProperty("external_data")
    private Map<String,Object> externalData;

    public Boolean getHasSales() {
        return hasSales;
    }

    public void setHasSales(Boolean hasSales) {
        this.hasSales = hasSales;
    }

    public SessionSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(SessionSettingsDTO settings) {
        this.settings = settings;
    }

    @Override
    public void convertDates() {
        if (getVenueTemplate() != null && getVenueTemplate().getVenue() != null) {
            super.convertDates();
            String timezone = getVenueTemplate().getVenue().getTimezone();
            convertReleaseDates(timezone);
            convertBookingsDates(timezone);
            convertSalesDates(timezone);
            convertAccessControlDates(timezone);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Map<String,Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String,Object> externalData) {
        this.externalData = externalData;
    }

    private void convertReleaseDates(String timezone) {
        if (settings.getRelease() != null && settings.getRelease().getDate() != null) {
            settings.getRelease().setDate(ZonedDateTimeWithRelative.of(this.settings.getRelease().getDate().absolute().withZoneSameInstant(ZoneId.of(timezone))));
        }
    }

    private void convertBookingsDates(String timezone) {
        if (settings.getBooking() != null) {
            if (settings.getBooking().getStartDate() != null) {
                settings.getBooking().setStartDate(ZonedDateTimeWithRelative.of(this.settings.getBooking().getStartDate().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
            if (settings.getBooking().getEndDate() != null) {
                settings.getBooking().setEndDate(ZonedDateTimeWithRelative.of(this.settings.getBooking().getEndDate().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
        }
    }

    private void convertSalesDates(String timezone) {
        if (settings.getSale() != null) {
            if (settings.getSale().getStartDate() != null) {
                settings.getSale().setStartDate(ZonedDateTimeWithRelative.of(this.settings.getSale().getStartDate().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
            if (settings.getSale().getEndDate() != null) {
                settings.getSale().setEndDate(ZonedDateTimeWithRelative.of(this.settings.getSale().getEndDate().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
        }
    }

    private void convertAccessControlDates(String timezone) {
        if (settings.getAccessControl() != null && settings.getAccessControl().getDates() != null) {
            if (settings.getAccessControl().getDates().getStart() != null) {
                settings.getAccessControl().getDates().setStart(ZonedDateTimeWithRelative.of(this.settings.getAccessControl().getDates().getStart().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
            if (settings.getAccessControl().getDates().getEnd() != null) {
                settings.getAccessControl().getDates().setEnd(ZonedDateTimeWithRelative.of(this.settings.getAccessControl().getDates().getEnd().absolute().withZoneSameInstant(ZoneId.of(timezone))));
            }
        }
    }

}
