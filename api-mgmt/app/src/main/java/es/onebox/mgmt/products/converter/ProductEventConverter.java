package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.AddProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEvent;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEvent;
import es.onebox.mgmt.products.dto.AddProductEventsDTO;
import es.onebox.mgmt.products.dto.ProductEventDTO;
import es.onebox.mgmt.products.dto.ProductEventDataDTO;
import es.onebox.mgmt.products.dto.ProductEventsDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDTO;

public class ProductEventConverter {

    private ProductEventConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductEventsDTO toDto(ProductEvents productEvents) {
        if (productEvents == null || productEvents.isEmpty()) {
            return new ProductEventsDTO();
        }
        ProductEventsDTO productEventsDTO = new ProductEventsDTO();
        for (ProductEvent productEvent : productEvents) {
            ProductEventDTO productEventDTO = new ProductEventDTO();
            ProductEventDataDTO eventDataDTO = new ProductEventDataDTO();
            eventDataDTO.setStartDate(productEvent.getEvent().getStartDate());
            eventDataDTO.setId(productEvent.getEvent().getId());
            eventDataDTO.setName(productEvent.getEvent().getName());

            productEventDTO.setProduct(productEvent.getProduct());
            productEventDTO.setStatus(productEvent.getStatus());
            productEventDTO.setEvent(eventDataDTO);
            productEventDTO.setSessionsSelectionType(productEvent.getSessionsSelectionType());
            productEventsDTO.add(productEventDTO);
        }
        return productEventsDTO;
    }

    public static AddProductEvents convert(AddProductEventsDTO addProductEventsDTO) {
        AddProductEvents addProductEvents = new AddProductEvents();

        addProductEvents.setEventIds(addProductEventsDTO.getEventIds());

        return addProductEvents;
    }

    public static UpdateProductEvent convert(UpdateProductEventDTO updateProductEventDTO) {
        UpdateProductEvent updateProductEvent = new UpdateProductEvent();

        updateProductEvent.setStatus(updateProductEventDTO.getStatus());

        return updateProductEvent;
    }
}
