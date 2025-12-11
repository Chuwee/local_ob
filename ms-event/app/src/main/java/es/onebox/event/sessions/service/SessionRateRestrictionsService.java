package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.domain.RateRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.events.converter.RateConverter;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.utils.RateRestrictionsValidator;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SessionRateRestrictionsService {

    private final PriceTypeConfigDao priceTypeConfigDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final SessionValidationHelper sessionValidationHelper;
    private final RateDao rateDao;
    private final CommonRatesService commonRatesService;
    private final RateRestrictionsValidator rateRestrictionsValidator;


    @Autowired
    public SessionRateRestrictionsService(
            PriceTypeConfigDao priceTypeConfigDao,
            SessionConfigCouchDao sessionConfigCouchDao,
            SessionValidationHelper sessionValidationHelper,
            RateDao rateDao,
            CommonRatesService commonRatesService,
            RateRestrictionsValidator rateRestrictionsValidator) {
        this.priceTypeConfigDao = priceTypeConfigDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.sessionValidationHelper = sessionValidationHelper;
        this.rateDao = rateDao;
        this.commonRatesService = commonRatesService;
        this.rateRestrictionsValidator = rateRestrictionsValidator;
    }

    public void upsertSessionRateRestrictions(Long eventId, Long sessionId, Integer rateId, UpdateRateRestrictionsDTO restrictionsRequest) {
        if(Objects.isNull(restrictionsRequest)){
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE_RESTRICTIONS);
        }

        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        commonRatesService.checkEventRate(eventId.intValue(), rateId);
        rateRestrictionsValidator.validateRateRestrictions(restrictionsRequest);
        List <IdNameDTO> requiredRates = rateRestrictionsValidator.validateRateRelationsRestriction(restrictionsRequest.getRateRelationsRestriction(), eventId.intValue(), rateId);
        rateRestrictionsValidator.validatePriceZoneRestriction(restrictionsRequest.getPriceZoneRestriction(),eventId.intValue());
        CustomerTypes customerTypes = rateRestrictionsValidator.validateCustomerTypesRestriction(restrictionsRequest, session.getEntityId());
        rateRestrictionsValidator.validateChannelRestrictions(restrictionsRequest.getChannelRestriction(), eventId.intValue());
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);

        if (sessionConfig.getRestrictions() == null) {
            sessionConfig.setRestrictions(new Restrictions());
        }
        if(sessionConfig.getRestrictions().getRates() == null){
            sessionConfig.getRestrictions().setRates(new RatesRestrictions());
        }

        RateRestrictions previousRestriction = sessionConfig.getRestrictions().getRates().get(rateId);
        sessionConfig.getRestrictions().getRates()
                .put(
                        rateId,
                        RateConverter.convert(restrictionsRequest, previousRestriction, customerTypes, requiredRates)
                );
        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    public void deleteSessionRateRestrictions(Long eventId, Long sessionId, Integer rateId) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        commonRatesService.checkEventRate(eventId.intValue(), rateId);
        validateRestrictionExists(sessionConfig, rateId);

        sessionConfig.getRestrictions().getRates().remove(rateId);
        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    public List<RateRestrictedDTO> getRestrictedRates(Long eventId, Long sessionId) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId.intValue());

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sessionConfig == null || sessionConfig.getRestrictions() == null || sessionConfig.getRestrictions().getRates() == null) {
            return Collections.emptyList();
        }

        RatesRestrictions rateRestrictions = sessionConfig.getRestrictions().getRates();

        return RateConverter.convertRecord(rateRestrictions, eventRates);
    }

    private List<Integer> getPriceTypeIds(Integer templateId) {
        return priceTypeConfigDao.findByVenueTemplateId(templateId).stream()
                .map(CpanelZonaPreciosConfigRecord::getIdzona).collect(Collectors.toList());
    }

    private List<Integer> getRateIds(Integer eventId) {
        List<CpanelTarifaRecord> eventRates = rateDao.getEventRates(eventId);
        if (CollectionUtils.isEmpty(eventRates)) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE)
                    .setMessage("Update rate restrictions: " + eventId + " - Rate not found for event").build();
        }
        return eventRates.stream().map(CpanelTarifaRecord::getIdtarifa).collect(Collectors.toList());
    }

    private static void validateRestrictionExists(SessionConfig sessionConfig, Integer rateId){
        if (sessionConfig.getRestrictions() == null
                || MapUtils.isEmpty(sessionConfig.getRestrictions().getRates())
                || sessionConfig.getRestrictions().getRates().get(rateId) == null) {

            throw new OneboxRestException(MsEventErrorCode.RATE_RESTRICTIONS_NOT_FOUND);
        }
    }

}
