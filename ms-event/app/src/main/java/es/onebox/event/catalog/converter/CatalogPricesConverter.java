package es.onebox.event.catalog.converter;

import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.catalog.dto.CatalogPrice;
import es.onebox.event.catalog.dto.CatalogPriceSurcharge;
import es.onebox.event.catalog.dto.CatalogPrices;
import es.onebox.event.catalog.dto.CatalogPromotedPrice;
import es.onebox.event.catalog.dto.CatalogPromotedPriceVariation;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionPriceVariationType;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionValidationPeriodDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionValidationPeriodType;
import es.onebox.event.catalog.elasticsearch.pricematrix.Price;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceSurcharges;
import es.onebox.event.catalog.elasticsearch.pricematrix.PromotedPrice;
import es.onebox.event.promotions.dto.restriction.PromotionValidationPeriod;
import es.onebox.event.promotions.enums.PromotionPriceVariationType;
import es.onebox.event.promotions.enums.PromotionValidationPeriodType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class CatalogPricesConverter {

    private CatalogPricesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static CatalogPrices convert(PriceMatrix prices) {
        if (prices == null) {
            return null;
        }
        CatalogPrices data = new CatalogPrices();
        data.setMaxBasePrice(convert(prices.getMaxBasePrice()));
        data.setMinBasePrice(convert(prices.getMinBasePrice()));
        data.setMinPromotedPrice(getMinPromotedCatalogPrice(prices, PriceMatrix::getMinPromotedPrices));

        data.setMaxNetPrice(convert(prices.getMaxNetPrice()));
        data.setMinNetPrice(convert(prices.getMinNetPrice()));
        data.setMinNetPromotedPrice(getMinPromotedCatalogPrice(prices, PriceMatrix::getMinNetPromotedPrices));

        data.setMinFinalPrice(convert(prices.getMinFinalPrice()));
        data.setMaxFinalPrice(convert(prices.getMaxFinalPrice()));
        data.setMinFinalPromotedPrice(getMinPromotedCatalogPrice(prices, PriceMatrix::getMinFinalPromotedPrices));

        return data;
    }

    private static CatalogPromotedPrice getMinPromotedCatalogPrice(PriceMatrix prices, Function<PriceMatrix, List<PromotedPrice>> promotedPricesGetter) {
        List<PromotedPrice> minPromotedPrices = promotedPricesGetter.apply(prices);

        if (CollectionUtils.isEmpty(minPromotedPrices)) {
            return null;
        }

        Date currentDate = new Date();
        PromotedPrice minPromotedPrice = minPromotedPrices.stream()
                .filter(p -> p.getValidationPeriod() == null
                        || PromotionValidationPeriodType.ALL.equals(p.getValidationPeriod().getType())
                        || (p.getValidationPeriod().getFrom().before(currentDate) && p.getValidationPeriod().getTo().after(currentDate)))
                .min(Comparator.comparing(PromotedPrice::getEventPromotionTemplateId))
                .orElse(null);
        return convert(minPromotedPrice);
    }

    private static CatalogPrice convert(Price price) {
        if (price == null) {
            return null;
        }
        CatalogPrice catalogPrice = new CatalogPrice();
        fill(catalogPrice, price);
        return catalogPrice;
    }

    private static CatalogPromotedPrice convert(PromotedPrice price) {
        if (price == null) {
            return null;
        }
        CatalogPromotedPrice catalogPrice = new CatalogPromotedPrice();
        fill(catalogPrice, price);
        catalogPrice.setDiscountedValue(price.getDiscountedValue());
        catalogPrice.setEventPromotionTemplateId(price.getEventPromotionTemplateId());
        catalogPrice.setValidityPeriod(convert(price.getValidationPeriod()));
        catalogPrice.setVariation(convert(price.getVariationType(), price.getVariationValue()));
        catalogPrice.setOriginalPrice(price.getOriginalPrice());
        return catalogPrice;
    }

    private static CatalogPromotionValidationPeriodDTO convert(PromotionValidationPeriod in) {
        if (in == null) return null;
        CatalogPromotionValidationPeriodDTO out = new CatalogPromotionValidationPeriodDTO();
        out.setFrom(DateUtils.getZonedDateTime(in.getFrom()));
        out.setTo(DateUtils.getZonedDateTime(in.getTo()));
        out.setType(CatalogPromotionValidationPeriodType.valueOf(in.getType().name()));
        return out;
    }


    private static void fill(CatalogPrice catalogPrice, Price price) {
        catalogPrice.setValue(price.getValue());
        catalogPrice.setSurcharge(convert(price.getSurcharge()));
    }

    private static CatalogPriceSurcharge convert(PriceSurcharges surcharge) {
        if (surcharge == null) {
            return null;
        }
        CatalogPriceSurcharge result = new CatalogPriceSurcharge();
        result.setChannel(surcharge.getChannel());
        result.setPromoter(surcharge.getPromoter());
        result.setSecondaryMarket(surcharge.getSecondaryMarket());
        return result;
    }

    private static CatalogPromotedPriceVariation convert(PromotionPriceVariationType type, Double value) {
        if (type == null) return null;
        CatalogPromotedPriceVariation out = new CatalogPromotedPriceVariation();
        out.setValue(value);
        out.setType(CatalogPromotionPriceVariationType.fromName(type.name()));
        return out;
    }
}