package es.onebox.event.sessions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.converter.PriceTypeRestrictionConverter;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.PriceTypeRestrictionDTO;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

@Service
public class SessionSaleRestrictionsService {

    private final PriceTypeConfigDao priceTypeConfigDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final SessionValidationHelper sessionValidationHelper;

    @Autowired
    public SessionSaleRestrictionsService(
            PriceTypeConfigDao priceTypeConfigDao,
            SessionConfigCouchDao sessionConfigCouchDao,
            SessionValidationHelper sessionValidationHelper) {
        this.priceTypeConfigDao = priceTypeConfigDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.sessionValidationHelper = sessionValidationHelper;
    }

    @MySQLWrite
    public void upsertSaleRestrictions(Long eventId, Long sessionId, Long priceTypeId, UpdateSaleRestrictionDTO request) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypes(session.getVenueTemplateId());
        validatePriceTypeExists(priceTypeId, priceTypesById);
        
        if(CollectionUtils.isEmpty(request.getRequiredPriceTypeIds())){
            throw new OneboxRestException(MsEventSessionErrorCode.REQUIRED_PRICE_TYPE_MANDATORY);
        }
        
        request.getRequiredPriceTypeIds().forEach(id -> validatePriceTypeExists(id, priceTypesById));
        if (request.getRequiredPriceTypeIds().stream().anyMatch(priceTypeId::equals)) {
            throw new OneboxRestException(MsEventSessionErrorCode.CIRCULAR_PRICE_TYPE_RESTRICTION);
        }

        request.getRequiredPriceTypeIds().forEach(id -> validatePriceTypeExists(id, priceTypesById));
        if ((request.getLockedTicketsNumber() != null && request.getRequiredTicketsNumber() != null)
                || (request.getLockedTicketsNumber() == null && request.getRequiredTicketsNumber() == null)) {
            throw new OneboxRestException(MsEventSessionErrorCode.TICKET_NUMBER_EXCLUSION_INPUT);
        }

        PriceZoneRestriction pzr = PriceTypeRestrictionConverter.convert(request.getRequiredPriceTypeIds(),
                request.getRequiredTicketsNumber(), request.getLockedTicketsNumber());

        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sessionConfig.getRestrictions() == null) {
            sessionConfig.setRestrictions(new Restrictions());
        }
        if(sessionConfig.getRestrictions().getPriceZones() == null){
            sessionConfig.getRestrictions().setPriceZones(new PriceZonesRestrictions());
        }
        sessionConfig.getRestrictions().getPriceZones().put(priceTypeId.intValue(), pzr);

        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    @MySQLWrite
    public void deleteSaleRestrictions(Long eventId, Long sessionId, Long priceTypeId) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypes(session.getVenueTemplateId());
        validatePriceTypeExists(priceTypeId, priceTypesById);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        validateRestrictionExists(sessionConfig, priceTypeId);
        sessionConfig.getRestrictions().getPriceZones().remove(priceTypeId.intValue());
        sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
    }

    @MySQLRead
    public PriceTypeRestrictionDTO getSaleRestriction(Long eventId, Long sessionId, Long priceTypeId) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        validateRestrictionExists(sessionConfig, priceTypeId);
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypes(session.getVenueTemplateId());
        validatePriceTypeExists(priceTypeId, priceTypesById);
        PriceZoneRestriction priceZoneRestriction = sessionConfig.getRestrictions().getPriceZones().get(priceTypeId.intValue());
        return PriceTypeRestrictionConverter.convert(priceTypeId, priceZoneRestriction, priceTypesById);
    }

    @MySQLWrite
    public List<IdNameDTO> getRestrictedPriceTypes(Long eventId, Long sessionId) {
        SessionRecord session = sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);

        if(sessionConfig.getRestrictions() == null || sessionConfig.getRestrictions().getPriceZones() == null
        || sessionConfig.getRestrictions().getPriceZones().isEmpty()) {
            return new ArrayList<>();
        }
        PriceZonesRestrictions priceZonesRestrictions = sessionConfig.getRestrictions().getPriceZones();
        Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById = getPriceTypes(session.getVenueTemplateId());
        return PriceTypeRestrictionConverter.convert(priceZonesRestrictions, priceTypesById);
    }

    private Map<Integer, CpanelZonaPreciosConfigRecord> getPriceTypes(Integer templateId) {
        return priceTypeConfigDao.findByVenueTemplateId(templateId).stream()
                .collect(Collectors.toMap(CpanelZonaPreciosConfigRecord::getIdzona, Function.identity()));
    }

    private static void validatePriceTypeExists(Long priceTypeId, Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById) {
        if (!priceTypesById.containsKey(priceTypeId.intValue())) {
            throw new OneboxRestException(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);
        }
    }

    private static void validateRestrictionExists(SessionConfig sessionConfig, Long priceTypeId){
        if (sessionConfig.getRestrictions() == null || sessionConfig.getRestrictions().getPriceZones() == null ||
                !sessionConfig.getRestrictions().getPriceZones().containsKey(priceTypeId.intValue())) {
            throw new OneboxRestException(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND);
        }
    }

}
