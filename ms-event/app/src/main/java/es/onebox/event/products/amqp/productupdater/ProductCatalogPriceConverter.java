package es.onebox.event.products.amqp.productupdater;

import es.onebox.core.order.utils.tax.TaxUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.surcharges.SurchargeUtils;
import es.onebox.event.priceengine.surcharges.dto.ProductSurchargesRanges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;
import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dao.couch.ProductCatalogPriceDetail;
import es.onebox.event.products.dao.couch.ProductCatalogPricePromotedDetail;
import es.onebox.event.products.dao.couch.ProductCatalogPriceSurcharge;
import es.onebox.event.products.dao.couch.ProductCatalogPromotion;
import es.onebox.event.products.dao.couch.ProductCatalogVariantMinPrice;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPrice;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceSurcharges;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceTaxes;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceTaxesBreakdown;
import es.onebox.event.products.dao.couch.ProductPromotionActivator;
import es.onebox.event.products.dao.couch.ProductSurchargeType;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.promotions.enums.PromotionType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;

public class ProductCatalogPriceConverter {


    private ProductCatalogPriceConverter() {}

    public static ProductCatalogVariantPrice buildProductCatalogVariantPrice(
            Double basePrice,
            SurchargeRanges promoterSurchargesRanges,
            List<ProductTaxInfo> taxes,
            List<ProductTaxInfo> surchargesTaxes
    ) {

        ProductSurchargesRanges productSurchargesRanges = new ProductSurchargesRanges();
        productSurchargesRanges.setPromoter(promoterSurchargesRanges);

        ProductCatalogPriceSurcharge productCatalogPriceSurcharge = new ProductCatalogPriceSurcharge();
        productCatalogPriceSurcharge.setPromoter(SurchargeUtils.calculateProductPromoterSurcharge(basePrice, productSurchargesRanges, false));

        ProductCatalogVariantPrice price = new ProductCatalogVariantPrice();
        price.setMin(buildProductCatalogVariantMinPrice(basePrice, productCatalogPriceSurcharge));
        price.setBase(NumberUtils.zeroIfNull(basePrice));
        price.setTotal(NumberUtils.sum(basePrice, productCatalogPriceSurcharge.getPromoter()));

        if (CollectionUtils.isNotEmpty(taxes)) {
            price.setNet(TaxUtils.calculateNetPrice(
                            basePrice,
                            taxes.stream().map(ProductTaxInfo::getValue).toList()
                    )
            );
            price.setTaxes(buildProductCatalogVariantPriceTaxes(basePrice, taxes));
        }

        price.setSurcharges(buildProductCatalogVariantPriceSurcharges(productCatalogPriceSurcharge, surchargesTaxes));

        return price;
    }

    private static ProductCatalogVariantMinPrice buildProductCatalogVariantMinPrice(Double price, ProductCatalogPriceSurcharge surcharges) {

        ProductCatalogVariantMinPrice minPrice = new ProductCatalogVariantMinPrice();
        ProductCatalogPriceSurcharge surcharge = new ProductCatalogPriceSurcharge();

        surcharge.setPromoter(NumberUtils.zeroIfNull(surcharges.getPromoter()));

        minPrice.setValue(price);
        minPrice.setSurcharge(surcharge);

        return minPrice;
    }

    public static ProductCatalogPrice buildProductCatalogPrice(ProductCatalogUpdaterCache productCatalogUpdaterCache) {

        if (productCatalogUpdaterCache == null || MapUtils.isEmpty(productCatalogUpdaterCache.getVariantPrices())) {
            return null;
        }

        ProductCatalogPrice out = new ProductCatalogPrice();
        ProductCatalogVariantPrice minVariant = null;
        ProductCatalogVariantPrice maxVariant = null;
        ProductCatalogVariantPrice minFinalVariant = null;
        ProductCatalogVariantPrice maxFinalVariant = null;
        List<ProductTaxInfo> taxes = productCatalogUpdaterCache.getTaxes();
        List<ProductTaxInfo> surchargesTaxes = productCatalogUpdaterCache.getSurchargesTaxes();

        for (ProductCatalogVariantPrice variant : productCatalogUpdaterCache.getVariantPrices().values()) {
            if (variant == null) {
                continue;
            }

            Double base = NumberUtils.zeroIfNull(variant.getBase());
            Double total = NumberUtils.zeroIfNull(variant.getTotal());

            if (minVariant == null || base < minVariant.getBase()) {
                minVariant = variant;
            }

            if (maxVariant == null || base > maxVariant.getBase()) {
                maxVariant = variant;
            }

            if (minFinalVariant == null || total < minFinalVariant.getTotal()) {
                minFinalVariant = variant;
            }

            if (maxFinalVariant == null || total > maxFinalVariant.getTotal()) {
                maxFinalVariant = variant;
            }
        }

        out.setMin(
                buildProductCatalogPriceDetail(
                        minVariant,
                        ProductCatalogVariantPrice::getBase,
                        ProductCatalogVariantPriceSurcharges::getValue
                )
        );

        out.setMinNet(
                buildProductCatalogPriceDetail(
                        minVariant,
                        ProductCatalogVariantPrice::getNet,
                        ProductCatalogVariantPriceSurcharges::getNet
                )
        );

        ProductCatalogPricePromotedDetail minPromoted = buildProductCatalogPricePromotedDetail(productCatalogUpdaterCache, minVariant);
        out.setMinPromoted(minPromoted);

        out.setMinNetPromoted(
                convertToNetPromotedDetail(
                        minPromoted,
                        taxes,
                        surchargesTaxes
                )
        );

        out.setMax(
                buildProductCatalogPriceDetail(
                        maxVariant,
                        ProductCatalogVariantPrice::getBase,
                        ProductCatalogVariantPriceSurcharges::getValue
                )
        );

        out.setMaxNet(
                buildProductCatalogPriceDetail(
                        maxVariant,
                        ProductCatalogVariantPrice::getNet,
                        ProductCatalogVariantPriceSurcharges::getNet
                )
        );

        out.setMinFinal(
                buildProductCatalogPriceDetail(
                        minFinalVariant,
                        ProductCatalogVariantPrice::getBase,
                        ProductCatalogVariantPriceSurcharges::getValue
                )
        );

        out.setMaxFinal(
                buildProductCatalogPriceDetail(
                        maxFinalVariant,
                        ProductCatalogVariantPrice::getBase,
                        ProductCatalogVariantPriceSurcharges::getValue
                )
        );

        out.setMinFinalPromoted(buildProductCatalogPriceMinFinalPromoted(productCatalogUpdaterCache));
        return out;
    }

    private static ProductCatalogPriceDetail buildProductCatalogPriceDetail(
            ProductCatalogVariantPrice variantPrice,
            ToDoubleFunction<ProductCatalogVariantPrice> priceGetter,
            ToDoubleFunction<ProductCatalogVariantPriceSurcharges> surchargeGetter) {

        if (variantPrice == null) {
            return null;
        }

        ProductCatalogPriceDetail priceDetail = new ProductCatalogPriceDetail();
        priceDetail.setValue(priceGetter.applyAsDouble(variantPrice));
        ProductCatalogPriceSurcharge surcharge = new ProductCatalogPriceSurcharge(DOUBLE_ZERO);

        if (CollectionUtils.isNotEmpty(variantPrice.getSurcharges())) {
            Double promoterSurcharges = DOUBLE_ZERO;

            for (ProductCatalogVariantPriceSurcharges productCatalogVariantPriceSurcharges : variantPrice.getSurcharges()) {
                if (productCatalogVariantPriceSurcharges == null || productCatalogVariantPriceSurcharges.getType() == null) {
                    continue;
                }

                promoterSurcharges = switch (productCatalogVariantPriceSurcharges.getType()) {
                    case PROMOTER ->
                            NumberUtils.sum(promoterSurcharges, surchargeGetter.applyAsDouble(productCatalogVariantPriceSurcharges));
                };
            }

            surcharge.setPromoter(promoterSurcharges);
        }

        priceDetail.setSurcharge(surcharge);
        return priceDetail;
    }

    private static ProductCatalogPricePromotedDetail buildProductCatalogPricePromotedDetail(ProductCatalogUpdaterCache productCatalogUpdaterCache, ProductCatalogVariantPrice variantPrice) {

        if (productCatalogUpdaterCache == null || CollectionUtils.isEmpty(productCatalogUpdaterCache.getPromotions()) || variantPrice == null) {
            return null;
        }

        ProductCatalogPromotion promotion = getApplicablePromotion(productCatalogUpdaterCache);

        if (promotion == null) {
            return null;
        }

        ProductSurchargesRanges productSurchargesRanges = new ProductSurchargesRanges();
        productSurchargesRanges.setPromoter(productCatalogUpdaterCache.getSurcharges().getPromoter());

        ProductCatalogPricePromotedDetail out = new ProductCatalogPricePromotedDetail();

        Double promotionAmount = calculateAutomaticPromotionAmount(variantPrice.getBase(), promotion);
        Double basePromotedPrice = NumberUtils.minus(variantPrice.getBase(), promotionAmount);

        ProductCatalogPriceSurcharge surcharges = new ProductCatalogPriceSurcharge();

        surcharges.setPromoter(SurchargeUtils.calculateProductPromoterSurcharge(basePromotedPrice, productSurchargesRanges, true));

        out.setOriginalPrice(NumberUtils.zeroIfNull(variantPrice.getBase()));
        out.setDiscountedValue(NumberUtils.zeroIfNull(promotionAmount));
        out.setValue(basePromotedPrice);
        out.setVariationType(promotion.getDiscountType());
        out.setSurcharge(surcharges);

        return out;

    }

    private static Double calculateAutomaticPromotionAmount(Double price, ProductCatalogPromotion promotion) {
        if (price == null || promotion == null || promotion.getDiscountType() == null) {
            return null;
        }

        return switch (promotion.getDiscountType()) {
            case FIXED -> Math.min(price, promotion.getDiscountValue());
            case PERCENTAGE -> NumberUtils.percentageOf(price, promotion.getDiscountValue());
        };
    }

    private static ProductCatalogVariantPriceTaxes buildProductCatalogVariantPriceTaxes(Double basePrice, List<ProductTaxInfo> taxes) {
        if (basePrice == null || CollectionUtils.isEmpty(taxes)) {
            return null;
        }

        List<Double> allTaxesValues = taxes.stream().map(ProductTaxInfo::getValue).toList();

        ProductCatalogVariantPriceTaxes productCatalogVariantPriceTaxes = new ProductCatalogVariantPriceTaxes();
        Double netPrice = TaxUtils.calculateNetPrice(basePrice, allTaxesValues);

        productCatalogVariantPriceTaxes.setBreakdown(buildProductCatalogVariantPriceTaxesBreakdown(basePrice, netPrice, taxes));
        productCatalogVariantPriceTaxes.setTotal(NumberUtils.minus(basePrice, netPrice));

        return productCatalogVariantPriceTaxes;
    }

    private static List<ProductCatalogVariantPriceTaxesBreakdown> buildProductCatalogVariantPriceTaxesBreakdown(Double basePrice, Double netPrice, List<ProductTaxInfo> taxes) {

        if (basePrice == null || netPrice == null || CollectionUtils.isEmpty(taxes)) {
            return null;
        }

        return TaxSimulationUtils.createTaxAmount(NumberUtils.minus(basePrice, netPrice), netPrice, taxes, ProductCatalogVariantPriceTaxesBreakdown::new);
    }

    private static List<ProductCatalogVariantPriceSurcharges> buildProductCatalogVariantPriceSurcharges(ProductCatalogPriceSurcharge surcharges, List<ProductTaxInfo> surchargesTaxes) {

        List<ProductCatalogVariantPriceSurcharges> result = new ArrayList<>();
        if (surcharges == null) {
            return result;
        }

        ProductCatalogVariantPriceSurcharges out = new ProductCatalogVariantPriceSurcharges();
        Double surchargePromoter = NumberUtils.zeroIfNull(surcharges.getPromoter());
        out.setValue(surchargePromoter);
        out.setType(ProductSurchargeType.PROMOTER);
        if (CollectionUtils.isNotEmpty(surchargesTaxes)) {
            List<Double> taxes = surchargesTaxes.stream().map(ProductTaxInfo::getValue).toList();
            out.setNet(TaxUtils.calculateNetPrice(surchargePromoter, taxes));
            out.setTaxes(buildProductCatalogVariantPriceTaxes(surchargePromoter, surchargesTaxes));
        }

        result.add(out);
        return result;
    }

    private static ProductCatalogPricePromotedDetail convertToNetPromotedDetail(ProductCatalogPricePromotedDetail promotedDetail,
                                                                                List<ProductTaxInfo> taxes,
                                                                                List<ProductTaxInfo> surchargesTaxes) {
        if (promotedDetail == null || CollectionUtils.isEmpty(taxes) || CollectionUtils.isEmpty(surchargesTaxes)) {
            return null;
        }

        ProductCatalogPricePromotedDetail result = new ProductCatalogPricePromotedDetail();

        List<Double> taxesAmounts = taxes.stream().map(ProductTaxInfo::getValue).toList();
        List<Double> surchargesTaxesAmounts = surchargesTaxes.stream().map(ProductTaxInfo::getValue).toList();

        if (promotedDetail.getSurcharge() != null) {
            ProductCatalogPriceSurcharge surcharge = new ProductCatalogPriceSurcharge();
            surcharge.setPromoter(TaxUtils.calculateNetPrice(promotedDetail.getSurcharge().getPromoter(), surchargesTaxesAmounts));
            result.setSurcharge(surcharge);
        }

        result.setOriginalPrice(TaxUtils.calculateNetPrice(promotedDetail.getOriginalPrice(),taxesAmounts));
        result.setValue(TaxUtils.calculateNetPrice(promotedDetail.getValue(), taxesAmounts));

        result.setDiscountedValue(
                NumberUtils.minus(
                        result.getOriginalPrice(),
                        result.getValue()
                )
        );

        result.setVariationType(promotedDetail.getVariationType());

        return result;
    }

    private static ProductCatalogPricePromotedDetail buildProductCatalogPriceMinFinalPromoted(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        if (productCatalogUpdaterCache == null) {
            return null;
        }

        List<ProductCatalogPricePromotedDetail> productCatalogPricePromotedDetails = calculateProductCatalogPricePromotedDetails(productCatalogUpdaterCache);

        if (CollectionUtils.isEmpty(productCatalogPricePromotedDetails)) {
            return null;
        }

        ProductCatalogPricePromotedDetail minFinalPromoted = null;
        double total = DOUBLE_ZERO;

        for (ProductCatalogPricePromotedDetail productCatalogPricePromotedDetail : productCatalogPricePromotedDetails) {

            Double basePromoted = productCatalogPricePromotedDetail.getValue();
            Double promoterSurcharge = productCatalogPricePromotedDetail.getSurcharge() != null ? productCatalogPricePromotedDetail.getSurcharge().getPromoter() : null;
            Double currentTotal = NumberUtils.sum(basePromoted, promoterSurcharge);

            if (minFinalPromoted == null || currentTotal < total) {
                minFinalPromoted = productCatalogPricePromotedDetail;
                total = currentTotal;
            }
        }
        return minFinalPromoted;
    }

    private static List<ProductCatalogPricePromotedDetail> calculateProductCatalogPricePromotedDetails(ProductCatalogUpdaterCache productCatalogUpdaterCache) {

        if (productCatalogUpdaterCache == null || MapUtils.isEmpty(productCatalogUpdaterCache.getVariantPrices())) {
            return Collections.emptyList();
        }
        ProductCatalogPromotion promotion = getApplicablePromotion(productCatalogUpdaterCache);

        if (promotion == null) {
            return Collections.emptyList();
        }

        List<ProductCatalogPricePromotedDetail> result = new ArrayList<>();

        for (ProductCatalogVariantPrice variantPrice : productCatalogUpdaterCache.getVariantPrices().values()) {

            if (variantPrice == null) {
                continue;
            }

            Double basePromotedPrice = NumberUtils.minus(
                    variantPrice.getBase(),
                    calculateAutomaticPromotionAmount(variantPrice.getBase(), promotion)
            );

            ProductSurchargesRanges productSurchargesRanges = new ProductSurchargesRanges();
            SurchargeRanges promoterSurchargesRanges = productCatalogUpdaterCache.getSurcharges().getPromoter();
            productSurchargesRanges.setPromoter(promoterSurchargesRanges);

            Double promoterSurcharge = SurchargeUtils.calculateProductPromoterSurcharge(basePromotedPrice, productSurchargesRanges, true);

            ProductCatalogPricePromotedDetail out = new ProductCatalogPricePromotedDetail();

            out.setOriginalPrice(variantPrice.getBase());
            out.setValue(basePromotedPrice);
            out.setDiscountedValue(NumberUtils.minus(variantPrice.getBase(), basePromotedPrice));
            out.setVariationType(promotion.getDiscountType());

            ProductCatalogPriceSurcharge productCatalogPriceSurcharge = new ProductCatalogPriceSurcharge();
            productCatalogPriceSurcharge.setPromoter(promoterSurcharge);
            out.setSurcharge(productCatalogPriceSurcharge);

            result.add(out);
        }

        return result;
    }

    private static ProductCatalogPromotion getApplicablePromotion(ProductCatalogUpdaterCache productCatalogUpdaterCache) {

        if (productCatalogUpdaterCache == null) {
            return null;
        }

        List<ProductCatalogPromotion> promotions = ProductCatalogConverter.productPromotions(productCatalogUpdaterCache).stream()
                .filter(productCatalogPromotion -> productCatalogPromotion != null && PromotionType.AUTOMATIC.equals(productCatalogPromotion.getType()))
                .filter(productCatalogPromotion -> productCatalogPromotion.getActivatorId() == null || !ProductPromotionActivator.COLLECTIVE.equals(productCatalogPromotion.getActivator()))
                .toList();

        if (CollectionUtils.isEmpty(promotions)) {
            return null;
        }

        return promotions.stream().min(Comparator.comparing(ProductCatalogPromotion::getId)).orElse(null);
    }
}
