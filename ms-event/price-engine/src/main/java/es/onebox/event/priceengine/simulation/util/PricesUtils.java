package es.onebox.event.priceengine.simulation.util;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.PromotionPricesRange;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.enums.SurchargeType;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.priceengine.taxes.domain.SessionTaxes;
import es.onebox.event.priceengine.taxes.domain.TaxBreakdown;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import es.onebox.event.priceengine.taxes.domain.Taxes;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PricesUtils {

    private static final Double ZERO = 0.0;
    private static final Double ONE_HUNDRED = 100.0;

    private static final Integer TYPE_DISCOUNT = 3;
    private static final Integer TYPE_PROMOTION = 2;
    private static final Integer TYPE_AUTOMATIC = 1;

    private static final Integer FIXED_DISCOUNT = 0;
    private static final Integer PERCENTAGE_DISCOUNT = 1;
    private static final Integer NEW_PRICE_DISCOUNT = 2;

    private PricesUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static Price getPrice(PriceZone priceZone, ChannelEventSurcharges surcharges,
                                 boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        return getPrice(priceZone, surcharges, null, eventChannelUseSpecificChannelSurcharges, taxes);
    }

    public static Price getPrice(PriceZone priceZone, ChannelEventSurcharges surcharges, List<Promotion> promotions,
                                 boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        taxes = taxes == null ? new SessionTaxes() : taxes;
        Price price = new Price();
        Double promotedPrice = getPromotedPriceFinal(priceZone.getPrice(), promotions);
        Double net = getNetPriceFromPromotedPrice(promotedPrice, taxes.getPricesTaxes());
        price.setBase(promotedPrice);
        price.setNet(net);
        price.setTaxes(getTaxes(taxes.getPricesTaxes(), promotedPrice, net));
        price.setSurcharges(getListSurcharge(promotedPrice, surcharges, promotions, eventChannelUseSpecificChannelSurcharges, taxes));
        price.setTotal(getTotal(promotedPrice, price.getSurcharges()));
        return price;
    }

    private static Taxes getTaxes(List<? extends TaxInfo> pricesTaxes, Double base, Double net) {
        if (CollectionUtils.isEmpty(pricesTaxes) || base == null || net == null) {
            return null;
        }
        Taxes taxes = new Taxes();
        Double totalTaxes = NumberUtils.minus(base, net);
        taxes.setTotal(totalTaxes);
        taxes.setBreakdown(getTaxesBreakdown(pricesTaxes, net, totalTaxes));
        return taxes;
    }

    private static List<TaxBreakdown> getTaxesBreakdown(List<? extends TaxInfo> priceTaxes, Double net, Double totalTaxes) {
        return TaxSimulationUtils.createTaxAmount(totalTaxes, net, priceTaxes, TaxBreakdown::new);
    }

    private static Double getNetPriceFromPromotedPrice(Double promotedPrice, List<? extends TaxInfo> taxes) {
        if (CollectionUtils.isEmpty(taxes)) {
            return null;
        }
        double totalTaxRate = taxes.stream()
                .filter(tax -> tax.getValue() != null)
                .mapToDouble(tax -> NumberUtils.divide(tax.getValue(), ONE_HUNDRED))
                .sum();
        return roundUp(promotedPrice / (1 + totalTaxRate));
    }

    private static Double getTotal(Double base, List<Surcharge> surcharges) {
        return roundUp(base + getSurchargesTotal(surcharges));
    }

    private static Double getSurchargesTotal(List<Surcharge> surcharges) {
        if (CollectionUtils.isNotEmpty(surcharges)) {
            return roundUp(surcharges.stream().filter(su -> !SurchargeType.SECONDARY_MARKET_PROMOTER.equals(su.getType()) && !SurchargeType.SECONDARY_MARKET_CHANNEL.equals(su.getType())).map(Surcharge::getValue).reduce(ZERO, Double::sum));
        }
        return ZERO;
    }

    private static Double getTotalWithPromo(Double base, List<Promotion> promotions) {
        if (CollectionUtils.isEmpty(promotions)) {
            return roundUp(base);
        }
        Double total = base;
        Promotion promo = getPromotionByType(promotions, TYPE_AUTOMATIC);
        if (promo != null) {
            total = applyPromoToBasePrice(total, promo);
        }
        promo = getPromotionByType(promotions, TYPE_DISCOUNT);
        if (promo != null) {
            total = applyPromoToBasePrice(total, promo);
        }
        promo = getPromotionByType(promotions, TYPE_PROMOTION);
        if (promo != null) {
            total = applyPromoToBasePrice(total, promo);
        }
        return total < ZERO ? ZERO : total;
    }

    private static Double getTotalDiscountWithPromoNewBasePriceCase(Double base, Double discount, List<Promotion> promotions) {
        Double total = ZERO;

        if (CollectionUtils.isNotEmpty(promotions)) {
            Promotion promo = getPromotionByType(promotions, TYPE_AUTOMATIC);
            if (promo != null) {
                total += applyPromoToBasePriceNewBasePriceCase(base, promo);
            }
            promo = getPromotionByType(promotions, TYPE_DISCOUNT);
            if (promo != null) {
                total += discount;
            }
            //check if the accumulated discount is higher than the original price in case there is a negative promotion
            if (total > base) {
                total = base;
            }
            promo = getPromotionByType(promotions, TYPE_PROMOTION);
            if (promo != null) {
                total += applyPromoToBasePriceNewBasePriceCase(base - total, promo);
            }
        }

        return total;
    }

    private static Double applyPromoToBasePriceNewBasePriceCase(Double base, Promotion promo) {
        Double total = ZERO;
        if (FIXED_DISCOUNT.equals(promo.getDiscountType())) {
            total += promo.getFixedDiscountValue();
        } else if (PERCENTAGE_DISCOUNT.equals(promo.getDiscountType())) {
            total = (base * promo.getPercentualDiscountValue() / 100);
        }
        return roundUp(total);
    }

    private static Double applyPromoToBasePrice(Double base, Promotion promo) {
        Double total = base;
        if (FIXED_DISCOUNT.equals(promo.getDiscountType())) {
            total = base - promo.getFixedDiscountValue();
        } else if (PERCENTAGE_DISCOUNT.equals(promo.getDiscountType())) {
            total = base - roundUp(base * promo.getPercentualDiscountValue() / 100);
        }
        return total < ZERO ? ZERO : roundUp(total);
    }

    private static Promotion getPromotionByType(List<Promotion> promotions, Integer type) {
        return promotions.stream().filter(promotion -> type.equals(promotion.getSubtype())).findAny().orElse(null);
    }

    private static Double getPromotedPriceFinal(Double originalPrice, List<Promotion> promotions) {
        if (CollectionUtils.isEmpty(promotions)) {
            return roundUp(originalPrice);
        }
        Double newBasePrice = originalPrice;
        if (existsTwoPromosWithNewBasePrice(promotions)) {
            newBasePrice = getNewBasePriceCumulative(newBasePrice, promotions);
        } else if (exitsPromoWithNewBasePriceByType(promotions, TYPE_AUTOMATIC)) {
            newBasePrice = getNewBasePriceByPromos(newBasePrice, promotions);
            if (newBasePrice > originalPrice) {
                newBasePrice = originalPrice;
            }
        } else if (exitsPromoWithNewBasePriceByType(promotions, TYPE_DISCOUNT)) {
            Double totalDiscount = ZERO;
            newBasePrice = getNewBasePriceByPromos(newBasePrice, promotions);
            if (newBasePrice < originalPrice) {
                totalDiscount += getTotalDiscountWithPromoNewBasePriceCase(originalPrice, originalPrice - newBasePrice, promotions);
                totalDiscount = originalPrice - totalDiscount;
                return totalDiscount < ZERO ? ZERO : roundUp(totalDiscount);
            }
            newBasePrice = originalPrice;
        }
        return getTotalWithPromo(newBasePrice, promotions);
    }

    private static boolean existsTwoPromosWithNewBasePrice(List<Promotion> promotions) {
        return exitsPromoWithNewBasePriceByType(promotions, TYPE_AUTOMATIC) && exitsPromoWithNewBasePriceByType(promotions, TYPE_DISCOUNT);
    }


    private static boolean exitsPromoWithNewBasePriceByType(List<Promotion> promotions, Integer type) {
        return promotions.stream()
                .anyMatch(promo -> (type.equals(promo.getSubtype())) && NEW_PRICE_DISCOUNT.equals(promo.getDiscountType()));
    }

    private static Double getNewBasePriceCumulative(Double originalPrice, List<Promotion> promotions) {
        if (CollectionUtils.isEmpty(promotions)) {
            roundUp(originalPrice);
        }

        Double newBasePriceCumulative = ZERO;
        Promotion promo = getPromotionByType(promotions, TYPE_AUTOMATIC);
        Double automaticNewBasePrice = getNewBasePrice(originalPrice, promo);
        if (automaticNewBasePrice < originalPrice) {
            newBasePriceCumulative += originalPrice - automaticNewBasePrice;
        }
        promo = getPromotionByType(promotions, TYPE_DISCOUNT);
        Double discountNewBasePrice = getNewBasePrice(originalPrice, promo);
        if (discountNewBasePrice < originalPrice) {
            newBasePriceCumulative += originalPrice - discountNewBasePrice;
        }
        newBasePriceCumulative = originalPrice - newBasePriceCumulative;
        if (newBasePriceCumulative < ZERO) {
            return ZERO;
        }
        return roundUp(newBasePriceCumulative);
    }

    private static Double getNewBasePriceByPromos(Double base, List<Promotion> promotions) {
        if (CollectionUtils.isNotEmpty(promotions)) {
            List<Promotion> promos = promotions.stream()
                    .filter(promo -> (TYPE_AUTOMATIC.equals(promo.getSubtype()) || TYPE_DISCOUNT.equals(promo.getSubtype()))
                            && NEW_PRICE_DISCOUNT.equals(promo.getDiscountType()))
                    .toList();

            if (CollectionUtils.isNotEmpty(promos)) {
                Double newBase = base;
                for (Promotion promo : promos) {
                    newBase = getNewBasePrice(newBase, promo);
                }
                return newBase;
            }
        }
        return base;
    }

    private static Double getNewBasePrice(Double base, Promotion promo) {
        if (promo != null && CollectionUtils.isNotEmpty(promo.getRanges())) {
            return promo.getRanges()
                    .stream()
                    .filter(r -> r.getFrom().compareTo(base) <= 0)
                    .findAny()
                    .map(PromotionPricesRange::getValue)
                    .map(PricesUtils::roundUp)
                    .orElse(roundUp(base));
        }
        return roundUp(base);
    }

    private static List<Surcharge> getListSurcharge(Double promotedPrice, ChannelEventSurcharges surcharges, List<Promotion> promotions,
                                                    boolean eventChannelUseSpecificChannelSurcharges, SessionTaxes taxes) {
        List<Surcharge> surchargesDto = new ArrayList<>();
        if (surcharges != null) {
            surchargesDto.add(getSurcharges(promotedPrice, surcharges.getPromoter(), true, promotions, eventChannelUseSpecificChannelSurcharges, false, taxes.getSurchargesTaxes()));
            surchargesDto.add(getSurcharges(promotedPrice, surcharges.getChannel(), false, promotions, eventChannelUseSpecificChannelSurcharges, false, taxes.getChannelSurchargesTaxes()));
            Surcharge secondaryMarketPromoterSurcharge = getSurcharges(promotedPrice, surcharges.getPromoter(), true, promotions, eventChannelUseSpecificChannelSurcharges, true, taxes.getSurchargesTaxes());
            if (!secondaryMarketPromoterSurcharge.getValue().equals(0d)) {
                surchargesDto.add(secondaryMarketPromoterSurcharge);
            }
            Surcharge secondaryMarketChannelSurcharge = getSurcharges(promotedPrice, surcharges.getPromoter(), false, promotions, eventChannelUseSpecificChannelSurcharges, true, taxes.getChannelSurchargesTaxes());
            if (!secondaryMarketChannelSurcharge.getValue().equals(0d)) {
                surchargesDto.add(secondaryMarketChannelSurcharge);
            }
        }
        return CollectionUtils.isEmpty(surchargesDto) ? null : surchargesDto;
    }

    private static Surcharge getSurcharges(Double promotedPrice, SurchargeRanges surcharges, boolean isSurchargePromoter,
                                           List<Promotion> promotions, boolean eventChannelUseSpecificChannelSurcharges,
                                           boolean isSecondaryMarket, List<? extends TaxInfo> surchargesTaxes) {
        Surcharge surcharge = new Surcharge();
        Double surchargeWithConstrains = getSurchargeTotalWithConstrains(promotedPrice, surcharges, promotions, isSurchargePromoter, eventChannelUseSpecificChannelSurcharges, isSecondaryMarket);
        Double net = getNetPriceFromPromotedPrice(surchargeWithConstrains, surchargesTaxes);
        surcharge.setNet(net);
        surcharge.setTaxes(getTaxes(surchargesTaxes, surchargeWithConstrains, net));
        surcharge.setValue(surchargeWithConstrains);
        surcharge.setType(getSurchargeType(isSurchargePromoter, isSecondaryMarket));
        return surcharge;
    }

    private static SurchargeType getSurchargeType(boolean isSurchargePromoter, boolean isSecondaryMarket) {
        SurchargeType surchargeType;
        if (isSecondaryMarket) {
            surchargeType = isSurchargePromoter ? SurchargeType.SECONDARY_MARKET_PROMOTER : SurchargeType.SECONDARY_MARKET_CHANNEL;
        } else {
            surchargeType = isSurchargePromoter ? SurchargeType.PROMOTER : SurchargeType.CHANNEL;
        }
        return surchargeType;
    }

    private static Double getSurchargeTotalWithConstrains(Double promotedPrice, SurchargeRanges surcharges, List<Promotion> promotions,
                                                          boolean isSurchargePromoter, boolean eventChannelUseSpecificChannelSurcharges, boolean isSecondaryMarket) {
        if (surcharges == null) {
            return ZERO;
        }
        Double totalSurcharge = ZERO;
        SurchargeRange surchargeRange = null;
        if (isSecondaryMarket || CollectionUtils.isNotEmpty(surcharges.getMain())) {
            surchargeRange = getSurchargeRangeWithConstraints(promotedPrice, surcharges, promotions, isSurchargePromoter, eventChannelUseSpecificChannelSurcharges, isSecondaryMarket);
        }
        if (surchargeRange != null) {
            totalSurcharge = getSurchargeTotalWithConstrainsAndRanges(promotedPrice, surchargeRange);
        }
        return totalSurcharge;
    }

    private static Double getSurchargeTotalWithConstrainsAndRanges(Double base, SurchargeRange surchargeRange) {
        Double totalSurcharge = ZERO;
        if (surchargeRange.getFixedValue() != null) {
            totalSurcharge += surchargeRange.getFixedValue();
        }
        if (surchargeRange.getPercentageValue() != null) {
            totalSurcharge += base * surchargeRange.getPercentageValue() / 100;
        }
        if (surchargeRange.getMaximumValue() != null && totalSurcharge > surchargeRange.getMaximumValue()) {
            totalSurcharge = surchargeRange.getMaximumValue();
        } else if (surchargeRange.getMinimumValue() != null && totalSurcharge < surchargeRange.getMinimumValue()) {
            totalSurcharge = surchargeRange.getMinimumValue();
        }
        return roundUp(totalSurcharge);
    }

    private static SurchargeRange getSurchargeRangeWithConstraints(Double promotedPrice, SurchargeRanges surcharges,
                                                                   List<Promotion> promotions,
                                                                   boolean isSurchargePromoter,
                                                                   boolean eventChannelUseSpecificChannelSurcharges, boolean isSecondaryMarket) {

        SurchargeRange surchargeRange;
        if (isSecondaryMarket) {
            return getSurchargeRange(promotedPrice, surcharges.getSecondaryMarket());
        } else {
            if (CollectionUtils.isEmpty(promotions)) {
                return getSurchargeRange(promotedPrice, surcharges.getMain());
            }
            if (isSurchargePromoter) {
                if (promotions.stream().anyMatch(item -> BooleanUtils.isTrue(item.getApplyPromoterSpecificCharges()))) {
                    surchargeRange = getSurchargeRange(promotedPrice, surcharges.getPromotion());
                    return isValidSurchargeRange(surchargeRange) ? surchargeRange : getSurchargeRange(promotedPrice, surcharges.getMain());
                } else {
                    return getSurchargeRange(promotedPrice, surcharges.getMain());
                }
            } else {
                if (eventChannelUseSpecificChannelSurcharges
                        && promotions.stream().anyMatch(item -> BooleanUtils.isTrue(item.getApplyChannelSpecificCharges()))) {
                    surchargeRange = getSurchargeRange(promotedPrice, surcharges.getPromotion());
                    return isValidSurchargeRange(surchargeRange) ? surchargeRange : getSurchargeRange(promotedPrice, surcharges.getMain());
                } else {
                    return getSurchargeRange(promotedPrice, surcharges.getMain());
                }
            }
        }
    }

    private static boolean isValidSurchargeRange(SurchargeRange surchargeRange) {
        return Objects.nonNull(surchargeRange)
                && (Objects.nonNull(surchargeRange.getFixedValue()) || Objects.nonNull(surchargeRange.getPercentageValue()));
    }

    private static SurchargeRange getSurchargeRange(Double promotedPrice, List<SurchargeRange> surchargeRages) {
        if (CollectionUtils.isEmpty(surchargeRages)) {
            return null;
        }
        return surchargeRages
                .stream()
                .filter(range -> promotedPrice >= range.getFrom() && promotedPrice < range.getTo())
                .findAny()
                .orElse(null);
    }

    private static Double roundUp(Double source) {
        return BigDecimal.valueOf(source).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
