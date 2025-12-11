package es.onebox.event.events.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.domain.RateRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.events.converter.RateConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.GroupPricesDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateExternalTypeDao;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.RateGroupRateDao;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.CreateEventRateDTO;
import es.onebox.event.events.dto.EventRateRestrictionsDTO;
import es.onebox.event.events.dto.EventRatesDTO;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.RatesDTO;
import es.onebox.event.events.dto.UpdateEventRateDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.utils.RateRestrictionsValidator;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalRateTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaGrupoTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.event.exception.MsEventErrorCode.EVENTS_NOT_FOUND_OR_INVALID_EVENT_STATE;

@Service
public class EventRateService {

    private final EventDao eventDao;
    private final RateDao rateDao;
    private final RateGroupDao rateGroupDao;
    private final SessionRateDao sessionRateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final GroupPricesDao groupPricesDao;
    private final CommonRatesService commonRatesService;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final RateRestrictionsValidator rateRestrictionsValidator;
    private final RateGroupRateDao rateGroupRateDao;
    private final RateExternalTypeDao rateExternalTypeDao;

    @Autowired
    public EventRateService(EventDao eventDao, RateDao rateDao, RateGroupDao rateGroupDao, SessionRateDao sessionRateDao, PriceZoneAssignmentDao priceZoneAssignmentDao,
                            GroupPricesDao groupPricesDao, CommonRatesService commonRatesService, ItemDescSequenceDao itemDescSequenceDao,
                            EventConfigCouchDao eventConfigCouchDao, RateRestrictionsValidator rateRestrictionsValidator, RateGroupRateDao rateGroupRateDao,
                            RateExternalTypeDao rateExternalTypeDao) {
        this.eventDao = eventDao;
        this.rateDao = rateDao;
        this.rateGroupDao = rateGroupDao;
        this.sessionRateDao = sessionRateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.groupPricesDao = groupPricesDao;
        this.commonRatesService = commonRatesService;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.rateRestrictionsValidator = rateRestrictionsValidator;
        this.rateGroupRateDao = rateGroupRateDao;
        this.rateExternalTypeDao = rateExternalTypeDao;
    }

    @MySQLRead
    public RatesDTO findRatesByEventId(Integer eventId, RatesFilter filter) {
        checkEvent(eventId);
        RatesDTO ratesDTO = new RatesDTO();
        ratesDTO.setMetadata(MetadataBuilder.build(filter, rateDao.countByEventId(eventId)));
        ratesDTO.setData(rateDao.getEventRatesByEventId(eventId, filter.getLimit(), filter.getOffset()).stream()
                .map(RateConverter::convert)
                .collect(Collectors.toList()));
        return ratesDTO;
    }

    @MySQLRead
    public EventRatesDTO findEventRatesByEventId(Integer eventId, RatesFilter filter) {
        checkEvent(eventId);
        EventRatesDTO eventRatesDTO = new EventRatesDTO();
        eventRatesDTO.setMetadata(MetadataBuilder.build(filter, rateDao.countByEventId(eventId)));
        eventRatesDTO.setData(rateDao.getEventRatesByEventId(eventId, filter.getLimit(), filter.getOffset()).stream()
                .map(RateConverter::convertRecord)
                .collect(Collectors.toList()));
        return eventRatesDTO;
    }


    @MySQLRead
    public EventRatesDTO searchRatesByFilter(RatesFilter filter) {
        EventRatesDTO eventRatesDTO = new EventRatesDTO();
        eventRatesDTO.setMetadata(MetadataBuilder.build(filter, rateDao.countByRatesFilter(filter)));
        eventRatesDTO.setData(rateDao.searchRatesByFilter(filter).stream()
                .map(RateConverter::convertRecord)
                .collect(Collectors.toList()));
        return eventRatesDTO;
    }

    @MySQLRead
    public RateDTO findRate(Integer eventId, Integer rateId) {
        List<RateRecord> rateRecords = rateDao.getRatesByEventId(eventId);
        Optional<RateRecord> rateRecord = rateRecords.stream().filter(rr -> rr.getIdTarifa().equals(rateId)).findFirst();
        return rateRecord.map(RateConverter::convert).orElse(null);
    }

    @MySQLWrite
    public CommonIdResponse createEventRate(Integer eventId, CreateEventRateDTO eventRateDTO) {

        CpanelEventoRecord cpanelEventoRecord = checkEvent(eventId);

        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);

        this.checkEventRateNames(eventRateDTO.getName(), eventRates);

        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));

        if (EventUtils.isItalianCompliance(EventUtils.getInventoryProvider(eventConfig))) {
            if (eventRateDTO.getExternalRateTypeId() == null) {
                throw new OneboxRestException(MsEventRateErrorCode.RATE_EXTERNAL_TYPE_REQUIRED);
            }
        }

        Integer itemDescSequenceId = commonRatesService.insertRateTranslations(eventRateDTO.getTranslations());

        if (CommonUtils.isTrue(eventRateDTO.getDefaultRate())) {
            commonRatesService.unsetDefaultEventRate(eventRates);

            if (EventType.AVET.getId().equals(cpanelEventoRecord.getTipoevento())) {
                CpanelGrupoTarifaRecord defaultRateEventGroupRecord = rateGroupDao.getDefaultGroupRateByEventId(eventId);
                if (defaultRateEventGroupRecord == null) {
                    throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                            .setMessage("Update event: " + eventId + " - Rate not found by id").build();

                }
                eventRateDTO.setRateGroupId(defaultRateEventGroupRecord.getIdgrupotarifa());
                itemDescSequenceId = defaultRateEventGroupRecord.getElementocomdescripcion();
            }
        }

        int position;
        if (CollectionUtils.isEmpty(eventRates)) {
            position = 1;
        } else {
            position = eventRates.stream().map(CpanelTarifaRecord::getPosition).filter(Objects::nonNull).max(Integer::compareTo).orElse(0) + 1;
        }
        Integer rateId = commonRatesService.createEventRate(eventRateDTO, eventId, itemDescSequenceId, position);

        commonRatesService.updateEventVenueTemplatePriceZones(eventId, rateId);

        if (EventUtils.isSGA(EventUtils.getInventoryProvider(eventConfig))) {
            createRateGroupRateRelationship(eventRateDTO.getRateGroupIds(), rateId, eventId);
        }
        return new CommonIdResponse(rateId);
    }

    private void createRateGroupRateRelationship(List<Integer> rateGroupIds, Integer rateId, Integer eventId) {
        for (Integer rateGroupId : rateGroupIds) {
            checkGroupRate(eventId, rateGroupId);
            rateGroupRateDao.insert(new CpanelTarifaGrupoTarifaRecord(rateId, rateGroupId));
        }
    }

    private void checkGroupRate(Integer eventId, Integer rateGroupId) {
        if (rateGroupId == null) {
            throw OneboxRestException.builder(CoreErrorCode.REQUIRED_PARAMETER).setMessage("RateGroupId is required").build();
        }
        CpanelGrupoTarifaRecord rateGroupRecord = rateGroupDao.getById(rateGroupId);
        if (rateGroupRecord == null) {
            throw OneboxRestException.builder(CoreErrorCode.NOT_FOUND).setMessage("RateGroup not found: " + rateGroupId).build();
        }
        if (!rateGroupRecord.getIdevento().equals(eventId)) {
            throw new OneboxRestException(EVENTS_NOT_FOUND_OR_INVALID_EVENT_STATE);
        }
    }

    public void updateEventRates(Integer eventId, List<UpdateEventRateDTO> modifyRates) {

        this.checkEvent(eventId);

        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);

        List<Long> rateIds = modifyRates.stream().map(UpdateEventRateDTO::getId).toList();
        List<CpanelTarifaRecord> notModifyRates = eventRates.stream().
                filter(r -> !rateIds.contains(r.getIdtarifa().longValue())).collect(Collectors.toList());

        long newDefaultRates = modifyRates.stream().filter(r -> CommonUtils.isTrue(r.getDefaultRate())).count();
        if (newDefaultRates > 1) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update event: " + eventId + " - No more than 1 default rate allowed").build();
        }

        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));

        for (UpdateEventRateDTO rateDTO : modifyRates) {
            if (rateDTO.getId() == null) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                        .setMessage("Update event: " + eventId + " - Rate not found by id").build();
            }
            CpanelTarifaRecord eventRate = eventRates.stream().
                    filter(r -> r.getIdtarifa().equals(rateDTO.getId().intValue())).findAny().orElse(null);
            if (eventRate == null) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                        .setMessage("Update event: " + eventId + " - Rate not found for event").build();
            }
            if (EventUtils.isItalianCompliance(EventUtils.getInventoryProvider(eventConfig))) {
                if (eventRate.getExternalratetypeid() == null && rateDTO.getExternalRateTypeId() == null) {
                    throw new OneboxRestException(MsEventRateErrorCode.RATE_EXTERNAL_TYPE_REQUIRED);
                }
            }
            if (rateDTO.getName() != null && !eventRate.getNombre().equals(rateDTO.getName())) {
                this.checkEventRateNames(rateDTO.getName(), notModifyRates);
                this.checkEventRateNames(rateDTO.getName(), modifyRates.stream().
                        filter(r -> !r.getId().equals(rateDTO.getId())).
                        map(UpdateEventRateDTO::getName).collect(Collectors.toList()));
            }
            if (rateDTO.getTranslations() != null) {
                fixEmptyTranslations(rateDTO, eventRate);
                if (eventRate.getElementocomdescripcion() == null) {
                    CpanelItemDescSequenceRecord cpanelItemDescSequence = new CpanelItemDescSequenceRecord();
                    cpanelItemDescSequence.setDescripcion("rate item");
                    eventRate.setElementocomdescripcion(itemDescSequenceDao.insert(cpanelItemDescSequence).getIditem());
                }
                commonRatesService.updateRateTranslations(eventRate.getElementocomdescripcion(), rateDTO.getTranslations());
            }
            updateDefaultRates(eventRates, newDefaultRates, rateDTO, eventRate);
            RateConverter.updateRecord(eventRate, rateDTO);
            rateDao.update(eventRate);
        }
    }

    private static void fixEmptyTranslations(UpdateEventRateDTO rate, CpanelTarifaRecord eventRate) {
        Map<String, String> translations = rate.getTranslations().entrySet().stream()
                .peek(elem -> {
                    if (elem.getValue() == null || elem.getValue().isEmpty()) {
                        elem.setValue(eventRate.getNombre());
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        rate.setTranslations(translations);
    }

    @MySQLWrite
    public void updateEventRate(Integer eventId, Integer rateId, UpdateEventRateDTO updateEventRateDTO) {

        this.checkEvent(eventId);

        EventConfig eventConfig = eventConfigCouchDao.get(String.valueOf(eventId));
        if (EventUtils.isItalianCompliance(EventUtils.getInventoryProvider(eventConfig))) {
            if (updateEventRateDTO.getExternalRateTypeId() == null) {
                throw new OneboxRestException(MsEventRateErrorCode.RATE_EXTERNAL_TYPE_REQUIRED);
            }
        }

        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);
        CpanelTarifaRecord eventRate = eventRates.stream().
                filter(r -> r.getIdtarifa().equals(rateId)).findAny().orElse(null);
        if (eventRate == null) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update event: " + eventId + " - Rate not found for event").build();
        }

        if (updateEventRateDTO.getName() != null && !eventRate.getNombre().equals(updateEventRateDTO.getName())) {
            this.checkEventRateNames(updateEventRateDTO.getName(), eventRates);
        }
        if (updateEventRateDTO.getDefaultRate() != null) {
            if (CommonUtils.isTrue(updateEventRateDTO.getDefaultRate())) {
                commonRatesService.unsetDefaultEventRate(eventRates);
            } else if (CommonUtils.isTrue(eventRate.getDefecto())) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).
                        setMessage("Update event: " + eventId + " - Cant disable the default rate, another one must be set as default before.").build();
            }
        }
        if (updateEventRateDTO.getTranslations() != null) {
            fixEmptyTranslations(updateEventRateDTO, eventRate);
            commonRatesService.updateRateTranslations(eventRate.getElementocomdescripcion(), updateEventRateDTO.getTranslations());
        }

        RateConverter.updateRecord(eventRate, updateEventRateDTO);
        rateDao.update(eventRate);
    }

    @MySQLWrite
    public void deleteEventRate(Integer eventId, Integer rateId) {

        CpanelEventoRecord cpanelEventoRecord = this.checkEvent(eventId);

        EventType eventType = EventType.byId(cpanelEventoRecord.getTipoevento());

        CpanelTarifaRecord eventRate = commonRatesService.checkEventRateToDelete(eventType, eventId, rateId);

        cleanEventRateRestrictions(eventId, rateId);
        priceZoneAssignmentDao.deleteByRateId(rateId);
        groupPricesDao.deleteByRateId(rateId);
        rateDao.delete(eventRate);
    }

    private void cleanEventRateRestrictions(Integer eventId, Integer rateId) {
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig != null && eventConfig.getRestrictions() != null && MapUtils.isNotEmpty(eventConfig.getRestrictions().getRates())) {
            eventConfig.getRestrictions().getRates().remove(rateId);
            eventConfig.getRestrictions().getRates().forEach((key, value) -> {
                if (value.getRateRelationsRestriction() != null && CollectionUtils.isNotEmpty(value.getRateRelationsRestriction().getRequiredRates())) {
                    value.getRateRelationsRestriction().getRequiredRates().stream().filter(requiredRate -> requiredRate.getId().equals(rateId.longValue())).findAny()
                        .ifPresent(requiredRate -> value.getRateRelationsRestriction().getRequiredRates().remove(requiredRate));
                    if (CollectionUtils.isEmpty(value.getRateRelationsRestriction().getRequiredRates())) {
                        value.setRateRelationsRestriction(null);
                    }
                }
            });
            eventConfigCouchDao.upsert(eventId.toString(), eventConfig);
        }
    }

    public EventRateRestrictionsDTO getEventRateRestrictions(Integer eventId, Integer rateId) {
        checkEvent(eventId);
        commonRatesService.checkEventRate(eventId, rateId);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getRates() == null) {
            throw new OneboxRestException(MsEventErrorCode.RATE_RESTRICTIONS_NOT_FOUND);
        }

        RateRestrictions rateRestrictions = eventConfig.getRestrictions().getRates().get(rateId);

        if (rateRestrictions == null) {
            throw new OneboxRestException(MsEventErrorCode.RATE_RESTRICTIONS_NOT_FOUND);
        }

        return RateConverter.convert(rateRestrictions);
    }

    public List<IdNameCodeDTO> getRateExternalTypes(Long eventId) {
        List<IdNameCodeDTO> result = new ArrayList<>();
        checkEvent(eventId.intValue());
		Optional<EventConfig> eventConfigOpt = Optional.ofNullable(eventConfigCouchDao.get(eventId.toString()));
		eventConfigOpt.ifPresent(eventConfig -> {
			if (eventConfig.getInventoryProvider() != null
					&& Provider.ITALIAN_COMPLIANCE.equals(eventConfig.getInventoryProvider())) {
				List<CpanelExternalRateTypeRecord> externalRateTypeRecords = rateExternalTypeDao
						.getEventRateExternalTypes(eventConfig.getInventoryProvider().name());
				for (CpanelExternalRateTypeRecord cpanelExternalRateTypeRecord : externalRateTypeRecords) {
					IdNameCodeDTO idNameCodeDTO = new IdNameCodeDTO(cpanelExternalRateTypeRecord.getId().longValue(),
							cpanelExternalRateTypeRecord.getName(), cpanelExternalRateTypeRecord.getCode());
					result.add(idNameCodeDTO);
				}
			}
		});

        return result;
    }

    public void updateEventRateRestrictions(Integer eventId, Integer rateId, UpdateRateRestrictionsDTO restrictionsDTO) {
        CpanelEventoRecord event = checkEvent(eventId);
        commonRatesService.checkEventRate(eventId, rateId);
        rateRestrictionsValidator.validateRateRestrictions(restrictionsDTO);
        List<IdNameDTO> requiredRates = rateRestrictionsValidator.validateRateRelationsRestriction(restrictionsDTO.getRateRelationsRestriction(), eventId, rateId);
        rateRestrictionsValidator.validatePriceZoneRestriction(restrictionsDTO.getPriceZoneRestriction(),eventId);

        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(Long.valueOf(eventId));
        if (eventConfig.getRestrictions() == null) {
            eventConfig.setRestrictions(new Restrictions());
        }
        if (eventConfig.getRestrictions().getRates() == null) {
            eventConfig.getRestrictions().setRates(new RatesRestrictions());
        }
        CustomerTypes customerTypes = rateRestrictionsValidator.validateCustomerTypesRestriction(restrictionsDTO, event.getIdentidad());
        rateRestrictionsValidator.validateChannelRestrictions(restrictionsDTO.getChannelRestriction(), eventId);

        eventConfig.getRestrictions().getRates().compute(rateId,
                (k, restrictions) -> RateConverter.convert(restrictionsDTO, restrictions, customerTypes, requiredRates));

        eventConfigCouchDao.upsert(eventId.toString(), eventConfig);
    }

    public void deleteEventRateRestrictions(Integer eventId, Integer rateId) {
        checkEvent(eventId);
        commonRatesService.checkEventRate(eventId, rateId);
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getRates() == null
                || !eventConfig.getRestrictions().getRates().containsKey(rateId)) {
            throw new OneboxRestException(MsEventErrorCode.RATE_RESTRICTIONS_NOT_FOUND);
        }

        eventConfig.getRestrictions().getRates().remove(rateId);

        eventConfigCouchDao.upsert(eventId.toString(), eventConfig);
    }

    public List<RateRestrictedDTO> getRestrictedRates(Integer eventId) {
        checkEvent(eventId);
        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (eventConfig == null || eventConfig.getRestrictions() == null || eventConfig.getRestrictions().getRates() == null) {
            return Collections.emptyList();
        }

        RatesRestrictions rateRestrictions = eventConfig.getRestrictions().getRates();

        return RateConverter.convertRecord(rateRestrictions, eventRates);
    }

    private void updateDefaultRates(List<CpanelTarifaRecord> eventRates, long newDefaultRates,
                                    UpdateEventRateDTO rateDTO, CpanelTarifaRecord eventRate) {
        if (rateDTO.getDefaultRate() != null) {
            if (CommonUtils.isTrue(rateDTO.getDefaultRate())) {
                commonRatesService.unsetDefaultEventRate(eventRates);
            } else if (CommonUtils.isTrue(eventRate.getDefecto()) && newDefaultRates == 0) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).
                        setMessage("Cant disable the default rate, another one must be set as default before.").build();
            }
        }
    }

    private CpanelEventoRecord checkEvent(Integer eventId) {
        try {
            return eventDao.getById(eventId);
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + eventId + " not found").build();
        }
    }

    private void checkEventRateNames(String newRateName, List<CpanelTarifaRecord> eventRates) {
        checkEventRateNames(newRateName, eventRates.stream().
                map(CpanelTarifaRecord::getNombre).collect(Collectors.toList()));
    }

    private void checkEventRateNames(String newRateName, Collection<String> rateNames) {
        if (rateNames.stream().anyMatch(newRateName::equals)) {
            throw OneboxRestException.builder(MsEventErrorCode.REPEATED_NAME).
                    setMessage("Rate name:" + newRateName + " already in use").build();
        }
    }

    public void deleteEventRateForAvetEvents(Integer eventId, Integer rateId, Integer sessionId) {
        CpanelEventoRecord cpanelEventoRecord = this.checkEvent(eventId);

        EventType eventType = EventType.byId(cpanelEventoRecord.getTipoevento());

        CpanelTarifaRecord eventRate = commonRatesService.checkEventRateToDelete(eventType, eventId, rateId);

        priceZoneAssignmentDao.deleteByRateId(rateId);
        sessionRateDao.deleteRateForSessionId(sessionId, rateId);
        groupPricesDao.deleteByRateId(rateId);
        rateDao.delete(eventRate);
    }

}
