package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductPublishingSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSession;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessions;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductSessionConverter;
import es.onebox.mgmt.products.dto.ProductPublishingSessionsDTO;
import es.onebox.mgmt.products.dto.ProductSessionSearchFilterDTO;
import es.onebox.mgmt.products.dto.ProductSessionsDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionsDTO;
import es.onebox.mgmt.products.enums.SelectionType;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSessionService {
    private final ValidationService validationService;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductSessionService(ValidationService validationService, ProductsRepository productsRepository) {
        this.validationService = validationService;
        this.productsRepository = productsRepository;
    }

    public ProductPublishingSessionsDTO getPublishingSessions(Long productId, Long eventId) {
        //Check product and channel
        checkProductAndEvent(productId, eventId);

        ProductPublishingSessions productSessions = productsRepository.getProductPublishingSessions(productId, eventId);

        return ProductSessionConverter.toProductPublishingSessionsDto(productSessions);
    }

    public void updatePublishingSessions(Long productId, Long eventId,
                                         UpdateProductSessionsDTO updateProductSessionsDTO) {
        //Check product and channel
        checkProductAndEvent(productId, eventId);

        if (updateProductSessionsDTO.getType().equals(SelectionType.RESTRICTED) &&
                CollectionUtils.isEmpty(updateProductSessionsDTO.getSessions())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_EMPTY_TARGET);
        }

        UpdateProductSessions updateProductSessions =
                ProductSessionConverter.toUpdateProductSessions(updateProductSessionsDTO);

        productsRepository.updateProductSessions(productId, eventId, updateProductSessions);
    }

    public ProductSessionsDTO getProductSessions(Long productId, Long eventId, ProductSessionSearchFilterDTO filterDTO) {
        //Check product and channel
        checkProductAndEvent(productId, eventId);
        ProductSessions productSessions = productsRepository.getProductSessions(productId, eventId, filterDTO);
        return ProductSessionConverter.toProductSessionsDTO(productSessions);
    }

    public void updateProductSession(Long productId, Long eventId, Long sessionId, UpdateProductSessionDTO request) {
        //Check product and channel
        checkProductAndEvent(productId, eventId);

        UpdateProductSession updateProductSessionRequest = new UpdateProductSession();
        if (CollectionUtils.isNotEmpty(request.getVariants())) {
            updateProductSessionRequest.setVariants(request.getVariants().stream().map(v -> {
                        ProductSessionVariant variant = new ProductSessionVariant();
                        variant.setId(v.getId());
                        variant.setUseCustomStock(v.getUseCustomStock());
                        variant.setStock(v.getStock());
                        variant.setUseCustomPrice(v.getUseCustomPrice());
                        variant.setPrice(v.getPrice());
                        return variant;
                    }
            ).toList());
        }
        //TODO remove after migration
        updateProductSessionRequest.setStock(request.getStock());
        updateProductSessionRequest.setUseCustomStock(request.getUseCustomStock());
        productsRepository.updateProductSession(productId, eventId, sessionId, updateProductSessionRequest);
    }

    private void checkProductAndEvent(Long productId, Long eventId) {
        //check product
        validationService.getAndCheckProduct(productId);

        //check event
        validationService.getAndCheckEvent(eventId);
    }
}
