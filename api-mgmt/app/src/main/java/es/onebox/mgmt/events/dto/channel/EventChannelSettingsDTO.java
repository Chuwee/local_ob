package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;

import java.io.Serial;
import java.io.Serializable;

public class EventChannelSettingsDTO implements Serializable, DateConvertible {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("use_event_dates")
    private Boolean useEventDates;
    @JsonProperty("release")
    private EventChannelReleaseSettingsDTO release;
    @JsonProperty("sale")
    private EventChannelSaleSettingsDTO sale;
    @JsonProperty("booking")
    private EventChannelBookingSettingsDTO booking;
    @JsonProperty("secondary_market_sale")
    private EventChannelSecondaryMarketSettingsDTO secondaryMarketSale;
    @JsonProperty("languages")
    private ChannelLanguagesDTO languages;

    public Boolean getUseEventDates() {
        return useEventDates;
    }
    public void setUseEventDates(Boolean useEventDates) {
        this.useEventDates = useEventDates;
    }

    public EventChannelReleaseSettingsDTO getRelease() {
        return release;
    }
    public void setRelease(EventChannelReleaseSettingsDTO release) {
        this.release = release;
    }

    public EventChannelSaleSettingsDTO getSale() {
        return sale;
    }
    public void setSale(EventChannelSaleSettingsDTO sale) {
        this.sale = sale;
    }

    public EventChannelBookingSettingsDTO getBooking() {
        return booking;
    }
    public void setBooking(EventChannelBookingSettingsDTO booking) {
        this.booking = booking;
    }

    public EventChannelSecondaryMarketSettingsDTO getSecondaryMarketSale() {
        return secondaryMarketSale;
    }
    public void setSecondaryMarketSale(EventChannelSecondaryMarketSettingsDTO secondaryMarketSale) {
        this.secondaryMarketSale = secondaryMarketSale;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }
    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }

    @Override
    public void convertDates() {
        if (release != null) {
            release.convertDates();
        }
        if (sale != null) {
            sale.convertDates();
        }
        if (booking != null) {
            booking.convertDates();
        }
        if (secondaryMarketSale != null) {
            secondaryMarketSale.convertDates();
        }
    }
}
