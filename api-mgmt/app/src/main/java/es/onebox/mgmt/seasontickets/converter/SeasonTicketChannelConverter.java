package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.event.dto.event.BaseEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelSettings;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelStatusInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannelSettings;
import es.onebox.mgmt.events.enums.EventStatus;
import es.onebox.mgmt.seasontickets.dto.channels.BaseSeasonTicketChannelDTO;
import es.onebox.mgmt.seasontickets.dto.channels.ChannelEntityDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelBookingSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelInfoDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelQuotaDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelReleaseSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSaleSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSecondaryMarketSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSettingsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelStatusInfoDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketInfoDTO;
import es.onebox.mgmt.seasontickets.dto.channels.UpdateSeasonTicketChannelDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketChannelConverter {

    private SeasonTicketChannelConverter() {

    }

    public static SeasonTicketChannelsDTO fromEntity(EventChannels eventChannels) {
        if (eventChannels == null) {
            return null;
        }
        SeasonTicketChannelsDTO dto = new SeasonTicketChannelsDTO();
        dto.setData(fromEntity(eventChannels.getData()));
        dto.setMetadata(eventChannels.getMetadata());
        return dto;
    }

    public static List<BaseSeasonTicketChannelDTO> fromEntity(List<BaseEventChannel> eventChannels) {
        if (eventChannels == null || eventChannels.isEmpty()) {
            return new ArrayList<>();
        }
        return eventChannels
                .stream()
                .map(SeasonTicketChannelConverter::convertToBaseDTO)
                .collect(Collectors.toList());
    }

    private static BaseSeasonTicketChannelDTO convertToBaseDTO(BaseEventChannel eventChannel) {
        if (eventChannel == null) {
            return null;
        }
        return fillBaseSeasonTicketChannel(new BaseSeasonTicketChannelDTO(), eventChannel, null);
    }

    private static BaseSeasonTicketChannelDTO fillBaseSeasonTicketChannel(BaseSeasonTicketChannelDTO target,
                                                                          BaseEventChannel eventChannel, ChannelLanguagesDTO languages) {
        if (target == null) {
            return null;
        }
        target.setChannel(getSeasonTicketChannelInfo(eventChannel.getChannel()));
        target.setSeasonTicket(getSeasonTicketInfo(eventChannel.getEvent()));
        target.setStatus(getStatusInfo(eventChannel.getStatus()));
        target.setSettings(getSettings(eventChannel.getSettings(), languages));
        return target;
    }

    private static SeasonTicketChannelInfoDTO getSeasonTicketChannelInfo(EventChannelInfo eventChannelInfo) {
        if (eventChannelInfo == null) {
            return null;
        }
        SeasonTicketChannelInfoDTO dto = new SeasonTicketChannelInfoDTO();
        dto.setEntity(new ChannelEntityDTO());
        dto.getEntity().setId(eventChannelInfo.getEntityId());
        dto.getEntity().setName(eventChannelInfo.getEntityName());
        dto.getEntity().setLogo(eventChannelInfo.getEntityLogo());
        dto.setId(eventChannelInfo.getId());
        dto.setName(eventChannelInfo.getName());
        dto.setType(ChannelSubtype.getById(eventChannelInfo.getType().getIdSubtipo()));
        dto.setIsV4(CommonUtils.isTrue(eventChannelInfo.getV4Enabled())
                || CommonUtils.isTrue(eventChannelInfo.getV4ConfigEnabled()));
        return dto;
    }

    private static SeasonTicketInfoDTO getSeasonTicketInfo(EventInfo eventInfo) {
        if (eventInfo == null) {
            return null;
        }
        SeasonTicketInfoDTO dto = new SeasonTicketInfoDTO();
        dto.setId(eventInfo.getId());
        dto.setStatus(EventStatus.valueOf(eventInfo.getStatus().name()));
        return dto;
    }

    private static SeasonTicketChannelSettingsDTO getSettings(EventChannelSettings eventChannelSettings, ChannelLanguagesDTO languages) {
        if (eventChannelSettings == null) {
            return null;
        }
        SeasonTicketChannelSettingsDTO dto = new SeasonTicketChannelSettingsDTO();
        dto.setUseEventDates(eventChannelSettings.getUseEventDates());
        dto.setRelease(new SeasonTicketChannelReleaseSettingsDTO());
        dto.getRelease().setDate(eventChannelSettings.getReleaseDate());
        dto.getRelease().setEnabled(eventChannelSettings.getReleaseEnabled());

        dto.setSale(new SeasonTicketChannelSaleSettingsDTO());
        dto.getSale().setEnabled(eventChannelSettings.getSaleEnabled());
        dto.getSale().setEndDate(eventChannelSettings.getSaleEndDate());
        dto.getSale().setStartDate(eventChannelSettings.getSaleStartDate());

        dto.setBooking(new SeasonTicketChannelBookingSettingsDTO());
        dto.getBooking().setEnabled(eventChannelSettings.getBookingEnabled());
        dto.getBooking().setEndDate(eventChannelSettings.getBookingEndDate());
        dto.getBooking().setStartDate(eventChannelSettings.getBookingStartDate());

        dto.setSecondaryMarket(new SeasonTicketChannelSecondaryMarketSettingsDTO());
        dto.getSecondaryMarket().setEnabled(eventChannelSettings.getSecondaryMarketEnabled());
        dto.getSecondaryMarket().setStartDate(eventChannelSettings.getSecondaryMarketStartDate());
        dto.getSecondaryMarket().setEndDate(eventChannelSettings.getSecondaryMarketEndDate());

        dto.setLanguages(languages);

        return dto;
    }

    private static SeasonTicketChannelStatusInfoDTO getStatusInfo(EventChannelStatusInfo eventChannelStatusInfo) {
        if (eventChannelStatusInfo == null) {
            return null;
        }
        SeasonTicketChannelStatusInfoDTO dto = new SeasonTicketChannelStatusInfoDTO();
        dto.setRelease(eventChannelStatusInfo.getRelease());
        dto.setRequest(eventChannelStatusInfo.getRequest());
        dto.setSale(eventChannelStatusInfo.getSale());
        return dto;
    }


    public static SeasonTicketChannelDTO fromEntity(EventChannel eventChannel, ChannelLanguagesDTO languages) {
        SeasonTicketChannelDTO seasonTicketChannel = new SeasonTicketChannelDTO();
        fillBaseSeasonTicketChannel(seasonTicketChannel, eventChannel, languages);
        if (eventChannel.getSaleGroups() != null) {
            seasonTicketChannel.setQuotas(eventChannel.getSaleGroups().stream().map(saleGroup -> {
                SeasonTicketChannelQuotaDTO quota = new SeasonTicketChannelQuotaDTO();
                quota.setId(saleGroup.getId());
                quota.setDescription(saleGroup.getDescription());
                quota.setTemplateName(saleGroup.getTemplateName());
                quota.setSelected(saleGroup.getSelected());
                return quota;
            }).collect(Collectors.toList()));
        }
        seasonTicketChannel.setUseAllQuotas(eventChannel.getUseAllSaleGroups());
        return seasonTicketChannel;
    }

    public static UpdateEventChannel fromDTO(UpdateSeasonTicketChannelDTO dto) {
        UpdateEventChannel updateEventChannel = new UpdateEventChannel();

        updateEventChannel.setSaleGroups(dto.getQuotas());

        updateEventChannel.setUseAllSaleGroups(dto.getUseAllQuotas());
        if (dto.getSettings() == null) {
            return updateEventChannel;
        }
        updateEventChannel.setSettings(new UpdateEventChannelSettings());
        updateEventChannel.getSettings().setUseEventDates(dto.getSettings().getUseEventDates());

        if (dto.getSettings().getBooking() != null) {
            updateEventChannel.getSettings().setBookingEnabled(dto.getSettings().getBooking().getEnabled());
            updateEventChannel.getSettings().setBookingEndDate(dto.getSettings().getBooking().getEndDate());
            updateEventChannel.getSettings().setBookingStartDate(dto.getSettings().getBooking().getStartDate());
        }

        if (dto.getSettings().getRelease() != null) {
            updateEventChannel.getSettings().setReleaseEnabled(dto.getSettings().getRelease().getEnabled());
            updateEventChannel.getSettings().setReleaseDate(dto.getSettings().getRelease().getDate());
        }

        if (dto.getSettings().getSale() != null) {
            updateEventChannel.getSettings().setSaleEnabled(dto.getSettings().getSale().getEnabled());
            updateEventChannel.getSettings().setSaleEndDate(dto.getSettings().getSale().getEndDate());
            updateEventChannel.getSettings().setSaleStartDate(dto.getSettings().getSale().getStartDate());
        }
        if (dto.getSettings().getSecondaryMarket() != null) {
            updateEventChannel.getSettings().setSecondaryMarketEnabled(dto.getSettings().getSecondaryMarket().getEnabled());
            updateEventChannel.getSettings().setSecondaryMarketStartDate(dto.getSettings().getSecondaryMarket().getStartDate());
            updateEventChannel.getSettings().setSecondaryMarketEndDate(dto.getSettings().getSecondaryMarket().getEndDate());
        }
        return updateEventChannel;
    }

}
