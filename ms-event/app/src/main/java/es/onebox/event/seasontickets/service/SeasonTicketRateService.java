package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.GroupPricesDao;
import es.onebox.event.events.dao.PriceZoneAssignmentDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.seasontickets.amqp.renewals.elastic.RenewalsElasticUpdaterService;
import es.onebox.event.seasontickets.converter.SeasonTicketRateConverter;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketRatesDTO;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeasonTicketRateService {

    private final SeasonTicketRateServiceHelper seasonTicketRateServiceHelper;
    private final SeasonTicketRateValidator seasonTicketRateValidator;

    private final RateDao rateDao;
    private final CommonRatesService commonRatesService;
    private final SessionRateDao sessionRateDao;
    private final PriceZoneAssignmentDao priceZoneAssignmentDao;
    private final GroupPricesDao groupPricesDao;
    private final SeasonTicketRenewalsService seasonTicketRenewalsService;
    private final RenewalsElasticUpdaterService renewalsElasticUpdaterService;
    private final ItemDescSequenceDao itemDescSequenceDao;

    @Autowired
    public SeasonTicketRateService(SeasonTicketRateServiceHelper seasonTicketRateServiceHelper,
                                   SeasonTicketRateValidator seasonTicketRateValidator, RateDao rateDao,
                                   CommonRatesService commonRatesService, SessionRateDao sessionRateDao,
                                   PriceZoneAssignmentDao priceZoneAssignmentDao, GroupPricesDao groupPricesDao,
                                   @Lazy SeasonTicketRenewalsService seasonTicketRenewalsService, RenewalsElasticUpdaterService renewalsElasticUpdaterService,
                                   ItemDescSequenceDao itemDescSequenceDao) {
        this.seasonTicketRateServiceHelper = seasonTicketRateServiceHelper;
        this.seasonTicketRateValidator = seasonTicketRateValidator;
        this.rateDao = rateDao;
        this.commonRatesService = commonRatesService;
        this.sessionRateDao = sessionRateDao;
        this.priceZoneAssignmentDao = priceZoneAssignmentDao;
        this.groupPricesDao = groupPricesDao;
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
        this.renewalsElasticUpdaterService = renewalsElasticUpdaterService;
        this.itemDescSequenceDao = itemDescSequenceDao;
    }

    @MySQLRead
    public SeasonTicketRatesDTO findRatesBySeasonTicketId(Integer seasonTicketId, RatesFilter filter) {
        seasonTicketRateValidator.checkSeasonTicket(seasonTicketId);

        Integer sessionId = seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId);
        List<Integer> visibleIdRates = seasonTicketRateServiceHelper.getVisibleIdRatesFromSession(sessionId);

        SeasonTicketRatesDTO seasonTicketRatesDTO = new SeasonTicketRatesDTO();
        seasonTicketRatesDTO.setMetadata(MetadataBuilder.build(filter, rateDao.countByEventId(seasonTicketId)));
        seasonTicketRatesDTO.setData(rateDao.getSeasonTicketRates(seasonTicketId, filter.getLimit(), filter.getOffset()).stream()
                .map(rateRecord -> {
                    SeasonTicketRateDTO seasonTicketRateDTO = SeasonTicketRateConverter.convert(rateRecord);
                    seasonTicketRateDTO.setEnabled(visibleIdRates.contains(seasonTicketRateDTO.getId().intValue()));
                    return seasonTicketRateDTO;
                })
                .collect(Collectors.toList()));

        return seasonTicketRatesDTO;
    }

    @MySQLWrite
    public CommonIdResponse createSeasonTicketRate(Integer seasonTicketId, SeasonTicketRateDTO seasonTicketRateDTO) {
        seasonTicketRateValidator.checkSeasonTicket(seasonTicketId);

        Integer sessionId = seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId);

        List<CpanelTarifaRecord> seasonTicketRates = rateDao.getSeasonTicketRates(seasonTicketId);
        seasonTicketRateValidator.checkSeasonTicketRateNames(seasonTicketRateDTO.getName(), seasonTicketRates);
        seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);

        if (CollectionUtils.isEmpty(seasonTicketRates)) {
            seasonTicketRateDTO.setPosition(1);
        } else {
            seasonTicketRateDTO.setPosition(seasonTicketRates.stream().map(CpanelTarifaRecord::getPosition).filter(Objects::nonNull).max(Integer::compareTo).orElse(0) + 1);
        }

        Integer itemDescSequenceId = commonRatesService.insertRateTranslations(seasonTicketRateDTO.getTranslations());
        Integer rateId = commonRatesService.createRate(seasonTicketRateDTO, seasonTicketId, itemDescSequenceId);
        commonRatesService.updateSeasonTicketVenueTemplatePriceZones(seasonTicketId, rateId);

        List<SessionRate> sessionRates;
        if (CommonUtils.isTrue(seasonTicketRateDTO.getDefaultRate())) {
            // Add existing ones to session rates list
            sessionRates = sessionRateDao.getRatesBySessionId(sessionId).stream()
                    .map(rate -> new SessionRate(sessionId.longValue(), rate.getIdtarifa(), false))
                    .collect(Collectors.toList());
            commonRatesService.unsetDefaultSeasonTicketRate(seasonTicketRates, sessionId);
        } else {
            sessionRates = new ArrayList<>();
        }

        // Force new rate to visible if it is default
        if (CommonUtils.isTrue(seasonTicketRateDTO.getEnabled()) || CommonUtils.isTrue(seasonTicketRateDTO.getDefaultRate())) {
            sessionRates.add(new SessionRate(sessionId.longValue(), rateId, seasonTicketRateDTO.getDefaultRate()));
            sessionRateDao.bulkInsertSessionRates(sessionRates);
        }

        return new CommonIdResponse(rateId);
    }

    @MySQLWrite
    public void updateSeasonTicketRates(Integer seasonTicketId, List<SeasonTicketRateDTO> modifyRates) {
        seasonTicketRateValidator.checkSeasonTicket(seasonTicketId);

        List<CpanelTarifaRecord> actualSeasonTicketRates = rateDao.getSeasonTicketRates(seasonTicketId);

        Map<Integer, String> originalNamesById = actualSeasonTicketRates.stream()
                .collect(Collectors.toMap(
                        CpanelTarifaRecord::getIdtarifa,
                        CpanelTarifaRecord::getNombre
                ));

        // Get a list of all rate ids to modify
        List<Long> modifyIdRates = modifyRates.stream().map(SeasonTicketRateDTO::getId).collect(Collectors.toList());

        // Get the list of all rates in db that are not in the list of rates to modify
        List<CpanelTarifaRecord> notModifyRates = actualSeasonTicketRates.stream().
                filter(r -> !modifyIdRates.contains(r.getIdtarifa().longValue())).collect(Collectors.toList());

        long newDefaultRates = modifyRates.stream().filter(r -> CommonUtils.isTrue(r.getDefaultRate())).count();
        if (newDefaultRates > 1) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("No more than 1 default rate allowed").build();
        }

        Integer sessionId = seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId);

        // We get all actual session rates to get enabled value stored in DB
        Map<Integer, SessionRate> allSessionRatesInDB = sessionRateDao.getRatesBySessionId(sessionId).stream()
                .map(r ->
                        // If there is a new default rate, others are set to false
                        new SessionRate(sessionId.longValue(), r.getIdtarifa(), newDefaultRates == 0 && rateWasDefault(r))
                )
                .collect(Collectors.toMap(SessionRate::getRateId, Function.identity()));

        // We delete the list of visible rates to insert them later, so we store all in db that are not in the modify rate list
        List<SessionRate> allSessionRatesToBulk = allSessionRatesInDB.values().stream()
                // Add all session rates in db that are not in the list of rates to modify
                .filter(r -> !modifyIdRates.contains(r.getRateId().longValue()))
                .collect(Collectors.toList());
        sessionRateDao.cleanRatesForSessionId(sessionId);

        for (SeasonTicketRateDTO seasonTicketRateDTO : modifyRates) {
            if (seasonTicketRateDTO.getId() == null) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                        .setMessage("Rate not found by id").build();
            }
            CpanelTarifaRecord actualRateToModify = actualSeasonTicketRates.stream().
                    filter(r -> r.getIdtarifa().equals(seasonTicketRateDTO.getId().intValue())).findAny().orElse(null);
            if (actualRateToModify == null) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                        .setMessage("Rate not found for season ticket").build();
            }

            checkRateNames(modifyRates, notModifyRates, seasonTicketRateDTO, actualRateToModify);

            seasonTicketRateValidator.checkDescription(seasonTicketRateDTO);

            if (seasonTicketRateDTO.getEnabled() == null) {
                seasonTicketRateDTO.setEnabled(allSessionRatesInDB.containsKey(Math.toIntExact(seasonTicketRateDTO.getId())));
            }

            updateDefaultRates(actualSeasonTicketRates, newDefaultRates, sessionId, seasonTicketRateDTO, actualRateToModify);

            // Force rate to enabled if it is default
            if (CommonUtils.isTrue(seasonTicketRateDTO.getEnabled()) || CommonUtils.isTrue(seasonTicketRateDTO.getDefaultRate()) || rateWasDefault(actualRateToModify)) {
                boolean isDefault = CommonUtils.isTrue(seasonTicketRateDTO.getDefaultRate()) || rateWasDefault(actualRateToModify);
                allSessionRatesToBulk.add(new SessionRate(sessionId.longValue(), seasonTicketRateDTO.getId().intValue(), isDefault));
            }

            if (seasonTicketRateDTO.getTranslations() != null) {
                if (actualRateToModify.getElementocomdescripcion() == null) {
                    CpanelItemDescSequenceRecord cpanelItemDescSequence = new CpanelItemDescSequenceRecord();
                    cpanelItemDescSequence.setDescripcion("rate item");
                    actualRateToModify.setElementocomdescripcion(itemDescSequenceDao.insert(cpanelItemDescSequence).getIditem());
                }
                commonRatesService.updateRateTranslations(actualRateToModify.getElementocomdescripcion(), seasonTicketRateDTO.getTranslations());
            }

            SeasonTicketRateConverter.updateRecord(actualRateToModify, seasonTicketRateDTO);
            rateDao.update(actualRateToModify);
        }
        sessionRateDao.bulkInsertSessionRates(allSessionRatesToBulk);

        updateRateNameOnRenewals(modifyRates, originalNamesById);
    }

    @MySQLWrite
    public void deleteSeasonTicketRate(Integer seasonTicketId, Integer rateId) {
        seasonTicketRateValidator.checkSeasonTicket(seasonTicketId);

        CpanelTarifaRecord eventRate = commonRatesService.checkSeasonTicketRateToDelete(seasonTicketId, rateId);

        Integer sessionId = seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId);
        validateRateUsedOnRenewals(seasonTicketId, rateId);
        sessionRateDao.deleteRateForSessionId(sessionId, rateId);

        priceZoneAssignmentDao.deleteByRateId(rateId);
        groupPricesDao.deleteByRateId(rateId);
        rateDao.delete(eventRate);
    }

    @MySQLWrite
    public void cleanRatesForSessionId(Integer sessionId) {
        sessionRateDao.cleanRatesForSessionId(sessionId);
    }

    private void checkRateNames(List<SeasonTicketRateDTO> modifyRates, List<CpanelTarifaRecord> notModifyRates,
                                SeasonTicketRateDTO seasonTicketRateDTO, CpanelTarifaRecord actualRateToModify) {
        if (seasonTicketRateDTO.getName() != null && !actualRateToModify.getNombre().equals(seasonTicketRateDTO.getName())) {
            // Check name over db rates that are NOT IN payload
            seasonTicketRateValidator.checkSeasonTicketRateNames(seasonTicketRateDTO.getName(), notModifyRates);

            // Check name over db rates that are IN payload
            seasonTicketRateValidator.checkSeasonTicketRateNames(seasonTicketRateDTO.getName(), modifyRates.stream().
                    filter(r -> !r.getId().equals(seasonTicketRateDTO.getId())).
                    map(SeasonTicketRateDTO::getName).collect(Collectors.toList()));
        }
    }


    private void updateDefaultRates(List<CpanelTarifaRecord> actualSeasonTicketRates, long newDefaultRates,
                                    Integer sessionId, SeasonTicketRateDTO seasonTicketRateDTO, CpanelTarifaRecord actualRateToModify) {
        if (seasonTicketRateDTO.getDefaultRate() != null) {
            if (CommonUtils.isTrue(seasonTicketRateDTO.getDefaultRate())) {
                commonRatesService.unsetDefaultSeasonTicketRate(actualSeasonTicketRates, sessionId);
            } else
                // Check that almost one rate is set default
                if (CommonUtils.isTrue(actualRateToModify.getDefecto()) && newDefaultRates == 0) {
                    throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).
                            setMessage("Cant disable the default rate, another one must be set as default before.").build();
                }
        }
    }

    private Boolean rateWasDefault(CpanelTarifaRecord actualRateToModify) {
        return ConverterUtils.isByteAsATrue(actualRateToModify.getDefecto());
    }

    private void validateRateUsedOnRenewals(Integer seasonTicketId, Integer rateId) {
        if(seasonTicketRenewalsService.isRateUsedOnRenewal(seasonTicketId.longValue(), rateId.longValue())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RATE_USED_ON_RENEWAL);
        }
    }

    private void updateRateNameOnRenewals(List<SeasonTicketRateDTO> modifyRates, Map<Integer, String> originalNamesById) {
        modifyRates.stream()
                .filter(rate -> {
                    String originalName = originalNamesById.get(rate.getId().intValue());
                    return originalName != null && rate.getName() != null && !originalName.equals(rate.getName());
                })
                .map(SeasonTicketRateDTO::getId)
                .forEach(renewalsElasticUpdaterService::sendMessage);
    }
}
