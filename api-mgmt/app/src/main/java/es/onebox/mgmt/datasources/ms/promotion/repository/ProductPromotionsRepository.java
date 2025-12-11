package es.onebox.mgmt.datasources.ms.promotion.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.promotion.MsPromotionDatasource;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.CreateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.UpdateProductPromotion;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPromotionsRepository {

    private final MsPromotionDatasource msPromotionDatasource;

    @Autowired
    public ProductPromotionsRepository(MsPromotionDatasource msPromotionDatasource) {
        this.msPromotionDatasource = msPromotionDatasource;
    }

    public ProductPromotions getProductPromotions(Long productId, ProductPromotionsFilter filter) {
        return msPromotionDatasource.getProductPromotions(productId, filter);
    }

    public ProductPromotion getProductPromotion(Long productId, Long promotionId) {
        return msPromotionDatasource.getProductPromotion(productId, promotionId);
    }

    public IdDTO createProductPromotion(Long productId, CreateProductPromotion request) {
        return msPromotionDatasource.createProductPromotion(productId, request);
    }

    public void updateProductPromotion(Long productId, Long promotionId, UpdateProductPromotion request) {
        msPromotionDatasource.updateProductPromotion(productId, promotionId, request);
    }

    public void deleteProductPromotion(Long productId, Long promotionId) {
        msPromotionDatasource.deleteProductPromotion(productId, promotionId);
    }

}
