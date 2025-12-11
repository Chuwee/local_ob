package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketChannelSettingsDTO implements Serializable, DateConvertible {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("use_season_ticket_dates")
    private Boolean useEventDates;
    @JsonProperty("release")
    private SeasonTicketChannelReleaseSettingsDTO release;
    @JsonProperty("sale")
    private SeasonTicketChannelSaleSettingsDTO sale;
    @JsonProperty("booking")
    private SeasonTicketChannelBookingSettingsDTO booking;
    @JsonProperty("secondary_market_sale")
    private SeasonTicketChannelSecondaryMarketSettingsDTO secondaryMarket;
    @JsonProperty("languages")
    private ChannelLanguagesDTO languages;

    public Boolean getUseEventDates() {
        return useEventDates;
    }

    public void setUseEventDates(Boolean useEventDates) {
        this.useEventDates = useEventDates;
    }

    public SeasonTicketChannelReleaseSettingsDTO getRelease() {
        return release;
    }

    public void setRelease(SeasonTicketChannelReleaseSettingsDTO release) {
        this.release = release;
    }

    public SeasonTicketChannelSaleSettingsDTO getSale() {
        return sale;
    }

    public void setSale(SeasonTicketChannelSaleSettingsDTO sale) {
        this.sale = sale;
    }

    public SeasonTicketChannelBookingSettingsDTO getBooking() {
        return booking;
    }

    public void setBooking(SeasonTicketChannelBookingSettingsDTO booking) {
        this.booking = booking;
    }

    public SeasonTicketChannelSecondaryMarketSettingsDTO getSecondaryMarket() {
        return secondaryMarket;
    }

    public void setSecondaryMarket(SeasonTicketChannelSecondaryMarketSettingsDTO secondaryMarket) {
        this.secondaryMarket = secondaryMarket;
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
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }
}
