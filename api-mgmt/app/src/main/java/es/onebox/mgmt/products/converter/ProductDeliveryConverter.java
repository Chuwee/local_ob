package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDelivery;
import es.onebox.mgmt.products.dto.ProductDeliveryDTO;
import es.onebox.mgmt.products.dto.UpdateProductDeliveryDTO;

public class ProductDeliveryConverter {

    private ProductDeliveryConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductDelivery convert(UpdateProductDeliveryDTO updateProductDeliveryDTO) {
        ProductDelivery productDelivery = new ProductDelivery();
        productDelivery.setDeliveryType(updateProductDeliveryDTO.getDeliveryType());
        productDelivery.setStartTimeUnit(updateProductDeliveryDTO.getStartTimeUnit());
        productDelivery.setStartTimeValue(updateProductDeliveryDTO.getStartTimeValue());
        productDelivery.setEndTimeUnit(updateProductDeliveryDTO.getEndTimeUnit());
        productDelivery.setEndTimeValue(updateProductDeliveryDTO.getEndTimeValue());
        productDelivery.setDeliveryDateFrom(updateProductDeliveryDTO.getDeliveryDateFrom());
        productDelivery.setDeliveryDateTo(updateProductDeliveryDTO.getDeliveryDateTo());

        return productDelivery;
    }

    public static ProductDeliveryDTO toDto(ProductDelivery productDelivery) {
        if(productDelivery == null) {
            return new ProductDeliveryDTO();
        }
        ProductDeliveryDTO productDeliveryDTO = new ProductDeliveryDTO();
        productDeliveryDTO.setDeliveryType(productDelivery.getDeliveryType());
        productDeliveryDTO.setStartTimeUnit(productDelivery.getStartTimeUnit());
        productDeliveryDTO.setStartTimeValue(productDelivery.getStartTimeValue());
        productDeliveryDTO.setEndTimeUnit(productDelivery.getEndTimeUnit());
        productDeliveryDTO.setEndTimeValue(productDelivery.getEndTimeValue());
        productDeliveryDTO.setDeliveryDateFrom(productDelivery.getDeliveryDateFrom());
        productDeliveryDTO.setDeliveryDateTo(productDelivery.getDeliveryDateTo());
        return productDeliveryDTO;
    }
}
