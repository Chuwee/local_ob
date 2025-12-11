package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dto.ChangeSeatAllowedSessions;
import es.onebox.event.catalog.dto.ChangeSeatAmountType;
import es.onebox.event.catalog.dto.ChangeSeatRefundType;
import es.onebox.event.common.dto.BaseTicketTemplatesDTO;
import es.onebox.event.events.converter.ChannelConfigWhitelabelConverter;
import es.onebox.event.events.converter.EventConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.AccommodationsConfig;
import es.onebox.event.events.domain.eventconfig.ChangeSeatPrice;
import es.onebox.event.events.domain.eventconfig.EventChangeSeatConfig;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventExternalConfig;
import es.onebox.event.events.domain.eventconfig.EventPassbookConfig;
import es.onebox.event.events.domain.eventconfig.EventSessionSelection;
import es.onebox.event.events.domain.eventconfig.EventTransferTicketConfig;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import es.onebox.event.events.domain.eventconfig.PostBookingQuestionsConfig;
import es.onebox.event.events.domain.eventconfig.SessionCalendar;
import es.onebox.event.events.domain.eventconfig.SessionList;
import es.onebox.event.events.dto.AccommodationsConfigDTO;
import es.onebox.event.events.dto.AccommodationsVendor;
import es.onebox.event.events.dto.EventChangeSeatDTO;
import es.onebox.event.events.dto.EventExternalConfigDTO;
import es.onebox.event.events.dto.EventSeatSelectionDTO;
import es.onebox.event.events.dto.EventSessionSelectionDTO;
import es.onebox.event.events.dto.EventSessionSettingsDTO;
import es.onebox.event.events.dto.EventTransferTicketDTO;
import es.onebox.event.events.dto.EventVenueViewConfigDTO;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.event.events.dto.SessionCalendarDTO;
import es.onebox.event.events.dto.SessionListDTO;
import es.onebox.event.events.enums.CardDesignType;
import es.onebox.event.events.enums.ChangeSessionType;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.MediaType;
import es.onebox.event.events.enums.SessionCalendarSelectType;
import es.onebox.event.events.enums.SessionCalendarType;
import es.onebox.event.events.enums.SessionSelectType;
import es.onebox.event.events.postbookingquestions.enums.EventChannelsPBQType;
import es.onebox.event.exception.MsEventErrorCode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventConfigService {

    private final EventConfigCouchDao eventConfigCouchDao;

    @Autowired
    public EventConfigService(EventConfigCouchDao eventConfigCouchDao) {
        this.eventConfigCouchDao = eventConfigCouchDao;
    }

    public EventConfig getEventConfig(Long eventId) {
        return eventConfigCouchDao.get(String.valueOf(eventId));
    }

    public void storeEventConfig(Long eventId, EventConfig eventConfig) {
        eventConfigCouchDao.upsert(String.valueOf(eventId), eventConfig);
    }

    public EventVenueViewConfigDTO getEventVenueViewConfig(Long eventId) {
        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));
        return extractEventVenueViewConfig(eventConfig);

    }

    public static EventVenueViewConfigDTO extractEventVenueViewConfig(EventConfig eventConfig) {
        EventVenueViewConfigDTO out = new EventVenueViewConfigDTO();
        if (eventConfig != null) {
            out.setVenue3dId(eventConfig.getVenue3dId());
            out.setInteractiveVenueType(eventConfig.getInteractiveVenueType());
            out.setUse3dVenueModule(eventConfig.isUse3dVenueModule());
            out.setUse3dVenueModuleV2(eventConfig.isUse3dVenueModuleV2());
            out.setUseVenue3dView(eventConfig.isUseVenue3dView());
            out.setUseSector3dView(eventConfig.isUseSector3dView());
            out.setUseSeat3dView(eventConfig.isUseSeat3dView());
        }
        return out;
    }

    public void updateEventVenueViewConfig(Long eventId, EventVenueViewConfigDTO eventVenueViewConfigDTO) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);

        if (StringUtils.isNotBlank(eventVenueViewConfigDTO.getVenue3dId())) {
            eventConfig.setVenue3dId(eventVenueViewConfigDTO.getVenue3dId());
        }
        eventConfig.setInteractiveVenueType(eventVenueViewConfigDTO.getInteractiveVenueType());
        eventConfig.setUse3dVenueModule(eventVenueViewConfigDTO.isUse3dVenueModule());
        eventConfig.setUse3dVenueModuleV2(eventVenueViewConfigDTO.isUse3dVenueModuleV2());
        eventConfig.setUseVenue3dView(eventVenueViewConfigDTO.isUseVenue3dView());
        eventConfig.setUseSector3dView(eventVenueViewConfigDTO.isUseSector3dView());
        eventConfig.setUseSeat3dView(eventVenueViewConfigDTO.isUseSeat3dView());

        eventConfigCouchDao.upsert(String.valueOf(eventId), eventConfig);
    }

    public void updateCustomSelectTemplate(Long eventId, String customSelectTemplate) {
        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));
        if (eventConfig == null) {
            eventConfig = new EventConfig();
            eventConfig.setCustomSelectTemplate(customSelectTemplate);
        }

        eventConfigCouchDao.upsert(String.valueOf(eventId), eventConfig);
    }

    public void updateEventPassbookConfig(Long eventId, BaseTicketTemplatesDTO template) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);

        if (eventConfig.getEventPassbookConfig() == null) {
            eventConfig.setEventPassbookConfig(new EventPassbookConfig());
        }

        EventPassbookConfig eventPassbookConfig = eventConfig.getEventPassbookConfig();

        if (StringUtils.isNotBlank(template.getGroupInvitationPassbookTemplateCode())) {
            eventPassbookConfig.setGroupInvitationPassbookTemplate(template.getGroupInvitationPassbookTemplateCode());
        }
        if (StringUtils.isNotBlank(template.getGroupTicketPassbookTemplateCode())) {
            eventPassbookConfig.setGroupPassbookTemplate(template.getGroupTicketPassbookTemplateCode());
        }
        if (StringUtils.isNotBlank(template.getIndividualInvitationPassbookTemplateCode())) {
            eventPassbookConfig.setIndividualInvitationPassbookTemplate(template.getIndividualInvitationPassbookTemplateCode());
        }
        if (StringUtils.isNotBlank(template.getIndividualTicketPassbookTemplateCode())) {
            eventPassbookConfig.setIndividualPassbookTemplate(template.getIndividualTicketPassbookTemplateCode());
        }
        if (StringUtils.isNotBlank(template.getSessionPackPassbookTemplateCode())) {
            eventPassbookConfig.setSessionPackPassbookTemplate(template.getSessionPackPassbookTemplateCode());
        }
        eventConfigCouchDao.upsert(String.valueOf(eventId), eventConfig);
    }

    public static AccommodationsConfigDTO extractEventAccommodationsConfig(EventConfig eventConfig) {
        if (eventConfig != null && eventConfig.getAccommodationsConfig() != null) {
            AccommodationsConfig in = eventConfig.getAccommodationsConfig();
            AccommodationsConfigDTO dto = new AccommodationsConfigDTO();
            dto.setEnabled(in.getEnabled());
            dto.setVendor(AccommodationsVendor.valueOf(in.getVendor().name()));
            dto.setValue(in.getValue());
            return dto;
        }
        return null;
    }


    public void updateEventAccommodationsConfig(Long eventId, AccommodationsConfigDTO dto) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        if (eventConfig.getAccommodationsConfig() == null) {
            eventConfig.setAccommodationsConfig(new AccommodationsConfig());
        }
        eventConfig.getAccommodationsConfig().setEnabled(dto.getEnabled());
        if (dto.getVendor() != null) {
            eventConfig.getAccommodationsConfig().setVendor(es.onebox.event.events.domain.eventconfig.AccommodationsVendor.valueOf(dto.getVendor().name()));
        }
        if (dto.getValue() != null) {
            eventConfig.getAccommodationsConfig().setValue(dto.getValue());
        }

        this.storeEventConfig(eventId, eventConfig);
    }

    public void updateEventWhitelabelSettings(Long eventId, Boolean isSupraEventUpdating, Boolean isSupraEvent, EventWhitelabelSettingsDTO in) {
        EventConfig ec = eventConfigCouchDao.getOrInitEventConfig(eventId);

        if (isSupraEventUpdating) {
            EventWhitelabelSettingsDTO wl = ChannelConfigWhitelabelConverter.getDefaultUiSettings(isSupraEvent);
            ec.setWhitelabelSettings(ChannelConfigWhitelabelConverter.toDB(wl, ec.getWhitelabelSettings()));
        } else {
            ec.setWhitelabelSettings(ChannelConfigWhitelabelConverter.toDB(in, ec.getWhitelabelSettings()));
        }

        this.storeEventConfig(eventId, ec);
    }

    public void updateEventExternalConfig(Long eventId, EventExternalConfigDTO eventExternalConfig) {
        EventConfig ec = eventConfigCouchDao.getOrInitEventConfig(eventId);
        if (ec.getEventExternalConfig() == null) {
            ec.setEventExternalConfig(new EventExternalConfig());
        }
        ec.getEventExternalConfig().setDigitalTicketMode(eventExternalConfig.getDigitalTicketMode());
        this.storeEventConfig(eventId, ec);
    }

    public void updateEventChangeSeatsConfig(Long eventId, Boolean allowChangeSeat, EventChangeSeatDTO changeSeat) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        EventChangeSeatConfig changeSeatConfig = EventConverter.toEventChangeSeat(changeSeat);

        changeSeatConfig.setAllowChangeSeat(allowChangeSeat);
        ChangeSeatPrice price = changeSeatConfig.getNewTicketSelection().getPrice();

        if (ChangeSeatAmountType.GREATER_OR_EQUAL.equals(price.getType()) && price.getRefund() != null) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (allowChangeSeat && changeSeatConfig.getReallocationChannel().getId() == null) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (ChangeSeatAmountType.ANY.equals(price.getType()) && (price.getRefund() == null || price.getRefund().getType() == null)) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (price.getRefund() != null && price.getRefund().getType() != null &&
                ChangeSeatRefundType.NONE.equals(price.getRefund().getType()) && price.getRefund().getVoucherExpiry() != null) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (price.getRefund() != null && price.getRefund().getVoucherExpiry() != null &&
                (price.getRefund().getVoucherExpiry().getEnabled() == true && price.getRefund().getVoucherExpiry().getExpiryTime() == null)) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (changeSeatConfig.getNewTicketSelection() != null && changeSeatConfig.getNewTicketSelection().getSameDateOnly() != null
                && !ChangeSeatAllowedSessions.DIFFERENT.equals(changeSeatConfig.getNewTicketSelection().getAllowedSessions())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_SESSION_DATE_CHANGE_SEAT_CONFIG);
        }

        eventConfig.setEventChangeSeatConfig(changeSeatConfig);
        storeEventConfig(eventId, eventConfig);
    }

    public void updateEventConfigPostBookingQuestions(Long eventId, Boolean enabled, EventChannelsPBQType type) {

        EventConfig ec = eventConfigCouchDao.getOrInitEventConfig(eventId);
        if (ec.getPostBookingQuestionsConfig() == null) {
            ec.setPostBookingQuestionsConfig(new PostBookingQuestionsConfig());
        }
        ec.getPostBookingQuestionsConfig().setEnabled(enabled);
        ec.getPostBookingQuestionsConfig().setType(type);
        this.storeEventConfig(eventId, ec);
    }

    public static EventWhitelabelSettingsDTO extractEventWhitelabelSettings(Boolean isSupraEvent, EventConfig eventConfig, ChannelSubtype channelSubtype) {
        if (eventConfig == null) return getWhiteLabelSettingsDefault(isSupraEvent);
        return extractEventWhitelabelSettings(isSupraEvent, eventConfig.getWhitelabelSettings(), channelSubtype);
    }

    public static EventExternalConfigDTO extractEventExternalConfig(EventConfig eventConfig) {
        if (eventConfig == null || eventConfig.getEventExternalConfig() == null) {
            return null;
        }
        EventExternalConfigDTO result = new EventExternalConfigDTO();
        result.setDigitalTicketMode(eventConfig.getEventExternalConfig().getDigitalTicketMode());
        return result;
    }


    public static EventWhitelabelSettingsDTO extractEventWhitelabelSettings(Boolean isSupraEvent, EventWhitelabelSettings in, ChannelSubtype channelSubtype) {
        if (in == null) return getWhiteLabelSettingsDefault(isSupraEvent);

        EventWhitelabelSettingsDTO outWithLabelSettings = new EventWhitelabelSettingsDTO();
        EventSessionSelectionDTO outSessionSelection = null;
        if (in.getSessionSelection() != null) {
            EventSessionSelection inSs = in.getSessionSelection();
            outSessionSelection = new EventSessionSelectionDTO();
            outSessionSelection.setRestrictType(getRestrictType(inSs, channelSubtype));
            outSessionSelection.setShowAvailability(inSs.getShowAvailability());
            outSessionSelection.setType(inSs.getType());
            if (inSs.getCalendar() != null) {
                SessionCalendarDTO calendar = new SessionCalendarDTO();
                SessionCalendarType calendarType = Objects.equals(inSs.getCalendar().getCalendarType(), SessionCalendarType.SLIDER) ?
                        SessionCalendarType.WEEKLY : inSs.getCalendar().getCalendarType();
                calendar.setType(calendarType);
                calendar.setCalendarSelectType(inSs.getCalendar().getSessionSelectType());
                calendar.setEnabled(inSs.getCalendar().getEnabled());
                outSessionSelection.setCalendar(calendar);
            }
            if (inSs.getList() != null) {
                SessionListDTO list = new SessionListDTO();
                list.setContainsImage(inSs.getList().getListContainsImage());
                list.setMedia(inSs.getList().getMedia());
                list.setCardDesignType(inSs.getList().getCardDesignType());
                list.setEnabled(inSs.getList().getEnabled());
                outSessionSelection.setList(list);
            }
        }
        EventSeatSelectionDTO outSeatSelection = null;
        if (in.getSeatSelection() != null) {
            outSeatSelection = new EventSeatSelectionDTO();
            outSeatSelection.setShowAvailability(in.getSessionSelection().getShowAvailability());
            outSeatSelection.setRestrictType(in.getSeatSelection().getRestrictType());
            outSeatSelection.setType(in.getSeatSelection().getType());
            outSeatSelection.setChangeSessionSelectType(in.getSeatSelection().getChangeSessionSelectType());
            SessionCalendar sCalendar = in.getSeatSelection().getCalendar();
            SessionList sList = in.getSeatSelection().getList();
            if (sCalendar != null) {
                SessionCalendarDTO outCalendar = new SessionCalendarDTO();
                SessionCalendarType calendarType = SessionCalendarType.SLIDER.equals(sCalendar.getCalendarType()) ?
                        SessionCalendarType.WEEKLY : sCalendar.getCalendarType();
                outCalendar.setCalendarSelectType(sCalendar.getSessionSelectType());
                outCalendar.setType(calendarType);
                outCalendar.setEnabled(sCalendar.getEnabled());
                outSeatSelection.setCalendar(outCalendar);
            }
            if (sList != null) {
                SessionListDTO outList = new SessionListDTO();
                outList.setCardDesignType(sList.getCardDesignType());
                outList.setMedia(sList.getMedia());
                outList.setContainsImage(sList.getListContainsImage());
                outList.setEnabled(sList.getEnabled());
                outSeatSelection.setList(outList);
            }
        }
        EventSessionSettingsDTO outSettings = null;
        if (in.getSessionSettings() != null) {
            outSettings = new EventSessionSettingsDTO();
            outSettings.setShowPriceFrom(in.getSessionSettings().getShowPriceFrom());
        }
        outWithLabelSettings.setSessionSettings(outSettings);
        outWithLabelSettings.setSeatSelection(outSeatSelection);
        outWithLabelSettings.setSessionSelection(outSessionSelection);
        return outWithLabelSettings;
    }

    public static EventWhitelabelSettingsDTO getWhiteLabelSettingsDefault(Boolean isSupraEvent) {
        EventSessionSelectionDTO ss = getEventSessionSelectionDTO(isSupraEvent);

        SessionCalendarDTO sCalendar = Boolean.TRUE.equals(isSupraEvent) ? null :
                new SessionCalendarDTO(SessionCalendarType.WEEKLY, SessionCalendarSelectType.BY_HOUR, true);
        SessionSelectType sessionSelectType = Boolean.TRUE.equals(isSupraEvent) ? SessionSelectType.LIST : SessionSelectType.CALENDAR;
        EventSeatSelectionDTO seatSelection = new EventSeatSelectionDTO(ChangeSessionType.NONE, true, sCalendar, null,
                sessionSelectType, true);
        EventSessionSettingsDTO settings = new EventSessionSettingsDTO(true);
        return new EventWhitelabelSettingsDTO(ss, settings, seatSelection);
    }

    private static EventSessionSelectionDTO getEventSessionSelectionDTO(Boolean isSupraEvent) {
        SessionListDTO list = Boolean.TRUE.equals(isSupraEvent) ?
                new SessionListDTO(Boolean.TRUE, MediaType.IMAGE, CardDesignType.VERTICAL, true) :
                new SessionListDTO(Boolean.FALSE, MediaType.NONE, CardDesignType.HORIZONTAL, true);
        SessionCalendarDTO calendar = Boolean.TRUE.equals(isSupraEvent) ? null :
                new SessionCalendarDTO(SessionCalendarType.WEEKLY, SessionCalendarSelectType.BY_HOUR, true);
        Boolean restrictType = Boolean.TRUE.equals(isSupraEvent) ? Boolean.TRUE : Boolean.FALSE;
        return new EventSessionSelectionDTO(SessionSelectType.LIST, restrictType, calendar, list, true);
    }

    public void updateEventTransferTicketConfig(Long eventId, Boolean allowTransferTicket, EventTransferTicketDTO transfer) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);

        EventTransferTicketConfig eventTransferTicketConfig = new EventTransferTicketConfig();
        eventTransferTicketConfig.setTransferPolicy(transfer.getTransferPolicy());
        eventTransferTicketConfig.setAllowTransferTicket(allowTransferTicket);
        eventTransferTicketConfig.setMaxTicketTransfers(transfer.getMaxTicketTransfers());
        eventTransferTicketConfig.setRecoveryTicketMaxDelayTime(transfer.getRecoveryTicketMaxDelayTime());
        eventTransferTicketConfig.setEnableMaxTicketTransfers(transfer.getEnableMaxTicketTransfers());
        eventTransferTicketConfig.setTransferTicketMinDelayTime(transfer.getTransferTicketMinDelayTime());
        eventTransferTicketConfig.setTransferTicketMaxDelayTime(transfer.getTransferTicketMaxDelayTime());
        eventTransferTicketConfig.setRestrictTransferBySessions(transfer.getRestrictTransferBySessions());
        eventTransferTicketConfig.setAllowedTransferSessions(transfer.getAllowedTransferSessions());
        if (transfer.getAllowMultipleTransfers() == null && eventConfig.getEventTransferTicketConfig() != null
                && eventConfig.getEventTransferTicketConfig().getAllowMultipleTransfers() != null) {
            eventTransferTicketConfig.setAllowMultipleTransfers(eventConfig.getEventTransferTicketConfig().getAllowMultipleTransfers());
        } else {
            eventTransferTicketConfig.setAllowMultipleTransfers(BooleanUtils.isTrue(transfer.getAllowMultipleTransfers()));
        }

        eventConfig.setEventTransferTicketConfig(eventTransferTicketConfig);
        storeEventConfig(eventId, eventConfig);
    }

    public void updatePhoneVerificationRequired(Long eventId, Boolean phoneVerificationRequired, Boolean attendantVerificationRequired) {
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        if (phoneVerificationRequired != null) {
            eventConfig.setPhoneVerificationRequired(phoneVerificationRequired);
        }
        if (attendantVerificationRequired != null) {
            eventConfig.setAttendantVerificationRequired(attendantVerificationRequired);
        }
        storeEventConfig(eventId, eventConfig);
    }

    private static Boolean getRestrictType(EventSessionSelection inSs, ChannelSubtype channelSubtype) {

        if (ChannelSubtype.BOX_OFFICE_ONEBOX.equals(channelSubtype)) {
            return true;
        }
        return inSs.getRestrictType();
    }

}
