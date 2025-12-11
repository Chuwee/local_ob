package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEventDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEventDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEventDeliveryPoints;
import es.onebox.mgmt.products.dto.ProductEventDeliveryPointDTO;
import es.onebox.mgmt.products.dto.ProductEventDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDeliveryPointDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDeliveryPointsDTO;

public class ProductEventDeliveryPointConverter {

    private ProductEventDeliveryPointConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static UpdateProductEventDeliveryPoints convert(UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO) {
        UpdateProductEventDeliveryPoints updateProductEventDeliveryPoints = new UpdateProductEventDeliveryPoints();
        for (UpdateProductEventDeliveryPointDTO updateProductEventDeliveryPointDTO : updateProductEventDeliveryPointsDTO) {
            UpdateProductEventDeliveryPoint updateProductEventDeliveryPoint = new UpdateProductEventDeliveryPoint();
            updateProductEventDeliveryPoint.setDeliveryPointId(updateProductEventDeliveryPointDTO.getDeliveryPointId());
            updateProductEventDeliveryPoint.setIsDefault(updateProductEventDeliveryPointDTO.getIsDefault());
            updateProductEventDeliveryPoints.add(updateProductEventDeliveryPoint);
        }
        return updateProductEventDeliveryPoints;
    }

    public static ProductEventDeliveryPointsDTO toDto(ProductEventDeliveryPoints productEventDeliveryPoints) {
        if(productEventDeliveryPoints == null || productEventDeliveryPoints.isEmpty()) {
            return new ProductEventDeliveryPointsDTO();
        }
        ProductEventDeliveryPointsDTO productEventDeliveryPointDTOS = new ProductEventDeliveryPointsDTO();
        for (ProductEventDeliveryPoint productEventDeliveryPoint : productEventDeliveryPoints) {
            ProductEventDeliveryPointDTO productEventDeliveryPointDTO = new ProductEventDeliveryPointDTO();
            productEventDeliveryPointDTO.setProduct(productEventDeliveryPoint.getProduct());
            productEventDeliveryPointDTO.setEvent(productEventDeliveryPoint.getEvent());
            productEventDeliveryPointDTO.setDeliveryPoint(productEventDeliveryPoint.getDeliveryPoint());
            productEventDeliveryPointDTO.setIsDefault(productEventDeliveryPoint.getIsDefault());
            productEventDeliveryPointDTOS.add(productEventDeliveryPointDTO);
        }
        return productEventDeliveryPointDTOS;
    }
}
