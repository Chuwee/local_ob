package es.onebox.mgmt.products.promotions;

import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.CreateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.UpdateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import es.onebox.mgmt.products.promotions.dto.CreateProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionActivatorDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionCollectiveDetailDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionDiscountDTO;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsDTO;
import es.onebox.mgmt.products.promotions.dto.UpdateProductPromotionDTO;
import es.onebox.mgmt.products.promotions.enums.ProductPromotionActivator;
import es.onebox.mgmt.products.promotions.enums.ProductPromotionDiscountType;
import es.onebox.mgmt.products.promotions.enums.ProductPromotionType;

import java.util.stream.Collectors;

public class ProductPromotionConverter {

    public static ProductPromotionsDTO fromMsPromotions(ProductPromotions promotions) {
        ProductPromotionsDTO dto = new ProductPromotionsDTO();
        dto.setData(promotions.getData().stream()
                .map(ProductPromotionConverter::fromMsPromotion)
                .collect(Collectors.toList()));
        dto.setMetadata(promotions.getMetadata());
        return dto;
    }

    public static ProductPromotionDTO fromMsPromotion(ProductPromotion promotion) {
        ProductPromotionDTO dto = new ProductPromotionDTO();
        dto.setId(promotion.getId().intValue());
        dto.setName(promotion.getName());
        dto.setStatus(PromotionStatus.fromId(promotion.getStatus().getId()));
        dto.setType(ProductPromotionType.valueOf(promotion.getType().name()));
        if (promotion.getDiscountType() != null) {
            ProductPromotionDiscountDTO discountDTO = new ProductPromotionDiscountDTO();
            discountDTO.setType(ProductPromotionDiscountType.fromId(promotion.getDiscountType().getId()));
            discountDTO.setValue(promotion.getDiscountValue());
            //TODO multicurrency?
            dto.setDiscount(discountDTO);
        }
        if (promotion.getActivator() != null) {
            ProductPromotionActivatorDTO activatorDTO = new ProductPromotionActivatorDTO();
            ProductPromotionActivator type = ProductPromotionActivator.fromId(promotion.getActivator().getType());
            activatorDTO.setType(type);
            if (ProductPromotionActivator.COLLECTIVE.equals(type)) {
                ProductPromotionCollectiveDetailDTO collective = new ProductPromotionCollectiveDetailDTO();
                collective.setId(promotion.getActivatorId() == null ? null : Long.valueOf(promotion.getActivatorId()));
                collective.setName(promotion.getActivatorName());
                collective.setType(promotion.getActivatorType());
                collective.setValidationMethod(promotion.getActivatorValidationMethod());
                activatorDTO.setCollective(collective);
            }
            dto.setActivator(activatorDTO);
        }
        return dto;
    }

    public static CreateProductPromotion toMsCreatePromotion(CreateProductPromotionDTO request) {
        CreateProductPromotion target = new CreateProductPromotion();
        target.setName(request.getName());
        target.setType(es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotionType.fromId(request.getType().getType()));
        return target;
    }

    public static UpdateProductPromotion toMsUpdatePromotion(UpdateProductPromotionDTO request) {
        UpdateProductPromotion target = new UpdateProductPromotion();
        target.setName(request.getName());
        if (request.getStatus() != null) {
            target.setStatus(PromotionActivationStatus.fromId(request.getStatus().getId()));
        }
        if (request.getDiscount() != null) {
            target.setDiscountType(es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotionDiscountType.fromId(request.getDiscount().getType().getId()));
            target.setDiscountValue(request.getDiscount().getValue());
        }
        if (request.getActivator() != null) {
            target.setActivator(es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotionActivator.fromId(request.getActivator().getType().getType()));
            target.setActivatorId(request.getActivator().getId());
        }
        return target;
    }
}
