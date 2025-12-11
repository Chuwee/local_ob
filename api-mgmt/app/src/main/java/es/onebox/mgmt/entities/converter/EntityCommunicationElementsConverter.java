package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementText;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsText;
import es.onebox.mgmt.products.dto.CreateProductCommunicationElementImageDTO;
import es.onebox.mgmt.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.CreateProductContentTextListDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementImageDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementTextDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.mgmt.products.dto.ProductContentTextDTO;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsTextsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityCommunicationElementsConverter {

    private EntityCommunicationElementsConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static CreateProductCommunicationElementsText convertTexts(CreateProductContentTextListDTO createProductContentTextListDTO, Map<String, Long> languagesMap) {
        CreateProductCommunicationElementsText createProductCommunicationElementsText = new CreateProductCommunicationElementsText();
        for(ProductContentTextDTO<ProductCommunicationElementsTextsType> productContentTextDTO : createProductContentTextListDTO.getTexts()) {
            CreateProductCommunicationElementText createProductCommunicationElementText = new CreateProductCommunicationElementText();
            createProductCommunicationElementText.setType(productContentTextDTO.getType().name());
            createProductCommunicationElementText.setLanguage(ConverterUtils.toLocale(productContentTextDTO.getLanguage()));
            createProductCommunicationElementText.setLanguageId(languagesMap.get(productContentTextDTO.getLanguage()));
            createProductCommunicationElementText.setValue(productContentTextDTO.getValue());
            createProductCommunicationElementsText.add(createProductCommunicationElementText);
        }
        return createProductCommunicationElementsText;
    }

    public static ProductCommunicationElementsTextsDTO toDtoTexts(ProductCommunicationElementsText productCommunicationElementsText) {
        if(productCommunicationElementsText == null) {
            return null;
        }
        ProductCommunicationElementsTextsDTO productCommunicationElementsTextsDTO = new ProductCommunicationElementsTextsDTO();
        for(ProductCommunicationElementText productCommunicationElementText : productCommunicationElementsText) {
            ProductCommunicationElementTextDTO productCommunicationElementTextDTO = new ProductCommunicationElementTextDTO();
            productCommunicationElementTextDTO.setType(productCommunicationElementText.getType());
            productCommunicationElementTextDTO.setLanguageId(productCommunicationElementText.getLanguageId());
            productCommunicationElementTextDTO.setLanguage(ConverterUtils.toLanguageTag(productCommunicationElementText.getLanguage()));
            productCommunicationElementTextDTO.setValue(productCommunicationElementText.getValue());
            productCommunicationElementsTextsDTO.add(productCommunicationElementTextDTO);
        }
        return productCommunicationElementsTextsDTO;
    }

    public static List<CreateProductCommunicationElementImage> convertImages(CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO, Map<String, Long> languagesMap) {
        List<CreateProductCommunicationElementImage> createProductCommunicationElementImages = new ArrayList<>();
        for(CreateProductCommunicationElementImageDTO<ProductCommunicationElementsImagesType> createProductCommunicationElementImageDTO : createProductCommunicationElementsImagesDTO) {
            CreateProductCommunicationElementImage createProductCommunicationElementImage = new CreateProductCommunicationElementImage();
            createProductCommunicationElementImage.setLanguage(ConverterUtils.toLocale(createProductCommunicationElementImageDTO.getLanguage()));
            createProductCommunicationElementImage.setLanguageId(languagesMap.get(createProductCommunicationElementImageDTO.getLanguage()));
            createProductCommunicationElementImage.setImageBinary(Optional.of(createProductCommunicationElementImageDTO.getImageBinary()));
            createProductCommunicationElementImage.setValue(createProductCommunicationElementImageDTO.getImageUrl());
            createProductCommunicationElementImage.setTagId(createProductCommunicationElementImageDTO.getType().getTagId());
            createProductCommunicationElementImage.setPosition(createProductCommunicationElementImageDTO.getPosition());
            createProductCommunicationElementImage.setType(createProductCommunicationElementImageDTO.getType());
            createProductCommunicationElementImage.setAltText(createProductCommunicationElementImageDTO.getAltText());
            createProductCommunicationElementImages.add(createProductCommunicationElementImage);
        }
        return createProductCommunicationElementImages;
    }

    public static ProductCommunicationElementsImagesDTO<ProductCommunicationElementsImagesType> toDtoImages(ProductCommunicationElementsImage productCommunicationElementsImage) {
        if(productCommunicationElementsImage == null) {
            return null;
        }
        ProductCommunicationElementsImagesDTO<ProductCommunicationElementsImagesType> productCommunicationElementImageDTOS = new ProductCommunicationElementsImagesDTO<>();
        for(ProductCommunicationElementImage productCommunicationElementImage : productCommunicationElementsImage) {
            ProductCommunicationElementImageDTO<ProductCommunicationElementsImagesType> productCommunicationElementImageDTO = new ProductCommunicationElementImageDTO<>();
            productCommunicationElementImageDTO.setPosition(productCommunicationElementImage.getPosition());
            productCommunicationElementImageDTO.setImageBinary(productCommunicationElementImage.getImageBinary().isPresent() ? productCommunicationElementImage.getImageBinary().get() : null);
            productCommunicationElementImageDTO.setImageUrl(productCommunicationElementImage.getValue());
            productCommunicationElementImageDTO.setLanguage(ConverterUtils.toLanguageTag(productCommunicationElementImage.getLanguage()));
            productCommunicationElementImageDTO.setType(productCommunicationElementImage.getType());
            productCommunicationElementImageDTO.setAltText(productCommunicationElementImage.getAltText());
            productCommunicationElementImageDTOS.add(productCommunicationElementImageDTO);
        }
        return productCommunicationElementImageDTOS;
    }
}
