package es.onebox.event.catalog.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dto.ChannelCatalogProductDTO;
import es.onebox.event.catalog.dto.product.DeliveryPointDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogAttributeDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogCommunicationElementsDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogDeliveryPointAddressDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogPromotionDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogSessionDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogValueDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogVariantDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogsDTO;
import es.onebox.event.catalog.dto.product.ProductSessionDTO;
import es.onebox.event.catalog.dto.product.ProductTaxInfoDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.events.dto.TaxonomyDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.couch.DeliveryPoint;
import es.onebox.event.products.dao.couch.EventSessionsDeliveryPoints;
import es.onebox.event.products.dao.couch.ProductCatalogAttribute;
import es.onebox.event.products.dao.couch.ProductCatalogChannel;
import es.onebox.event.products.dao.couch.ProductCatalogCommElement;
import es.onebox.event.products.dao.couch.ProductCatalogCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.event.products.dao.couch.ProductCatalogPromotion;
import es.onebox.event.products.dao.couch.ProductCatalogValue;
import es.onebox.event.products.dao.couch.ProductCatalogVariant;
import es.onebox.event.products.dao.couch.ProductStockStatus;
import es.onebox.event.products.dto.DeliveryPointAddressDTO;
import es.onebox.event.products.dto.ProductTaxInfo;
import es.onebox.event.products.enums.ProductDeliveryType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CatalogProductConverter {

    private CatalogProductConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductCatalogsDTO convertList(List<ProductCatalogDocument> productCatalogDocuments,
                                                 List<ChannelSession> channelSessions, String s3Repository,
                                                 Map<Long, Map<Long, ProductStockStatus>> productsAvailability) {
        ProductCatalogsDTO productCatalogsDTO = new ProductCatalogsDTO();
        if (CollectionUtils.isEmpty(productCatalogDocuments)) {
            return new ProductCatalogsDTO();
        }
        for (ProductCatalogDocument p : productCatalogDocuments) {
            productCatalogsDTO.add(convert(p, channelSessions, s3Repository, productsAvailability));
        }
        return productCatalogsDTO;
    }

    public static ProductCatalogDTO convert(ProductCatalogDocument in, List<ChannelSession> channelSessions,
                                            String s3Repository, Map<Long, Map<Long, ProductStockStatus>> productsAvailability) {
        if (Objects.isNull(in)) {
            return null;
        }
        Long productId = in.getId();

        ProductCatalogDTO out = new ProductCatalogDTO();
        out.setProductId(productId);
        out.setName(in.getName());
        out.setStartTimeUnit(in.getStartTimeUnit());
        out.setStartTimeValue(in.getStartTimeValue());
        out.setEndTimeUnit(in.getEndTimeUnit());
        out.setEndTimeValue(in.getEndTimeValue());
        out.setDeliveryDateFrom(in.getDeliveryDateFrom());
        out.setDeliveryDateTo(in.getDeliveryDateTo());
        out.setEntity(in.getEntity());
        out.setTax(in.getTax());
        out.setSurcharges(in.getSurcharges());
        out.setProducer(in.getProducer());
        out.setType(in.getType());
        out.setCurrencyId(in.getCurrencyId());
        out.setTicketTemplateId(in.getTicketTemplateId());
        out.setDeliveryType(in.getDeliveryType());
        out.setPrice(in.getPrice());
        out.setCommElements(convertCommunicationElements(in.getCommElements(), s3Repository));
        out.setDeliveryPoints(convertDeliveryPoints(in.getDeliveryPoints()));
        out.setDefaultLanguage(in.getDefaultLanguage());
        out.setProductSessions(convertProductSessions(productId, in.getEvents(), channelSessions));
        out.setSessions(convertSessions(channelSessions));

        out.setStockType(in.getStockType());
        Map<Long, ProductStockStatus> productStockStatus = productsAvailability.get(productId);
        out.setVariants(convertVariants(in.getVariants(), productStockStatus));

        out.setAttribute1(convertAttribute(in.getAttribute1()));
        out.setAttribute2(convertAttribute(in.getAttribute2()));

        long onSaleVariants = 0L;
        if (MapUtils.isNotEmpty(productStockStatus)) {
            onSaleVariants = productStockStatus.values().stream().filter(va -> va.equals(ProductStockStatus.ON_SALE)).count();
        }
        out.setAvailability(onSaleVariants > 0 ? ProductStockStatus.ON_SALE : ProductStockStatus.SOLD_OUT);
        out.setPromotions(convertPromotions(in.getPromotions()));
        out.setState(in.getState());
        out.setUseExternalBarcodes(in.getUseExternalBarcodes());
        out.setHideDeliveryPoint(in.getHideDeliveryPoint());
        out.setHideDeliveryDateTime(in.getHideDeliveryDateTime());
        out.setTaxes(fillTaxes(in.getTaxes()));
        out.setSurchargesTaxes(fillTaxes(in.getSurchargesTaxes()));
        out.setTaxonomy(convertTaxonomies(in.getTaxonomyId(), in.getTaxonomyCode(), in.getTaxonomyDescription()));

        return out;
    }

    public static List<ProductCatalogSessionDTO> convertProductSessions(Long productId, List<EventSessionsDeliveryPoints> eventSessionsDeliveryPoints,
                                                                        List<ChannelSession> channelSessions) {
        Map<Long, ChannelSession> channelSessionsBySessionId = channelSessions.stream()
                .collect(Collectors.toMap(ChannelSession::getSessionId, java.util.function.Function.identity()));
        List<ProductCatalogSessionDTO> result = new ArrayList<>();
        for (EventSessionsDeliveryPoints eventSessionsDeliveryPoint : eventSessionsDeliveryPoints) {
            ProductCatalogSessionDTO productCatalogSessionDTO = new ProductCatalogSessionDTO();
            Set<Long> sessionIds = new HashSet<>();
            for (ChannelSession channelSession : channelSessions) {
                if (channelSession.getProductIds() != null && channelSession.getProductIds().contains(productId)) {
                    sessionIds.add(channelSession.getSessionId());
                }
            }
            productCatalogSessionDTO.setSessionIds(new ArrayList<>(sessionIds));

            if (productCatalogSessionDTO.getSessionDeliveryPoints() == null) {
                productCatalogSessionDTO.setSessionDeliveryPoints(new HashMap<>());
            }

            Set<Long> eventIds = sessionIds.stream()
                    .map(channelSessionsBySessionId::get)
                    .filter(java.util.Objects::nonNull)
                    .map(ChannelSession::getEventId)
                    .collect(Collectors.toSet());
            if (eventIds.contains(eventSessionsDeliveryPoint.getId())) {
                productCatalogSessionDTO.setEventDeliveryPoints(eventSessionsDeliveryPoint.getEventDeliveryPoints());
            } else {
                continue;
            }
            if (eventSessionsDeliveryPoint.getSessionsDeliveryPoints() != null) {
                for (Map.Entry<Long, Set<Long>> entry : eventSessionsDeliveryPoint.getSessionsDeliveryPoints().entrySet()) {
                    if (sessionIds.contains(entry.getKey())) {
                        productCatalogSessionDTO.getSessionDeliveryPoints().put(entry.getKey(), entry.getValue());
                    }
                }

            }

            if (eventSessionsDeliveryPoint.getSessionsDeliveryPoints() != null && Collections.disjoint(sessionIds, eventSessionsDeliveryPoint.getSessionsDeliveryPoints().keySet())) {
                productCatalogSessionDTO.setSessionDeliveryPoints(eventSessionsDeliveryPoint.getSessionsDeliveryPoints());
            }

            productCatalogSessionDTO.setSessionSelectionType(eventSessionsDeliveryPoint.getSessionSelectionType());
            result.add(productCatalogSessionDTO);
        }
        return result;
    }

    public static List<ChannelCatalogProductDTO> toChannelCatalogProducts(Long channelId, String s3Repository, Map<Long, Map<Long, ProductStockStatus>> productsAvailability, List<ProductCatalogDocument> productCatalogDocuments) {
        List<ChannelCatalogProductDTO> products = new ArrayList<>();
        if (CollectionUtils.isEmpty(productCatalogDocuments)) {
           return products;
        }
        productCatalogDocuments.stream()
                .map(document -> products.add(toChannelCatalogProduct(channelId, s3Repository, productsAvailability, document)))
                .collect(Collectors.toList());
        return products;
    }

    private static List<ProductSessionDTO> convertSessions(List<ChannelSession> channelSessions) {
        List<ProductSessionDTO> result = new ArrayList<>();
        for (ChannelSession channelSession : channelSessions) {
            ProductSessionDTO productSessionDTO = new ProductSessionDTO();
            productSessionDTO.setSessionId(channelSession.getSessionId());
            productSessionDTO.setStart(CatalogUtils.toZonedDateTime(channelSession.getDate().getStart()));
            productSessionDTO.setEnd(CatalogUtils.toZonedDateTime(channelSession.getDate().getEnd()));
            result.add(productSessionDTO);
        }
        return result;
    }

    private static List<ProductCatalogVariantDTO> convertVariants(List<ProductCatalogVariant> variants,
                                                                  Map<Long, ProductStockStatus> productsAvailability) {
        if (variants == null || variants.isEmpty()) {
            return null;
        }
        List<ProductCatalogVariantDTO> result = new ArrayList<>();
        for (ProductCatalogVariant productCatalogVariant : variants) {
            ProductCatalogVariantDTO productCatalogVariantDTO = new ProductCatalogVariantDTO();
            productCatalogVariantDTO.setId(productCatalogVariant.getId());
            productCatalogVariantDTO.setName(productCatalogVariant.getName());
            productCatalogVariantDTO.setPrice(productCatalogVariant.getPrice());
            productCatalogVariantDTO.setSessionPrice(productCatalogVariant.getSessionPrice());
            productCatalogVariantDTO.setSku(productCatalogVariant.getSku());
            productCatalogVariantDTO.setInitialStock(productCatalogVariant.getInitialStock());
            productCatalogVariantDTO.setAttribute1ValueId(productCatalogVariant.getValue1());
            productCatalogVariantDTO.setAttribute2ValueId(productCatalogVariant.getValue2());
            productCatalogVariantDTO.setAvailability(productsAvailability.get(productCatalogVariant.getId()));
            result.add(productCatalogVariantDTO);
        }
        return result;
    }

    private static ProductCatalogAttributeDTO convertAttribute(ProductCatalogAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        ProductCatalogAttributeDTO productCatalogAttributeDTO = new ProductCatalogAttributeDTO();
        productCatalogAttributeDTO.setId(attribute.getId());
        productCatalogAttributeDTO.setName(attribute.getName());
        productCatalogAttributeDTO.setPosition(attribute.getPosition());
        productCatalogAttributeDTO.setValues(convertAttributeValues(attribute.getValues()));
        productCatalogAttributeDTO.setContents(attribute.getContents());
        return productCatalogAttributeDTO;
    }

    private static List<ProductCatalogValueDTO> convertAttributeValues(List<ProductCatalogValue> values) {
        List<ProductCatalogValueDTO> result = new ArrayList<>();
        for (ProductCatalogValue productCatalogValue : values) {
            ProductCatalogValueDTO productCatalogValueDTO = new ProductCatalogValueDTO();
            productCatalogValueDTO.setId(productCatalogValue.getId());
            productCatalogValueDTO.setName(productCatalogValue.getName());
            productCatalogValueDTO.setPosition(productCatalogValue.getPosition());
            productCatalogValueDTO.setContents(convertContents(productCatalogValue.getContents()));
            result.add(productCatalogValueDTO);
        }
        return result;
    }

    private static Map<String, Map<String, String>> convertContents(Map<String, Map<String, String>> contents) {
        if (contents == null) {
            return null;
        }
        return new HashMap<>(contents);
    }

    public static ProductCatalogCommunicationElementsDTO convertCommunicationElements(ProductCatalogCommElement productCatalogCommElement, String s3Repository) {
        ProductCatalogCommunicationElementsDTO result = new ProductCatalogCommunicationElementsDTO();
        result.setTexts(new ArrayList<>());
        result.setImages(new ArrayList<>());
        if (productCatalogCommElement != null) {
            if (productCatalogCommElement.getTexts() != null) {
                for (ProductCatalogCommunicationElement productCatalogCommunicationElement : productCatalogCommElement.getTexts()) {
                    ProductCatalogCommunicationElementDTO productCatalogCommunicationElementDTO = new ProductCatalogCommunicationElementDTO();
                    productCatalogCommunicationElementDTO.setType(productCatalogCommunicationElement.getType());
                    productCatalogCommunicationElementDTO.setValue(productCatalogCommunicationElement.getValue());
                    result.getTexts().add(productCatalogCommunicationElementDTO);
                }
            }
            if (productCatalogCommElement.getImages() != null) {
                for (ProductCatalogCommunicationElement productCatalogCommunicationElement : productCatalogCommElement.getImages()) {
                    ProductCatalogCommunicationElementDTO productCatalogCommunicationElementDTO = new ProductCatalogCommunicationElementDTO();
                    productCatalogCommunicationElementDTO.setType(productCatalogCommunicationElement.getType());
                    Map<String, String> values = productCatalogCommunicationElement.getValue();

                    Map<String, String> languagesImages = values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,es -> s3Repository.concat(es.getValue())));
                    productCatalogCommunicationElementDTO.setValue(languagesImages);
                    productCatalogCommunicationElementDTO.setPosition(productCatalogCommunicationElement.getPosition());
                    productCatalogCommunicationElementDTO.setAltText(productCatalogCommunicationElement.getAltText());
                    result.getImages().add(productCatalogCommunicationElementDTO);
                }
            }
        }
        return result;
    }

    private static List<DeliveryPointDTO> convertDeliveryPoints(Set<DeliveryPoint> deliveryPoints) {
        List<DeliveryPointDTO> result = new ArrayList<>();
        if (deliveryPoints != null) {
            for (DeliveryPoint deliveryPoint : deliveryPoints) {
                DeliveryPointDTO productCatalogDeliveryPointDTO = new DeliveryPointDTO();
                productCatalogDeliveryPointDTO.setId(deliveryPoint.getId());
                productCatalogDeliveryPointDTO.setName(deliveryPoint.getName());
                productCatalogDeliveryPointDTO.setLocation(convertDeliveryPointsLocation(deliveryPoint.getLocation()));
                result.add(productCatalogDeliveryPointDTO);
            }
        }
        return result;
    }

    private static ProductCatalogDeliveryPointAddressDTO convertDeliveryPointsLocation(DeliveryPointAddressDTO deliveryPointAddressDTO) {
        ProductCatalogDeliveryPointAddressDTO productCatalogDeliveryPointAddressDTO = new ProductCatalogDeliveryPointAddressDTO();
        productCatalogDeliveryPointAddressDTO.setCountry(deliveryPointAddressDTO.getCountry());
        productCatalogDeliveryPointAddressDTO.setCountrySubdivision(deliveryPointAddressDTO.getCountrySubdivision());
        productCatalogDeliveryPointAddressDTO.setCity(deliveryPointAddressDTO.getCity());
        productCatalogDeliveryPointAddressDTO.setAddress(deliveryPointAddressDTO.getAddress());
        productCatalogDeliveryPointAddressDTO.setZipCode(deliveryPointAddressDTO.getZipCode());
        productCatalogDeliveryPointAddressDTO.setNotes(deliveryPointAddressDTO.getNotes());
        return productCatalogDeliveryPointAddressDTO;
    }

    private static List<ProductCatalogPromotionDTO> convertPromotions(List<ProductCatalogPromotion> promotions) {
        if (CollectionUtils.isNotEmpty(promotions)) {
            return promotions.stream().map(p -> {
                ProductCatalogPromotionDTO dto = new ProductCatalogPromotionDTO();
                dto.setId(p.getId());
                dto.setName(p.getName());
                dto.setType(p.getType());
                dto.setDiscountType(p.getDiscountType());
                dto.setDiscountValue(p.getDiscountValue());
                dto.setActivator(p.getActivator());
                dto.setActivatorId(p.getActivatorId());
                return dto;
            }).toList();
        }
        return null;
    }

    private static List<ProductTaxInfoDTO> fillTaxes(List<ProductTaxInfo> taxes) {
        if (CollectionUtils.isEmpty(taxes)) {
            return null;
        }
        return taxes.stream()
                .map(t -> {
                    ProductTaxInfoDTO tax = new ProductTaxInfoDTO();
                    tax.setId(t.getId());
                    tax.setName(t.getName());
                    tax.setValue(t.getValue());
                    tax.setDescription(t.getDescription());
                    return tax;
                })
                .collect(Collectors.toList());
    }

    private static ChannelCatalogProductDTO toChannelCatalogProduct(Long channelId, String s3Repository, Map<Long, Map<Long, ProductStockStatus>> productsAvailability,
                                                                    ProductCatalogDocument document) {
        ChannelCatalogProductDTO product = new ChannelCatalogProductDTO();

        ProductCatalogChannel channel = document.getChannels().stream().filter(c -> channelId.equals(c.getId()))
                .findFirst().orElseThrow(() -> new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND));

        product.setId(document.getId());
        product.setName(document.getName());
        product.setType(document.getType());
        product.setState(document.getState());
        product.setCheckoutSuggestionEnabled(channel.getCheckoutSuggestionEnabled());
        product.setStandaloneEnabled(channel.getStandaloneEnabled());
        product.setCurrencyId(document.getCurrencyId());
        product.setEntity(document.getEntity());
        product.setProducer(document.getProducer());
        product.setDefaultLanguage(document.getDefaultLanguage());
        product.setProductLanguages(document.getLanguages());
        product.setTaxonomy(convertTaxonomies(document.getTaxonomyId(), document.getTaxonomyCode(), document.getTaxonomyDescription()));
        product.setParentTaxonomy(convertTaxonomies(document.getTaxonomyParentId(), document.getTaxonomyParentCode(), document.getTaxonomyParentDescription()));
        product.setCustomTaxonomy(convertTaxonomies(document.getCustomTaxonomyId(), document.getCustomTaxonomyCode(), document.getCustomTaxonomyDescription()));
        product.setCustomParentTaxonomy(convertTaxonomies(document.getCustomParentTaxonomyId(), document.getCustomParentTaxonomyCode(), document.getCustomParentTaxonomyDescription()));
        product.setVariants(convertVariants(document.getVariants(), productsAvailability.get(document.getId())));
        product.setAttribute1(convertAttribute(document.getAttribute1()));
        product.setAttribute2(convertAttribute(document.getAttribute2()));
        product.setDeliveryType(ProductDeliveryType.valueOf(document.getDeliveryType().name()));
        product.setStartTimeUnit(document.getStartTimeUnit());
        product.setStartTimeValue(document.getStartTimeValue());
        product.setEndTimeUnit(document.getEndTimeUnit());
        product.setEndTimeValue(document.getEndTimeValue());
        product.setDeliveryPoints(convertDeliveryPoints(document.getDeliveryPoints()));
        product.setDeliveryDateFrom(document.getDeliveryDateFrom());
        product.setDeliveryDateTo(document.getDeliveryDateTo());
        product.setPrice(document.getPrice());
        product.setCommElements(convertCommunicationElements(document.getCommElements(), s3Repository));

        return product;

    }

    private static TaxonomyDTO convertTaxonomies(Integer id, String code, String description) {
        TaxonomyDTO taxonomyDTO = new TaxonomyDTO();
        taxonomyDTO.setId(id);
        taxonomyDTO.setCode(code);
        taxonomyDTO.setDescription(description);
        return taxonomyDTO;

    }
}
