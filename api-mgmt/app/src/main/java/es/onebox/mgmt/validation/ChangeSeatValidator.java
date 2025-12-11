package es.onebox.mgmt.validation;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangedSeatQuota;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangedSeatStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.LimitChangeSeatQuotas;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangedSeatQuotaDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.LimitChangeSeatQuotasDTO;
import es.onebox.mgmt.seasontickets.enums.ChangedSeatStatusDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.time.ZonedDateTime;
import java.util.List;

public class ChangeSeatValidator {

    private ChangeSeatValidator() {
    }

    public static boolean isChangeSeatModified(UpdateSeasonTicketChangeSeatDTO updateChangeSeat, SeasonTicketChangeSeat changeSeat) {
        return (updateChangeSeat.getEnable() != null && !updateChangeSeat.getEnable().equals(changeSeat.getChangeSeatEnabled()))
                || (updateChangeSeat.getStartDate() != null && !(updateChangeSeat.getStartDate()).equals(changeSeat.getChangeSeatStartingDate()))
                || (updateChangeSeat.getEndDate() != null && !(updateChangeSeat.getEndDate()).equals(changeSeat.getChangeSeatEndDate()))
                || (updateChangeSeat.getEnableMaxValue() != null && !updateChangeSeat.getEnableMaxValue().equals(changeSeat.getMaxChangeSeatValueEnabled()))
                || (updateChangeSeat.getMaxValue() != null && !updateChangeSeat.getMaxValue().equals(changeSeat.getMaxChangeSeatValue()))
                || isChangedSeatQuotaModified(updateChangeSeat.getChangedSeatQuota(), changeSeat.getChangedSeatQuota())
                || isChangedSeatStatusModified(updateChangeSeat.getChangedSeatStatus(), changeSeat.getChangedSeatStatus())
                || (updateChangeSeat.getChangedSeatBlockReasonId() != null && !updateChangeSeat.getChangedSeatBlockReasonId().equals(changeSeat.getChangedSeatBlockReasonId()))
                || (updateChangeSeat.getFixedSurcharge() != null && !updateChangeSeat.getFixedSurcharge().equals(changeSeat.getFixedSurcharge()))
                || isLimitChangeSeatQuotasModified(updateChangeSeat.getLimitChangeSeatQuotas(), changeSeat.getLimitChangeSeatQuotas());
    }

    public static void validateChangeSeatOnUpdate(UpdateSeasonTicketChangeSeatDTO updateChangeSeat, SeasonTicketChangeSeat changeSeat, SeasonTicket seasonTicket, List<Long> quotas) {
        if (updateChangeSeat == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }

        boolean enableChangeSeat = validateAndGetEnableChangeSeat(updateChangeSeat, changeSeat, seasonTicket);
        if (enableChangeSeat) {
            validateChangeSeatDates(updateChangeSeat, changeSeat, seasonTicket);
        }
        validateMaxValue(updateChangeSeat, changeSeat);
        if (quotaIsUpdated(updateChangeSeat)) {
            validateQuota(updateChangeSeat);
        }
        if (updateChangeSeat.getLimitChangeSeatQuotas() != null) {
            validateLimitQuotas(updateChangeSeat.getLimitChangeSeatQuotas(), quotas);
        }

    }

    private static boolean quotaIsUpdated(UpdateSeasonTicketChangeSeatDTO updateChangeSeat) {
        return ObjectUtils.anyNotNull(updateChangeSeat.getChangedSeatQuota(), updateChangeSeat.getChangedSeatStatus(), updateChangeSeat.getChangedSeatBlockReasonId());
    }

    private static void validateMaxValue(UpdateSeasonTicketChangeSeatDTO updateChangeSeat, SeasonTicketChangeSeat changeSeat) {
        Boolean enableMaxSeatValue;
        Integer maxChangeSeatValue;
        if (ObjectUtils.anyNotNull(updateChangeSeat.getEnableMaxValue(), updateChangeSeat.getMaxValue())) {
            enableMaxSeatValue = updateChangeSeat.getEnableMaxValue();
            maxChangeSeatValue = updateChangeSeat.getMaxValue();
        } else {
            enableMaxSeatValue = changeSeat.getMaxChangeSeatValueEnabled();
            maxChangeSeatValue = changeSeat.getMaxChangeSeatValue();
        }
        // Max change seat can't be null if change seat is enabled
        if (BooleanUtils.isTrue(enableMaxSeatValue) && maxChangeSeatValue == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_INVALID_CHANGE_SEAT_MAX_VALUE);
        }
    }

    private static void validateChangeSeatDates(UpdateSeasonTicketChangeSeatDTO updateChangeSeat, SeasonTicketChangeSeat changeSeat, SeasonTicket seasonTicket) {
        ZonedDateTime channelPublishingDate = seasonTicket.getChannelPublishingDate();
        ZonedDateTime changeSeatStartingDate = getField(updateChangeSeat.getStartDate(), changeSeat.getChangeSeatStartingDate());
        ZonedDateTime changeSeatEndDate = getField(updateChangeSeat.getEndDate(), changeSeat.getChangeSeatEndDate());

        // Incomplete change seat fields
        if (ObjectUtils.anyNull(channelPublishingDate, changeSeatStartingDate, changeSeatEndDate)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }

        // Change Seat date can't be before publishing date
        if (changeSeatStartingDate.isBefore(channelPublishingDate)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_PUBLISHING_DATES);
        }

        // Init date before end date
        if (changeSeatStartingDate.isAfter(changeSeatEndDate)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_INVALID_CHANGE_SEAT_DATES);
        }
    }

    private static boolean validateAndGetEnableChangeSeat(UpdateSeasonTicketChangeSeatDTO updateChangeSeat,
                                                 SeasonTicketChangeSeat changeSeat, SeasonTicket seasonTicket) {
        boolean enableChangeSeat;
        if (updateChangeSeat.getEnable() != null) {
            enableChangeSeat = updateChangeSeat.getEnable();
        } else if (seasonTicket.getChangeSeat() != null) {
            enableChangeSeat = changeSeat.getChangeSeatEnabled();
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }

        boolean allowChangeSeat = BooleanUtils.isTrue(seasonTicket.getAllowChangeSeat());
        if (enableChangeSeat && !allowChangeSeat) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_IS_NOT_ALLOWED);
        }
        // Change Seat can't be enabled until publishing is not enabled
        if (enableChangeSeat && BooleanUtils.isNotTrue(seasonTicket.getEnableChannels())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_PUBLISHING_NOT_ENABLED);
        }
        return enableChangeSeat;
    }

    private static void validateQuota(UpdateSeasonTicketChangeSeatDTO updateChangeSeat, SeasonTicketChangeSeat changeSeat) {
        Boolean enableQuota;
        Integer quotaId;
        String status = null;
        Integer blockReasonId;
        ChangedSeatQuotaDTO updateChangedSeatQuota = updateChangeSeat.getChangedSeatQuota();
        ChangedSeatQuota changedSeatQuota = changeSeat.getChangedSeatQuota();

        if (changedSeatQuota != null) {
            enableQuota = changedSeatQuota.getEnable();
            quotaId = changedSeatQuota.getId();
            blockReasonId = changeSeat.getChangedSeatBlockReasonId();
            if (changeSeat.getChangedSeatStatus() != null) {
                status = changeSeat.getChangedSeatStatus().name();
            }
        } else if (updateChangedSeatQuota != null) {
            enableQuota = updateChangedSeatQuota.getEnable();
            quotaId = updateChangedSeatQuota.getId();
            blockReasonId = updateChangeSeat.getChangedSeatBlockReasonId();
            if (updateChangeSeat.getChangedSeatStatus() != null) {
                status = updateChangeSeat.getChangedSeatStatus().name();
            }
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }

        validateQuota(enableQuota, quotaId, status, blockReasonId);
    }

    private static void validateQuota(UpdateSeasonTicketChangeSeatDTO updateChangeSeat) {
        Boolean enableQuota = null;
        Integer quotaId = null;
        String status = null;
        Integer blockReasonId;
        ChangedSeatQuotaDTO updateChangedSeatQuota = updateChangeSeat.getChangedSeatQuota();
        
        if (updateChangedSeatQuota != null) {
            enableQuota = updateChangedSeatQuota.getEnable();
            quotaId = updateChangedSeatQuota.getId();
        }
        blockReasonId = updateChangeSeat.getChangedSeatBlockReasonId();
        if (updateChangeSeat.getChangedSeatStatus() != null) {
            status = updateChangeSeat.getChangedSeatStatus().name();
        }
        validateQuota(enableQuota, quotaId, status, blockReasonId);
    }

    private static void validateQuota(Boolean enable, Integer quotaId, String status, Integer blockReasonId) {
        if (enable == null || (enable && (quotaId == null ||
                (ChangedSeatStatusDTO.PROMOTOR_LOCKED.name().equals(status) && blockReasonId == null)))) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED);
        }
    }

    private static void validateLimitQuotas(LimitChangeSeatQuotasDTO limitQuotas, List<Long> allQuotaIds) {
        List<Long> quotaIds = null;
        if (limitQuotas.getQuotaIds() != null) {
            quotaIds = limitQuotas.getQuotaIds().stream().map(Integer::longValue).toList();
        }
        if (limitQuotas.getEnable() && CollectionUtils.isEmpty(quotaIds) || !allQuotaIds.containsAll(quotaIds)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_QUOTAS);
        }
    }

    private static boolean isChangedSeatQuotaModified(ChangedSeatQuotaDTO updatedQuota, ChangedSeatQuota quota) {
        return updatedQuota != null && (quota == null || (updatedQuota.getEnable() != null && !updatedQuota.getEnable().equals(quota.getEnable()))
                || (updatedQuota.getId() != null && !updatedQuota.getId().equals(quota.getId())));
    }
    private static boolean isChangedSeatStatusModified(ChangedSeatStatusDTO updatedStatus, ChangedSeatStatus status) {
        return updatedStatus != null && (status == null || !updatedStatus.name().equals(status.name()));
    }

    private static boolean isLimitChangeSeatQuotasModified(LimitChangeSeatQuotasDTO updatedLimitQuotas, LimitChangeSeatQuotas limitQuotas) {
        return updatedLimitQuotas != null && (limitQuotas == null || (updatedLimitQuotas.getEnable() != null && !updatedLimitQuotas.getEnable().equals(limitQuotas.getEnable()))
                || (updatedLimitQuotas.getQuotaIds() != null && !updatedLimitQuotas.getQuotaIds().equals(limitQuotas.getQuotaIds())));
    }

    private static <T> T getField(T updateField, T dbField) {
        if (updateField != null) {
            return updateField;
        }
        return dbField;
    }
}
