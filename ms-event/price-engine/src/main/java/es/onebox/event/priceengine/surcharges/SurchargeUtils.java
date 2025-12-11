package es.onebox.event.priceengine.surcharges;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.ProductSurchargesRanges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;

import java.util.List;
import java.util.Optional;

public class SurchargeUtils {

    private static final Double ZERO = 0d;

    private SurchargeUtils() {
        super();
    }

    public static Double calculatePromoterSurcharge(Double price, ChannelEventSurcharges surcharges, boolean promotion) {
        return Optional.ofNullable(surcharges)
                .map(ChannelEventSurcharges::getPromoter)
                .map(ranges -> promotion ? ranges.getPromotion() : ranges.getMain())
                .map(ranges -> calculateSurcharge(price, ranges))
                .orElse(ZERO);
    }

    public static Double calculateProductPromoterSurcharge(Double price, ProductSurchargesRanges surcharges, boolean promotion) {
        return Optional.ofNullable(surcharges)
                .map(ProductSurchargesRanges::getPromoter)
                .map(ranges -> promotion ? ranges.getPromotion() : ranges.getMain())
                .map(ranges -> calculateSurcharge(price, ranges))
                .orElse(ZERO);
    }

    public static Double calculateChannelSurcharge(Double price, ChannelEventSurcharges surcharges, boolean promotion) {
        return Optional.ofNullable(surcharges)
                .map(ChannelEventSurcharges::getChannel)
                .map(ranges -> promotion ? ranges.getPromotion() : ranges.getMain())
                .map(ranges -> calculateSurcharge(price, ranges))
                .orElse(ZERO);
    }

    public static Double calculateSecondaryMarketSurcharge(Double price, ChannelEventSurcharges surcharges, boolean promotion) {
        return Optional.ofNullable(surcharges)
                .map(ChannelEventSurcharges::getPromoter)
                .map(ranges -> promotion ? ranges.getPromotion() : ranges.getSecondaryMarket())
                .map(ranges -> calculateSurcharge(price, ranges))
                .orElse(ZERO);
    }

    private static Double calculateSurcharge(Double basePrice, List<SurchargeRange> ranges) {
        return ranges.stream()
                .filter(range -> basePrice.compareTo(range.getFrom()) >= 0 && basePrice.compareTo(range.getTo()) < 0)
                .findFirst()
                .map(surchargeRange -> {
                    // surcharge = fixed value + basePrice with percentage value
                    Double surcharge = NumberUtils.sum(surchargeRange.getFixedValue(), NumberUtils.roundedPercentageOf(basePrice, surchargeRange.getPercentageValue()));
                    // Do NOT use NumberUtils.zeroIfNull()
                    if (surchargeRange.getMinimumValue() != null && surcharge.compareTo(surchargeRange.getMinimumValue()) < 0) {
                        surcharge = surchargeRange.getMinimumValue();
                    }
                    else if (surchargeRange.getMaximumValue() != null && surcharge.compareTo(surchargeRange.getMaximumValue()) > 0) {
                        surcharge = surchargeRange.getMaximumValue();
                    }
                    return surcharge;
                })
                .orElse(ZERO);
    }
}
