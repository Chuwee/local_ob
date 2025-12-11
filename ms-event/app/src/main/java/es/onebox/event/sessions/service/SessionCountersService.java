package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dao.SessionCustomersCounterCouchDao;
import es.onebox.event.sessions.dao.SessionPresaleCustomersCounterCouchDao;
import es.onebox.event.sessions.dao.SessionSecondaryMarketCustomersCounterCouchDao;
import es.onebox.event.sessions.dto.SessionCustomersActionType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionCountersService {

    private final SessionPresaleCustomersCounterCouchDao sessionPresaleCustomersCounterCouchDao;
    private final SessionCustomersCounterCouchDao sessionCustomersCounterCouchDao;
    private final CatalogSessionCouchDao catalogSessionCouchDao;
    private final SessionSecondaryMarketCustomersCounterCouchDao sessionSecondaryMarketCustomersCounterCouchDao;

    @Autowired
    public SessionCountersService(SessionPresaleCustomersCounterCouchDao sessionPresaleCustomersCounterCouchDao,
                                  SessionCustomersCounterCouchDao sessionCustomersCounterCouchDao,
                                  CatalogSessionCouchDao catalogSessionCouchDao, SessionSecondaryMarketCustomersCounterCouchDao sessionSecondaryMarketCustomersCounterCouchDao) {
        this.sessionPresaleCustomersCounterCouchDao = sessionPresaleCustomersCounterCouchDao;
        this.sessionCustomersCounterCouchDao = sessionCustomersCounterCouchDao;
        this.catalogSessionCouchDao = catalogSessionCouchDao;
        this.sessionSecondaryMarketCustomersCounterCouchDao = sessionSecondaryMarketCustomersCounterCouchDao;
    }

    public Long getPresaleCustomerCounter(Integer sessionId, String customerId) {
        Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
        if (session == null || CollectionUtils.isEmpty(session.getPresales())) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_PRESALE_NOT_FOUND);
        }
        Long counter = sessionPresaleCustomersCounterCouchDao.get(sessionId, customerId);
        return counter == null ? 0 : counter;
    }

    public Long updatePresaleCustomerCounter(Integer sessionId, String customerId, Integer amount, SessionCustomersActionType actionType) {
        if (SessionCustomersActionType.INCREMENT.equals(actionType)) {
            Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
            if (session == null || CollectionUtils.isEmpty(session.getPresales())) {
                throw new OneboxRestException(MsEventErrorCode.SESSION_PRESALE_NOT_FOUND);
            }
            return sessionPresaleCustomersCounterCouchDao.autoIncrementCounter(sessionId, customerId, amount);
        } else {
            Long counter = sessionPresaleCustomersCounterCouchDao.get(sessionId, customerId);
            if (counter != null && counter > 0) {
                return sessionPresaleCustomersCounterCouchDao.autoDecrementCounter(sessionId, customerId, amount);
            }
        }
        return 0L;
    }

    public Long getSessionCustomerCounter(Integer sessionId, Integer priceTypeId, String customerId) {
        Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
        if (session == null) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
        }
        Long counter = sessionCustomersCounterCouchDao.get(sessionId, priceTypeId, customerId);
        return counter == null ? 0 : counter;
    }

    public Long updateSessionCustomerCounter(Integer sessionId, Integer priceTypeId, String customerId, Integer amount, SessionCustomersActionType actionType) {
        if (SessionCustomersActionType.INCREMENT.equals(actionType)) {
            Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
            if (session == null) {
                throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
            }
            if (priceTypeId != null && priceTypeId > 0) {
                return sessionCustomersCounterCouchDao.autoIncrementSCPriceTypeCounter(sessionId, priceTypeId, customerId, amount);
            }
            return sessionCustomersCounterCouchDao.autoIncrementSCCounter(sessionId, customerId, amount);
        } else {
            Long counter = sessionCustomersCounterCouchDao.get(sessionId, priceTypeId, customerId);
            if (counter != null && counter > 0) {
                if (priceTypeId != null && priceTypeId > 0) {
                    return sessionCustomersCounterCouchDao.autoDecrementSCPriceTypeCounter(sessionId, priceTypeId, customerId, amount);
                }
                return sessionCustomersCounterCouchDao.autoDecrementSCCounter(sessionId, customerId, amount);
            }
        }
        return 0L;
    }

    public Long getSecondaryMarketCustomerCounter(Integer sessionId, String customerId) {
        Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
        if (session == null) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
        }
        Long counter = sessionSecondaryMarketCustomersCounterCouchDao.get(sessionId, customerId);
        return counter == null ? 0 : counter;
    }

    public Long updateSecondaryMarketCustomerCounter(Integer sessionId, String customerId, Integer amount, SessionCustomersActionType actionType) {
        if (SessionCustomersActionType.INCREMENT.equals(actionType)) {
            Session session = catalogSessionCouchDao.get(String.valueOf(sessionId));
            if (session == null) {
                throw new OneboxRestException(MsEventErrorCode.SESSION_NOT_FOUND);
            }
            return sessionSecondaryMarketCustomersCounterCouchDao.autoIncrementCounter(sessionId, customerId, amount);
        } else {
            Long counter = sessionSecondaryMarketCustomersCounterCouchDao.get(sessionId, customerId);
            if (counter != null && counter > 0) {
                return sessionSecondaryMarketCustomersCounterCouchDao.autoDecrementCounter(sessionId, customerId, amount);
            }
        }
        return 0L;
    }
}