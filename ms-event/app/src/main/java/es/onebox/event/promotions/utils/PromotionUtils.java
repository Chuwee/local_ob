package es.onebox.event.promotions.utils;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.enums.PromotionStatus;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PromotionUtils {

    private PromotionUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<EventPromotion> filterByStatus(List<EventPromotion> promotions, PromotionStatus status) {
        return filterPromotions(promotions, promotion -> promotion.getStatus() == status);
    }

    public static List<EventPromotion> filterBySession(List<EventPromotion> promotions, Long sessionId) {
        return filterPromotions(promotions, promotion -> isValid(promotion.getRestrictions().getSessions(), sessionId));
    }

    public static List<EventPromotion> filterByChannel(List<EventPromotion> promotions, Long channelId) {
        return filterPromotions(promotions, promotion -> isValid(promotion.getRestrictions().getChannels(), channelId));
    }

    public static List<EventPromotion> filterByPriceZones(List<EventPromotion> promotions, List<Long> priceZones) {
        return filterPromotions(promotions, promotion -> isValid(promotion.getRestrictions().getPriceZones(), priceZones));
    }

    public static List<EventPromotion> filterByRates(List<EventPromotion> promotions, List<Long> rates) {
        return filterPromotions(promotions, promotion -> isValid(promotion.getRestrictions().getRates(), rates));
    }

    private static boolean isValid(List<Long> restricted, Long value) {
        return CollectionUtils.isEmpty(restricted) || restricted.contains(value);
    }

    private static boolean isValid(List<Long> restricted, List<Long> values) {
        return CollectionUtils.isEmpty(restricted) || (values != null && values.stream().anyMatch(restricted::contains));
    }

    private static List<EventPromotion> filterPromotions(List<EventPromotion> promotions, Predicate<EventPromotion> filter) {
        if (promotions == null) {
            return Collections.emptyList();
        }
        return promotions.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static List<EventPromotionRecord> filterBySessionChannel(EventIndexationContext ctx, Long sessionId, Long channelId) {
        List<EventPromotion> sessionPromotions = PromotionUtils.filterBySession(ctx.getEventPromotions(), sessionId);
        List<Integer> sessionChannelPromotionIds = PromotionUtils.filterByChannel(sessionPromotions, channelId).stream().
                map(p -> p.getEventPromotionTemplateId().intValue()).toList();
        return ctx.getPromotions().stream().
                filter(p -> sessionChannelPromotionIds.contains(p.getEventPromotionTemplateId())).toList();
    }
}
