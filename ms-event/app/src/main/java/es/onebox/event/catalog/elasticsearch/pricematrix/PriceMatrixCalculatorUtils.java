/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.catalog.elasticsearch.pricematrix;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.Ticket;
import es.onebox.event.promotions.dto.restriction.PromotionValidationPeriod;
import es.onebox.event.promotions.enums.PromotionValidationPeriodType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PriceMatrixCalculatorUtils {

    private PriceMatrixCalculatorUtils() {
    }

    public static Price findMinPriceByDefaultRate(List<PriceZonePrices> prices, List<SecondaryMarketSearch> secondaryMarketSearch, Function<Price, Double> valueExtractor) {
        Price minPrimaryPrice = prices.stream()
                .flatMap(priceZone -> priceZone.getRates().stream())
                .filter(rate -> rate.getDefaultRate() && rate.getRatePrice() != null)
                .map(RatePrices::getRatePrice)
                .min(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        Price minSecondaryPrice = secondaryMarketSearch != null
                ? secondaryMarketSearch.stream()
                .map(search -> search.getTicket() != null ? createPriceFromTicket(search.getTicket()) : null)
                .filter(Objects::nonNull)
                .min(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null)
                : null;

        if (minPrimaryPrice == null) {
            return minSecondaryPrice;
        }

        if (minSecondaryPrice == null) {
            return minPrimaryPrice;
        }

        return minPrimaryPrice.getValue().compareTo(minSecondaryPrice.getValue()) < 0 ?
                minPrimaryPrice :
                minSecondaryPrice;
    }

    public static List<PromotedPrice> findMinPromotedPricesByDefaultRate(List<PriceZonePrices> priceZonePrices, Function<PromotedPrice, Double> valueExtractor) {
        List<PromotedPrice> prices = priceZonePrices.stream().map(PriceZonePrices::getRates)
                .flatMap(Collection::stream).filter(r -> r.getDefaultRate() && CollectionUtils.isNotEmpty(r.getPromotedPrices())).filter(Objects::nonNull)
                .map(RatePrices::getPromotedPrices).flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toList());
        return getMinPromotedPricesByPromotedPrice(prices, valueExtractor);
    }

    public static List<PromotedPrice> getMinPromotedPricesByPromotedPrice(List<PromotedPrice> promoPrices, Function<PromotedPrice, Double> valueExtractor) {
        List<PromotedPrice> out = new ArrayList<>();
        List<PromotionValidationPeriod> takenPeriods = new ArrayList<>();
        PromotedPrice price = promoPrices.stream()
                .min(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
        while (price != null) {
            out.add(price);
            promoPrices.remove(price);
            price = findNextPromotedPrice(price.getValidationPeriod(), promoPrices, takenPeriods, valueExtractor);
        }
        return CollectionUtils.isNotEmpty(out) ? out : null;
    }

    private static PromotedPrice findNextPromotedPrice(PromotionValidationPeriod valPeriod, List<PromotedPrice> prices,
                                                       List<PromotionValidationPeriod> takenPeriods, Function<PromotedPrice, Double> valueExtractor) {
        if (valPeriod == null || PromotionValidationPeriodType.ALL.equals(valPeriod.getType())) return null;
        takenPeriods.add(valPeriod);
        return prices.stream()
                .filter(r -> r.getValidationPeriod() == null
                        || PromotionValidationPeriodType.ALL.equals(r.getValidationPeriod().getType())
                        || compatibleValidationPeriods(r.getValidationPeriod(), takenPeriods))
                .min(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private static boolean compatibleValidationPeriods(PromotionValidationPeriod target, List<PromotionValidationPeriod> takenPeriods) {
        return takenPeriods.stream()
                .noneMatch(t -> (target.getTo().after(t.getFrom()) && target.getTo().before(t.getTo()))
                        || (target.getFrom().after(t.getFrom()) && target.getFrom().before(t.getTo()))
                        || target.getFrom().equals(t.getFrom()) || target.getTo().equals(t.getTo()));
    }

    public static Price findMaxPriceByDefaultRate(List<PriceZonePrices> prices, List<SecondaryMarketSearch> secondaryMarketSearch, Function<Price, Double> valueExtractor) {
        Price maxPrimaryPrice = prices.stream()
                .flatMap(priceZone -> priceZone.getRates().stream())
                .filter(rate -> rate.getDefaultRate() && rate.getRatePrice() != null)
                .map(RatePrices::getRatePrice)
                .max(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        Price maxSecondaryPrice = secondaryMarketSearch != null
                ? secondaryMarketSearch.stream()
                .map(search -> search.getTicket() != null ? createPriceFromTicket(search.getTicket()) : null)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(valueExtractor, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null)
                : null;

        if (maxPrimaryPrice == null) {
            return maxSecondaryPrice;
        }

        if (maxSecondaryPrice == null) {
            return maxPrimaryPrice;
        }

        return maxPrimaryPrice.getValue().compareTo(maxSecondaryPrice.getValue()) > 0 ?
                maxPrimaryPrice :
                maxSecondaryPrice;
    }

    private static RatePrices findPrice(List<PriceZonePrices> prices, Predicate<RatePrices> filterCriteria, Comparator<RatePrices> sortCriteria) {
        return prices.stream()
                .map(PriceZonePrices::getRates)
                .flatMap(Collection::stream)
                .filter(filterCriteria).min(sortCriteria)
                .orElse(null);
    }

    private static Price createPriceFromTicket(Ticket ticket) {
        if (ticket.getPrice() != null) {
            Price price = new Price();
            price.setValue(ticket.getPrice());

            PriceSurcharges surcharges = new PriceSurcharges();
            surcharges.setSecondaryMarket(ticket.getPromoterCharges() != null ? ticket.getPromoterCharges() : 0D);
            price.setSurcharge(surcharges);

            return price;
        }
        return null;
    }

    public static Double calculatePriceTotal(Price price) {
        if (price == null) {
            return null;
        }

        Double base = price.getValue();
        PriceSurcharges surcharges = price.getSurcharge();
        return NumberUtils.sum(base, calculateTotalSurcharges(surcharges));
    }

    private static Double calculateTotalSurcharges(PriceSurcharges surcharges) {
        if (surcharges == null) {
            return null;
        }

        Double promoter = surcharges.getPromoter();
        Double channel = surcharges.getChannel();
        Double secondaryMarket = surcharges.getSecondaryMarket();

        if (secondaryMarket != null && secondaryMarket > 0) {
            return secondaryMarket;
        }

        return NumberUtils.sum(
                promoter,
                channel
        );
    }

}
