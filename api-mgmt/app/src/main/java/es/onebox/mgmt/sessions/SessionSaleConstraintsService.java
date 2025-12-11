package es.onebox.mgmt.sessions;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleConstraint;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.sessions.converters.SessionSaleConstraintsConverter;
import es.onebox.mgmt.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.mgmt.sessions.dto.UpdateSaleConstraintDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionSaleConstraintsService {

    @Autowired
    private ValidationService validationService;
    @Autowired
    private SessionsRepository sessionsRepository;
    @Autowired
    private VenuesRepository venuesRepository;


    public SessionSaleConstraintDTO getSaleConstraints(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionSaleConstraint saleConstraint = sessionsRepository.getSaleConstraints(eventId, sessionId);
        SessionSaleConstraintDTO result = null;
        if (saleConstraint != null) {
            Map<Long, String> priceTypeNames = venuesRepository.getPriceTypes(session.getVenueConfigId()).stream()
                    .collect(Collectors.toMap(PriceType::getId, PriceType::getName));
            result = SessionSaleConstraintsConverter.convert(saleConstraint, priceTypeNames);
        }
        return result;
    }

    public void upsert(Long eventId, Long sessionId, UpdateSaleConstraintDTO request) {
        validationService.getAndCheckSessionExternal(eventId, sessionId);
        validateSaleConstraints(request);
        sessionsRepository.upsertSaleConstraints(eventId, sessionId, SessionSaleConstraintsConverter.convert(request));
    }

    private void validateSaleConstraints(UpdateSaleConstraintDTO request) {
        if (BooleanUtils.isTrue(request.getCartLimitsEnabled()) && (request.getCartLimits() == null
                || request.getCartLimits().getLimit() == null)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Cart limit cannot be null", null);
        }
        if (BooleanUtils.isTrue(request.getCartLimitsEnabled()) && request.getCartLimits() != null
                && BooleanUtils.isTrue(request.getCartLimits().getPriceTypeLimitsEnabled())
                && CollectionUtils.isEmpty(request.getCartLimits().getPriceTypeLimits())) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Cart limits price type limits cannot be null", null);
        }
        if (BooleanUtils.isTrue(request.getCustomersLimitsEnabled()) && (
                request.getCustomersLimits() == null
                || (CollectionUtils.isEmpty(request.getCustomersLimits().getPriceTypeLimits())
                    && (request.getCustomersLimits().getMin() == null && request.getCustomersLimits().getMax() == null)))
        ) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Customers limits cannot be null", null);
        }
    }
}
