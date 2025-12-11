package es.onebox.mgmt.products.service;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.products.converter.ProductSessionDeliveryPointConverter;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSessionDeliveryPointsService {
    private final ProductsRepository productsRepository;
    private final ValidationService validationService;

    @Autowired
    public ProductSessionDeliveryPointsService(ProductsRepository productsRepository,
                                               ValidationService validationService) {
        this.productsRepository = productsRepository;
        this.validationService = validationService;
    }

    public ProductSessionDeliveryPointsDTO getProductSessionDeliveryPoints(Long productId, Long eventId, ProductSessionDeliveryPointsFilterDTO filterDTO) {
        validationService.getAndCheckProduct(productId);
        ProductSessionDeliveryPoints productSessionDeliveryPoints = productsRepository.getProductSessionDeliveryPoints(productId, eventId, filterDTO);

        return ProductSessionDeliveryPointConverter.toDto(productSessionDeliveryPoints);
    }

    public ProductSessionDeliveryPointsDTO updateProductSessionDeliveryPoints(Long productId, Long eventId, UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointsDTO) {
        validationService.getAndCheckProduct(productId);

        UpdateProductSessionDeliveryPoints updateProductSessionDeliveryPoints = ProductSessionDeliveryPointConverter.convert(updateProductSessionDeliveryPointsDTO);
        ProductSessionDeliveryPoints productSessionDeliveryPoints = productsRepository.updateProductSessionDeliveryPoints(productId, eventId, updateProductSessionDeliveryPoints);
        return ProductSessionDeliveryPointConverter.toDto(productSessionDeliveryPoints);
    }

}
