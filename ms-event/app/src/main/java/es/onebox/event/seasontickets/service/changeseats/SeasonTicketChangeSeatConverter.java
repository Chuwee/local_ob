package es.onebox.event.seasontickets.service.changeseats;

import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelation;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceRelationDTO;
import es.onebox.event.seasontickets.dto.changeseat.ChangedSeatQuota;
import es.onebox.event.seasontickets.dto.changeseat.ChangedSeatStatus;
import es.onebox.event.seasontickets.dto.changeseat.LimitChangeSeatQuotas;
import es.onebox.event.seasontickets.dto.changeseat.SeasonTicketChangeSeatDTO;
import es.onebox.event.seasontickets.dto.changeseat.UpdateChangeSeatSeasonTicketPriceRelation;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatPricesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatQuotasRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.timestampToZonedDateTime;
import static es.onebox.core.utils.common.CommonUtils.zonedDateTimeToTimestamp;

public class SeasonTicketChangeSeatConverter {

    private SeasonTicketChangeSeatConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static CpanelSeasonTicketChangeSeatPricesRecord toRecord(Long seasonTicketId, ChangeSeatSeasonTicketPriceRelation pricesRelation) {
        if (pricesRelation == null) {
            return null;
        }

        CpanelSeasonTicketChangeSeatPricesRecord pricesRecord = new CpanelSeasonTicketChangeSeatPricesRecord();
        pricesRecord.setIdseasonticket(seasonTicketId.intValue());
        pricesRecord.setIdsourcepricetype(pricesRelation.getSourcePriceTypeId().intValue());
        pricesRecord.setIdtargetpricetype(pricesRelation.getTargetPriceTypeId().intValue());
        pricesRecord.setIdrate(pricesRelation.getRateId().intValue());
        pricesRecord.setValue(pricesRelation.getValue());

        return pricesRecord;
    }

    public static CpanelSeasonTicketChangeSeatPricesRecord toRecord(UpdateChangeSeatSeasonTicketPriceRelation pricesRelation) {
        if (pricesRelation == null) {
            return null;
        }

        CpanelSeasonTicketChangeSeatPricesRecord pricesRecord = new CpanelSeasonTicketChangeSeatPricesRecord();
        pricesRecord.setIdpricerelation(pricesRelation.getRelationId().intValue());
        pricesRecord.setValue(pricesRelation.getValue());

        return pricesRecord;
    }

    public static ChangeSeatSeasonTicketPriceRelationDTO toDTO(SeasonTicketChangeSeatPricesRecord pricesRelation) {
        if (pricesRelation == null) {
            return null;
        }

        ChangeSeatSeasonTicketPriceRelationDTO relationDTO = new ChangeSeatSeasonTicketPriceRelationDTO();
        relationDTO.setRelationId(pricesRelation.getIdpricerelation().longValue());
        relationDTO.setSeasonTicketId(pricesRelation.getIdseasonticket().longValue());
        relationDTO.setSourcePriceTypeId(pricesRelation.getIdsourcepricetype().longValue());
        relationDTO.setSourcePriceTypeName(pricesRelation.getSourcePriceTypeName());
        relationDTO.setTargetPriceTypeId(pricesRelation.getIdtargetpricetype().longValue());
        relationDTO.setTargetPriceTypeName(pricesRelation.getTargetPriceTypeName());
        relationDTO.setRateId(pricesRelation.getIdrate().longValue());
        relationDTO.setRateName(pricesRelation.getRateName());
        relationDTO.setValue(pricesRelation.getValue());

        return relationDTO;
    }


    public static SeasonTicketChangeSeatDTO fromRecords(CpanelSeasonTicketRecord seasonTicketRecord,
                                                        CpanelSeasonTicketChangeSeatRecord changeSeatRecord,
                                                        List<CpanelSeasonTicketChangeSeatQuotasRecord> quotasRecords) {
        SeasonTicketChangeSeatDTO seasonTicketChangeSeatDTO = new SeasonTicketChangeSeatDTO();
        seasonTicketChangeSeatDTO.setChangeSeatEnabled(seasonTicketRecord.getChangeseatenabled());
        seasonTicketChangeSeatDTO.setChangeSeatStartingDate(timestampToZonedDateTime(seasonTicketRecord.getChangeseatinitdate()));
        seasonTicketChangeSeatDTO.setChangeSeatEndDate(timestampToZonedDateTime(seasonTicketRecord.getChangeseatenddate()));
        seasonTicketChangeSeatDTO.setMaxChangeSeatValueEnabled(seasonTicketRecord.getMaxchangeseatvalueenabled());
        seasonTicketChangeSeatDTO.setMaxChangeSeatValue(seasonTicketRecord.getMaxchangeseatvalue());
        seasonTicketChangeSeatDTO.setFixedSurcharge(seasonTicketRecord.getChangeseatfixedsurcharge());

        ChangedSeatQuota changedSeatQuota = new ChangedSeatQuota();
        changedSeatQuota.setEnable(seasonTicketRecord.getEnablechangedseatquota());
        changedSeatQuota.setId(seasonTicketRecord.getChangedseatquotaid());
        seasonTicketChangeSeatDTO.setChangedSeatQuota(changedSeatQuota);

        seasonTicketChangeSeatDTO.setChangedSeatStatus(ChangedSeatStatus.byId(seasonTicketRecord.getChangedseatstatus()));
        seasonTicketChangeSeatDTO.setChangedSeatBlockReasonId(seasonTicketRecord.getChangedseatblockreasonid());

        if (changeSeatRecord != null) {
            List<Integer> quotaIds = quotasRecords.stream().map(CpanelSeasonTicketChangeSeatQuotasRecord::getIdquota).toList();
            LimitChangeSeatQuotas limitChangeSeatQuotas = new LimitChangeSeatQuotas();
            limitChangeSeatQuotas.setEnable(changeSeatRecord.getLimitchangeseatquotas());
            limitChangeSeatQuotas.setQuotaIds(quotaIds);
            seasonTicketChangeSeatDTO.setLimitChangeSeatQuotas(limitChangeSeatQuotas);
        }

        return seasonTicketChangeSeatDTO;
    }

    public static CpanelSeasonTicketRecord toRecord(CpanelSeasonTicketRecord record, UpdateSeasonTicketChangeSeat updateChangeSeat) {
        record.setChangeseatenabled(getField(updateChangeSeat.getChangeSeatEnabled(), record.getChangeseatenabled()));
        record.setChangeseatinitdate(getField(zonedDateTimeToTimestamp(updateChangeSeat.getChangeSeatStartingDate()), record.getChangeseatinitdate()));
        record.setChangeseatenddate(getField(zonedDateTimeToTimestamp(updateChangeSeat.getChangeSeatEndDate()), record.getChangeseatenddate()));
        record.setChangeseatfixedsurcharge(getField(updateChangeSeat.getFixedSurcharge(), record.getChangeseatfixedsurcharge()));
        record.setMaxchangeseatvalueenabled(getField(updateChangeSeat.getMaxChangeSeatValueEnabled(), record.getMaxchangeseatvalueenabled()));
        record.setMaxchangeseatvalue(getField(updateChangeSeat.getMaxChangeSeatValue(), record.getMaxchangeseatvalue()));
        if (updateChangeSeat.getChangedSeatQuota() != null) {
            record.setEnablechangedseatquota(updateChangeSeat.getChangedSeatQuota().getEnable());
            record.setChangedseatquotaid(updateChangeSeat.getChangedSeatQuota().getId());
        }
        if (updateChangeSeat.getChangedSeatStatus() != null) {
            record.setChangedseatstatus(updateChangeSeat.getChangedSeatStatus().getStatus());
        }
        record.setChangedseatblockreasonid(updateChangeSeat.getChangedSeatBlockReasonId());
        return record;
    }

    public static CpanelSeasonTicketChangeSeatRecord toRecord(Integer seasonTicketId, UpdateSeasonTicketChangeSeat updateChangeSeat) {
        CpanelSeasonTicketChangeSeatRecord record = new CpanelSeasonTicketChangeSeatRecord();
        record.setIdseasonticket(seasonTicketId);
        if (updateChangeSeat.getLimitChangeSeatQuotas() != null) {
            record.setLimitchangeseatquotas(updateChangeSeat.getLimitChangeSeatQuotas().getEnable());
        }
        return record;
    }

    public static Set<CpanelSeasonTicketChangeSeatQuotasRecord> toRecord(Integer seasonTicketId, LimitChangeSeatQuotas limit) {
        return limit.getQuotaIds().stream()
                .map(quotaId -> new CpanelSeasonTicketChangeSeatQuotasRecord(seasonTicketId, quotaId))
                .collect(Collectors.toSet());
    }

    private static <T> T getField(T updateField, T dbField) {
        if (updateField != null) {
            return updateField;
        }
        return dbField;
    }

}
