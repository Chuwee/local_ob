package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.event.dto.ProductChannelDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementImageDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementTextDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductDTO;
import es.onebox.common.datasources.ms.event.dto.ProductEvent;
import es.onebox.common.datasources.ms.event.dto.ProductEvents;
import es.onebox.common.datasources.ms.event.dto.ProductLanguage;
import es.onebox.common.datasources.ms.event.dto.ProductLanguages;
import es.onebox.common.datasources.ms.event.dto.ProductPublishingSessions;
import es.onebox.common.datasources.ms.event.dto.ProductSessionBase;
import es.onebox.common.datasources.ms.event.dto.ProductSurchargeDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.ProductVariants;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductChannelFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementImageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementTextFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductEventFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductLanguageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductPublishingSessionsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductSessionFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductSurchargeFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductVariantFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductVariantsFeverDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductConverter {

    public static ProductDetailDTO toProductDetailDTO(ProductDTO product) {
        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        productDetailDTO.setName(product.getName());
        productDetailDTO.setProductType(product.getProductType());
        productDetailDTO.setStockType(product.getStockType());
        productDetailDTO.setProductState(product.getProductState());
        productDetailDTO.setCurrencyId(product.getCurrencyId());
        productDetailDTO.setCategory(product.getCategory());
        productDetailDTO.setCustomCategory(product.getCustomCategory());
        productDetailDTO.setTax(product.getTax());
        productDetailDTO.setSurchargeTax(product.getSurchargeTax());

        return productDetailDTO;
    }

    public static List<ProductChannelFeverDTO> toProductChannelDTO(List<ProductChannelDTO> productChannels) {
        if (productChannels == null) {
            return null;
        }

        List<ProductChannelFeverDTO> channels = new ArrayList<>();
        productChannels.forEach(productChannel -> {
            channels.add(toProductChannelDTO(productChannel));
        });
        return channels;
    }

    public static ProductChannelFeverDTO toProductChannelDTO(ProductChannelDTO productChannel) {
        if (productChannel == null) {
            return null;
        }

        ProductChannelFeverDTO newProductChannel = new ProductChannelFeverDTO();
        newProductChannel.setProductId(productChannel.getProduct().getId());
        newProductChannel.setChannelId(productChannel.getChannel().getId());
        newProductChannel.setSaleRequestsStatus(productChannel.getSaleRequestsStatus());
        newProductChannel.setCheckoutSuggestionEnabled(productChannel.getCheckoutSuggestionEnabled());
        newProductChannel.setStandaloneEnabled(productChannel.getStandaloneEnabled());

        return newProductChannel;
    }

    public static List<ProductSurchargeFeverDTO> toProductSurchargeDTO(List<ProductSurchargeDTO> productSurcharges) {
        if (productSurcharges == null) {
            return null;
        }

        List<ProductSurchargeFeverDTO> surcharges = new ArrayList<>();
        productSurcharges.forEach(productSurcharge -> {
            surcharges.add(toProductSurchargeDTO(productSurcharge));
        });
        return surcharges;
    }

    private static ProductSurchargeFeverDTO toProductSurchargeDTO(ProductSurchargeDTO productSurcharge) {
        ProductSurchargeFeverDTO newProductSurcharge = new ProductSurchargeFeverDTO();
        newProductSurcharge.setType(productSurcharge.getType());
        newProductSurcharge.setRanges(productSurcharge.getRanges());

        return newProductSurcharge;
    }

    public static ProductVariantsFeverDTO toProductVariantsDTO(ProductVariants productVariants) {
        if (productVariants == null) {
            return null;
        }

        List<ProductVariantFeverDTO> variants = new ArrayList<>();
        if (productVariants.getData() != null) {
            productVariants.getData().forEach(variant -> {
                variants.add(toProductVariantDTO(variant));
            });
        }

        return new ProductVariantsFeverDTO(variants, productVariants.getMetadata());
    }

    private static ProductVariantFeverDTO toProductVariantDTO(ProductVariant productVariant) {
        ProductVariantFeverDTO newProductVariant = new ProductVariantFeverDTO();
        newProductVariant.setId(productVariant.getId());
        newProductVariant.setProduct(productVariant.getProduct());
        newProductVariant.setName(productVariant.getName());
        newProductVariant.setSku(productVariant.getSku());
        newProductVariant.setPrice(productVariant.getPrice());
        newProductVariant.setVariantOption1(productVariant.getVariantOption1());
        newProductVariant.setVariantOption2(productVariant.getVariantOption2());
        newProductVariant.setVariantValue1(productVariant.getVariantValue1());
        newProductVariant.setVariantValue2(productVariant.getVariantValue2());
        newProductVariant.setStock(productVariant.getStock());
        newProductVariant.setProductVariantStatus(productVariant.getProductVariantStatus());
        newProductVariant.setCreateDate(productVariant.getCreateDate());
        newProductVariant.setUpdateDate(productVariant.getUpdateDate());

        return newProductVariant;
    }

    public static List<ProductEventFeverDTO> toProductEventsDTO(ProductEvents productEvents) {
        if (productEvents == null) {
            return null;
        }

        List<ProductEventFeverDTO> events = new ArrayList<>();
        productEvents.forEach(productEvent -> {
            events.add(toProductEventDTO(productEvent));
        });

        return events;
    }

    private static ProductEventFeverDTO toProductEventDTO(ProductEvent productEvent) {
        ProductEventFeverDTO newProductEvent = new ProductEventFeverDTO();
        newProductEvent.setProduct(productEvent.getProduct());
        newProductEvent.setEvent(productEvent.getEvent());
        newProductEvent.setStatus(productEvent.getStatus());
        newProductEvent.setSessionsSelectionType(productEvent.getSessionsSelectionType());

        return newProductEvent;
    }

    public static ProductPublishingSessionsFeverDTO toProductPublishingSessionsDTO(ProductPublishingSessions productSessions) {
        if (productSessions == null) {
            return null;
        }

        Set<ProductSessionFeverDTO> sessions = new HashSet<>();
        if (productSessions.getSessions() != null) {
            productSessions.getSessions().forEach(session -> {
                sessions.add(toProductSessionDTO(session));
            });
        }

        return new ProductPublishingSessionsFeverDTO(productSessions.getType(), sessions);
    }

    private static ProductSessionFeverDTO toProductSessionDTO(ProductSessionBase productSession) {
        ProductSessionFeverDTO newProductSession = new ProductSessionFeverDTO();
        newProductSession.setId(productSession.getId());
        newProductSession.setName(productSession.getName());
        newProductSession.setDates(productSession.getDates());

        return newProductSession;
    }

    public static List<ProductLanguageFeverDTO> toProductLanguagesDTO(ProductLanguages productLanguages) {
        if (productLanguages == null) {
            return null;
        }

        List<ProductLanguageFeverDTO> languages = new ArrayList<>();
        productLanguages.forEach(productLanguage -> {
            languages.add(toProductLanguageDTO(productLanguage));
        });

        return languages;
    }

    private static ProductLanguageFeverDTO toProductLanguageDTO(ProductLanguage productLanguage) {
        ProductLanguageFeverDTO newProductLanguage = new ProductLanguageFeverDTO();
        newProductLanguage.setProductId(productLanguage.getProductId());
        newProductLanguage.setCode(productLanguage.getCode());
        newProductLanguage.setLanguageId(productLanguage.getLanguageId());
        newProductLanguage.setIsDefault(productLanguage.getIsDefault());

        return newProductLanguage;
    }

    public static List<ProductCommunicationElementTextFeverDTO> toProductCommunicationElementTextsDTO(ProductCommunicationElementsTextsDTO productTexts) {
        if (productTexts == null) {
            return null;
        }

        List<ProductCommunicationElementTextFeverDTO> texts = new ArrayList<>();
        productTexts.forEach(productText -> {
            texts.add(toProductCommunicationElementTextDTO(productText));
        });

        return texts;
    }

    private static ProductCommunicationElementTextFeverDTO toProductCommunicationElementTextDTO(ProductCommunicationElementTextDTO productText) {
        ProductCommunicationElementTextFeverDTO newProductText = new ProductCommunicationElementTextFeverDTO();
        newProductText.setType(productText.getType());
        newProductText.setLanguageId(productText.getLanguageId());
        newProductText.setLanguage(productText.getLanguage());
        newProductText.setValue(productText.getValue());

        return newProductText;
    }

    public static List<ProductCommunicationElementImageFeverDTO> toProductCommunicationElementImagesDTO(ProductCommunicationElementsImagesDTO productImages) {
        if (productImages == null) {
            return null;
        }

        List<ProductCommunicationElementImageFeverDTO> images = new ArrayList<>();
        productImages.forEach(productImage -> {
            images.add(toProductCommunicationElementImageDTO(productImage));
        });

        return images;
    }

    private static ProductCommunicationElementImageFeverDTO toProductCommunicationElementImageDTO(ProductCommunicationElementImageDTO productImage) {
        ProductCommunicationElementImageFeverDTO newProductImage = new ProductCommunicationElementImageFeverDTO();
        newProductImage.setTagId(productImage.getTagId());
        newProductImage.setPosition(productImage.getPosition());
        newProductImage.setId(productImage.getId());
        newProductImage.setTag(productImage.getTag());
        newProductImage.setLanguage(productImage.getLanguage());
        newProductImage.setType(productImage.getType());
        newProductImage.setValue(productImage.getValue());
        newProductImage.setImageBinary(productImage.getImageBinary());
        newProductImage.setAltText(productImage.getAltText());

        return newProductImage;
    }

}
