package es.onebox.event.events.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.events.dto.BaseEventChannelDTO;
import es.onebox.event.events.dto.EventChannelDTO;
import es.onebox.event.events.dto.EventChannelInfoDTO;
import es.onebox.event.events.dto.EventChannelSettingsDTO;
import es.onebox.event.events.dto.EventChannelStatusDTO;
import es.onebox.event.events.dto.EventInfoDTO;
import es.onebox.event.events.dto.EventTicketTemplatesDTO;
import es.onebox.event.events.dto.ProviderPlanSettings;
import es.onebox.event.events.dto.UpdateEventChannelDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.WhitelabelType;
import es.onebox.event.events.utils.EventStatusUtil;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.secondarymarket.domain.EnabledChannel;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

public class EventChannelRecordConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventChannelRecordConverter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EventChannelRecordConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static BaseEventChannelDTO fromEntityToBase(EventChannelRecord record, List<SessionRecord> sessions,
                                                       EventSecondaryMarketConfig secMktConfig, String s3url,
                                                       ChannelConfigDTO channelConfig, List<SessionConfig> sessionConfigs) {
        if (record == null) {
            return null;
        }
        BaseEventChannelDTO target = new BaseEventChannelDTO();
        return fillBaseEventChannel(target, record, sessions, secMktConfig, s3url, channelConfig, sessionConfigs);
    }

    public static List<BaseEventChannelDTO> fromEntityToBase(
            List<EventChannelRecord> records, List<SessionRecord> sessions, String s3url,
            EventSecondaryMarketConfig eventSecondaryMarketConfig, List<SessionConfig> secMktSessionConfigs,
            LongFunction<ChannelConfigDTO> channelConfigGetter) {

        if (records == null) {
            return new ArrayList<>();
        }
        return records.stream()
                .map(r -> fromEntityToBase(r, sessions, eventSecondaryMarketConfig, s3url,
                        channelConfigGetter.apply(r.getChannelId()), secMktSessionConfigs))
                .collect(Collectors.toList());
    }

    private static void fillEvent(BaseEventChannelDTO target, EventChannelRecord record) {
        target.setEvent(new EventInfoDTO());

        target.getEvent().setId(record.getEventId());
        target.getEvent().setStatus(EventStatus.byId(record.getEventStatus()));
    }

    private static void fillChannel(BaseEventChannelDTO target, EventChannelRecord record, String s3Url,
                                    ChannelConfigDTO channelConfig) {
        target.setChannel(new EventChannelInfoDTO());

        target.getChannel().setId(record.getChannelId());
        target.getChannel().setName(record.getChannelName());
        target.getChannel().setType(ChannelSubtype.getById(record.getChannelType()));
        target.getChannel().setV4Enabled(channelConfig.getV4Enabled());
        target.getChannel().setV4ConfigEnabled(channelConfig.getV4ConfigEnabled());
        target.getChannel().setEntityId(record.getEntityId());
        target.getChannel().setEntityName(record.getEntityName());
        if (record.getEntityLogoPath() != null
                && record.getOperatorId() != null
                && record.getEntityId() != null) {
            target.getChannel().setEntityLogo(getLogoUrl(record, s3Url));
        }
        target.getChannel().setFavorite(record.getFavorite());
        target.getChannel().setWhitelabelType(WhitelabelType.INTERNAL);
        if (channelConfig.getWhitelabelType() != null) {
            target.getChannel().setWhitelabelType(channelConfig.getWhitelabelType());
        }
        if (record.getIndividualTicketTemplate() != null) {
            EventTicketTemplatesDTO templates = new EventTicketTemplatesDTO();
            templates.setIndividualTicketPdfTemplateId(record.getIndividualTicketTemplate().longValue());
            target.getChannel().setTicketTemplates(templates);
        }
    }

    private static String getLogoUrl(EventChannelRecord record, String s3url) {
        return S3URLResolver.builder()
                .withUrl(s3url)
                .withType(S3URLResolver.S3ImageType.ENTITY_IMAGE)
                .withEntityId(record.getEntityId())
                .withOperatorId(record.getOperatorId())
                .build()
                .buildPath(record.getEntityLogoPath());
    }

    private static void fillStatus(BaseEventChannelDTO target, EventChannelRecord record) {
        target.setStatus(new EventChannelStatusDTO());
        target.getStatus().setRequest(EventChannelStatus.byId(record.getRequestStatus()));
    }

    private static void fillSettings(BaseEventChannelDTO target, EventChannelRecord record,
                                     EventSecondaryMarketConfig secMktConfig,
                                     List<SessionConfig> secMktSessionConfigs) {
        EventChannelSettingsDTO eventChannelSettingsDTO = new EventChannelSettingsDTO();

        eventChannelSettingsDTO.setUseEventDates(record.getUseEventDates());

        eventChannelSettingsDTO.setReleaseEnabled(record.getReleaseEnable());
        eventChannelSettingsDTO.setReleaseDate(CommonUtils.timestampToZonedDateTime(record.getReleaseDate()));
        eventChannelSettingsDTO.setReleaseDateTZ(record.getReleaseDateTZ());

        eventChannelSettingsDTO.setSaleEnabled(record.getSaleEnable());
        eventChannelSettingsDTO.setSaleStartDate(CommonUtils.timestampToZonedDateTime(record.getSaleStartDate()));
        eventChannelSettingsDTO.setSaleStartDateTZ(record.getSaleStartDateTZ());
        eventChannelSettingsDTO.setSaleEndDate(CommonUtils.timestampToZonedDateTime(record.getSaleEndDate()));
        eventChannelSettingsDTO.setSaleEndDateTZ(record.getSaleEndDateTZ());

        eventChannelSettingsDTO.setBookingEnabled(record.getBookingEnable());
        eventChannelSettingsDTO.setBookingStartDate(CommonUtils.timestampToZonedDateTime(record.getBookingStartDate()));
        eventChannelSettingsDTO.setBookingStartDateTZ(record.getBookingStartDateTZ());
        eventChannelSettingsDTO.setBookingEndDate(CommonUtils.timestampToZonedDateTime(record.getBookingEndDate()));
        eventChannelSettingsDTO.setBookingEndDateTZ(record.getBookingEndDateTZ());

        if (secMktConfig != null && secMktConfig.getEnabledChannels() != null) {
            Optional<EnabledChannel> channel = secMktConfig.getEnabledChannels().stream()
                    .filter(eCh -> eCh.getId().equals(record.getChannelId())).findAny();

            List<SessionConfig> secMktSessions = null;
            if (CommonUtils.isTrue(record.getUseEventDates()) || channel.isEmpty() || channel.get().getStartDate() == null || channel.get().getEndDate() == null) {
                secMktSessions = secMktSessionConfigs;
            }

            eventChannelSettingsDTO.setSecondaryMarketEnabled(channel.isPresent());
            eventChannelSettingsDTO.setSecondaryMarketStartDate(
                    CommonUtils.isTrue(record.getUseEventDates()) || channel.isEmpty() || channel.get().getStartDate() == null ?
                            SecondaryMarketUtils.findFirstSessionSecMktStartDate(secMktSessions) : channel.get().getStartDate()
            );
            eventChannelSettingsDTO.setSecondaryMarketEndDate(
                    CommonUtils.isTrue(record.getUseEventDates()) || channel.isEmpty() || channel.get().getEndDate() == null ?
                            SecondaryMarketUtils.findLastSessionSecMktEndDate(secMktSessions) : channel.get().getEndDate()
            );
        }

        target.setSettings(eventChannelSettingsDTO);
    }

    private static BaseEventChannelDTO fillBaseEventChannel(BaseEventChannelDTO target, EventChannelRecord record,
                                                            List<SessionRecord> sessions, EventSecondaryMarketConfig secMktConfig,
                                                            String s3url, ChannelConfigDTO channelConfig,
                                                            List<SessionConfig> secMktSessionConfigs) {
        target.setId(record.getId());
        fillSettings(target, record, secMktConfig, secMktSessionConfigs);
        fillEvent(target, record);
        fillStatus(target, record);
        fillChannel(target, record, s3url, channelConfig);
        target.setProviderPlanSettings(deserializeProviderPlanSettings(record.getProviderPlanSettings()));
        EventStatusUtil.applyEventChannelFlagStatus(target, sessions);
        return target;
    }

    public static EventChannelDTO fromEntity(EventChannelRecord eventChannelRecord, List<SessionRecord> sessions,
                                             EventSecondaryMarketConfig secMktConfig, String s3url, ChannelConfigDTO channelConfig,
                                             List<SessionConfig> sessionConfigs) {
        EventChannelDTO dto = new EventChannelDTO();
        dto.setUseAllSaleGroups(eventChannelRecord.getAllSaleGroups());
        fillBaseEventChannel(dto, eventChannelRecord, sessions, secMktConfig, s3url, channelConfig, sessionConfigs);
        return dto;
    }

    public static CpanelCanalEventoRecord updateRecord(CpanelCanalEventoRecord record, UpdateEventChannelDTO updateData) {
        if (updateData == null) {
            return record;
        }

        if (updateData.getSettings() != null) {
            updateSettingsDates(record, updateData);

            if (updateData.getSettings().getBookingEnabled() != null) {
                record.setReservasactivas((byte) (updateData.getSettings().getBookingEnabled() ? 1 : 0));
            }
            if (updateData.getSettings().getReleaseEnabled() != null) {
                record.setPublicado((byte) (updateData.getSettings().getReleaseEnabled() ? 1 : 0));
            }
            if (updateData.getSettings().getSaleEnabled() != null) {
                record.setEnventa((byte) (updateData.getSettings().getSaleEnabled() ? 1 : 0));
            }
        }
        if (updateData.getUseAllSaleGroups() != null) {
            record.setTodosgruposventa((byte) (updateData.getUseAllSaleGroups() ? 1 : 0));
        }
        if (updateData.getTicketTemplates() != null && updateData.getTicketTemplates().getIndividualTicketPdfTemplateId() != null) {
            record.setIdplantillaticket(updateData.getTicketTemplates().getIndividualTicketPdfTemplateId().intValue());
        }
        if (updateData.getProviderPlanSettings() != null) {
            record.setConfiguracionplanproveedor(serializeProviderPlanSettings(updateData.getProviderPlanSettings()));
        }
        return record;
    }

    private static void updateSettingsDates(CpanelCanalEventoRecord record, UpdateEventChannelDTO updateData) {
        if (updateData.getSettings().getUseEventDates() != null) {
            record.setUsafechasevento((byte) (updateData.getSettings().getUseEventDates() ? 1 : 0));
        }
        if (!CommonUtils.isTrue(record.getUsafechasevento())) {
            if (updateData.getSettings().getReleaseDate() != null) {
                record.setFechapublicacion(Timestamp.from(updateData.getSettings().getReleaseDate().toInstant()));
            }
            if (updateData.getSettings().getSaleStartDate() != null) {
                record.setFechaventa(Timestamp.from(updateData.getSettings().getSaleStartDate().toInstant()));
            }
            if (updateData.getSettings().getSaleEndDate() != null) {
                record.setFechafin(Timestamp.from(updateData.getSettings().getSaleEndDate().toInstant()));
            }
            if (updateData.getSettings().getBookingStartDate() != null) {
                record.setFechainicioreserva(Timestamp.from(updateData.getSettings().getBookingStartDate().toInstant()));
            }
            if (updateData.getSettings().getBookingEndDate() != null) {
                record.setFechafinreserva(Timestamp.from(updateData.getSettings().getBookingEndDate().toInstant()));
            }
        }
    }

    private static String serializeProviderPlanSettings(ProviderPlanSettings settings) {
        if (settings == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(settings);
        } catch (Exception e) {
            LOGGER.error("Error serializing ProviderPlanSettings", e);
            return null;
        }
    }

    private static ProviderPlanSettings deserializeProviderPlanSettings(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, ProviderPlanSettings.class);
        } catch (Exception e) {
            LOGGER.error("Error deserializing ProviderPlanSettings from JSON: {}", json, e);
            return null;
        }
    }

}
