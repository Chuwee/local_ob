package es.onebox.event.secondarymarket.service;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.MsEntityDatasource;
import es.onebox.event.datasources.ms.entity.dto.CustomerType;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.secondarymarket.dto.CustomerLimitsDTO;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SecondaryMarketService {
    private final EventDao eventDao;
    private final EntityDao entityDao;
    private final SessionValidationHelper sessionValidationHelper;
    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public SecondaryMarketService(EventDao eventDao, EntityDao entityDao, SessionValidationHelper sessionValidationHelper,
        MsEntityDatasource msEntityDatasource) {
        this.eventDao = eventDao;
        this.entityDao = entityDao;
        this.sessionValidationHelper = sessionValidationHelper;
        this.msEntityDatasource = msEntityDatasource;
    }

    public Integer isAllowedByEventEntityOrThrow(Long eventId) {
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        Boolean allowSecondaryMarket = entityDao.getAllowSecondaryMarket(event.getIdentidad());
        SecondaryMarketUtils.validateSecondaryMarketAccess(allowSecondaryMarket);
        return event.getIdentidad();
    }

    public boolean isAllowedByEventEntity(Long eventId) {
        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        return entityDao.getAllowSecondaryMarket(event.getIdentidad());
    }


    public void validateCustomerLimits(CustomerLimitsDTO customerLimits, Integer entityId) {

        if (CollectionUtils.isEmpty(customerLimits.getExcludedCustomerTypes()) && customerLimits.getLimit() == null) {
            return;
        }

        if (customerLimits.getLimit() == null || customerLimits.getLimit() < 0) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_CUSTOMER_LIMITS);
        }

        CustomerTypes customerTypesInEntity = msEntityDatasource.getCustomerTypes(entityId, null);

        if ((customerTypesInEntity == null || CollectionUtils.isEmpty(customerTypesInEntity.getData())) && CollectionUtils.isNotEmpty(customerLimits.getExcludedCustomerTypes())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_CUSTOMER_TYPE);
        }

        if(customerTypesInEntity != null &&  CollectionUtils.isNotEmpty(customerTypesInEntity.getData())) {
            List<String> customerTypesCodeInEntity = customerTypesInEntity.getData().stream().map(CustomerType::getCode).toList();
            if (customerLimits.getExcludedCustomerTypes().stream().anyMatch(customerType -> !customerTypesCodeInEntity.contains(customerType))) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_CUSTOMER_TYPE);
            }
        }

    }

    public void checkSessionAndSecondaryMarketEnabled(Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidate(sessionId);
        Boolean allowSecondaryMarket = entityDao.getAllowSecondaryMarket(sessionRecord.getEntityId());
        SecondaryMarketUtils.validateSecondaryMarketAccess(allowSecondaryMarket);
    }

    public SessionRecord getSessionAndCheckSecondaryMarket(Long sessionId) {
        SessionRecord sessionRecord = sessionValidationHelper.getSessionAndValidate(sessionId);
        Boolean allowSecondaryMarket = entityDao.getAllowSecondaryMarket(sessionRecord.getEntityId());
        SecondaryMarketUtils.validateSecondaryMarketAccess(allowSecondaryMarket);
        return sessionRecord;
    }

    @Cached(key = "entitySecondaryMarket.isActive", expires = 30, timeUnit = TimeUnit.SECONDS)
    public Map<Integer, Boolean> getCachedAllowSecondaryMarket(@CachedArg List<Integer> entityIds) {
        return entityDao.getAllowSecondaryMarketList(entityIds);
    }

    public Boolean getAllowSecondaryMarket(Integer entityId) {
        return entityDao.getAllowSecondaryMarket(entityId);
    }

}