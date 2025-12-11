package es.onebox.mgmt.seasontickets.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;

import java.io.Serializable;

public class BaseSeasonTicketChannelDTO implements Serializable, DateConvertible {

    private static final long serialVersionUID = 1L;

    private SeasonTicketChannelInfoDTO channel;

    @JsonProperty("season_ticket")
    private SeasonTicketInfoDTO seasonTicket;
    private SeasonTicketChannelStatusInfoDTO status;
    private SeasonTicketChannelSettingsDTO settings;

    public SeasonTicketChannelInfoDTO getChannel() {
        return channel;
    }

    public void setChannel(SeasonTicketChannelInfoDTO channel) {
        this.channel = channel;
    }

    public SeasonTicketInfoDTO getSeasonTicket() {
        return seasonTicket;
    }

    public void setSeasonTicket(SeasonTicketInfoDTO seasonTicket) {
        this.seasonTicket = seasonTicket;
    }

    public SeasonTicketChannelStatusInfoDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketChannelStatusInfoDTO status) {
        this.status = status;
    }

    public SeasonTicketChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(SeasonTicketChannelSettingsDTO settings) {
        this.settings = settings;
    }

    @Override
    public void convertDates() {
        if (settings != null) {
            settings.convertDates();
        }
    }

}
