package es.onebox.event.products.converter;

import es.onebox.event.products.dao.couch.ProductTicketContentImageDetail;
import es.onebox.event.products.dao.couch.ProductTicketContentTextDetail;
import es.onebox.event.products.dao.couch.ProductTicketContentValue;
import es.onebox.event.products.dao.couch.ProductTicketLiterals;
import es.onebox.event.products.dto.ProductTicketContentImageDTO;
import es.onebox.event.products.dto.ProductTicketContentListImageDTO;
import es.onebox.event.products.dto.ProductTicketContentListTextDTO;
import es.onebox.event.products.dto.ProductTicketContentTextDTO;
import es.onebox.event.products.dto.ProductTicketLiteralsDTO;

import java.util.Map;

public class ProductTicketContentsConverter {
    private ProductTicketContentsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductTicketContentListTextDTO toTextsDto(Map<String, ProductTicketContentValue> productTicketContentValue) {

        ProductTicketContentListTextDTO productTicketContentListTextDTO = new ProductTicketContentListTextDTO();

        for (Map.Entry<String, ProductTicketContentValue> element : productTicketContentValue.entrySet()) {

            String language = element.getKey();
            ProductTicketContentValue value = element.getValue();

            if (value.getTexts() != null) {
                for (ProductTicketContentTextDetail productTicketContentTextDetail : value.getTexts()) {
                    ProductTicketContentTextDTO productTicketContentTextDTO = new ProductTicketContentTextDTO();
                    productTicketContentTextDTO.setType(productTicketContentTextDetail.getType());
                    productTicketContentTextDTO.setLanguage(language);
                    productTicketContentTextDTO.setValue(productTicketContentTextDetail.getValue());

                    productTicketContentListTextDTO.add(productTicketContentTextDTO);
                }
            }
        }
        return productTicketContentListTextDTO;
    }

    public static ProductTicketContentListImageDTO toImagesDto(Map<String, ProductTicketContentValue> productTicketContentValue, String staticDataContainer) {

        ProductTicketContentListImageDTO productTicketContentListImageDTO = new ProductTicketContentListImageDTO();

        for (Map.Entry<String, ProductTicketContentValue> element : productTicketContentValue.entrySet()) {

            String language = element.getKey();
            ProductTicketContentValue value = element.getValue();

            if (value.getImages() != null) {
                for (ProductTicketContentImageDetail productTicketContentImageDetail : value.getImages()) {
                    ProductTicketContentImageDTO productTicketContentImageDTO = new ProductTicketContentImageDTO();
                    productTicketContentImageDTO.setType(productTicketContentImageDetail.getType());
                    productTicketContentImageDTO.setLanguage(language);
                    productTicketContentImageDTO.setImageUrl(staticDataContainer + productTicketContentImageDetail.getValue());

                    productTicketContentListImageDTO.add(productTicketContentImageDTO);
                }
            }
        }
        return productTicketContentListImageDTO;
    }
}
