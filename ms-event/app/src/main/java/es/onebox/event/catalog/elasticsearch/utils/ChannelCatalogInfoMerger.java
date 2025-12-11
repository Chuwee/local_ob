package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogInfo;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogSessionInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.pricematrix.Price;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrixCalculatorUtils;
import es.onebox.event.catalog.elasticsearch.pricematrix.PromotedPrice;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChannelCatalogInfoMerger {

    private ChannelCatalogInfoMerger() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ChannelCatalogEventInfo merge(List<? extends ChannelCatalogSessionInfo> infos) {
        ChannelCatalogEventInfo info = mergeOccupation(infos);
        if (info == null) {
            return null;
        }
        info.setForSale(infos.stream().anyMatch(ChannelCatalogInfo::getForSale));
        info.setDate(mergeDates(infos));
        info.setPromotions(mergeCollection(infos, ChannelCatalogInfo::getPromotions));
        return info;
    }

    public static ChannelCatalogEventInfo mergeOccupation(List<? extends ChannelCatalogInfo> infos) {
        if (CollectionUtils.isEmpty(infos)) {
            return null;
        }
        ChannelCatalogEventInfo info = new ChannelCatalogEventInfo();
        List<PriceMatrix> prices = new java.util.ArrayList<>(infos.size());
        boolean allSoldOut = true;
        for (ChannelCatalogInfo i : infos) {
            if (allSoldOut && !Boolean.TRUE.equals(i.getSoldOut())) {
                allSoldOut = false;
            }
            prices.add(i.getPrices());
        }
        info.setSoldOut(allSoldOut);
        info.setPrices(mergePriceMatrix(prices));
        return info;
    }

    public static ChannelCatalogDatesWithTimeZones mergeDates(List<? extends ChannelCatalogSessionInfo> infos) {
        ChannelCatalogDatesWithTimeZones dates = new ChannelCatalogDatesWithTimeZones();
        minDate(infos, ChannelCatalogDates::getStart, dates::setStart, dates::setStartTimeZone);
        maxDate(infos, ChannelCatalogDates::getEnd, dates::setEnd, dates::setEndTimeZone);
        minDate(infos, ChannelCatalogDates::getPublish, dates::setPublish, dates::setPublishTimeZone);
        minDate(infos, ChannelCatalogDates::getSaleStart, dates::setSaleStart, dates::setSaleStartTimeZone);
        maxDate(infos, ChannelCatalogDates::getSaleEnd, dates::setSaleEnd, dates::setSaleEndTimeZone);
        return dates;
    }

    private static PriceMatrix mergePriceMatrix(List<PriceMatrix> prices) {
        PriceMatrix priceMatrix = new PriceMatrix();
        if (CollectionUtils.isNotEmpty(prices)) {
            priceMatrix.setMinBasePrice(getPrice(prices, PriceMatrix::getMinBasePrice, Stream::min));
            priceMatrix.setMinPromotedPrices(getMinPromotedPrices(prices, PriceMatrix::getMinPromotedPrices));
            priceMatrix.setMaxBasePrice(getPrice(prices, PriceMatrix::getMaxBasePrice, Stream::max));

            priceMatrix.setMinNetPrice(getPrice(prices, PriceMatrix::getMinNetPrice, Stream::min));
            priceMatrix.setMinNetPromotedPrices(getMinPromotedPrices(prices, PriceMatrix::getMinNetPromotedPrices));
            priceMatrix.setMaxNetPrice(getPrice(prices, PriceMatrix::getMaxNetPrice, Stream::max));

            priceMatrix.setMinFinalPrice(getPrice(prices, PriceMatrix::getMinFinalPrice, Stream::min));
            priceMatrix.setMinFinalPromotedPrices(getMinPromotedPrices(prices, PriceMatrix::getMinFinalPromotedPrices));
            priceMatrix.setMaxFinalPrice(getPrice(prices, PriceMatrix::getMaxFinalPrice, Stream::max));
        }
        return priceMatrix;
    }

    private static List<PromotedPrice> getMinPromotedPrices(List<PriceMatrix> prices, Function<PriceMatrix, List<PromotedPrice>> promotedPricesGetter) {
        if (CollectionUtils.isEmpty(prices)) return null;
        List<PromotedPrice> in = prices.stream().filter(Objects::nonNull).map(promotedPricesGetter).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        return PriceMatrixCalculatorUtils.getMinPromotedPricesByPromotedPrice(in, Price::getValue);
    }

    private static <T extends Price> T getPrice(List<PriceMatrix> prices, Function<PriceMatrix, T> getter, BiFunction<Stream<T>, Comparator<T>, Optional<T>> discriminator) {
        Stream<T> stream = prices.stream()
                .filter(Objects::nonNull)
                .map(getter)
                .filter(Objects::nonNull);
        return discriminator.apply(stream, Comparator.comparing(T::getValue, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private static void minDate(List<? extends ChannelCatalogSessionInfo> infos, Function<ChannelCatalogDates, Date> dateGetter, Consumer<Date> dateSetter, Consumer<String> timeZoneSetter) {
        extractDates(infos, dateGetter).min(DateWithTimeZone::compareTo).ifPresent(dateWithTimeZone -> dateWithTimeZone.setDataTo(dateSetter, timeZoneSetter));
    }

    private static void maxDate(List<? extends ChannelCatalogSessionInfo> infos, Function<ChannelCatalogDates, Date> dateGetter, Consumer<Date> dateSetter, Consumer<String> timeZoneSetter) {
        extractDates(infos, dateGetter).max(DateWithTimeZone::compareTo).ifPresent(dateWithTimeZone -> dateWithTimeZone.setDataTo(dateSetter, timeZoneSetter));
    }

    private static Stream<DateWithTimeZone> extractDates(List<? extends ChannelCatalogSessionInfo> infos, Function<ChannelCatalogDates, Date> dateGetter) {
        return infos.stream()
                .filter(i-> Objects.nonNull(i.getDate()))
                .map(info -> {
                    Date date = dateGetter.apply(info.getDate());
                    if (date == null) {
                        return null;
                    }
                    return new DateWithTimeZone(date, info.getTimeZone());
                })
                .filter(Objects::nonNull);
    }

    private static <T, R> List<R> mergeCollection(List<T> published, Function<T, List<R>> getter) {
        return published.stream().map(getter).filter(Objects::nonNull).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    private static class DateWithTimeZone implements Comparable<DateWithTimeZone> {

        private Date date;
        private String timeZone;

        private DateWithTimeZone(Date date, String timeZone) {
            this.date = date;
            this.timeZone = timeZone;
        }

        private void setDataTo(Consumer<Date> dateSetter, Consumer<String> timeZoneSetter) {
            dateSetter.accept(date);
            timeZoneSetter.accept(timeZone);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public int compareTo(DateWithTimeZone o) {
            return date.compareTo(o.date);
        }
    }
}
