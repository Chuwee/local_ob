package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.domain.RateDateRestriction;
import es.onebox.event.common.domain.RateRelationsRestriction;
import es.onebox.event.common.domain.RateRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.entity.dto.CustomerType;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.dto.CreateEventRateDTO;
import es.onebox.event.events.dto.EventRateDTO;
import es.onebox.event.events.dto.EventRateDateRestrictionDTO;
import es.onebox.event.events.dto.EventRateRestrictionsDTO;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.dto.RateGroupResponseDTO;
import es.onebox.event.events.dto.RatePriceZoneRestrictionDTO;
import es.onebox.event.events.dto.RateRelationsRestrictionDTO;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.UpdateEventRateDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RateConverter {

    private RateConverter() {
    }

    public static RateDTO convert(RateRecord rate) {
        if (rate == null) {
            return null;
        }
        RateDTO rateDTO = new RateDTO();
        rateDTO.setId(rate.getIdTarifa().longValue());
        rateDTO.setName(rate.getNombre());
        rateDTO.setRestrictive(NumberUtils.INTEGER_ONE.equals(rate.getAccesoRestrictivo()));
        rateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        rateDTO.setDescription(rate.getDescripcion());
        rateDTO.setTranslations(rate.getTranslations());
        rateDTO.setPosition(rate.getPosition());
        return rateDTO;
    }

    public static EventRateDTO convertDTO(RateRecord rate) {
        if (rate == null) {
            return null;
        }
        EventRateDTO rateDTO = new EventRateDTO();
        rateDTO.setId(rate.getIdTarifa().longValue());
        rateDTO.setName(rate.getNombre());
        rateDTO.setRestrictive(NumberUtils.INTEGER_ONE.equals(rate.getAccesoRestrictivo()));
        rateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        rateDTO.setDescription(rate.getDescripcion());
        rateDTO.setTranslations(rate.getTranslations());
        rateDTO.setRateGroup(
                new RateGroupResponseDTO(
                        rate.getIdGrupoTarifa(),
                        rate.getNombreGrupoTarifa()
                )
        );
        rateDTO.setEventId(rate.getIdEvento());
        return rateDTO;
    }

    public static EventRateRestrictionsDTO convert(RateRestrictions restrictions) {
        if (restrictions == null) {
            return null;
        }
        EventRateRestrictionsDTO rateRestrictionsDTO = new EventRateRestrictionsDTO();
        if (restrictions.getDateRestriction() != null) {
            rateRestrictionsDTO.setDateRestrictionEnabled(true);
            rateRestrictionsDTO.setDateRestriction(toDTO(restrictions.getDateRestriction()));
        } else {
            rateRestrictionsDTO.setDateRestrictionEnabled(false);
        }

        if (restrictions.getCustomerTypeRestriction() != null) {
            rateRestrictionsDTO.setCustomerTypeRestrictionEnabled(true);
            rateRestrictionsDTO.setCustomerTypeRestriction(restrictions.getCustomerTypeRestriction());
        } else {
            rateRestrictionsDTO.setCustomerTypeRestrictionEnabled(false);
        }

        if (restrictions.getRateRelationsRestriction() != null) {
            rateRestrictionsDTO.setRateRelationsRestrictionEnabled(true);
            rateRestrictionsDTO.setRateRelationsRestriction(toDTO(restrictions.getRateRelationsRestriction()));
        } else {
            rateRestrictionsDTO.setRateRelationsRestrictionEnabled(false);
        }

        if (restrictions.getPriceZoneRestriction() != null) {
            rateRestrictionsDTO.setPriceZoneRestrictionEnabled(true);

            RatePriceZoneRestrictionDTO ratePriceZoneRestrictionDTO = new RatePriceZoneRestrictionDTO();
            ratePriceZoneRestrictionDTO.setApplyToB2b(BooleanUtils.isTrue(restrictions.getPriceZoneRestrictionApplyToB2b()));
            ratePriceZoneRestrictionDTO.setRestrictedPriceZoneIds(restrictions.getPriceZoneRestriction());
            rateRestrictionsDTO.setRatePriceZoneRestriction(ratePriceZoneRestrictionDTO);


        } else {
            rateRestrictionsDTO.setPriceZoneRestrictionEnabled(false);
        }

        if (restrictions.getChannelRestriction() != null) {
            rateRestrictionsDTO.setChannelRestrictionEnabled(true);
            rateRestrictionsDTO.setChannelRestriction(restrictions.getChannelRestriction());
        } else {
            rateRestrictionsDTO.setChannelRestrictionEnabled(false);
        }

        if (restrictions.getPeriodRestrictions() != null) {
            rateRestrictionsDTO.setPeriodRestrictionEnabled(true);
            rateRestrictionsDTO.setPeriodRestriction(restrictions.getPeriodRestrictions());
        } else {
            rateRestrictionsDTO.setPeriodRestrictionEnabled(false);
        }

        if (restrictions.getMaxItemRestriction() != null) {
            rateRestrictionsDTO.setMaxItemRestrictionEnabled(true);
            rateRestrictionsDTO.setMaxItemRestriction(restrictions.getMaxItemRestriction());
        } else {
            rateRestrictionsDTO.setMaxItemRestrictionEnabled(false);
        }

        return rateRestrictionsDTO;
    }

    private static EventRateDateRestrictionDTO toDTO(RateDateRestriction rateDateRestriction) {
        EventRateDateRestrictionDTO eventRateDateRestrictionDTO = new EventRateDateRestrictionDTO();

        if (rateDateRestriction != null) {
            eventRateDateRestrictionDTO.setFrom(rateDateRestriction.getFrom());
            eventRateDateRestrictionDTO.setTo(rateDateRestriction.getTo());
        }

        return eventRateDateRestrictionDTO;
    }

    private static RateRelationsRestrictionDTO toDTO(RateRelationsRestriction rateRelationsRestriction) {
        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();

        if (rateRelationsRestriction != null) {
            rateRelationsRestrictionDTO.setRequiredRates(
                    rateRelationsRestriction.getRequiredRates().stream().map(IdNameDTO::getId).map(Long::intValue).toList());
            rateRelationsRestrictionDTO.setRestrictedPriceZones(rateRelationsRestriction.getRestrictedPriceZones());
            rateRelationsRestrictionDTO.setMaxItemsMultiplier(rateRelationsRestriction.getMaxItemsMultiplier());
            rateRelationsRestrictionDTO.setPriceZoneCriteria(rateRelationsRestriction.getPriceZoneCriteria());
            rateRelationsRestrictionDTO.setApplyToB2b(rateRelationsRestriction.getApplyToB2b());
        }

        return rateRelationsRestrictionDTO;
    }



    public static RateRestrictions convert(UpdateRateRestrictionsDTO restrictionsDTO, RateRestrictions rateRestrictions,
                                           CustomerTypes customerTypes, List<IdNameDTO> requiredRates) {
        if (restrictionsDTO == null) {
            return rateRestrictions;
        }
        if (rateRestrictions == null) {
            rateRestrictions = new RateRestrictions();
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getDateRestrictionEnabled())) {
            RateDateRestriction rateDateRestriction = new RateDateRestriction();
            rateDateRestriction.setFrom(restrictionsDTO.getDateRestriction().getFrom());
            rateDateRestriction.setTo(restrictionsDTO.getDateRestriction().getTo());
            rateRestrictions.setDateRestriction(rateDateRestriction);
        } else if (BooleanUtils.isFalse(restrictionsDTO.getDateRestrictionEnabled())) {
            rateRestrictions.setDateRestriction(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getCustomerTypeRestrictionEnabled())) {
            rateRestrictions.setCustomerTypeRestriction(toDomain(restrictionsDTO.getCustomerTypeRestriction(), customerTypes));
        } else if (BooleanUtils.isFalse(restrictionsDTO.getCustomerTypeRestrictionEnabled())) {
            rateRestrictions.setCustomerTypeRestriction(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getRateRelationsRestrictionEnabled())) {
            rateRestrictions.setRateRelationsRestriction(toDomain(restrictionsDTO.getRateRelationsRestriction(), requiredRates));
        } else if (BooleanUtils.isFalse(restrictionsDTO.getRateRelationsRestrictionEnabled())) {
            rateRestrictions.setRateRelationsRestriction(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getPriceZoneRestrictionEnabled()) && restrictionsDTO.getPriceZoneRestriction() != null &&
                CollectionUtils.isNotEmpty(restrictionsDTO.getPriceZoneRestriction().getRestrictedPriceZoneIds())) {
            rateRestrictions.setPriceZoneRestriction(restrictionsDTO.getPriceZoneRestriction().getRestrictedPriceZoneIds());
            rateRestrictions.setPriceZoneRestrictionApplyToB2b(BooleanUtils.isTrue(restrictionsDTO.getPriceZoneRestriction().getApplyToB2b()));
        } else if (BooleanUtils.isFalse(restrictionsDTO.getPriceZoneRestrictionEnabled())) {
            rateRestrictions.setPriceZoneRestriction(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getChannelRestrictionEnabled())) {
            rateRestrictions.setChannelRestriction(restrictionsDTO.getChannelRestriction());
        } else if (BooleanUtils.isFalse(restrictionsDTO.getChannelRestrictionEnabled())) {
            rateRestrictions.setChannelRestriction(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getPeriodRestrictionEnabled())) {
            rateRestrictions.setPeriodRestrictions(restrictionsDTO.getPeriodRestriction());
        } else if (BooleanUtils.isFalse(restrictionsDTO.getPeriodRestrictionEnabled())) {
            rateRestrictions.setPeriodRestrictions(null);
        }

        if (BooleanUtils.isTrue(restrictionsDTO.getMaxItemRestrictionEnabled())) {
            rateRestrictions.setMaxItemRestriction(restrictionsDTO.getMaxItemRestriction());
        } else if (BooleanUtils.isFalse(restrictionsDTO.getMaxItemRestrictionEnabled())) {
            rateRestrictions.setMaxItemRestriction(null);
        }

        return rateRestrictions;
    }


    private static List<IdNameCodeDTO> toDomain(List<Long> customerTypeRestriction, CustomerTypes customerTypes) {
        List<IdNameCodeDTO> idCodeDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(customerTypeRestriction) && customerTypes != null) {
            customerTypes.getData().forEach(ct -> {
                if (customerTypeRestriction.contains(ct.getId())) {
                    idCodeDTOs.add(toDomain(ct));
                }
            });
        }
        return idCodeDTOs;
    }

    private static IdNameCodeDTO toDomain(CustomerType customerType) {
        IdNameCodeDTO idCodeDTO = new IdNameCodeDTO();
        idCodeDTO.setId(customerType.getId());
        idCodeDTO.setCode(customerType.getCode());
        idCodeDTO.setName(customerType.getName());
        return idCodeDTO;
    }

    private static RateRelationsRestriction toDomain(RateRelationsRestrictionDTO rateRelationsRestrictionDTO,
                                                     List<IdNameDTO> requiredRates) {
        RateRelationsRestriction rateRelationsRestriction = new RateRelationsRestriction();

        if (rateRelationsRestrictionDTO != null) {
            rateRelationsRestriction.setRequiredRates(requiredRates);
            rateRelationsRestriction.setRestrictedPriceZones(rateRelationsRestrictionDTO.getRestrictedPriceZones());
            rateRelationsRestriction.setMaxItemsMultiplier(rateRelationsRestrictionDTO.getMaxItemsMultiplier());
            rateRelationsRestriction.setPriceZoneCriteria(rateRelationsRestrictionDTO.getPriceZoneCriteria());
            rateRelationsRestriction.setApplyToB2b(rateRelationsRestrictionDTO.getApplyToB2b());
        }

        return rateRelationsRestriction;
    }


    public static EventRateDTO convertRecord(RateRecord rate) {
        if (rate == null) {
            return null;
        }
        EventRateDTO eventRateDTO = new EventRateDTO();
        eventRateDTO.setId(rate.getIdTarifa().longValue());
        eventRateDTO.setName(rate.getNombre());
        eventRateDTO.setRestrictive(NumberUtils.INTEGER_ONE.equals(rate.getAccesoRestrictivo()));
        eventRateDTO.setDefaultRate(NumberUtils.INTEGER_ONE.equals(rate.getDefecto()));
        eventRateDTO.setDescription(rate.getDescripcion());
        eventRateDTO.setTranslations(rate.getTranslations());
        eventRateDTO.setRateGroup(convert(rate.getIdGrupoTarifa(), rate.getNombreGrupoTarifa()));
        eventRateDTO.setPosition(rate.getPosition());
        eventRateDTO.setEventId(rate.getIdEvento());
        if (rate.getExternalRateTypeId() != null) {
            eventRateDTO.setExternalRateType(new IdNameCodeDTO(rate.getExternalRateTypeId().longValue(), rate.getExternalRateTypeCode(), rate.getExternalRateTypeName()));
        }
        return eventRateDTO;
    }

    public static List<RateRestrictedDTO> convertRecord(RatesRestrictions restrictions, List<CpanelTarifaRecord> rates) {
        if (MapUtils.isEmpty(restrictions) || CollectionUtils.isEmpty(rates)) {
            return Collections.emptyList();
        }
        return rates.stream().filter(rate -> restrictions.containsKey(rate.getIdtarifa()))
                .map(rate -> convert(restrictions.get(rate.getIdtarifa()), rate)).toList();
    }

    private static RateRestrictedDTO convert(RateRestrictions restrictions, CpanelTarifaRecord rate) {
        RateRestrictedDTO rateRestrictedDTO = new RateRestrictedDTO();
        rateRestrictedDTO.setRate(new IdNameDTO(rate.getIdtarifa().longValue(), rate.getNombre()));
        rateRestrictedDTO.setRestrictions(convert(restrictions));
        return rateRestrictedDTO;
    }

    private static RateGroupResponseDTO convert(Integer idGrupoTarifa, String nombreGrupoTarifa) {
        if (idGrupoTarifa == null) {
            return null;
        }
        return new RateGroupResponseDTO(idGrupoTarifa, nombreGrupoTarifa);
    }

    public static void updateRecord(CpanelTarifaRecord rateRecord, RateDTO rateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, rateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDescripcion, rateDTO.getDescription());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(rateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(rateDTO.getRestrictive()));
    }

    public static void updateRecord(CpanelTarifaRecord rateRecord, EventRateDTO eventRateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, eventRateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDescripcion, eventRateDTO.getDescription());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(eventRateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(eventRateDTO.getRestrictive()));
        ConverterUtils.updateField(rateRecord::setIdgrupotarifa, eventRateDTO.getRateGroup().getId());
    }

    public static void updateRecord(CpanelTarifaRecord rateRecord, CreateEventRateDTO eventRateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, eventRateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDescripcion, eventRateDTO.getDescription());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(eventRateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(eventRateDTO.getRestrictive()));
        ConverterUtils.updateField(rateRecord::setIdgrupotarifa, eventRateDTO.getRateGroupId());
    }

    public static void updateRecord(CpanelTarifaRecord rateRecord, UpdateEventRateDTO eventRateDTO) {
        ConverterUtils.updateField(rateRecord::setNombre, eventRateDTO.getName());
        ConverterUtils.updateField(rateRecord::setDescripcion, eventRateDTO.getDescription());
        ConverterUtils.updateField(rateRecord::setDefecto, ConverterUtils.isTrueAsByte(eventRateDTO.getDefaultRate()));
        ConverterUtils.updateField(rateRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(eventRateDTO.getRestrictive()));
        ConverterUtils.updateField(rateRecord::setIdgrupotarifa, eventRateDTO.getRateGroupId());
        ConverterUtils.updateField(rateRecord::setPosition, eventRateDTO.getPosition());
        if (eventRateDTO.getExternalRateTypeId() != null) {
            ConverterUtils.updateField(rateRecord::setExternalratetypeid, eventRateDTO.getExternalRateTypeId().intValue());
        }
    }

    public static CpanelTarifaRecord toRecord(RateDTO rateDTO, Integer eventId, Integer itemDescSequenceId) {
        if (rateDTO == null) {
            return null;
        }
        CpanelTarifaRecord cpanelTarifaRecord = new CpanelTarifaRecord();
        cpanelTarifaRecord.setIdevento(eventId);
        cpanelTarifaRecord.setNombre(rateDTO.getName());
        cpanelTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(rateDTO.getDefaultRate()));
        cpanelTarifaRecord.setDescripcion(rateDTO.getDescription());
        cpanelTarifaRecord.setAccesorestrictivo((byte) BooleanUtils.toInteger(rateDTO.getRestrictive()));
        cpanelTarifaRecord.setElementocomdescripcion(itemDescSequenceId);
        cpanelTarifaRecord.setPosition(rateDTO.getPosition());
        if (rateDTO.getExternalRateType() != null && rateDTO.getExternalRateType().getId() != null) {
            cpanelTarifaRecord.setExternalratetypeid(rateDTO.getExternalRateType().getId().intValue());
        }
        return cpanelTarifaRecord;
    }

    public static CpanelTarifaRecord toRecord(EventRateDTO eventRateDTO, Integer eventId, Integer itemDescSequenceId) {
        if (eventRateDTO == null) {
            return null;
        }
        CpanelTarifaRecord cpanelTarifaRecord = new CpanelTarifaRecord();
        cpanelTarifaRecord.setIdevento(eventId);
        cpanelTarifaRecord.setNombre(eventRateDTO.getName());
        cpanelTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(eventRateDTO.getDefaultRate()));
        cpanelTarifaRecord.setDescripcion(eventRateDTO.getDescription());
        cpanelTarifaRecord.setAccesorestrictivo((byte) BooleanUtils.toInteger(eventRateDTO.getRestrictive()));
        cpanelTarifaRecord.setElementocomdescripcion(itemDescSequenceId);
        cpanelTarifaRecord.setIdgrupotarifa(eventRateDTO.getRateGroup().getId());
        return cpanelTarifaRecord;
    }

    public static CpanelTarifaRecord toRecord(CreateEventRateDTO createEventRateDTO, Integer eventId, Integer itemDescSequenceId, Integer position) {
        if (createEventRateDTO == null) {
            return null;
        }
        CpanelTarifaRecord cpanelTarifaRecord = new CpanelTarifaRecord();
        cpanelTarifaRecord.setIdevento(eventId);
        cpanelTarifaRecord.setNombre(createEventRateDTO.getName());
        cpanelTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(createEventRateDTO.getDefaultRate()));
        cpanelTarifaRecord.setDescripcion(createEventRateDTO.getDescription());
        cpanelTarifaRecord.setAccesorestrictivo((byte) BooleanUtils.toInteger(createEventRateDTO.getRestrictive()));
        cpanelTarifaRecord.setElementocomdescripcion(itemDescSequenceId);
        cpanelTarifaRecord.setIdgrupotarifa(createEventRateDTO.getRateGroupId());
        cpanelTarifaRecord.setPosition(position);
        if (createEventRateDTO.getExternalRateTypeId() != null) {
            cpanelTarifaRecord.setExternalratetypeid(createEventRateDTO.getExternalRateTypeId().intValue());
        }
        return cpanelTarifaRecord;
    }
}
