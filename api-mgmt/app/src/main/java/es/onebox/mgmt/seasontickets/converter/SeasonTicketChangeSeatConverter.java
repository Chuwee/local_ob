package es.onebox.mgmt.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceCompleteRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangedSeatQuota;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangedSeatStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.LimitChangeSeatQuotas;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateChangeSeatSeasonTicketPriceRelation;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceCompleteRelationDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceFilterDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangedSeatQuotaDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.LimitChangeSeatQuotasDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.UpdateChangeSeatSeasonTicketPriceRelationDTO;
import es.onebox.mgmt.seasontickets.enums.ChangedSeatStatusDTO;

public class SeasonTicketChangeSeatConverter {

    private SeasonTicketChangeSeatConverter() {
    }

    public static ChangeSeatSeasonTicketPriceCompleteRelationDTO toDTO (ChangeSeatSeasonTicketPriceCompleteRelation relation){
        ChangeSeatSeasonTicketPriceCompleteRelationDTO relationDTO = new ChangeSeatSeasonTicketPriceCompleteRelationDTO();
        relationDTO.setRelationId(relation.getRelationId());
        relationDTO.setSeasonTicketId(relation.getSeasonTicketId());
        relationDTO.setSourcePriceTypeId(relation.getSourcePriceTypeId());
        relationDTO.setSourcePriceTypeName(relation.getSourcePriceTypeName());
        relationDTO.setTargetPriceTypeId(relation.getTargetPriceTypeId());
        relationDTO.setTargetPriceTypeName(relation.getTargetPriceTypeName());
        relationDTO.setRateId(relation.getRateId());
        relationDTO.setRateName(relation.getRateName());
        relationDTO.setValue(relation.getValue());

        return relationDTO;
    }

    public static UpdateChangeSeatSeasonTicketPriceRelation toRelation (UpdateChangeSeatSeasonTicketPriceRelationDTO updateRelationDTO){
        UpdateChangeSeatSeasonTicketPriceRelation relationDTO = new UpdateChangeSeatSeasonTicketPriceRelation();
        relationDTO.setRelationId(updateRelationDTO.getRelationId());
        relationDTO.setValue(updateRelationDTO.getValue());

        return relationDTO;
    }

    public static ChangeSeatSeasonTicketPriceFilter toFilter (ChangeSeatSeasonTicketPriceFilterDTO dto){
        if (dto == null) {
            return null;
        }
        ChangeSeatSeasonTicketPriceFilter filter = new ChangeSeatSeasonTicketPriceFilter();
        filter.setSourcePriceTypeId(dto.getSourcePriceTypeId());
        filter.setTargetPriceTypeId(dto.getTargetPriceTypeId());
        filter.setRateId(dto.getRateId());
        return filter;
    }

    public static SeasonTicketChangeSeatDTO fromMsEvent(SeasonTicketChangeSeat seasonTicketChangeSeat) {
        if (seasonTicketChangeSeat == null) {
            return null;
        }
        SeasonTicketChangeSeatDTO target = new SeasonTicketChangeSeatDTO();
        target.setEnable(seasonTicketChangeSeat.getChangeSeatEnabled());
        target.setStartDate(seasonTicketChangeSeat.getChangeSeatStartingDate());
        target.setEndDate(seasonTicketChangeSeat.getChangeSeatEndDate());
        target.setEnableMaxValue(seasonTicketChangeSeat.getMaxChangeSeatValueEnabled());
        target.setMaxValue(seasonTicketChangeSeat.getMaxChangeSeatValue());
        target.setFixedSurcharge(seasonTicketChangeSeat.getFixedSurcharge());
        ChangedSeatQuota changedSeatQuota = seasonTicketChangeSeat.getChangedSeatQuota();
        if (changedSeatQuota != null) {
            ChangedSeatQuotaDTO changedSeatQuotaDTO = new ChangedSeatQuotaDTO();
            changedSeatQuotaDTO.setId(changedSeatQuota.getId());
            changedSeatQuotaDTO.setEnable(changedSeatQuota.getEnable());
            target.setChangedSeatQuota(changedSeatQuotaDTO);
            if (target.getChangedSeatQuota().getEnable() == null || !target.getChangedSeatQuota().getEnable()) {
                target.getChangedSeatQuota().setId(null);
            }
        }
        if (seasonTicketChangeSeat.getChangedSeatStatus() != null) {
            target.setChangedSeatStatus(ChangedSeatStatusDTO.valueOf(seasonTicketChangeSeat.getChangedSeatStatus().name()));
        }
        target.setChangedSeatBlockReasonId(seasonTicketChangeSeat.getChangedSeatBlockReasonId());
        LimitChangeSeatQuotas limitChangeSeatQuotas = seasonTicketChangeSeat.getLimitChangeSeatQuotas();
        if (limitChangeSeatQuotas != null) {
            LimitChangeSeatQuotasDTO limitChangeSeatQuotasDTO = new LimitChangeSeatQuotasDTO();
            limitChangeSeatQuotasDTO.setEnable(limitChangeSeatQuotas.getEnable());
            limitChangeSeatQuotasDTO.setQuotaIds(limitChangeSeatQuotas.getQuotaIds());
            target.setLimitChangeSeatQuotas(limitChangeSeatQuotasDTO);
        }

        return target;
    }

    public static SeasonTicketChangeSeat toMsEvent(UpdateSeasonTicketChangeSeatDTO updateChangeSeat) {
        SeasonTicketChangeSeat target = new SeasonTicketChangeSeat();
        target.setChangeSeatEnabled(updateChangeSeat.getEnable());
        target.setChangeSeatStartingDate(updateChangeSeat.getStartDate());
        target.setChangeSeatEndDate(updateChangeSeat.getEndDate());
        target.setMaxChangeSeatValueEnabled(updateChangeSeat.getEnableMaxValue());
        target.setMaxChangeSeatValue(updateChangeSeat.getMaxValue());
        target.setFixedSurcharge(updateChangeSeat.getFixedSurcharge());
        ChangedSeatQuotaDTO updateChangeSeatQuota = updateChangeSeat.getChangedSeatQuota();
        if (updateChangeSeatQuota != null) {
            ChangedSeatQuota changedSeatQuota = new ChangedSeatQuota();
            changedSeatQuota.setId(updateChangeSeatQuota.getId());
            changedSeatQuota.setEnable(updateChangeSeatQuota.getEnable());
            target.setChangedSeatQuota(changedSeatQuota);
        }
        if (updateChangeSeat.getChangedSeatStatus() != null) {
            target.setChangedSeatStatus(ChangedSeatStatus.valueOf(updateChangeSeat.getChangedSeatStatus().name()));
        }
        target.setChangedSeatBlockReasonId(updateChangeSeat.getChangedSeatBlockReasonId());
        LimitChangeSeatQuotasDTO updateLimitChangeSeatQuotas = updateChangeSeat.getLimitChangeSeatQuotas();
        if (updateLimitChangeSeatQuotas != null) {
            LimitChangeSeatQuotas limitChangeSeatQuotas = new LimitChangeSeatQuotas();
            limitChangeSeatQuotas.setEnable(updateLimitChangeSeatQuotas.getEnable());
            limitChangeSeatQuotas.setQuotaIds(updateLimitChangeSeatQuotas.getQuotaIds());
            target.setLimitChangeSeatQuotas(limitChangeSeatQuotas);
        }
        return target;
    }


}
