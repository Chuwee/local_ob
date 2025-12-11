package es.onebox.mgmt.products.promotions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.CreateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotions;
import es.onebox.mgmt.datasources.ms.promotion.repository.ProductPromotionsRepository;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.products.promotions.dto.CreateProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsFilter;
import es.onebox.mgmt.products.promotions.dto.UpdateProductPromotionDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductPromotionsService {

    private final ProductPromotionsRepository productPromotionsRepository;
    private final ValidationService validationService;

    @Autowired
    public ProductPromotionsService(ProductPromotionsRepository productPromotionsRepository, ValidationService validationService) {
        this.productPromotionsRepository = productPromotionsRepository;
        this.validationService = validationService;
    }

    public ProductPromotionsDTO getProductPromotions(Long productId, ProductPromotionsFilter filter) {
        validationService.getAndCheckProduct(productId);

        ProductPromotions promotions = productPromotionsRepository.getProductPromotions(productId, filter);

        return ProductPromotionConverter.fromMsPromotions(promotions);
    }

    public ProductPromotionDTO getProductPromotion(Long productId, Long promotionId) {
        validationService.getAndCheckProduct(productId);

        ProductPromotion promotion = productPromotionsRepository.getProductPromotion(productId, promotionId);

        return ProductPromotionConverter.fromMsPromotion(promotion);
    }

    public IdDTO createProductPromotion(Long productId, CreateProductPromotionDTO request) {
        validationService.getAndCheckProduct(productId);

        return productPromotionsRepository.createProductPromotion(productId,
                ProductPromotionConverter.toMsCreatePromotion(request));
    }

    public IdDTO cloneProductPromotion(Long productId, Long promotionId) {
        validationService.getAndCheckProduct(productId);

        CreateProductPromotion request = new CreateProductPromotion();
        request.setId(promotionId);

        return productPromotionsRepository.createProductPromotion(productId, request);
    }

    public void updateProductPromotion(Long productId, Long promotionId, UpdateProductPromotionDTO request) {
        validationService.getAndCheckProduct(productId);

        if (request.getDiscount() != null && request.getDiscount().getType() == null) {
            throw new OneboxRestException(ApiMgmtPromotionErrorCode.PRODUCT_PROMOTION_BAD_REQUEST,
                    "discount.type parameter is requered to update discount", null);
        }
        productPromotionsRepository.updateProductPromotion(productId, promotionId,
                ProductPromotionConverter.toMsUpdatePromotion(request));

    }

    public void deleteProductPromotion(Long productId, Long promotionId) {
        validationService.getAndCheckProduct(productId);
        getAndCheckPromotion(productId, promotionId);

        productPromotionsRepository.deleteProductPromotion(productId, promotionId);

    }

    private void getAndCheckPromotion(Long productId, Long promotionId) {
        ProductPromotion promotion = productPromotionsRepository.getProductPromotion(productId, promotionId);
        if (promotion == null) {
            throw new OneboxRestException(ApiMgmtPromotionErrorCode.PRODUCT_PROMOTION_NOT_FOUND);
        }
    }

}
