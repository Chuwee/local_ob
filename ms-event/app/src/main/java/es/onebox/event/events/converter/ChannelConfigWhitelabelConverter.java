package es.onebox.event.events.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.domain.eventconfig.EventSeatSelection;
import es.onebox.event.events.domain.eventconfig.EventSessionSelection;
import es.onebox.event.events.domain.eventconfig.EventSessionSettings;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import es.onebox.event.events.domain.eventconfig.SessionCalendar;
import es.onebox.event.events.domain.eventconfig.SessionList;
import es.onebox.event.events.dto.EventSeatSelectionDTO;
import es.onebox.event.events.dto.EventSessionSelectionDTO;
import es.onebox.event.events.dto.EventSessionSettingsDTO;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.event.events.dto.SessionCalendarDTO;
import es.onebox.event.events.dto.SessionListDTO;
import es.onebox.event.events.enums.CardDesignType;
import es.onebox.event.events.enums.ChangeSessionType;
import es.onebox.event.events.enums.MediaType;
import es.onebox.event.events.enums.SessionCalendarSelectType;
import es.onebox.event.events.enums.SessionCalendarType;
import es.onebox.event.events.enums.SessionSelectType;
import es.onebox.event.exception.MsEventErrorCode;

import java.util.Objects;


public class ChannelConfigWhitelabelConverter {

    private ChannelConfigWhitelabelConverter() {
    }

    public static EventWhitelabelSettings toDB(EventWhitelabelSettingsDTO in, EventWhitelabelSettings record) {
        if (in == null) return record;
        EventWhitelabelSettings out = record == null ? new EventWhitelabelSettings() : record;
        out.setSessionSelection(toDB(in.getSessionSelection(), record));
        out.setSeatSelection(toDB(in.getSeatSelection(), record));
        out.setSessionSettings(toDB(in.getSessionSettings(), record));
        return out;
    }

    private static EventSessionSelection toDB(EventSessionSelectionDTO in, EventWhitelabelSettings record) {
        boolean recordNotNull = record != null && record.getSessionSelection() != null;
        if (in == null) return recordNotNull ? record.getSessionSelection() : null;

        EventSessionSelection out = recordNotNull ? record.getSessionSelection() : new EventSessionSelection();
        ConverterUtils.updateField(out::setType, in.getType());

        Boolean calendarNotEnabled = in.getCalendar() == null || Boolean.FALSE.equals(in.getCalendar().getEnabled());
        Boolean listNotEnabled = in.getList() == null || Boolean.FALSE.equals(in.getList().getEnabled());

        if (listNotEnabled && calendarNotEnabled) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_WHITELABEL_UI_SETTINGS_CONFLICT);
        } else if (listNotEnabled || calendarNotEnabled){
            out.setRestrictType(true);
        } else {
            out.setRestrictType(in.getRestrictType());
        }

        if (in.getCalendar() != null) {
            out.setCalendar(toDB(in.getCalendar(), record));
        }
        if (in.getList() != null) {
            out.setList(toDB(in.getList(), record));
        }
        if(in.getShowAvailability() != null){
            out.setShowAvailability(in.getShowAvailability());
        }
        return out;
    }

    private static SessionCalendar toDB(SessionCalendarDTO in, EventWhitelabelSettings record) {
        boolean recordNotNull = record != null && record.getSessionSelection() != null && record.getSessionSelection().getCalendar() != null;
        if (in == null) return recordNotNull ? record.getSessionSelection().getCalendar() : null;

        SessionCalendar out = recordNotNull ? record.getSessionSelection().getCalendar() : new SessionCalendar();
        return updateSessionCalendar(in, out);
    }

    private static SessionList toDB(SessionListDTO in, EventWhitelabelSettings record) {
        boolean recordNotNull = record != null && record.getSessionSelection() != null && record.getSessionSelection().getList() != null;
        if (in == null) return recordNotNull ? record.getSessionSelection().getList() : null;

        SessionList out = recordNotNull ? record.getSessionSelection().getList() : new SessionList();
        return updateSessionList(in, out);
    }

    private static EventSeatSelection toDB(EventSeatSelectionDTO in, EventWhitelabelSettings record){
        boolean recordNotNull = record != null && record.getSeatSelection() != null;
        if (in == null) return recordNotNull ? record.getSeatSelection() : null;

        EventSeatSelection out = recordNotNull ? record.getSeatSelection() : new EventSeatSelection();
        ConverterUtils.updateField(out::setChangeSessionSelectType, in.getChangeSessionSelectType());
        ConverterUtils.updateField(out::setType, in.getType());

        out.setChangeSessionSelectType(in.getChangeSessionSelectType());

        Boolean calendarNotEnabled = in.getCalendar() == null || Boolean.FALSE.equals(in.getCalendar().getEnabled());
        Boolean listNotEnabled = in.getList() == null || Boolean.FALSE.equals(in.getList().getEnabled());
        if (Objects.equals(in.getChangeSessionSelectType(), ChangeSessionType.ALLOW) && (listNotEnabled && calendarNotEnabled)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_WHITELABEL_UI_SETTINGS_CONFLICT);
        } else if (listNotEnabled || calendarNotEnabled){
            out.setRestrictType(true);
        } else {
            out.setRestrictType(in.getRestrictType());
        }

        if (in.getCalendar() != null) {
            assert record != null;
            out.setCalendar(sSCalendarDB(in.getCalendar(), record));
        }
        if (in.getList() != null) {
            assert record != null;
            out.setList(sSListDB(in.getList(), record));
        }
        if(in.getShowAvailability() != null){
            out.setShowAvailability(in.getShowAvailability());
        }

        return out;
    }
    private static EventSessionSettings toDB(EventSessionSettingsDTO in, EventWhitelabelSettings record){
        boolean recordNotNull = record != null && record.getSessionSettings() != null;
        if (in == null) return recordNotNull ? record.getSessionSettings() : null;

        EventSessionSettings out = recordNotNull ? record.getSessionSettings() : new EventSessionSettings();
        ConverterUtils.updateField(out::setShowPriceFrom, in.getShowPriceFrom());
        return out;
    }
    private static SessionCalendar sSCalendarDB(SessionCalendarDTO in, EventWhitelabelSettings record) {
        boolean recordNotNull = record != null && record.getSeatSelection() != null && record.getSeatSelection().getCalendar() != null;
        if (in == null) return recordNotNull ? record.getSeatSelection().getCalendar() : null;

        SessionCalendar out = recordNotNull ? record.getSeatSelection().getCalendar() : new SessionCalendar();
        return updateSessionCalendar(in,out);
    }
    private static SessionList sSListDB(SessionListDTO in, EventWhitelabelSettings record) {
        boolean recordNotNull = record != null && record.getSeatSelection() != null && record.getSeatSelection().getList() != null;
        if (in == null) return recordNotNull ? record.getSeatSelection().getList() : null;

        SessionList out = recordNotNull ? record.getSeatSelection().getList(): new SessionList();
        return updateSessionList(in, out);
    }

    private static SessionList updateSessionList(SessionListDTO in, SessionList out) {
        ConverterUtils.updateField(out::setListContainsImage, in.getContainsImage());
        ConverterUtils.updateField(out::setCardDesignType, in.getCardDesignType());
        ConverterUtils.updateField(out::setMedia, in.getMedia());
        ConverterUtils.updateField(out::setEnabled, in.getEnabled());
        return out;
    }
    private static SessionCalendar updateSessionCalendar(SessionCalendarDTO in, SessionCalendar out) {
        ConverterUtils.updateField(out::setSessionSelectType, in.getCalendarSelectType());
        ConverterUtils.updateField(out::setCalendarType, in.getType());
        ConverterUtils.updateField(out::setEnabled, in.getEnabled());
        return out;
    }

    public static EventWhitelabelSettingsDTO getDefaultUiSettings(Boolean isSupraEvent){
        EventWhitelabelSettingsDTO out = new EventWhitelabelSettingsDTO();
        EventSessionSelectionDTO sessionSelection = new EventSessionSelectionDTO();
        EventSeatSelectionDTO seatSelection = new EventSeatSelectionDTO();
        EventSessionSettingsDTO sessionSettings = new EventSessionSettingsDTO(true);
        out.setSessionSettings(sessionSettings);

        if(isSupraEvent){
            sessionSelection.setType(SessionSelectType.LIST);
            sessionSelection.setShowAvailability(true);
            sessionSelection.setRestrictType(true);
            SessionListDTO list = new SessionListDTO(true, MediaType.IMAGE, CardDesignType.VERTICAL,true);
            sessionSelection.setList(list);
            sessionSelection.setCalendar(null);
            seatSelection.setChangeSessionSelectType(ChangeSessionType.ALLOW);
            seatSelection.setShowAvailability(true);
            seatSelection.setCalendar(null);
            seatSelection.setRestrictType(true);
            seatSelection.setType(SessionSelectType.LIST);
            seatSelection.setList(list);
            out.setSessionSelection(sessionSelection);
            out.setSeatSelection(seatSelection);

        }else{
            sessionSelection.setType(SessionSelectType.LIST);
            sessionSelection.setShowAvailability(true);
            sessionSelection.setRestrictType(false);
            SessionListDTO list = new SessionListDTO(false, MediaType.NONE, CardDesignType.HORIZONTAL,true);
            sessionSelection.setList(list);
            SessionCalendarDTO calendar = new SessionCalendarDTO(SessionCalendarType.WEEKLY,SessionCalendarSelectType.BY_HOUR,true);
            sessionSelection.setCalendar(calendar);
            seatSelection.setChangeSessionSelectType(ChangeSessionType.ALLOW);
            seatSelection.setShowAvailability(true);
            seatSelection.setCalendar(calendar);
            seatSelection.setRestrictType(true);
            seatSelection.setType(SessionSelectType.CALENDAR);
            seatSelection.setList(null);
            out.setSessionSelection(sessionSelection);
            out.setSeatSelection(seatSelection);
        }

        return out;
    }
}
