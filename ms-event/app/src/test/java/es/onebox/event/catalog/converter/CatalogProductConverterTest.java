package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.products.dao.couch.EventSessionsDeliveryPoints;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.dao.couch.ProductCatalogPrice;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CatalogProductConverterTest {

    @Test
    void convert() {
        testConvert(1L, ProductType.VARIANT, "Bounded Product", ProductStockType.BOUNDED, ProductStockStatus.ON_SALE);
        testConvert(2L, ProductType.SIMPLE, "Unbounded Product", ProductStockType.UNBOUNDED, ProductStockStatus.ON_SALE);
        testConvert(3L, ProductType.VARIANT, "Sold Out Product", ProductStockType.BOUNDED, ProductStockStatus.SOLD_OUT);
    }

    private void testConvert(Long id, ProductType type, String name, ProductStockType stockType, ProductStockStatus expectedAvailability) {
        ProductCatalogDocument productCatalogDocument = new ProductCatalogDocument();
        productCatalogDocument.setId(id);
        productCatalogDocument.setType(type);
        productCatalogDocument.setName(name);
        productCatalogDocument.setStartTimeUnit(ProductDeliveryTimeUnitType.HOURS);
        productCatalogDocument.setStartTimeValue(2L);
        productCatalogDocument.setEndTimeUnit(ProductDeliveryTimeUnitType.DAYS);
        productCatalogDocument.setEndTimeValue(1L);
        productCatalogDocument.setDeliveryDateFrom(ZonedDateTime.now());
        productCatalogDocument.setDeliveryDateTo(ZonedDateTime.now().plusDays(5));
        productCatalogDocument.setStockType(stockType);

        productCatalogDocument.setPrice(ObjectRandomizer.random(ProductCatalogPrice.class));
        List<EventSessionsDeliveryPoints> events = new ArrayList<>();
        events.add(new EventSessionsDeliveryPoints());
        productCatalogDocument.setEvents(events);

        Map<Long, Map<Long, ProductStockStatus>> productsAvailability = new HashMap<>();
        Map<Long, ProductStockStatus> stockStatusMap = new HashMap<>();
        stockStatusMap.put(id, expectedAvailability);
        productsAvailability.put(id, stockStatusMap);

        ProductCatalogDTO result = CatalogProductConverter.convert(productCatalogDocument, new ArrayList<>(), "test-repo", productsAvailability);

        assertNotNull(result);
        assertEquals(id, result.getProductId());
        assertEquals(name, result.getName());
        assertEquals(ProductDeliveryTimeUnitType.HOURS, result.getStartTimeUnit());
        assertEquals(2L, result.getStartTimeValue());
        assertEquals(expectedAvailability, result.getAvailability());
        checkPrices(result, productCatalogDocument);
    }

    private void checkPrices(ProductCatalogDTO result, ProductCatalogDocument expected) {

        assertNotNull(result);
        assertNotNull(result.getPrice());

        assertEquals(expected.getPrice().getMin().getValue(), result.getPrice().getMin().getValue());
        assertNotNull(result.getPrice().getMin().getSurcharge());
        assertEquals(expected.getPrice().getMin().getSurcharge().getPromoter(), result.getPrice().getMin().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMax().getValue(), result.getPrice().getMax().getValue());
        assertNotNull(result.getPrice().getMax().getSurcharge());
        assertEquals(expected.getPrice().getMax().getSurcharge().getPromoter(), result.getPrice().getMax().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMinPromoted().getValue(), result.getPrice().getMinPromoted().getValue());
        assertNotNull(result.getPrice().getMinPromoted().getSurcharge());
        assertEquals(expected.getPrice().getMinPromoted().getSurcharge().getPromoter(), result.getPrice().getMinPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinPromoted().getSurcharge().getPromoter(), result.getPrice().getMinPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinPromoted().getOriginalPrice(), result.getPrice().getMinPromoted().getOriginalPrice());
        assertEquals(expected.getPrice().getMinPromoted().getDiscountedValue(), result.getPrice().getMinPromoted().getDiscountedValue());
        assertEquals(expected.getPrice().getMinPromoted().getVariationType(), result.getPrice().getMinPromoted().getVariationType());

        assertEquals(expected.getPrice().getMinNet().getValue(), result.getPrice().getMinNet().getValue());
        assertNotNull(result.getPrice().getMinNet().getSurcharge());
        assertEquals(expected.getPrice().getMinNet().getSurcharge().getPromoter(), result.getPrice().getMinNet().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMaxNet().getValue(), result.getPrice().getMaxNet().getValue());
        assertNotNull(result.getPrice().getMaxNet().getSurcharge());
        assertEquals(expected.getPrice().getMaxNet().getSurcharge().getPromoter(), result.getPrice().getMaxNet().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMinNetPromoted().getValue(), result.getPrice().getMinNetPromoted().getValue());
        assertNotNull(result.getPrice().getMinNetPromoted().getSurcharge());
        assertEquals(expected.getPrice().getMinNetPromoted().getSurcharge().getPromoter(), result.getPrice().getMinNetPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinNetPromoted().getSurcharge().getPromoter(), result.getPrice().getMinNetPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinNetPromoted().getOriginalPrice(), result.getPrice().getMinNetPromoted().getOriginalPrice());
        assertEquals(expected.getPrice().getMinNetPromoted().getDiscountedValue(), result.getPrice().getMinNetPromoted().getDiscountedValue());
        assertEquals(expected.getPrice().getMinNetPromoted().getVariationType(), result.getPrice().getMinNetPromoted().getVariationType());

        assertEquals(expected.getPrice().getMinFinal().getValue(), result.getPrice().getMinFinal().getValue());
        assertNotNull(result.getPrice().getMinFinal().getSurcharge());
        assertEquals(expected.getPrice().getMinFinal().getSurcharge().getPromoter(), result.getPrice().getMinFinal().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMaxFinal().getValue(), result.getPrice().getMaxFinal().getValue());
        assertNotNull(result.getPrice().getMaxFinal().getSurcharge());
        assertEquals(expected.getPrice().getMaxFinal().getSurcharge().getPromoter(), result.getPrice().getMaxFinal().getSurcharge().getPromoter());

        assertEquals(expected.getPrice().getMinFinalPromoted().getValue(), result.getPrice().getMinFinalPromoted().getValue());
        assertNotNull(result.getPrice().getMinFinalPromoted().getSurcharge());
        assertEquals(expected.getPrice().getMinFinalPromoted().getSurcharge().getPromoter(), result.getPrice().getMinFinalPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinFinalPromoted().getSurcharge().getPromoter(), result.getPrice().getMinFinalPromoted().getSurcharge().getPromoter());
        assertEquals(expected.getPrice().getMinFinalPromoted().getOriginalPrice(), result.getPrice().getMinFinalPromoted().getOriginalPrice());
        assertEquals(expected.getPrice().getMinFinalPromoted().getDiscountedValue(), result.getPrice().getMinFinalPromoted().getDiscountedValue());
        assertEquals(expected.getPrice().getMinFinalPromoted().getVariationType(), result.getPrice().getMinFinalPromoted().getVariationType());

    }
}
