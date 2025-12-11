package es.onebox.event.events.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.utils.EventContextUtils;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventUtils {

    private EventUtils() {
    }

    public static PriceType checkPrices(Map<Long, PriceType> priceTypes, Double price, Long priceTypeId) {
        if (price < 0) {
            throw OneboxRestException.builder(MsEventErrorCode.FIELD_NOT_UPGRADEABLE)
                    .setMessage("Prices cannot be below zero")
                    .build();
        }

        PriceType priceType = priceTypes.get(priceTypeId);
        if (priceType == null) {
            throw new OneboxRestException(MsEventErrorCode.NO_PRICES_FOUND,
                    "Price does not exists for price-type: " + priceTypeId, null);
        }
        return priceType;
    }

    public static PriceType checkPrices(List<EventTemplatePriceDTO> prices, Double price, Long priceTypeId, PriceType requestPriceType) {
        if (price < 0) {
            throw OneboxRestException.builder(MsEventErrorCode.FIELD_NOT_UPGRADEABLE)
                    .setMessage("Prices cannot be below zero")
                    .build();
        }

        List<PriceType> priceTypes = prices.stream().
                filter(p -> p.getPriceTypeId().equals(priceTypeId) &&
                        (requestPriceType == null || p.getPriceType().equals(requestPriceType))).
                map(EventTemplatePriceDTO::getPriceType).
                collect(Collectors.toList());
        if (priceTypes.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.NO_PRICES_FOUND,
                    "Price does not exists for price-type: " + priceTypeId, null);
        } else if (priceTypes.stream().distinct().count() > 1) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_PRICE_TYPE,
                    "Invalid filter for price-type: " + priceTypeId, null);
        }

        return priceTypes.get(0);
    }

    public static boolean isSmartBookingSession(EventIndexationContext ctx, Integer sessionId) {
        if (CollectionUtils.isNotEmpty(ctx.getSessions())) {
            CpanelSesionRecord session = ctx.getSession(sessionId);
            if (session == null) {
                return false;
            }
            VenueDescriptor venueDescriptor = EventContextUtils.getVenueDescriptorBySessionId(ctx, sessionId.longValue());
            return (session.getSbsesionrelacionada() != null && venueDescriptor != null && EventUtils.isActivityTemplate(venueDescriptor.getType()));
        }
        return false;
    }

    public static boolean isSmartBookingSession(CpanelSesionRecord session, Integer venueTemplateType) {
        return (session.getSbsesionrelacionada() != null
                && EventUtils.isActivityTemplate(venueTemplateType));
    }

    public static boolean isSbAvetSession(CpanelSesionRecord session, Integer venueTemplateType) {
        return (session.getSbsesionrelacionada() != null
                && EventUtils.isAvetTemplate(venueTemplateType));
    }

    public static boolean isActivityTemplate(Integer venueTemplateType) {
        return VenueTemplateType.ACTIVITY.getId().equals(venueTemplateType);
    }

    public static boolean isAvetTemplate(Integer venueTemplateType) {
        return VenueTemplateType.AVET.getId().equals(venueTemplateType);
    }

    public static boolean isActivity(Integer eventType) {
        return EventType.ACTIVITY.getId().equals(eventType) ||
                EventType.THEME_PARK.getId().equals(eventType) ||
                EventType.PRODUCT.getId().equals(eventType);
    }

    public static boolean isAvet(Integer eventType) {
        return EventType.AVET.getId().equals(eventType);
    }

    public static boolean isSGA(Provider provider) {
        return Provider.SGA.equals(provider);
    }

    public static boolean isItalianCompliance(Provider provider) {
        return Provider.ITALIAN_COMPLIANCE.equals(provider);
    }

    public static Provider getInventoryProvider(EventConfig eventConfig) {
        if (eventConfig == null) {
            return null;
        }
        return eventConfig.getInventoryProvider();
    }
}
