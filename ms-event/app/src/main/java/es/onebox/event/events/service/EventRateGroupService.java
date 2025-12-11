package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.services.CommonRatesGroup;
import es.onebox.event.datasources.integration.avet.config.dto.AvetPrice;
import es.onebox.event.datasources.integration.avet.config.dto.ClubConfig;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.events.amqp.avetintegration.IntegrationAvetService;
import es.onebox.event.events.converter.RateGroupConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateGroupSessionRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.CreateRateGroupRequestDTO;
import es.onebox.event.events.dto.RateGroupDTO;
import es.onebox.event.events.dto.RateGroupType;
import es.onebox.event.events.dto.RatesGroupDTO;
import es.onebox.event.events.dto.UpdateRateGroupRequestDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.language.dao.LanguageDao;
import es.onebox.event.venues.dao.VenueTemplateDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelAsignacionZonaPreciosRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGrupoTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventRateGroupService {

    private static final String DEFAULT_RATE = "General";
    private static final String DEFAULT_RATE_LANG = "es_ES";
    private final EventDao eventDao;
    private final RateGroupDao rateGroupDao;
    private final RateDao rateDao;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final LanguageDao languageDao;
    private final VenueTemplateDao venueTemplateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final IntegrationAvetService integrationAvetService;
    private final EventConfigCouchDao eventConfigCouchDao;

    @Autowired
    public EventRateGroupService(EventDao eventDao, RateGroupDao rateGroupDao, RateDao rateDao,
                                 ItemDescSequenceDao itemDescSequenceDao, DescPorIdiomaDao descPorIdiomaDao,
                                 LanguageDao languageDao, VenueTemplateDao venueTemplateDao,
                                 PriceZoneAssignmentDao priceZoneAssignmentDao,
                                 IntAvetConfigRepository intAvetConfigRepository, IntegrationAvetService integrationAvetService, EventConfigCouchDao eventConfigCouchDao) {
        this.eventDao = eventDao;
        this.rateGroupDao = rateGroupDao;
        this.rateDao = rateDao;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.languageDao = languageDao;
        this.venueTemplateDao = venueTemplateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.integrationAvetService = integrationAvetService;
        this.eventConfigCouchDao = eventConfigCouchDao;
    }

    @MySQLRead
    public RatesGroupDTO findRatesGroupByEventId(Integer eventId, RatesFilter filter) {
        checkEvent(eventId);
        RatesGroupDTO ratesResponses = new RatesGroupDTO();
        ratesResponses.setMetadata(MetadataBuilder.build(filter, rateGroupDao.countByEventId(eventId)));
        ratesResponses.setData(rateGroupDao.getRatesGroupByEventId(eventId, filter.getType(), filter.getLimit(), filter.getOffset()).stream()
                .map(RateGroupConverter::convert)
                .collect(Collectors.toList()));
        return ratesResponses;
    }

    @MySQLRead
    public RateGroupDTO findRate(Integer eventId, Integer rateId) {
        List<RateGroupRecord> rateRecords = rateGroupDao.getRatesGroupByEventId(eventId);
        Optional<RateGroupRecord> rateRecord = rateRecords.stream().filter(rr -> rr.getIdGrupoTarifa().equals(rateId)).findFirst();
        return rateRecord.map(RateGroupConverter::convert).orElse(null);
    }

    @MySQLWrite
    public CommonIdResponse createEventRateGroup(Integer eventId, CreateRateGroupRequestDTO createRateGroupDTO) {

        CpanelEventoRecord eventoRecord = checkEvent(eventId);

        List<CpanelGrupoTarifaRecord> eventRates = rateGroupDao.getEventRates(eventId);

        RateGroupDTO rateGroupDTO = RateGroupConverter.toDTO(createRateGroupDTO);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());

        boolean isSGAEvent = EventUtils.isSGA(EventUtils.getInventoryProvider(eventConfig));

        if (!isSGAEvent) {
            CommonRatesGroup.checkEventRateNames(rateGroupDTO.getName(), eventRates.stream().
                    map(CpanelGrupoTarifaRecord::getNombre).collect(Collectors.toList()));
        }

        CommonRatesGroup.checkEventRateExternalDescription(rateGroupDTO.getExternalDescription(),
                eventRates.stream().map(CpanelGrupoTarifaRecord::getDescripcionexterna).
                collect(Collectors.toList()));

        boolean isAvetEvent = EventType.AVET.getId().equals(eventoRecord.getTipoevento());

        Integer clubCode = null;
        Integer seasonCode = null;
        Integer capacityId = null;

        if(isAvetEvent) {
            List<AvetPrice> pricesList = intAvetConfigRepository.getAvetPrices(eventId);

            if(pricesList != null
                    && !pricesList.isEmpty()
                    && pricesList.get(0) != null){
                AvetPrice price = pricesList.get(0);
                clubCode = price.getClubCode();
                seasonCode = price.getSeasonCode();
                capacityId = price.getCapacityId();
            }

            if(rateGroupDTO.getExternalDescription() != null) {
                CommonRatesGroup.checkExternalDescriptionExistsInAVET(rateGroupDTO.getExternalDescription(), pricesList);
            }

        }

        Integer itemDescSequenceId = null;
        if (rateGroupDTO.getTranslations() != null) {
            CommonRatesGroup.fixEmptyTranslations(rateGroupDTO, createRateGroupDTO.getName());
            itemDescSequenceId = insertRateTranslations(rateGroupDTO.getTranslations());
        }

        Integer position = CommonRatesGroup.getEventRateGroupPosition(eventRates, createRateGroupDTO.getType());

        CpanelGrupoTarifaRecord cpanelGrupoTarifaRecord =
                rateGroupDao.insert(CommonRatesGroup.createRateGroup(rateGroupDTO, eventId, itemDescSequenceId, position));
        Integer rateGroupId = cpanelGrupoTarifaRecord.getIdgrupotarifa();

        List<RateGroupSessionRecord> rateSessions = rateGroupDao.getSessionsDefaultRates(eventId);
        //Create cpanelSesionRecord for any session already exists
        for (RateGroupSessionRecord rateSession: rateSessions) {
            String rateSessionName = rateSession.getNombre() != null ? rateSession.getNombre() : "";
            String rateName = rateSessionName + " - " +  rateGroupDTO.getName();
            CpanelTarifaRecord cpanelTarifaRecord =
                    CommonRatesGroup.createSessionRates(rateName, rateGroupId, eventId, itemDescSequenceId);

            cpanelTarifaRecord = rateDao.insert(cpanelTarifaRecord);
            if (cpanelTarifaRecord.getDefecto().equals((byte) 1)) {
                updateEventVenueTemplatePriceZones(eventId, cpanelTarifaRecord.getIdtarifa());
            }
            rateGroupDao.createSesionRate(rateSession.getIdSesion(), cpanelTarifaRecord.getIdtarifa());

        }

        if(isAvetEvent) {
            integrationAvetService.sendMessage(clubCode, seasonCode, capacityId);
        }

        return new CommonIdResponse(rateGroupId);
    }

    @MySQLWrite
    public void updateEventRatesGroup(Integer eventId, List<UpdateRateGroupRequestDTO> updateModifyRatesRequest) {

        CpanelEventoRecord eventoRecord = checkEvent(eventId);

        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        boolean isAvetEvent = EventType.AVET.getId().equals(eventoRecord.getTipoevento());

        List<CpanelGrupoTarifaRecord> eventGroupRates = rateGroupDao.getEventRates(eventId);

        List<RateGroupDTO> groupRatesToModify = RateGroupConverter.toDTO(updateModifyRatesRequest);

        List<Long> rateIds = groupRatesToModify.stream().map(RateGroupDTO::getId).toList();
        List<CpanelGrupoTarifaRecord> eventGroupRatesNotModified = eventGroupRates.stream().
                filter(r -> !rateIds.contains(r.getIdgrupotarifa().longValue())).collect(Collectors.toList());

        CommonRatesGroup.checkNullIds(groupRatesToModify, eventId);
        CommonRatesGroup.checkIfExternalDescriptionIsRepeated(updateModifyRatesRequest, eventGroupRates);

        List<AvetPrice> pricesList = null;

        if(isAvetEvent){
            pricesList = intAvetConfigRepository.getAvetPrices(eventId);
        }

        for (RateGroupDTO rateGroupDTO : groupRatesToModify) {

            CpanelGrupoTarifaRecord currentEventGroupRate = eventGroupRates.stream().
                    filter(r -> r.getIdgrupotarifa().equals(rateGroupDTO.getId().intValue())).findAny().orElse(null);

            CommonRatesGroup.checkGroupEventRateExists(currentEventGroupRate, eventId);

            CommonRatesGroup.checkRateGroupNames(rateGroupDTO, currentEventGroupRate, eventGroupRatesNotModified, groupRatesToModify);

            if(isAvetEvent && rateGroupDTO.getExternalDescription() != null) {
                CommonRatesGroup.checkExternalDescriptionExistsInAVET(rateGroupDTO.getExternalDescription(), pricesList);
            }

            if(rateGroupDTO.getExternalDescription() != null
                    && currentEventGroupRate.getDescripcionexterna() != null
                    && !rateGroupDTO.getExternalDescription().equals(currentEventGroupRate.getDescripcionexterna())
                    && isAvetEvent) {
                //Get all cpanel_tarifa rows related to rateGroupRateId
                List<RateGroupSessionRecord> rateSessions = rateGroupDao.getSessionsRatesByRateId(eventId, rateGroupDTO.getId().intValue());
                //Update cpanelSesionRecord for any session already exists
                for (RateGroupSessionRecord rateSession: rateSessions) {
                    CpanelTarifaRecord cpanelTarifaRecord = rateDao.getEventRate(eventId, rateSession.getIdTarifa());
                    String name = cpanelTarifaRecord.getNombre();
                    String oldExternalDescription = currentEventGroupRate.getDescripcionexterna();
                    name = name.replace(oldExternalDescription, rateGroupDTO.getExternalDescription());
                    cpanelTarifaRecord.setNombre(name);
                    cpanelTarifaRecord.setDescripcion(name);
                    rateDao.update(cpanelTarifaRecord);
                    //update price zone assignment if is updating external_description
                    priceZoneAssignmentDao.deleteByRateId(rateSession.getIdTarifa());
                    if (cpanelTarifaRecord.getDefecto().equals((byte) 1)) {
                        updateEventVenueTemplatePriceZones(eventId, rateSession.getIdTarifa());
                    }
                }
            }
            if (rateGroupDTO.getTranslations() != null) {
                CommonRatesGroup.fixEmptyTranslations(rateGroupDTO, currentEventGroupRate);
                if(currentEventGroupRate.getElementocomdescripcion() == null) {
                    CpanelItemDescSequenceRecord cpanelItemDescSequence = new CpanelItemDescSequenceRecord();
                    cpanelItemDescSequence.setDescripcion("rate item");
                    Integer idItem = itemDescSequenceDao.insert(cpanelItemDescSequence).getIditem();
                    currentEventGroupRate.setElementocomdescripcion(idItem);
                    List<CpanelTarifaRecord> ratesByRateGroupId = rateDao.getRatesByRateGroupId(eventId, rateGroupDTO.getId().intValue());
                    for (CpanelTarifaRecord cpanelTarifaRecord : ratesByRateGroupId) {
                        cpanelTarifaRecord.setElementocomdescripcion(idItem);
                        rateDao.update(cpanelTarifaRecord);
                    }
                    List<CpanelTarifaRecord> ratesByRateGroupIdAndType = rateDao.getRatesByRateGroupIdAndType(eventId, rateGroupDTO.getId().intValue(), RateGroupType.RATE);
                    for (CpanelTarifaRecord cpanelTarifaRecord : ratesByRateGroupIdAndType) {
                        cpanelTarifaRecord.setElementocomdescripcion(idItem);
                        rateDao.update(cpanelTarifaRecord);
                    }
                }
                updateRateTranslations(currentEventGroupRate.getElementocomdescripcion(), rateGroupDTO.getTranslations());
            }
            RateGroupConverter.updateRecord(currentEventGroupRate, rateGroupDTO);
            rateGroupDao.update(currentEventGroupRate);

            if (isAvetEvent) {
                Integer clubCode = null;
                Integer seasonCode = null;
                Integer capacityId = null;

                if(pricesList != null
                        && !pricesList.isEmpty()
                        && pricesList.get(0) != null){
                    AvetPrice price = pricesList.get(0);
                    clubCode = price.getClubCode();
                    seasonCode = price.getSeasonCode();
                    capacityId = price.getCapacityId();
                }
                integrationAvetService.sendMessage(clubCode, seasonCode, capacityId);
            }
        }
    }

    @MySQLWrite
    public void deleteEventRateGroup(Integer eventId, Integer rateGroupId) {
        CpanelEventoRecord cpanelEventoRecord = this.checkEvent(eventId);

        CommonRatesGroup.checkEventsExists(cpanelEventoRecord, eventId);
        CpanelGrupoTarifaRecord eventGroupRate = rateGroupDao.getEventRate(eventId, rateGroupId);
        CommonRatesGroup.checkEventRateToDelete(rateGroupId, eventGroupRate);

        List<RateGroupSessionRecord> rateSessions = rateGroupDao.getSessionsRatesByRateId(eventId, rateGroupId);

        //Delete cpanelSesionRecord for any session already exists
        for (RateGroupSessionRecord rateSession: rateSessions) {
            if(rateSession.getIdGrupoTarifa() != null
                    && rateSession.getIdGrupoTarifa().equals(rateGroupId)) {
                CpanelTarifaRecord cpanelTarifaRecord = rateDao.getEventRate(eventId, rateSession.getIdTarifa());
                rateGroupDao.deleteSessionRate(rateSession.getIdSesion(), rateSession.getIdTarifa());
                priceZoneAssignmentDao.deleteByRateId(rateSession.getIdTarifa());
                rateDao.delete(cpanelTarifaRecord);
            }
        }
        rateGroupDao.delete(eventGroupRate);
    }

    private CpanelEventoRecord checkEvent(Integer eventId) {
        try {
            return eventDao.getById(eventId);
        } catch (Exception ex) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + eventId + " not found").build();
        }
    }

    public void updateRateTranslations(Integer itemId, Map<String, String> translations) {
        Map<String, CpanelIdiomaRecord> availableLanguages = getAvailableLanguages(translations);
        for (Map.Entry<String, String> texts : translations.entrySet()) {
            Integer langId = availableLanguages.get(texts.getKey()).getIdidioma();
            descPorIdiomaDao.upsert(itemId, langId, texts.getValue());
        }
    }

    public Integer insertRateTranslations(Map<String, String> translations) {
        Map<String, CpanelIdiomaRecord> availableLanguages = getAvailableLanguages(translations);

        if (translations != null && !translations.isEmpty()) {
            CpanelItemDescSequenceRecord cpanelItemDescSequence = new CpanelItemDescSequenceRecord();
            cpanelItemDescSequence.setDescripcion("rate item");
            Integer idItem = itemDescSequenceDao.insert(cpanelItemDescSequence).getIditem();
            translations.forEach((code, value) -> {
                CpanelDescPorIdiomaRecord cpanelDescPorIdioma = new CpanelDescPorIdiomaRecord();
                cpanelDescPorIdioma.setIditem(idItem);
                cpanelDescPorIdioma.setIdidioma(availableLanguages.get(code).getIdidioma());
                cpanelDescPorIdioma.setDescripcion(value);
                descPorIdiomaDao.insert(cpanelDescPorIdioma);
            });
            return idItem;
        }
        return null;
    }

    public void createAvetDefaultEventRateGroup(Integer eventId) {
        CpanelGrupoTarifaRecord defaultRate = new CpanelGrupoTarifaRecord();
        defaultRate.setNombre(DEFAULT_RATE);
        defaultRate.setDefecto((byte) 1);
        defaultRate.setIdevento(eventId);
        defaultRate.setElementocomdescripcion(insertDefaultRateElementoComDescripcion());
        rateGroupDao.insert(defaultRate);
    }

    private Integer insertDefaultRateElementoComDescripcion() {
        Map<String, String> translations = new HashMap<>();
        translations.put(DEFAULT_RATE_LANG, DEFAULT_RATE);
        return insertRateTranslations(translations);
    }

    private Map<String, CpanelIdiomaRecord> getAvailableLanguages(Map<String, String> translations) {
        Map<String, CpanelIdiomaRecord> idiomaRecords;
        if (translations != null && !translations.isEmpty()) {
            idiomaRecords = languageDao.getIdiomasByCodes(new ArrayList<>(translations.keySet())).stream()
                    .collect(Collectors.toMap(CpanelIdiomaRecord::getCodigo, Function.identity()));
            if (idiomaRecords.size() != translations.size()) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE)
                        .setMessage("Language not found for rate")
                        .build();
            }
        } else {
            idiomaRecords = new HashMap<>();
        }
        return idiomaRecords;
    }

    public void updateEventVenueTemplatePriceZones(Integer eventId, Integer rateId) {
        final Map<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatesPriceZones =
                venueTemplateDao.getEventVenueTemplatesWithPriceZones(eventId);

        for (Map.Entry<CpanelConfigRecintoRecord, List<CpanelZonaPreciosConfigRecord>> templatePriceZone : templatesPriceZones.entrySet()) {
            for (CpanelZonaPreciosConfigRecord priceZone : templatePriceZone.getValue()) {
                CpanelAsignacionZonaPreciosRecord pz = new CpanelAsignacionZonaPreciosRecord();
                pz.setIdtarifa(rateId);
                pz.setIdzona(priceZone.getIdzona());
                pz.setPrecio(0.0);
                priceZoneAssignmentDao.insert(pz);
            }
        }
    }

    public static <T> T getAdditionalProperty(ClubConfig clubConfig, String key, Class T) {
        Map<String, Object> additionalProperties = clubConfig.getAdditionalProperties();
        if (additionalProperties == null) {
            return null;
        }
        return (T) additionalProperties.get(key);
    }

}
