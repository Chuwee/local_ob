package es.onebox.channels.catalog.eci;


import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.channels.catalog.ChannelCatalogUtils;
import es.onebox.common.datasources.catalog.dto.common.Price;
import es.onebox.common.datasources.catalog.dto.common.PriceSurcharge;
import es.onebox.common.datasources.catalog.dto.common.Prices;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogPrice;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogPriceType;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogRate;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogSurcharge;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.prices.SurchargeType;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ECIConverterUtils {

    private ECIConverterUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static <T, R> List<R> map(Collection<T> list, Function<T, R> mapper) {
        return map(list, mapper, null);
    }

    public static <T, R> List<R> map(Collection<T> list, Function<T, R> mapper, Comparator<R> sortBy) {
        if (list == null) {
            return Collections.emptyList();
        }
        Stream<R> stream = list.stream()
                .map(mapper)
                .filter(Objects::nonNull);
        if (sortBy != null) {
            stream = stream.sorted(sortBy);
        }
        return stream.collect(Collectors.toList());
    }

    public static String getEventLinkOutUrl(ChannelCatalogContext context, Long eventId) {
        return ChannelCatalogUtils.getEventCardUrl(context, eventId, context.getDefaultLanguage());
    }

    public static String getSessionLinkOutUrl(ChannelCatalogContext context, Long sessionId, Long eventId) {
        return ChannelCatalogUtils.getSessionSelectUrl(context, sessionId, eventId, context.getDefaultLanguage());
    }

    public static <T> ZonedDateTime minDate(List<T> list, Function<T, ZonedDateTime> getter) {
        return getOne(list, getter, Stream::min);
    }

    public static <T> ZonedDateTime maxDate(List<T> list, Function<T, ZonedDateTime> getter) {
        return getOne(list, getter, Stream::max);
    }

    public static ZonedDateTime getEndDay(ZonedDateTime date, String timeZone) {
        return date
                .withZoneSameInstant(ZoneId.of(timeZone))
                .with(LocalTime.of(23, 59));
    }

    public static void overrideMinPrice(ChannelSession channelSession, SessionPrices sessionPriceMatrix) {
        CatalogPrice min = sessionPriceMatrix.getRates().stream().filter(CatalogRate::isDefaultRate)
                .flatMap(catalogRate -> catalogRate.getPriceTypes().stream())
                .map(CatalogPriceType::getPrice)
                .min(Comparator.comparingDouble(CatalogPrice::getBase))
                .orElse(null);
        if (min != null && channelSession.getPrice() == null) {
            Prices prices = new Prices();

            Price minPrice = new Price();
            minPrice.setValue(min.getBase());

            PriceSurcharge priceSurcharge = new PriceSurcharge();
            CatalogSurcharge catalogPromoterSurcharge = min.getSurcharges().stream().filter(s -> SurchargeType.PROMOTER.equals(s.getType())).findFirst().orElse(null);
            CatalogSurcharge catalogChannelSurcharge = min.getSurcharges().stream().filter(s -> SurchargeType.CHANNEL.equals(s.getType())).findFirst().orElse(null);
            priceSurcharge.setChannel(catalogChannelSurcharge != null ? catalogChannelSurcharge.getValue() : 0.0);
            priceSurcharge.setPromoter(catalogPromoterSurcharge != null ? catalogPromoterSurcharge.getValue() : 0.0);
            minPrice.setSurcharge(priceSurcharge);

            prices.setMin(minPrice);
            channelSession.setPrice(prices);
        }
    }

    private static <T, R extends Comparable<? super R>> R getOne(List<T> list, Function<T, R> getter,
                                                                 BiFunction<Stream<R>, Comparator<R>, Optional<R>> filter) {
        Stream<R> stream = list.stream()
                .filter(Objects::nonNull)
                .map(getter)
                .filter(Objects::nonNull);
        return filter.apply(stream, Comparator.naturalOrder()).orElse(null);
    }
}
