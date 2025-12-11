package es.onebox.event.seasontickets.service.changeseats;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.time.ZonedDateTime;

import static es.onebox.core.utils.common.CommonUtils.timestampToZonedDateTime;
import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;
import static es.onebox.event.common.utils.ConverterUtils.isByteAsATrue;

public class SeasonTicketChangeSeatsValidator {

    private SeasonTicketChangeSeatsValidator() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static void validateChangeSeatOnUpdateSeasonTicket(UpdateSeasonTicketRequestDTO body, CpanelSeasonTicketRecord cpanelSeasonTicketRecord, SessionRecord sessionRecord) {

        UpdateSeasonTicketChangeSeat changeSeat = body.getChangeSeat();
        Boolean allowChangeSeat = getField(body.getAllowChangeSeat(), cpanelSeasonTicketRecord.getAllowchangeseat());

        Boolean changeSeatEnabled;
        ZonedDateTime changeSeatStartingDate;
        ZonedDateTime changeSeatEndDate;
        Integer maxChangeSeatValue;

        if (changeSeat != null) {
            changeSeatEnabled = getField(changeSeat.getChangeSeatEnabled(), cpanelSeasonTicketRecord.getChangeseatenabled());
            changeSeatStartingDate = getField(changeSeat.getChangeSeatStartingDate(),
                    timestampToZonedDateTime(cpanelSeasonTicketRecord.getChangeseatinitdate()));
            changeSeatEndDate = getField(changeSeat.getChangeSeatEndDate(), timestampToZonedDateTime(cpanelSeasonTicketRecord.getChangeseatenddate()));
            maxChangeSeatValue = getField(changeSeat.getMaxChangeSeatValue(), cpanelSeasonTicketRecord.getMaxchangeseatvalue());
        } else {
            changeSeatEnabled = cpanelSeasonTicketRecord.getChangeseatenabled();
            changeSeatStartingDate = timestampToZonedDateTime(cpanelSeasonTicketRecord.getChangeseatinitdate());
            changeSeatEndDate = timestampToZonedDateTime(cpanelSeasonTicketRecord.getChangeseatenddate());
            maxChangeSeatValue = cpanelSeasonTicketRecord.getMaxchangeseatvalue();
        }

        Boolean enableChannels = getField(body.getEnableChannels(), isByteAsATrue(sessionRecord.getPublicado()));
        ZonedDateTime channelPublishingDate = getField(body.getChannelPublishingDate(), timestampToZonedDateTime(sessionRecord.getFechapublicacion()));

        // Change Seat fields are not allowed
        boolean anyRenewalFieldInformedOnUpdateRequest = changeSeat != null
                && (BooleanUtils.isTrue(changeSeat.getChangeSeatEnabled()) || changeSeat.getChangeSeatStartingDate() != null
                || changeSeat.getChangeSeatEndDate() != null || changeSeat.getMaxChangeSeatValue() != null);
        if (Boolean.FALSE.equals(allowChangeSeat) && anyRenewalFieldInformedOnUpdateRequest) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_CHANGE_SEAT_IS_NOT_ALLOWED);
        }

        // Incomplete change seat fields
        if (BooleanUtils.isTrue(changeSeatEnabled) && !ObjectUtils.allNotNull(changeSeatStartingDate, changeSeatEndDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }

        // Init date before end date
        if(changeSeatStartingDate != null && changeSeatEndDate != null && changeSeatStartingDate.isAfter(changeSeatEndDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_CHANGE_SEAT_DATES);
        }

        // Max change seat value must be 0 or above
        if(maxChangeSeatValue != null && maxChangeSeatValue < 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_CHANGE_SEAT_MAX_VALUE);
        }

        // Change Seat can't be enabled until publishing is not enabled
        if(!enableChannels && Boolean.TRUE.equals(changeSeatEnabled)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_CHANGE_SEAT_PUBLISHING_NOT_ENABLED);
        }

        // Change Seat date can't be before publishing date
        if(changeSeatStartingDate != null && channelPublishingDate != null && changeSeatStartingDate.isBefore(channelPublishingDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_CHANGE_SEAT_PUBLISHING_DATES);
        }
    }

    public static boolean isChangeSeatModified(UpdateSeasonTicketChangeSeat seasonTicketChangeSeat, CpanelSeasonTicketRecord cpanelSeasonTicketRecord) {
        return (seasonTicketChangeSeat.getChangeSeatEnabled() != null && !seasonTicketChangeSeat.getChangeSeatEnabled().equals(cpanelSeasonTicketRecord.getChangeseatenabled()))
                || (seasonTicketChangeSeat.getChangeSeatStartingDate() != null && !zonedDateTimeToTimestamp(seasonTicketChangeSeat.getChangeSeatStartingDate()).equals(cpanelSeasonTicketRecord.getChangeseatinitdate()))
                || (seasonTicketChangeSeat.getChangeSeatEndDate() != null && !zonedDateTimeToTimestamp(seasonTicketChangeSeat.getChangeSeatEndDate()).equals(cpanelSeasonTicketRecord.getChangeseatenddate()))
                || (seasonTicketChangeSeat.getMaxChangeSeatValueEnabled() != null && !seasonTicketChangeSeat.getMaxChangeSeatValueEnabled().equals(cpanelSeasonTicketRecord.getMaxchangeseatvalueenabled()))
                || (seasonTicketChangeSeat.getMaxChangeSeatValue() != null && !seasonTicketChangeSeat.getMaxChangeSeatValue().equals(cpanelSeasonTicketRecord.getMaxchangeseatvalue()));
    }

    private static <T> T getField(T updateField, T dbField) {
        if (updateField != null) {
            return updateField;
        }
        return dbField;
    }
}
