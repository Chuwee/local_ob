package es.onebox.event.events.products.amqp.productupdater;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.surcharges.SurchargeUtils;
import es.onebox.event.priceengine.surcharges.dto.ProductSurchargesRanges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;
import es.onebox.event.products.amqp.productupdater.ProductCatalogPriceConverter;
import es.onebox.event.products.amqp.productupdater.ProductCatalogUpdaterCache;
import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dao.couch.ProductCatalogPriceDetail;
import es.onebox.event.products.dao.couch.ProductCatalogPricePromotedDetail;
import es.onebox.event.products.dao.couch.ProductCatalogPriceSurcharge;
import es.onebox.event.products.dao.couch.ProductCatalogVariant;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPrice;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceSurcharges;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceTaxes;
import es.onebox.event.products.dao.couch.ProductCatalogVariantPriceTaxesBreakdown;
import es.onebox.event.products.dao.couch.ProductPromotionDiscountType;
import es.onebox.event.products.dao.couch.ProductSurchargeType;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.surcharges.product.ProductSurcharges;
import es.onebox.jooq.cpanel.tables.records.CpanelPromocionProductoRecord;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class ProductCatalogConverterTest {

    @Test
    void buildProductCatalogVariantPrice() {

        ProductCatalogUpdaterCache productCatalogUpdaterCache = createProductCatalogUpdaterCache();
        Double basePrice = 10.0;
        List<ProductTaxInfo> taxes = createProductTaxInfoList();
        List<ProductTaxInfo> surchargesTaxes = createProductTaxInfoList();
        ProductSurchargesRanges productSurchargesRanges = new ProductSurchargesRanges();
        productSurchargesRanges.setPromoter(productCatalogUpdaterCache.getSurcharges().getPromoter());

        ProductSurchargesRanges surcharges = new ProductSurchargesRanges();
        surcharges.setPromoter(productCatalogUpdaterCache.getSurcharges().getPromoter());

        try (MockedStatic<SurchargeUtils> mockSurchargeUtilsStatic = mockStatic(SurchargeUtils.class, CALLS_REAL_METHODS)) {

            ProductCatalogVariantPrice result = ProductCatalogPriceConverter.buildProductCatalogVariantPrice(basePrice, surcharges.getPromoter(), taxes, surchargesTaxes);
            assertNotNull(result);

            assertNotNull(result.getMin());
            assertEquals(basePrice, result.getMin().getValue());
            assertNotNull(result.getSurcharges());
            assertEquals(2d, result.getMin().getSurcharge().getPromoter());

            assertEquals(basePrice, result.getBase());
            assertEquals(NumberUtils.sum(basePrice, 2d), result.getTotal());
            assertEquals(7.63, result.getNet());

            assertNotNull(result.getTaxes());
            assertEquals(2.37, result.getTaxes().getTotal());

            assertNotNull(result.getTaxes().getBreakdown());

            assertEquals(2, result.getTaxes().getBreakdown().size());
            assertEquals(1.61, result.getTaxes().getBreakdown().get(0).getAmount());
            assertEquals(1L, result.getTaxes().getBreakdown().get(0).getId());

            assertEquals(0.76, result.getTaxes().getBreakdown().get(1).getAmount());
            assertEquals(2L, result.getTaxes().getBreakdown().get(1).getId());

            assertEquals(1, result.getSurcharges().size());

            assertEquals(2d, result.getSurcharges().get(0).getValue());
            assertEquals(1.53, result.getSurcharges().get(0).getNet());
            assertEquals(ProductSurchargeType.PROMOTER, result.getSurcharges().get(0).getType());

            assertNotNull(result.getSurcharges().get(0).getTaxes());
            assertEquals(0.47, result.getSurcharges().get(0).getTaxes().getTotal());

            assertNotNull(result.getSurcharges().get(0).getTaxes().getBreakdown());
            assertEquals(2, result.getSurcharges().get(0).getTaxes().getBreakdown().size());
            assertEquals(1L, result.getSurcharges().get(0).getTaxes().getBreakdown().get(0).getId());
            assertEquals(0.32, result.getSurcharges().get(0).getTaxes().getBreakdown().get(0).getAmount());

            assertEquals(2L, result.getSurcharges().get(0).getTaxes().getBreakdown().get(1).getId());
            assertEquals(0.15, result.getSurcharges().get(0).getTaxes().getBreakdown().get(1).getAmount());

            mockSurchargeUtilsStatic.verify(() -> SurchargeUtils.calculateProductPromoterSurcharge(basePrice, productSurchargesRanges, false));
        }
    }

    @Test
    void buildProductCatalogPrice() {
        ProductCatalogUpdaterCache productCatalogUpdaterCache = createProductCatalogUpdaterCache();
        ProductCatalogPriceDetail minMax = createProductCatalogPriceDetail(false);
        ProductCatalogPriceDetail minMaxNet = createProductCatalogPriceDetail(true);
        ProductCatalogPricePromotedDetail minPromoted = createProductCatalogPricePromotedDetail(false);
        ProductCatalogPricePromotedDetail minNetPromoted = createProductCatalogPricePromotedDetail(true);
        ProductCatalogPriceDetail minFinal = createProductCatalogPriceDetail(false);
        ProductCatalogPriceDetail maxFinal = createProductCatalogPriceDetail(false);
        ProductCatalogPricePromotedDetail minFinalPromoted = createProductCatalogPricePromotedDetail(false);

        ProductSurchargesRanges surcharges = new ProductSurchargesRanges();
        surcharges.setPromoter(productCatalogUpdaterCache.getSurcharges().getPromoter());

        try (MockedStatic<SurchargeUtils> mockSurchargeUtils = mockStatic(SurchargeUtils.class, CALLS_REAL_METHODS)) {

            ProductCatalogPrice result = ProductCatalogPriceConverter.buildProductCatalogPrice(productCatalogUpdaterCache);

            assertNotNull(result);
            assertEquals(result.getMin(), minMax);
            assertEquals(result.getMax(), minMax);
            assertEquals(result.getMinNet(), minMaxNet);
            assertEquals(result.getMaxNet(), minMaxNet);
            assertEquals(result.getMinFinal(), minFinal);
            assertEquals(result.getMaxFinal(), maxFinal);
            assertEquals(result.getMinPromoted(), minPromoted);
            assertEquals(result.getMinNetPromoted(), minNetPromoted);
            assertEquals(result.getMinFinalPromoted(), minFinalPromoted);

            mockSurchargeUtils.verify(() -> SurchargeUtils.calculateProductPromoterSurcharge(0d, surcharges, true), times(2));
        }
    }

    private List<ProductTaxInfo> createProductTaxInfoList() {

        ProductTaxInfo taxInfo = new ProductTaxInfo();
        taxInfo.setId(1L);
        taxInfo.setValue(21d);
        taxInfo.setName("Tax 1");

        ProductTaxInfo taxInfo2 = new ProductTaxInfo();
        taxInfo2.setId(2L);
        taxInfo2.setValue(10d);
        taxInfo2.setName("Tax 2");

        return List.of(taxInfo, taxInfo2);

    }

    private ProductCatalogUpdaterCache createProductCatalogUpdaterCache() {
        ProductCatalogVariantPriceTaxesBreakdown breakdown = new ProductCatalogVariantPriceTaxesBreakdown();
        breakdown.setId(1L);
        breakdown.setAmount(1.60);

        ProductCatalogVariantPriceTaxesBreakdown breakdown2 = new ProductCatalogVariantPriceTaxesBreakdown();
        breakdown2.setId(2L);
        breakdown2.setAmount(0.77);

        ProductCatalogVariantPriceTaxes productCatalogVariantPriceTaxes = new ProductCatalogVariantPriceTaxes();
        productCatalogVariantPriceTaxes.setTotal(2.37);
        productCatalogVariantPriceTaxes.setBreakdown(List.of(breakdown, breakdown2));

        SurchargeRange surchargeRangePromotion = new SurchargeRange();
        surchargeRangePromotion.setFrom(0d);
        surchargeRangePromotion.setTo(11d);
        surchargeRangePromotion.setFixedValue(1D);
        surchargeRangePromotion.setPercentageValue(10d);
        surchargeRangePromotion.setMinimumValue(null);
        surchargeRangePromotion.setMaximumValue(null);

        SurchargeRange surchargeRangeMain = new SurchargeRange();
        surchargeRangeMain.setFrom(0d);
        surchargeRangeMain.setTo(11d);
        surchargeRangeMain.setFixedValue(1D);
        surchargeRangeMain.setPercentageValue(10d);
        surchargeRangeMain.setMinimumValue(null);
        surchargeRangeMain.setMaximumValue(null);

        SurchargeRanges surchargeRanges = new SurchargeRanges();
        surchargeRanges.setPromotion(List.of(surchargeRangePromotion));
        surchargeRanges.setMain(List.of(surchargeRangeMain));

        ProductCatalogVariantPriceTaxesBreakdown surchargesBreakdown1 = new ProductCatalogVariantPriceTaxesBreakdown();
        surchargesBreakdown1.setId(1L);
        surchargesBreakdown1.setAmount(0.16);

        ProductCatalogVariantPriceTaxesBreakdown surchargesBreakdown2 = new ProductCatalogVariantPriceTaxesBreakdown();
        surchargesBreakdown2.setId(2L);
        surchargesBreakdown2.setAmount(0.08);

        ProductCatalogVariantPriceTaxes surchargesTaxes = new ProductCatalogVariantPriceTaxes();
        surchargesTaxes.setTotal(0.24);
        surchargesTaxes.setBreakdown(List.of(surchargesBreakdown1, surchargesBreakdown2));

        ProductCatalogVariantPriceSurcharges productCatalogVariantPriceSurcharges = new ProductCatalogVariantPriceSurcharges();
        productCatalogVariantPriceSurcharges.setValue(1D);
        productCatalogVariantPriceSurcharges.setNet(0.76);
        productCatalogVariantPriceSurcharges.setType(ProductSurchargeType.PROMOTER);
        productCatalogVariantPriceSurcharges.setTaxes(surchargesTaxes);

        ProductCatalogVariantPrice productCatalogVariantPrice = new ProductCatalogVariantPrice();
        productCatalogVariantPrice.setBase(10d);
        productCatalogVariantPrice.setNet(7.63);
        productCatalogVariantPrice.setTotal(11d);
        productCatalogVariantPrice.setTaxes(productCatalogVariantPriceTaxes);
        productCatalogVariantPrice.setSurcharges(List.of(productCatalogVariantPriceSurcharges));

        ProductCatalogVariant variant = new ProductCatalogVariant();
        variant.setId(1L);
        variant.setPrice(productCatalogVariantPrice);

        ProductSurcharges surcharges = new ProductSurcharges();
        surcharges.setPromoter(surchargeRanges);

        CpanelPromocionProductoRecord promotion = new CpanelPromocionProductoRecord();
        promotion.setIdpromocion(1);
        promotion.setNombre("Promocion 1");
        promotion.setTipo((byte) 1);
        promotion.setTipoactivador((byte) 0);
        promotion.setTipodescuento((byte) 0);
        promotion.setActivada((byte) 1);
        promotion.setValordescuento(11D);

        ProductCatalogUpdaterCache productCatalogUpdaterCache = new ProductCatalogUpdaterCache();
        productCatalogUpdaterCache.setVariantPrices(Map.of(variant.getId().intValue(), productCatalogVariantPrice));
        productCatalogUpdaterCache.setSurcharges(surcharges);
        productCatalogUpdaterCache.setTaxes(createProductTaxInfoList());
        productCatalogUpdaterCache.setSurchargesTaxes(createProductTaxInfoList());
        productCatalogUpdaterCache.setPromotions(List.of(promotion));

        return productCatalogUpdaterCache;

    }

    private ProductCatalogPriceDetail createProductCatalogPriceDetail(boolean isNet) {

        ProductCatalogPriceDetail productCatalogPriceDetail = new ProductCatalogPriceDetail();
        ProductCatalogPriceSurcharge surcharge = new ProductCatalogPriceSurcharge();
        productCatalogPriceDetail.setSurcharge(surcharge);

        if (isNet) {
            productCatalogPriceDetail.setValue(7.63);
            surcharge.setPromoter(0.76);
        } else {
            productCatalogPriceDetail.setValue(10D);
            surcharge.setPromoter(1D);

        }
        return productCatalogPriceDetail;
    }

    private ProductCatalogPricePromotedDetail createProductCatalogPricePromotedDetail(boolean isNet) {

        ProductCatalogPricePromotedDetail productCatalogPricePromotedDetail = new ProductCatalogPricePromotedDetail();
        productCatalogPricePromotedDetail.setVariationType(ProductPromotionDiscountType.FIXED);
        ProductCatalogPriceSurcharge surcharge = new ProductCatalogPriceSurcharge();
        productCatalogPricePromotedDetail.setSurcharge(surcharge);

        if (isNet) {
            productCatalogPricePromotedDetail.setOriginalPrice(7.63);
            productCatalogPricePromotedDetail.setValue(0D);
            productCatalogPricePromotedDetail.setDiscountedValue(7.63);
            surcharge.setPromoter(0.76);

        } else {
            productCatalogPricePromotedDetail.setOriginalPrice(10D);
            productCatalogPricePromotedDetail.setValue(0D);
            productCatalogPricePromotedDetail.setDiscountedValue(10D);
            surcharge.setPromoter(1d);
        }

        return productCatalogPricePromotedDetail;
    }

}
