package es.onebox.event.products.converter;

import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.dao.couch.ProductContentDocumentValue;
import es.onebox.event.products.dto.ProductLiteralDTO;
import es.onebox.event.products.dto.ProductLiteralsDTO;
import es.onebox.event.products.dto.ProductValueLiteralDTO;
import es.onebox.event.products.dto.ProductValueLiteralsDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

public class ProductContentsConverter {

    private ProductContentsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductLiteralsDTO toAttributesDto(ProductContentDocument productContentDocument, String languageCode) {
        ProductLiteralsDTO result = new ProductLiteralsDTO();
        if (productContentDocument.getLanguageElements() == null || productContentDocument.getLanguageElements().isEmpty()) {
            return new ProductLiteralsDTO();
        }
        if (languageCode != null) {
            if (!productContentDocument.getLanguageElements().containsKey(languageCode)) {
                return null;
            }
            ProductCommunicationElement productCommunicationElement = productContentDocument.getLanguageElements().get(languageCode);
            if (CollectionUtils.isNotEmpty(productCommunicationElement.getTexts())) {
                for (ProductCommunicationElementDetail productCommunicationElementDetail : productCommunicationElement.getTexts()) {
                    ProductLiteralDTO productLiteralDTO = new ProductLiteralDTO();
                    productLiteralDTO.setKey(productCommunicationElementDetail.getType());
                    productLiteralDTO.setValue(productCommunicationElementDetail.getValue());
                    productLiteralDTO.setLanguageCode(languageCode);
                    result.add(productLiteralDTO);
                }
            }
        } else {
            for (Map.Entry<String, ProductCommunicationElement> entry : productContentDocument.getLanguageElements().entrySet()) {
                if (CollectionUtils.isNotEmpty(entry.getValue().getTexts())) {
                    for (ProductCommunicationElementDetail productCommunicationElementDetail : entry.getValue().getTexts()) {
                        ProductLiteralDTO productLiteralDTO = new ProductLiteralDTO();
                        productLiteralDTO.setKey(productCommunicationElementDetail.getType());
                        productLiteralDTO.setValue(productCommunicationElementDetail.getValue());
                        productLiteralDTO.setLanguageCode(entry.getKey());
                        result.add(productLiteralDTO);
                    }
                }
            }
        }
        return result;
    }

    public static ProductLiteralsDTO toValuesDto(ProductContentDocument productContentDocument, String languageCode, Long valueId) {
        ProductLiteralsDTO result = new ProductLiteralsDTO();
        if (productContentDocument.getValues() == null || productContentDocument.getValues().isEmpty()) {
            return new ProductLiteralsDTO();
        }
        ProductContentDocumentValue productContentDocumentValue = productContentDocument.getValues().get(valueId.toString());
        if (languageCode != null) {
            if (!productContentDocumentValue.getLanguageElements().containsKey(languageCode)) {
                return null;
            }
            ProductCommunicationElement productCommunicationElement = productContentDocumentValue.getLanguageElements().get(languageCode);
            if (productCommunicationElement != null && CollectionUtils.isNotEmpty(productCommunicationElement.getTexts())) {
                for (ProductCommunicationElementDetail productCommunicationElementDetail : productCommunicationElement.getTexts()) {
                    ProductLiteralDTO productLiteralDTO = new ProductLiteralDTO();
                    productLiteralDTO.setKey(productCommunicationElementDetail.getType());
                    productLiteralDTO.setValue(productCommunicationElementDetail.getValue());
                    productLiteralDTO.setLanguageCode(languageCode);
                    result.add(productLiteralDTO);
                }
            }
        } else {
            for (Map.Entry<String, ProductContentDocumentValue> entry : productContentDocument.getValues().entrySet()) {
                for (Map.Entry<String, ProductCommunicationElement> valueEntry : entry.getValue().getLanguageElements().entrySet()) {
                    if (valueEntry.getValue() != null && CollectionUtils.isNotEmpty(valueEntry.getValue().getTexts())) {
                        for (ProductCommunicationElementDetail productCommunicationElementDetail : valueEntry.getValue().getTexts()) {
                            ProductLiteralDTO productLiteralDTO = new ProductLiteralDTO();
                            productLiteralDTO.setKey(productCommunicationElementDetail.getType());
                            productLiteralDTO.setValue(productCommunicationElementDetail.getValue());
                            productLiteralDTO.setLanguageCode(valueEntry.getKey());
                            result.add(productLiteralDTO);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static ProductValueLiteralsDTO toValuesDto(ProductContentDocument productContentDocument, String languageCode) {
        ProductValueLiteralsDTO productValueLiteralsDTO = new ProductValueLiteralsDTO();
        for (Map.Entry<String, ProductContentDocumentValue> entry : productContentDocument.getValues().entrySet()) {
            for (Map.Entry<String, ProductCommunicationElement> commElementEntry : entry.getValue().getLanguageElements().entrySet()) {
                if (commElementEntry.getValue() != null && CollectionUtils.isNotEmpty(commElementEntry.getValue().getTexts())) {
                    for (ProductCommunicationElementDetail productCommunicationElementDetail : commElementEntry.getValue().getTexts()) {
                        if (languageCode == null || commElementEntry.getKey().equals(languageCode)) {
                            ProductValueLiteralDTO productValueLiteralDTO = new ProductValueLiteralDTO();
                            productValueLiteralDTO.setValueId(Long.valueOf(entry.getKey()));
                            productValueLiteralDTO.setLanguageCode(commElementEntry.getKey());
                            productValueLiteralDTO.setKey(productCommunicationElementDetail.getType());
                            productValueLiteralDTO.setValue(productCommunicationElementDetail.getValue());
                            productValueLiteralsDTO.add(productValueLiteralDTO);
                        }
                    }
                }
            }
        }
        return productValueLiteralsDTO;
    }

}
