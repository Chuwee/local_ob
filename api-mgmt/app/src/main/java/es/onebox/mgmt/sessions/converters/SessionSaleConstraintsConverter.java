package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.CustomersLimits;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypeLimit;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleConstraint;
import es.onebox.mgmt.sessions.dto.CartLimitsDTO;
import es.onebox.mgmt.sessions.dto.CustomersLimitsDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeLimitDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.mgmt.sessions.dto.UpdateCustomersLimitsDTO;
import es.onebox.mgmt.sessions.dto.UpdatePriceTypeLimitDTO;
import es.onebox.mgmt.sessions.dto.UpdateSaleConstraintDTO;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionSaleConstraintsConverter {

    private SessionSaleConstraintsConverter() {
    }

    public static SessionSaleConstraintDTO convert(SessionSaleConstraint source, Map<Long, String> priceTypeNames) {
        if (source == null) {
            return null;
        }
        SessionSaleConstraintDTO target = new SessionSaleConstraintDTO();
        if (source.getCartLimit() != null) {
            target.setCartLimits(convert(source.getCartLimit(), source.getCartPriceTypeLimits(), priceTypeNames));
            target.setCartLimitsEnabled(true);
        } else {
            target.setCartLimitsEnabled(false);
        }

        if (source.getCustomersLimits() != null) {
            target.setCustomersLimits(convert(source.getCustomersLimits(), priceTypeNames));
            target.setCustomersLimitsEnabled(true);
        } else {
            target.setCustomersLimitsEnabled(false);
        }
        return target;
    }

    public static SessionSaleConstraint convert(UpdateSaleConstraintDTO source) {
        if (source == null) {
            return null;
        }
        SessionSaleConstraint target = new SessionSaleConstraint();
        if (source.getCartLimitsEnabled() != null && BooleanUtils.isTrue(source.getCartLimitsEnabled())) {
            target.setCartLimit(source.getCartLimits().getLimit());
            Boolean priceTypeLimitsEnabled = source.getCartLimits().getPriceTypeLimitsEnabled();
            if (BooleanUtils.isTrue(priceTypeLimitsEnabled)) {
                target.setCartPriceTypeLimits(convert(source.getCartLimits().getPriceTypeLimits()));
            }
            target.setCartPriceTypeLimitsEnabled(priceTypeLimitsEnabled);
        }
        target.setCartLimitsEnabled(source.getCartLimitsEnabled());
        if (source.getCustomersLimitsEnabled() != null && BooleanUtils.isTrue(source.getCustomersLimitsEnabled())) {
            target.setCustomersLimits(convert(source.getCustomersLimits()));
        }
        target.setCustomersLimitsEnabled(source.getCustomersLimitsEnabled());
        return target;
    }

    private static CartLimitsDTO convert(Integer cartLimit, List<PriceTypeLimit> cartPriceTypeLimits, Map<Long, String> priceTypeNames) {
        if (cartLimit == null) {
            return null;
        }
        CartLimitsDTO target = new CartLimitsDTO();
        target.setLimit(cartLimit);
        if (cartPriceTypeLimits != null) {
            target.setPriceTypeLimits(convert(cartPriceTypeLimits, priceTypeNames));
            target.setPriceTypeLimitsEnabled(true);
        } else {
            target.setPriceTypeLimitsEnabled(false);
        }
        return target;
    }

    private static CustomersLimitsDTO convert(CustomersLimits source, Map<Long, String> priceTypeNames) {
        if (source == null) {
            return null;
        }
        CustomersLimitsDTO target = new CustomersLimitsDTO();
        target.setPriceTypeLimits(convert(source.getPriceTypeLimits(), priceTypeNames));
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    private static List<PriceTypeLimitDTO> convert(List<PriceTypeLimit> source, Map<Long, String> priceTypeNames) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(pt -> convert(pt, priceTypeNames.get(pt.getId())))
                .collect(Collectors.toList());
    }

    private static PriceTypeLimitDTO convert(PriceTypeLimit source, String priceTypeName) {
        if (source == null) {
            return null;
        }
        PriceTypeLimitDTO target = new PriceTypeLimitDTO();
        target.setPriceTypeId(source.getId());
        target.setPriceTypeName(priceTypeName);
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    private static CustomersLimits convert(UpdateCustomersLimitsDTO source) {
        if (source == null) {
            return null;
        }
        CustomersLimits target = new CustomersLimits();
        target.setPriceTypeLimits(convert(source.getPriceTypeLimits()));
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    private static List<PriceTypeLimit> convert(List<UpdatePriceTypeLimitDTO> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(SessionSaleConstraintsConverter::convert)
                .collect(Collectors.toList());
    }

    private static PriceTypeLimit convert(UpdatePriceTypeLimitDTO source) {
        if (source == null) {
            return null;
        }
        PriceTypeLimit target = new PriceTypeLimit();
        target.setId(source.getPriceTypeId());
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    public static MsSaleRequestsFilter toSalePresaleFilter(Long eventId) {
        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setEventId(Arrays.asList(eventId));
        filter.setStatus(Arrays.asList(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(50L);
        return filter;
    }
}
