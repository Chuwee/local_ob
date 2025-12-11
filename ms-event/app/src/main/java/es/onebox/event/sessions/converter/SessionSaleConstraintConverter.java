package es.onebox.event.sessions.converter;

import es.onebox.event.sessions.domain.sessionconfig.CustomersLimits;
import es.onebox.event.sessions.domain.sessionconfig.PriceTypeLimit;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.CustomersLimitsDTO;
import es.onebox.event.sessions.dto.PriceTypeLimitDTO;
import es.onebox.event.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.event.sessions.dto.UpdateCustomersLimitsDTO;
import es.onebox.event.sessions.dto.UpdatePriceTypeLimitDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionSaleConstraintConverter {

    private SessionSaleConstraintConverter() {
    }

    public static SessionSaleConstraintDTO convert(SessionConfig sessionConfig, Integer cartLimit, Integer idSession) {
        if (sessionConfig.getPriceTypeLimits() == null && cartLimit == null && sessionConfig.getCustomersLimits() == null) {
            return null;
        }
        SessionSaleConstraintDTO target = new SessionSaleConstraintDTO();
        target.setCartLimit(cartLimit);
        target.setSessionId(idSession);
        if (sessionConfig.getPriceTypeLimits() != null) {
            target.setCartPriceTypeLimits(sessionConfig.getPriceTypeLimits().stream()
                    .map(SessionSaleConstraintConverter::convertPriceTypeLimit)
                    .collect(Collectors.toList()));
        }
        target.setCustomersLimits(convertCustomersLimits(sessionConfig.getCustomersLimits()));
        return target;
    }

    private static CustomersLimitsDTO convertCustomersLimits(CustomersLimits source) {
        if (source == null || CollectionUtils.isEmpty(source.getPriceTypeLimits()) && source.getMin() == null && source.getMax() == null) {
            return null;
        }
        CustomersLimitsDTO target = new CustomersLimitsDTO();
        if(source.getPriceTypeLimits() != null && !source.getPriceTypeLimits().isEmpty()) {
            target.setPriceTypeLimits(source.getPriceTypeLimits().stream().map(SessionSaleConstraintConverter::convertPriceTypeLimit)
                    .collect(Collectors.toList()));
        }
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    public static CustomersLimits convertCustomersLimits(UpdateCustomersLimitsDTO source) {
        if (source == null || (CollectionUtils.isEmpty(source.getPriceTypeLimits()) && source.getMin() == null && source.getMax() == null)) {
            return null;
        }
        CustomersLimits target = new CustomersLimits();
        if(source.getPriceTypeLimits() != null && !source.getPriceTypeLimits().isEmpty()) {
            target.setPriceTypeLimits(source.getPriceTypeLimits().stream().map(SessionSaleConstraintConverter::convert)
                    .collect(Collectors.toList()));
        }
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    private static PriceTypeLimitDTO convertPriceTypeLimit(PriceTypeLimit source) {
        if (source == null) {
            return null;
        }
        PriceTypeLimitDTO target = new PriceTypeLimitDTO();
        target.setId(source.getId());
        target.setMax(source.getMax());
        target.setMin(source.getMin());
        return target;
    }

    public static PriceTypeLimit convert(UpdatePriceTypeLimitDTO source) {
        if (source == null) {
            return null;
        }
        PriceTypeLimit target = new PriceTypeLimit();
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        target.setId(source.getId());
        return target;
    }

    public static List<PriceTypeLimit> convert(List<UpdatePriceTypeLimitDTO> source) {
        if (source == null) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(SessionSaleConstraintConverter::convert)
                .collect(Collectors.toList());
    }
}
