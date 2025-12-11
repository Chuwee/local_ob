package es.onebox.event.seasontickets.service.renewals;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewal;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.time.ZonedDateTime;

import static es.onebox.core.utils.common.CommonUtils.timestampToZonedDateTime;
import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;
import static es.onebox.event.common.utils.ConverterUtils.isByteAsATrue;

public class SeasonTicketRenewalsValidator {

    private SeasonTicketRenewalsValidator() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }


    public static void validateRenewalOnUpdateSeasonTicket(UpdateSeasonTicketRequestDTO body, CpanelSeasonTicketRecord cpanelSeasonTicketRecord, SessionRecord sessionRecord) {

        UpdateSeasonTicketRenewal renewal = body.getRenewal();
        Boolean allowRenewal = getField(body.getAllowRenewal(), cpanelSeasonTicketRecord.getAllowrenewal());

        Boolean renewalEnabled;
        ZonedDateTime renewalStartingDate;
        ZonedDateTime renewalEndDate;

        if (renewal != null) {
            renewalEnabled = getField(renewal.getRenewalEnabled(), cpanelSeasonTicketRecord.getRenewalenabled());
            renewalStartingDate = getField(renewal.getRenewalStartingDate(),
                    timestampToZonedDateTime(cpanelSeasonTicketRecord.getRenewalinitdate()));
            renewalEndDate = getField(renewal.getRenewalEndDate(), timestampToZonedDateTime(cpanelSeasonTicketRecord.getRenewalenddate()));
        } else {
            renewalEnabled = cpanelSeasonTicketRecord.getRenewalenabled();
            renewalStartingDate = timestampToZonedDateTime(cpanelSeasonTicketRecord.getRenewalinitdate());
            renewalEndDate = timestampToZonedDateTime(cpanelSeasonTicketRecord.getRenewalenddate());
        }

        Boolean enableChannels = getField(body.getEnableChannels(), isByteAsATrue(sessionRecord.getPublicado()));
        ZonedDateTime channelPublishingDate = getField(body.getChannelPublishingDate(), timestampToZonedDateTime(sessionRecord.getFechapublicacion()));

        // Renewal fields are not allowed
        boolean anyRenewalFieldInformedOnUpdateRequest = renewal != null
                && (BooleanUtils.isTrue(renewal.getRenewalEnabled()) || renewal.getRenewalStartingDate() != null
                        || renewal.getRenewalEndDate() != null);
        if (Boolean.FALSE.equals(allowRenewal) && anyRenewalFieldInformedOnUpdateRequest) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }

        // Incomplete renewal fields
        if (BooleanUtils.isTrue(renewalEnabled) && !ObjectUtils.allNotNull(renewalStartingDate, renewalEndDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_FIELDS_NOT_INFORMED);
        }

        // Init date before end date
        if(renewalStartingDate != null && renewalEndDate != null && renewalStartingDate.isAfter(renewalEndDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_INVALID_RENEWAL_DATES);
        }

        // Renewal can't be enabled until publishing is not enabled
        if(!enableChannels && Boolean.TRUE.equals(renewalEnabled)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_NOT_ENABLED);
        }

        // Renewal date can't be before publishing date
        if(renewalStartingDate != null && channelPublishingDate != null && renewalStartingDate.isBefore(channelPublishingDate)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_PUBLISHING_DATES);
        }
    }

    public static void validateRenewalSeasonTicket(SeasonTicketDTO renewalSeasonTicketDTO, SeasonTicketDTO originSeasonTicketDTO,
                                                   SeasonTicketInternalGenerationStatus originGenerationStatus,
                                                   SeasonTicketInternalGenerationStatus renewalGenerationStatus) {
        if(renewalSeasonTicketDTO.getId().equals(originSeasonTicketDTO.getId())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_SAME_SEASON_TICKETS);
        }

        // Allow renewal is required
        if(!Boolean.TRUE.equals(renewalSeasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }

        // Same venue
        Long seasonTicketVenueId = renewalSeasonTicketDTO.getVenues().stream().findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION))
                .getId();
        Long renewalSeasonTicketVenueId = originSeasonTicketDTO.getVenues().stream().findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION))
                .getId();
        if(!seasonTicketVenueId.equals(renewalSeasonTicketVenueId)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_VENUES);
        }

        // Same entity
        if(!renewalSeasonTicketDTO.getEntityId().equals(originSeasonTicketDTO.getEntityId())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY);
        }

        // Season ticket not in sale
        if(SeasonTicketStatusDTO.READY.equals(renewalSeasonTicketDTO.getStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE);
        }
        if(SeasonTicketStatusDTO.READY.equals(originSeasonTicketDTO.getStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE);
        }

        // Same member mandatory
        if(!renewalSeasonTicketDTO.getMemberMandatory().equals(originSeasonTicketDTO.getMemberMandatory())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_MEMBER_MANDATORY);
        }

        // Validate generation status
        if(!SeasonTicketInternalGenerationStatus.READY.equals(originGenerationStatus)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY);
        }

        if(!SeasonTicketInternalGenerationStatus.READY.equals(renewalGenerationStatus)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY);
        }
    }

    public static void validateRenewalSeasonTicket(SeasonTicketDTO renewalSeasonTicketDTO, ExternalEventDTO originExternalEvent,
                                                   SeasonTicketInternalGenerationStatus renewalGenerationStatus) {
        // Allow renewal is required
        if(!Boolean.TRUE.equals(renewalSeasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }

        // Same entity
        if(!renewalSeasonTicketDTO.getEntityId().equals(originExternalEvent.getEntityId().longValue())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY);
        }

        // Season ticket not in sale
        if(SeasonTicketStatusDTO.READY.equals(renewalSeasonTicketDTO.getStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_ON_SALE);
        }

        if(!SeasonTicketInternalGenerationStatus.READY.equals(renewalGenerationStatus)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY);
        }
    }

    public static boolean isRenewalModified(UpdateSeasonTicketRenewal seasonTicketRenewal, CpanelSeasonTicketRecord cpanelSeasonTicketRecord) {
        return (seasonTicketRenewal.getRenewalEnabled() != null && !seasonTicketRenewal.getRenewalEnabled().equals(cpanelSeasonTicketRecord.getRenewalenabled())) ||
                (seasonTicketRenewal.getRenewalStartingDate() != null && !zonedDateTimeToTimestamp(seasonTicketRenewal.getRenewalStartingDate()).equals(cpanelSeasonTicketRecord.getRenewalinitdate())) ||
                (seasonTicketRenewal.getRenewalEndDate() != null && !zonedDateTimeToTimestamp(seasonTicketRenewal.getRenewalEndDate()).equals(cpanelSeasonTicketRecord.getRenewalenddate())) ||
                (seasonTicketRenewal.getAutoRenewal() != null && !seasonTicketRenewal.getAutoRenewal().equals(cpanelSeasonTicketRecord.getAutorenewal()));
    }

    private static <T> T getField(T updateField, T dbField) {
        if (updateField != null) {
            return updateField;
        }
        return dbField;
    }
}
