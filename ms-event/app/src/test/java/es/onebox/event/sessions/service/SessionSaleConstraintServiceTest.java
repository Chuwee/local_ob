package es.onebox.event.sessions.service;

import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.dto.CustomerCondition;
import es.onebox.event.datasources.ms.entity.dto.ExternalLoginConfig;
import es.onebox.event.datasources.ms.entity.dto.Login;
import es.onebox.event.datasources.ms.entity.dto.LoginMethod;
import es.onebox.event.datasources.ms.entity.dto.LoginRequest;
import es.onebox.event.datasources.ms.entity.dto.PurchaseLimit;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.UpdateCustomersLimitsDTO;
import es.onebox.event.sessions.dto.UpdatePriceTypeLimitDTO;
import es.onebox.event.sessions.dto.UpdateSaleConstraintDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class SessionSaleConstraintServiceTest {

    @Mock
    private SessionDao sessionDao;
    @Mock
    private PriceTypeConfigDao priceZoneConfigDao;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private RefreshDataService refreshDataService;
    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @Mock
    private EntitiesRepository entitiesRepository;

    @InjectMocks
    private SessionSaleConstraintService service;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upsertSaleConstraints() {
        final int venueTemplate = 1;
        final long sessionId = 1L;
        final int zoneId = 1;
        final long eventId = 1L;

        when(eventConfigCouchDao.getOrInitEventConfig(anyLong())).thenReturn(new EventConfig());
        when(sessionDao.findSession(anyLong())).thenReturn(null);
        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, null),
                "On session not found code should fail",
                MsEventSessionErrorCode.SESSION_NOT_FOUND);

        SessionRecord session = new SessionRecord();
        session.setVenueTemplateId(venueTemplate);
        session.setNummaxlocalidadescompra(50);
        when(sessionDao.findSession(sessionId)).thenReturn(session);

        CpanelZonaPreciosConfigRecord priceType = new CpanelZonaPreciosConfigRecord();
        priceType.setIdzona(zoneId);

        UpdatePriceTypeLimitDTO limit = new UpdatePriceTypeLimitDTO();
        UpdateSaleConstraintDTO request = new UpdateSaleConstraintDTO();
        request.setCartPriceTypeLimitsEnabled(true);
        request.setCartPriceTypeLimits(new ArrayList<>());
        request.getCartPriceTypeLimits().add(limit);

        when(priceZoneConfigDao.findByVenueTemplateId(venueTemplate)).thenReturn(Collections.singletonList(priceType));

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On price type null or less than 1, code should fail",
                MsEventErrorCode.INVALID_PRICE_TYPE);

        limit.setId((long) zoneId + 1);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On price type not in session's venue template code should fail",
                MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);

        limit.setId((long) zoneId);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On invalid max limit code should fail",
                MsEventSessionErrorCode.INVALID_MAX_PRICE_TYPE_LIMIT);

        limit.setMax(100);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On invalid min limit code should fail",
                MsEventSessionErrorCode.INVALID_MIN_PRICE_TYPE_LIMIT);

        limit.setMin(200);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On max smaller than min limit code should fail",
                MsEventSessionErrorCode.MAX_SMALLER_THAN_MIN);

        limit.setMin(1);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On max greater than session's limit code should fail",
                MsEventSessionErrorCode.MAX_GREATER_THAN_SESSION_CART_LIMIT);

        limit.setMax(5);

        UpdatePriceTypeLimitDTO limitRepeated = new UpdatePriceTypeLimitDTO();
        limitRepeated.setId(limit.getId());
        request.getCartPriceTypeLimits().add(limitRepeated);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On duplicated price type code should fail",
                MsEventSessionErrorCode.DUPLICATED_PRICE_TYPE_LIMIT);

        UpdateCustomersLimitsDTO customerLimits = new UpdateCustomersLimitsDTO();
        customerLimits.setMax(11);
		request.setCustomersLimits(customerLimits);
        request.setCustomersLimitsEnabled(true);
		EventConfig eventConfig = new EventConfig();
        Provider italianCompliance = Provider.ITALIAN_COMPLIANCE;
        eventConfig.setInventoryProvider(italianCompliance);
		ExternalLoginConfig config = new ExternalLoginConfig(italianCompliance, new Login(new Login.ConfigAllowed(
				List.of(LoginMethod.THIRD_PARTY), false, false, List.of(LoginRequest.ADD_SEAT)),
				new Login.ConfigDefault(LoginMethod.THIRD_PARTY, false, false, LoginRequest.ADD_SEAT)),
				new PurchaseLimit(new PurchaseLimit.Customer(List.of(
						CustomerCondition.SESSION), 10, CustomerCondition.SESSION, 10)));

		when(eventConfigCouchDao.getOrInitEventConfig(anyLong())).thenReturn(eventConfig);
        when(entitiesRepository.getExternalLoginConfig(any())).thenReturn(config);
        validateException(() -> service.upsertSaleConstraints(eventId, sessionId, request), "Session limit not allowed",
				MsEventSessionErrorCode.SESSION_LIMIT_NOT_ALLOWED);
        request.setCustomersLimitsEnabled(false);
        request.setCustomersLimits(null);
        when(eventConfigCouchDao.getOrInitEventConfig(anyLong())).thenReturn(new EventConfig());

        request.getCartPriceTypeLimits().remove(limitRepeated);

        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSessionId((int) sessionId);
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);

        request.setCartLimitsEnabled(true);
        request.setCartLimit(0);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On invalid cart limit code should fail",
                MsEventSessionErrorCode.INVALID_CART_LIMIT);

        request.setCartLimit(5);

        UpdateCustomersLimitsDTO customersLimits = new UpdateCustomersLimitsDTO();
        UpdatePriceTypeLimitDTO customerLimit = new UpdatePriceTypeLimitDTO();
        customersLimits.setPriceTypeLimits(new ArrayList<>());
        customersLimits.getPriceTypeLimits().add(customerLimit);
        request.setCustomersLimitsEnabled(true);
        request.setCustomersLimits(customersLimits);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On price type null or less than 1, code should fail",
                MsEventErrorCode.INVALID_PRICE_TYPE);

        customerLimit.setId((long) zoneId + 1);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On price type not in session's venue template code should fail",
                MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);

        customerLimit.setId((long) zoneId);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On invalid max limit code should fail",
                MsEventSessionErrorCode.INVALID_MAX_PRICE_TYPE_LIMIT);

        customerLimit.setMax(5);

        UpdatePriceTypeLimitDTO customerLimitRepeated = new UpdatePriceTypeLimitDTO();
        customerLimitRepeated.setId(customerLimit.getId());
        request.getCustomersLimits().getPriceTypeLimits().add(customerLimitRepeated);

        validateException(
                () -> service.upsertSaleConstraints(eventId, sessionId, request),
                "On duplicated price type code should fail",
                MsEventSessionErrorCode.DUPLICATED_PRICE_TYPE_LIMIT);

        request.getCustomersLimits().getPriceTypeLimits().remove(customerLimitRepeated);

        service.upsertSaleConstraints(eventId, sessionId, request);
    }

    @Test
    void getSaleConstraints() {
        Long sessionId = 1L;
        when(sessionDao.findSession(anyLong())).thenReturn(null);

        validateException(() -> service.getSaleConstraints(sessionId),
                "should fail on non found session", MsEventSessionErrorCode.SESSION_NOT_FOUND);

        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(new SessionConfig());
        when(sessionDao.findSession(anyLong())).thenReturn(new SessionRecord());
        service.getSaleConstraints(sessionId);
    }

    private void validateException(Runnable runnable, String message, ErrorCode errorCode) {
        try {
            runnable.run();
            fail(message);
        } catch (OneboxRestException e) {
            assertEquals(errorCode.getErrorCode(), e.getErrorCode());
        } catch (Exception e) {
            fail("Exception should be of type OneboxRestException");
        }
    }


}
