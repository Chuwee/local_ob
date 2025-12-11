package es.onebox.event.products.converter;

import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.dto.ProductCommunicationElementImageDTO;
import es.onebox.event.products.dto.ProductCommunicationElementTextDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.event.products.enums.ProductCommunicationElementTextsType;
import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;

import java.util.Map;
import java.util.Optional;

public class ProductCommunicationElementsConverter {

    private ProductCommunicationElementsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductCommunicationElementsTextsDTO toTextsDto(ProductCommunicationElementDocument productCommunicationElementDocument) {
        ProductCommunicationElementsTextsDTO productCommunicationElementsTextsDTO = new ProductCommunicationElementsTextsDTO();

        for(Map.Entry<String, ProductCommunicationElement> element : productCommunicationElementDocument.getLanguageElements().entrySet()) {
            if(element.getValue() != null && element.getValue().getTexts() != null) {
                for(ProductCommunicationElementDetail textElement : element.getValue().getTexts()) {
                    ProductCommunicationElementTextDTO productCommunicationElementTextDTO = new ProductCommunicationElementTextDTO();
                    productCommunicationElementTextDTO.setLanguage(element.getKey());
                    productCommunicationElementTextDTO.setType(ProductCommunicationElementTextsType.valueOf(textElement.getType()));
                    productCommunicationElementTextDTO.setValue(textElement.getValue());
                    productCommunicationElementsTextsDTO.add(productCommunicationElementTextDTO);
                }
            }
        }

        return productCommunicationElementsTextsDTO;
    }

    public static ProductCommunicationElementsImagesDTO toImagesDto(ProductCommunicationElementDocument document, String srRepositoryPath) {
        ProductCommunicationElementsImagesDTO productCommunicationElementsImagesDTO = new ProductCommunicationElementsImagesDTO();
        for(Map.Entry<String, ProductCommunicationElement> mapElement : document.getLanguageElements().entrySet()) {
            if(mapElement.getValue() != null && mapElement.getValue().getImages() != null) {
                for(ProductCommunicationElementDetail productCommunicationElementDetail : mapElement.getValue().getImages()) {
                    ProductCommunicationElementImageDTO productCommunicationElementImageDTO = new ProductCommunicationElementImageDTO();
                    productCommunicationElementImageDTO.setLanguage(mapElement.getKey());
                    productCommunicationElementImageDTO.setType(ProductCommunicationElementsImagesType.valueOf(productCommunicationElementDetail.getType()));
                    productCommunicationElementImageDTO.setPosition(productCommunicationElementDetail.getPosition());
                    productCommunicationElementImageDTO.setAltText(productCommunicationElementDetail.getAltText());
                    productCommunicationElementImageDTO.setValue(srRepositoryPath + productCommunicationElementDetail.getValue());
                    productCommunicationElementImageDTO.setTag(productCommunicationElementDetail.getType());
                    productCommunicationElementImageDTO.setImageBinary(Optional.empty());
                    productCommunicationElementsImagesDTO.add(productCommunicationElementImageDTO);
                }
            }
        }
        return productCommunicationElementsImagesDTO;
    }

}
