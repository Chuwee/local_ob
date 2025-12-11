package es.onebox.event.products.amqp.productupdater;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.products.dao.couch.DeliveryPoint;
import es.onebox.event.products.dao.couch.EventSessionsDeliveryPoints;
import es.onebox.event.products.dao.couch.ProductCatalogAttribute;
import es.onebox.event.products.dao.couch.ProductCatalogChannel;
import es.onebox.event.products.dao.couch.ProductCatalogCommElement;
import es.onebox.event.products.dao.couch.ProductCatalogCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCatalogPromotion;
import es.onebox.event.products.dao.couch.ProductCatalogValue;
import es.onebox.event.products.dao.couch.ProductCatalogVariant;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.dao.couch.ProductPromotionActivator;
import es.onebox.event.products.dao.couch.ProductPromotionDiscountType;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.DeliveryPointAddressDTO;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.promotions.enums.PromotionType;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPromocionProductoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductCatalogConverter {

    private ProductCatalogConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<ProductCatalogVariant> variants(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        List<ProductCatalogVariant> variants = new ArrayList<>();
        for (CpanelProductVariantRecord cpanelProductVariantRecord : productCatalogUpdaterCache.getProductVariantRecords()) {
            Integer variantId = cpanelProductVariantRecord.getVariantid();
            ProductCatalogVariant productCatalogVariant = new ProductCatalogVariant();
            productCatalogVariant.setId(variantId.longValue());
            productCatalogVariant.setName(cpanelProductVariantRecord.getName());
            productCatalogVariant.setPrice(productCatalogUpdaterCache.getVariantPrices().get(variantId));
            productCatalogVariant.setSessionPrice(productCatalogUpdaterCache.getVariantSessionPrices().get(variantId));
            productCatalogVariant.setSku(cpanelProductVariantRecord.getSku());
            productCatalogVariant.setInitialStock(cpanelProductVariantRecord.getStock() != null ? cpanelProductVariantRecord.getStock() : null);
            productCatalogVariant.setValue1(cpanelProductVariantRecord.getVariantvalue1() != null ? cpanelProductVariantRecord.getVariantvalue1().longValue() : null);
            productCatalogVariant.setValue2(cpanelProductVariantRecord.getVariantvalue2() != null ? cpanelProductVariantRecord.getVariantvalue2().longValue() : null);
            productCatalogVariant.setCreateDate(CommonUtils.timestampToZonedDateTime(cpanelProductVariantRecord.getCreateDate()));
            productCatalogVariant.setUpdateDate(CommonUtils.timestampToZonedDateTime(cpanelProductVariantRecord.getUpdateDate()));
            Map<Integer, List<CpanelProductVariantSessionRecord>> variantSessionPrices = productCatalogUpdaterCache.getProductVariantSessionRecords();
            variants.add(productCatalogVariant);
        }
        return variants;
    }


    public static ProductCatalogAttribute attribute(Integer attributeId, ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        CpanelProductAttributeRecord attributeRecord = Optional.ofNullable(productCatalogUpdaterCache.getProductAttributeRecords())
                .orElse(Collections.emptyList()).stream()
                .filter(a -> attributeId.equals(a.getAttributeid()))
                .findAny().orElse(null);

        if (attributeRecord == null) {
            return null;
        }

        ProductCatalogAttribute result = new ProductCatalogAttribute();
        result.setId(attributeRecord.getAttributeid().longValue());
        result.setName(attributeRecord.getName());
        result.setPosition(attributeRecord.getPosition().longValue());

        ProductContentDocument attributeContent = Optional.ofNullable(productCatalogUpdaterCache.getAttributeContents())
                .orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .filter(attContent -> attributeId.longValue() == attContent.getAttributeId())
                .findAny().orElse(null);

        result.setContents(getProductAttributeContent(attributeContent));

        List<CpanelProductAttributeValueRecord> attributeValues = Optional.ofNullable(productCatalogUpdaterCache.getProductAttributeValueRecords())
                .orElse(Collections.emptyList()).stream()
                .filter(v -> attributeId.equals(v.getProductattributeid()))
                .collect(Collectors.toList());

        result.setValues(getProductAttributeValues(attributeValues, attributeContent));

        return result;
    }

    private static List<ProductCatalogValue> getProductAttributeValues(List<CpanelProductAttributeValueRecord> productAttributeValues,
                                                                       ProductContentDocument attributeContent) {
        if (CollectionUtils.isEmpty(productAttributeValues)) {
            return null;
        }

        return productAttributeValues.stream()
                .map(pvr -> new ProductCatalogValue(pvr.getValueid().longValue(), pvr.getName(), pvr.getPosition(),
                        getProductValueContent(pvr.getValueid(), attributeContent)))
                .collect(Collectors.toList());
    }

    private static Map<String, Map<String, String>> getProductValueContent(Integer valueId, ProductContentDocument attributeContent) {
        String value = valueId.toString();
        if (attributeContent == null || attributeContent.getValues() == null || !attributeContent.getValues().containsKey(value)
                || attributeContent.getValues().get(value).getLanguageElements() == null
                || attributeContent.getValues().get(value).getLanguageElements().isEmpty()) {
            return null;
        }

        return attributeContent.getValues().get(value).getLanguageElements().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (e) -> getCommunicationElementTexts(e.getValue())));
    }

    private static Map<String, Map<String, String>> getProductAttributeContent(ProductContentDocument attributeContent) {
        if (attributeContent == null || attributeContent.getLanguageElements() == null
                || attributeContent.getLanguageElements().isEmpty()) {
            return null;
        }
        return attributeContent.getLanguageElements().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e ->
                                getCommunicationElementTexts(e.getValue()),
                        (first, second) -> second));
    }

    private static Map<String, String> getCommunicationElementTexts(ProductCommunicationElement e) {
        if (e == null || CollectionUtils.isEmpty(e.getTexts())) {
            return new HashMap<>();
        }
        return e.getTexts().stream()
                .filter(commElement -> StringUtils.isNotEmpty(commElement.getValue()))
                .collect(Collectors.toMap(ProductCommunicationElementDetail::getType, ProductCommunicationElementDetail::getValue));
    }


    public static List<ProductCatalogChannel> productChannels(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        List<ProductChannelRecord> productChannelRecords = productCatalogUpdaterCache.getProductChannelRecords();

        //Set all channels for the productId
        return productChannelRecords.stream()
                .map(productChannelRecord -> {
                    ProductCatalogChannel productCatalogChannel = new ProductCatalogChannel();
                    productCatalogChannel.setId(productChannelRecord.getChannelid().longValue());
                    productCatalogChannel.setCheckoutSuggestionEnabled(
                            ConverterUtils.isByteAsATrue(productChannelRecord.getCheckoutsuggestionenabled()));
                    productCatalogChannel.setStandaloneEnabled(
                            ConverterUtils.isByteAsATrue(productChannelRecord.getStandaloneenabled()));
                    return productCatalogChannel;
                })
                .collect(Collectors.toList());
    }

    public static List<EventSessionsDeliveryPoints> getProductEventSessionsAndDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        List<ProductEventRecord> productEvents = getProductEvents(productCatalogUpdaterCache);
        Map<Long, List<DeliveryPointRecord>> productEventDeliveryPoints = getProductEventDeliveryPoints(productCatalogUpdaterCache);
        Map<Long, List<DeliveryPointRecord>> productSessionsDeliveryPoints = getProductSessionDeliveryPoints(productCatalogUpdaterCache);

        List<EventSessionsDeliveryPoints> events = new ArrayList<>();

        for (ProductEventRecord productEventRecord : productEvents) {
            EventSessionsDeliveryPoints eventSessionsDeliveryPoints = createEventSessionsDeliveryPoints(productEventRecord, productEventDeliveryPoints, productSessionsDeliveryPoints);
            events.add(eventSessionsDeliveryPoints);
        }

        return events;
    }

    private static EventSessionsDeliveryPoints createEventSessionsDeliveryPoints(ProductEventRecord productEventRecord,
                                                                                 Map<Long, List<DeliveryPointRecord>> productEventDeliveryPoints,
                                                                                 Map<Long, List<DeliveryPointRecord>> productSessionsDeliveryPoints) {
        EventSessionsDeliveryPoints eventSessionsDeliveryPoints = new EventSessionsDeliveryPoints();
        eventSessionsDeliveryPoints.setId(productEventRecord.getEventid().longValue());
        eventSessionsDeliveryPoints.setSessionSelectionType(SelectionType.get(productEventRecord.getSessionsselectiontype()));

        Map<Long, Set<Long>> sessionsDeliveryPoints = new HashMap<>();

        Long eventId = eventSessionsDeliveryPoints.getId();

        eventSessionsDeliveryPoints.setEventDeliveryPoints(new HashSet<>());

        if (productEventDeliveryPoints.containsKey(eventId)) {
            List<DeliveryPointRecord> deliveryPointRecords = productEventDeliveryPoints.get(eventId);
            Set<Long> uniqueDeliveryPoints = new HashSet<>(deliveryPointRecords.stream().map(DeliveryPointRecord::getDeliverypointid).map(Number::longValue).toList());
            eventSessionsDeliveryPoints.setEventDeliveryPoints(new HashSet<>(uniqueDeliveryPoints));
        }

        if (productSessionsDeliveryPoints != null) {
            for (Map.Entry<Long, List<DeliveryPointRecord>> entry : productSessionsDeliveryPoints.entrySet()) {
                List<Integer> deliveryPointIds = entry.getValue().stream().map(DeliveryPointRecord::getDeliverypointid).collect(Collectors.toList());
                Set<Long> uniqueList = deliveryPointIds.stream().distinct().map(Long::valueOf).collect(Collectors.toSet());
                sessionsDeliveryPoints.put(entry.getKey(), uniqueList);
            }
        }
        eventSessionsDeliveryPoints.setSessionsDeliveryPoints(sessionsDeliveryPoints);

        return eventSessionsDeliveryPoints;
    }

    public static Set<DeliveryPoint> getDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache,
                                                       Integer productDeliveryType) {
        if (productDeliveryType == null) {
            return Collections.emptySet();
        }

        if (productDeliveryType.equals(ProductDeliveryType.SESSION.getId())) {
            Map<Long, List<DeliveryPointRecord>> productEventDeliveryPoints = getProductEventDeliveryPoints(productCatalogUpdaterCache);
            Map<Long, List<DeliveryPointRecord>> productSessionsDeliveryPoints = getProductSessionDeliveryPoints(productCatalogUpdaterCache);

            Set<DeliveryPointRecord> allDeliveryPointRecords = new HashSet<>();
            productEventDeliveryPoints.values().forEach(allDeliveryPointRecords::addAll);
            productSessionsDeliveryPoints.values().forEach(allDeliveryPointRecords::addAll);

            return mapDeliveryPointsToDTO(allDeliveryPointRecords);
        } else {
            return mapDeliveryPointsToDTO(new HashSet<>(getProductDeliveryPoints(productCatalogUpdaterCache)));
        }
    }

    public static List<ProductCatalogPromotion> productPromotions(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        List<ProductCatalogPromotion> promotions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productCatalogUpdaterCache.getPromotions())) {
            for (CpanelPromocionProductoRecord record : productCatalogUpdaterCache.getPromotions()) {
                ProductCatalogPromotion catalogPromotion = new ProductCatalogPromotion();
                catalogPromotion.setId(record.getIdpromocion().longValue());
                catalogPromotion.setName(record.getNombre());
                catalogPromotion.setType(PromotionType.fromId(record.getTipo().intValue()));
                if (record.getTipodescuento() != null) {
                    catalogPromotion.setDiscountType(ProductPromotionDiscountType.fromId(record.getTipodescuento().intValue()));
                    catalogPromotion.setDiscountValue(record.getValordescuento());
                }
                if (record.getTipoactivador() != null) {
                    catalogPromotion.setActivator(ProductPromotionActivator.fromId(record.getTipoactivador().intValue()));
                    catalogPromotion.setActivatorId(record.getIdactivador());
                }
                promotions.add(catalogPromotion);
            }
        }
        return promotions;
    }

    private static Set<DeliveryPoint> mapDeliveryPointsToDTO(Set<DeliveryPointRecord> deliveryPoints) {
        return deliveryPoints.stream()
                .map(deliveryPointRecord -> {
                    DeliveryPoint deliveryPoint = new DeliveryPoint();
                    deliveryPoint.setId(deliveryPointRecord.getDeliverypointid().longValue());
                    deliveryPoint.setName(deliveryPointRecord.getName());
                    DeliveryPointAddressDTO deliveryPointAddressDTO = getDeliveryPointAddressDTO(deliveryPointRecord);
                    deliveryPoint.setLocation(deliveryPointAddressDTO);
                    return deliveryPoint;
                })
                .collect(Collectors.toSet());
    }

    private static DeliveryPointAddressDTO getDeliveryPointAddressDTO(DeliveryPointRecord deliveryPointRecord) {
        DeliveryPointAddressDTO out = new DeliveryPointAddressDTO();
        out.setAddress(deliveryPointRecord.getAddress());
        out.setCity(deliveryPointRecord.getCity());
        out.setNotes(deliveryPointRecord.getNotes() != null ? deliveryPointRecord.getNotes() : null);
        out.setZipCode(deliveryPointRecord.getZipcode() != null ? deliveryPointRecord.getZipcode() : null);
        out.setCountrySubdivision(new CodeNameDTO(deliveryPointRecord.getCountrySubdivisionCode(), deliveryPointRecord.getCountrySubdivisionName()));
        out.setCountry(new CodeNameDTO(deliveryPointRecord.getCountryCode(), deliveryPointRecord.getCountryName()));
        return out;
    }

    private static List<ProductEventRecord> getProductEvents(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        return productCatalogUpdaterCache.getProductEventRecords();
    }

    private static Map<Long, List<DeliveryPointRecord>> getProductEventDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        return productCatalogUpdaterCache.getEventDeliveryPoints();
    }

    private static List<DeliveryPointRecord> getProductDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        return productCatalogUpdaterCache.getProductDeliveryPoints();
    }

    private static Map<Long, List<DeliveryPointRecord>> getProductSessionDeliveryPoints(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        return productCatalogUpdaterCache.getSessionDeliveryPoints();
    }

    public static ProductCatalogCommElement commElement(ProductCatalogUpdaterCache productCatalogUpdaterCache) {
        ProductCatalogCommElement commElements = new ProductCatalogCommElement();

        ProductCommunicationElementDocument productCommunicationElementDocument = productCatalogUpdaterCache.getCommunicationElements();
        Set<String> activeLanguages = productCatalogUpdaterCache.getProductLanguages().stream()
                .map(ProductLanguageRecord::getCode)
                .collect(Collectors.toSet());

        if (productCommunicationElementDocument != null) {
            commElements.setTexts(processCommunicationElements(productCommunicationElementDocument.getLanguageElements(), true, activeLanguages));
            commElements.setImages(processCommunicationElements(productCommunicationElementDocument.getLanguageElements(), false, activeLanguages));
        }
        return commElements;
    }

    private static List<ProductCatalogCommunicationElement> processCommunicationElements(Map<String, ProductCommunicationElement> elements,
                                                                                         boolean isText, Set<String> activeLanguages) {
        Map<String, List<ProductCatalogCommunicationElement>> groupedElements = new HashMap<>();

        for (Map.Entry<String, ProductCommunicationElement> communicationElement : elements.entrySet()) {
            if (activeLanguages.contains(communicationElement.getKey())) {
                List<ProductCommunicationElementDetail> detailList = isText ? communicationElement.getValue().getTexts() : communicationElement.getValue().getImages();
                if (detailList != null) {
                    for (ProductCommunicationElementDetail productCommunicationElementDetail : detailList) {
                        ProductCatalogCommunicationElement productCatalogCommunicationElement = new ProductCatalogCommunicationElement();
                        productCatalogCommunicationElement.setType(productCommunicationElementDetail.getType());

                        Map<String, String> values = new HashMap<>();
                        values.put(communicationElement.getKey(), productCommunicationElementDetail.getValue());
                        productCatalogCommunicationElement.setValue(values);

                        if (!isText) {
                            productCatalogCommunicationElement.setAltText(productCommunicationElementDetail.getAltText());
                            if (productCommunicationElementDetail.getPosition() != null) {
                                productCatalogCommunicationElement.setPosition(productCommunicationElementDetail.getPosition());
                            }
                        }

                        String type = productCatalogCommunicationElement.getType();
                        groupedElements.putIfAbsent(type, new ArrayList<>());
                        groupedElements.get(type).add(productCatalogCommunicationElement);
                    }
                }
            }
        }

        List<ProductCatalogCommunicationElement> processedElements = new ArrayList<>();
        for (Map.Entry<String, List<ProductCatalogCommunicationElement>> entry : groupedElements.entrySet()) {
            if (isText) {
                processedElements.add(processGroupedCommunicationElements(entry.getKey(), entry.getValue()));
            } else {
                processedElements = processGroupedImages(entry.getValue());
            }
        }
        return processedElements;
    }

    private static List<ProductCatalogCommunicationElement> processGroupedImages(List<ProductCatalogCommunicationElement> elements) {
        return new ArrayList<>(elements);
    }

    private static ProductCatalogCommunicationElement processGroupedCommunicationElements(String type, List<ProductCatalogCommunicationElement> elements) {
        ProductCatalogCommunicationElement groupedElement = new ProductCatalogCommunicationElement();
        groupedElement.setType(type);

        for (ProductCatalogCommunicationElement element : elements) {
            if (groupedElement.getValue() == null) {
                groupedElement.setValue(new HashMap<>());
            }
            element.getValue().forEach((key, value) -> groupedElement.getValue().put(key, value));

            if (element.getPosition() != null) {
                groupedElement.setPosition(element.getPosition());
            }
        }
        return groupedElement;
    }


}
