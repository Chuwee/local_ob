package es.onebox.mgmt.products.service;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.products.converter.ProductEventDeliveryPointConverter;
import es.onebox.mgmt.products.dto.ProductEventDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.UpdateProductEventDeliveryPointsDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductEventDeliveryPointsService {
    private final ProductsRepository productsRepository;
    private final ValidationService validationService;

    @Autowired
    public ProductEventDeliveryPointsService(ProductsRepository productsRepository,
                                              ValidationService validationService) {
        this.productsRepository = productsRepository;
        this.validationService = validationService;
    }

    public ProductEventDeliveryPointsDTO getProductEventDeliveryPoints(Long productId, Long eventId) {
        validationService.getAndCheckProduct(productId);
        ProductEventDeliveryPoints productEventDeliveryPoints = productsRepository.getProductEventDeliveryPoints(productId, eventId);

        return ProductEventDeliveryPointConverter.toDto(productEventDeliveryPoints);
    }

    public ProductEventDeliveryPointsDTO updateProductEventDelvieryPoints(Long productId, Long eventId, UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO) {
        validationService.getAndCheckProduct(productId);

        UpdateProductEventDeliveryPoints updateProductLanguagesDTO = ProductEventDeliveryPointConverter.convert(updateProductEventDeliveryPointsDTO);
        ProductEventDeliveryPoints productEventDeliveryPoints = productsRepository.updateProductEventDeliveryPoints(productId, eventId, updateProductLanguagesDTO);
        return ProductEventDeliveryPointConverter.toDto(productEventDeliveryPoints);
    }

}
