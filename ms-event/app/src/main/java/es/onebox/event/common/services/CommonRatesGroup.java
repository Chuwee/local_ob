package es.onebox.event.common.services;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.integration.avet.config.dto.AvetPrice;
import es.onebox.event.events.converter.RateConverter;
import es.onebox.event.events.dto.EventRateDTO;
import es.onebox.event.events.dto.RateGroupDTO;
import es.onebox.event.events.dto.RateGroupResponseDTO;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.dto.UpdateRateGroupRequestDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommonRatesGroup {

    public static CpanelGrupoTarifaRecord createRateGroup(RateGroupDTO rateGroupDTO, Integer eventId, Integer itemDescSequenceId, Integer position) {
        CpanelGrupoTarifaRecord cpanelGrupoTarifaRecord = new CpanelGrupoTarifaRecord();
        cpanelGrupoTarifaRecord.setIdevento(eventId);
        cpanelGrupoTarifaRecord.setNombre(rateGroupDTO.getName());
        cpanelGrupoTarifaRecord.setDefecto((byte) BooleanUtils.toInteger(BooleanUtils.isTrue(rateGroupDTO.getDefaultRate())));
        cpanelGrupoTarifaRecord.setDescripcionexterna(rateGroupDTO.getExternalDescription());
        cpanelGrupoTarifaRecord.setElementocomdescripcion(itemDescSequenceId);
        if (rateGroupDTO.getType() != null) {
            cpanelGrupoTarifaRecord.setTipo(rateGroupDTO.getType().getId().byteValue());
        }
        cpanelGrupoTarifaRecord.setPosition(position);
        return cpanelGrupoTarifaRecord;
    }

    public static void checkEventRateToDelete(Integer rateGroupId, CpanelGrupoTarifaRecord eventRate) {
        if(eventRate == null) {
            throw OneboxRestException.builder(MsEventRateErrorCode.RATE_NOT_FOUND).
                    setMessage("Rate: " + rateGroupId + " not found").build();
        }

        checkDefault(BooleanUtils.toBoolean(eventRate.getDefecto()), rateGroupId);
    }

    public static void checkDefault(Boolean isDefaultRateGroup, Integer rateId) {
        if (isDefaultRateGroup) {
            throw OneboxRestException.builder(MsEventRateErrorCode.NOT_MODIFIABLE_DEFAULT_RATE).
                    setMessage("Default rate: " + rateId + " cannot modify").build();
        }
    }

    public static CpanelTarifaRecord createSessionRates(String rateName, Integer idGrupoTarifa, Integer eventId, Integer itemDescSequenceId) {
        EventRateDTO eventRateDTO = new EventRateDTO(null,
                rateName,
                rateName,
                false,
                false,
                null,
                new RateGroupResponseDTO(idGrupoTarifa, null));

        return RateConverter.toRecord(eventRateDTO, eventId, itemDescSequenceId);
    }

    public static CpanelTarifaRecord createSessionRates(String rateName, Integer idGrupoTarifa, Integer eventId, Integer elementoComDescripcion, Boolean defaultRate) {
        EventRateDTO eventRateDTO = new EventRateDTO(null,
                rateName,
                rateName,
                false,
                defaultRate,
                null,
                new RateGroupResponseDTO(idGrupoTarifa, null));

        return RateConverter.toRecord(eventRateDTO, eventId, elementoComDescripcion);
    }

    public static void checkEventsExists(CpanelEventoRecord event, Integer eventId) {
        if(event == null) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + eventId + " not found").build();
        }
    }
    public static void checkEventRateNames(String newRateName, Collection<String> rateNames) {
        if (rateNames.stream().anyMatch(newRateName::equals)) {
            throw OneboxRestException.builder(MsEventErrorCode.REPEATED_NAME).
                    setMessage("Rate name:" + newRateName + " already in use").build();
        }
    }

    public static void checkEventRateExternalDescription(String externalDescription, Collection<String> rateNames) {
        if (externalDescription.length() <= 3) {
            throw OneboxRestException.builder(MsEventErrorCode.WRONG_EXTERNAL_DESCRIPTION).
                    setMessage("Wrong external description rate:" + externalDescription).build();
        }

        if (rateNames.stream().anyMatch(externalDescription::equals)) {
            throw OneboxRestException.builder(MsEventErrorCode.REPEATED_EXTERNAL_DESCRIPTION).
                    setMessage("External description rate:" + externalDescription + " already in use").build();
        }
    }

    public static void checkExternalDescriptionExistsInAVET(String externalDescription, List<AvetPrice> avetPricesList) {
        if (avetPricesList == null) {
            throw OneboxRestException.builder(MsEventErrorCode.PRICE_LIST_VOID_IN_AVET).
                    setMessage("Void avet price list").build();
        }
        if (avetPricesList.stream().noneMatch(e -> e.getPriceDescription().contains(externalDescription))) {
            throw OneboxRestException.builder(MsEventErrorCode.EXTERNAL_DESCRIPTION_NOT_FOUND).
                    setMessage("External description:" + externalDescription + " not found in AVET").build();
        }
    }

    public static void checkGroupEventRateExists(CpanelGrupoTarifaRecord eventRate, Integer eventId) {
        if (eventRate == null) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update event: " + eventId + " - Rate not found for event").build();
        }
    }

    public static void checkNullIds(List<RateGroupDTO> groupRatesToModify, Integer eventId) {
        if(groupRatesToModify.stream().anyMatch(groupRate -> Objects.isNull(groupRate.getId()))){
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update event: " + eventId + " - Rate not found by id").build();
        }
    }

    public static void checkIfExternalDescriptionIsRepeated(List<UpdateRateGroupRequestDTO> updateModifyRatesRequest, List<CpanelGrupoTarifaRecord> modifyRates) {
        updateModifyRatesRequest.forEach(
                e-> modifyRates.forEach(
                        modifyRate -> {
                            if (e.getExternalDescription() != null &&
                                    e.getExternalDescription().length() <= 3) {
                                throw OneboxRestException.builder(MsEventErrorCode.WRONG_EXTERNAL_DESCRIPTION).
                                        setMessage("Wrong external description rate:" + e.getExternalDescription()).build();
                            }
                            if(e.getId() != modifyRate.getIdgrupotarifa().longValue()
                                    && e.getExternalDescription() != null
                                    && e.getExternalDescription().equals(modifyRate.getDescripcionexterna())){
                                throw OneboxRestException.builder(MsEventErrorCode.REPEATED_EXTERNAL_DESCRIPTION).
                                        setMessage("Rate group: " + modifyRate.getIdgrupotarifa() + " repeated description").build();
                            }
                        }));

    }

    public static void fixEmptyTranslations(RateGroupDTO rate, CpanelGrupoTarifaRecord eventRateName) {
        fixEmptyTranslations(rate, eventRateName.getNombre());
    }

    public static void fixEmptyTranslations(RateGroupDTO rate, String eventRateName) {
        Map<String, String> translations = rate.getTranslations().entrySet().stream()
                .peek(elem -> {
                    if (elem.getValue() == null || elem.getValue().isEmpty()) {
                        elem.setValue(eventRateName);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        rate.setTranslations(translations);
    }

    public static void checkRateGroupNames(RateGroupDTO rateGroupDTO, CpanelGrupoTarifaRecord currentEventGroupRate, List<CpanelGrupoTarifaRecord> eventGroupRatesNotModified, List<RateGroupDTO> groupRatesToModify) {
        if (rateGroupDTO.getName() != null && !currentEventGroupRate.getNombre().equals(rateGroupDTO.getName())) {
            CommonRatesGroup.checkEventRateNames(rateGroupDTO.getName(), eventGroupRatesNotModified.stream()
                    .filter(r-> !r.getIdgrupotarifa().equals(rateGroupDTO.getId().intValue()))
                    .map(CpanelGrupoTarifaRecord::getNombre).collect(Collectors.toList())
            );

            CommonRatesGroup.checkEventRateNames(rateGroupDTO.getName(), groupRatesToModify.stream().
                    filter(r -> !r.getId().equals(rateGroupDTO.getId())).
                    map(RateGroupDTO::getName).collect(Collectors.toList()));
        }
    }

    public static Integer getEventRateGroupPosition(List<CpanelGrupoTarifaRecord> eventRates, RateGroupType type){
        return eventRates.stream()
                .filter(eventRate -> eventRate.getTipo() != null && RateGroupType.fromId(eventRate.getTipo().intValue()).equals(type))
                .map(CpanelGrupoTarifaRecord::getPosition)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

}
