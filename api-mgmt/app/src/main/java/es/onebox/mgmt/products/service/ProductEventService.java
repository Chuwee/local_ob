package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.ProductEventsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.AddProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEvent;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductConverter;
import es.onebox.mgmt.products.converter.ProductEventConverter;
import es.onebox.mgmt.products.dto.AddProductEventsDTO;
import es.onebox.mgmt.products.dto.ProductEventsDTO;
import es.onebox.mgmt.products.dto.ProductEventsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDTO;
import es.onebox.mgmt.products.enums.ProductEventStatus;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductEventService {
    private final ValidationService validationService;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductEventService(ValidationService validationService,
                               ProductsRepository productsRepository) {
        this.validationService = validationService;
        this.productsRepository = productsRepository;
    }

    public ProductEventsDTO addProductEvents(Long productId, AddProductEventsDTO addProductEventsDTO) {
        validationService.getAndCheckProduct(productId);

        AddProductEvents addProductEvents = ProductEventConverter.convert(addProductEventsDTO);

        ProductEvents productEvents = productsRepository.addProductEvents(productId, addProductEvents);

        return ProductEventConverter.toDto(productEvents);
    }

    public ProductEventsDTO getProductEvents(Long productId, ProductEventsFilterDTO filterDTO) {
        validationService.getAndCheckProduct(productId);

        ProductEventsFilter filter = ProductConverter.convertFilter(filterDTO);
        ProductEvents productEvents = productsRepository.getProductEvents(productId, filter);

        return ProductEventConverter.toDto(productEvents);
    }

    public void updateProductEvent(Long productId, Long eventId, UpdateProductEventDTO updateProductEventDTO) {
        validationService.getAndCheckProduct(productId);
        validationService.getAndCheckEvent(eventId);

        if (updateProductEventDTO.getStatus().equals(ProductEventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_EVENT_STATE_NOT_VALID);
        }

        UpdateProductEvent updateProductEvent = ProductEventConverter.convert(updateProductEventDTO);
        productsRepository.updateProductEvents(productId, eventId, updateProductEvent);
    }

    public void deleteProductEvent(Long productId, Long eventId) {
        validationService.getAndCheckProduct(productId);
        validationService.getAndCheckEvent(eventId);

        productsRepository.deleteProductEvent(productId, eventId);
    }
}
