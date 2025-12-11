package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.UpsertProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelationDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelations;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductDeliveryPointRelationFilter;
import es.onebox.mgmt.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointRelationDetailDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.mgmt.products.dto.SearchProductDeliveryPointRelationFilterDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductDeliveryPointRelationConverter {

    private ProductDeliveryPointRelationConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static UpsertProductDeliveryPointRelation convert(UpsertProductDeliveryPointRelationDTO dto) {
        UpsertProductDeliveryPointRelation upsertProductDeliveryPointRelation = new UpsertProductDeliveryPointRelation();
        upsertProductDeliveryPointRelation.setDeliveryPointIds(dto.getDeliveryPointIds());
        return upsertProductDeliveryPointRelation;
    }

    public static ProductDeliveryPointRelationDTO toDto(ProductDeliveryPointRelation productDeliveryPointRelation) {
        ProductDeliveryPointRelationDTO productDeliveryPointRelationDTO = new ProductDeliveryPointRelationDTO();
        productDeliveryPointRelationDTO.setId(productDeliveryPointRelation.getId());
        productDeliveryPointRelationDTO.setProduct(productDeliveryPointRelation.getProduct());
        productDeliveryPointRelationDTO.setDeliveryPoint(productDeliveryPointRelation.getDeliveryPoint());
        return productDeliveryPointRelationDTO;
    }

    public static ProductDeliveryPointsRelationsDTO toDtoList(ProductDeliveryPointRelations productDeliveryPointsRelations) {
        ProductDeliveryPointsRelationsDTO productDeliveryPointsRelationsDTO = new ProductDeliveryPointsRelationsDTO();
        List<ProductDeliveryPointRelationDetailDTO> productDeliveryPointRelationDTOS = new ArrayList<>();
        if (productDeliveryPointsRelations.getData() != null) {
            productDeliveryPointsRelations.getData().forEach(dp -> productDeliveryPointRelationDTOS.add(toDtoDetail(dp)));
        }
        productDeliveryPointsRelationsDTO.setData(productDeliveryPointRelationDTOS);
        productDeliveryPointsRelationsDTO.setMetadata(productDeliveryPointsRelations.getMetadata());
        return productDeliveryPointsRelationsDTO;
    }

    public static ProductDeliveryPointRelationDetailDTO toDtoDetail(ProductDeliveryPointRelationDetail productDeliveryPointRelationDetail) {
        ProductDeliveryPointRelationDetailDTO productDeliveryPointRelationDetailDTO = new ProductDeliveryPointRelationDetailDTO();
        productDeliveryPointRelationDetailDTO.setId(productDeliveryPointRelationDetail.getId());
        productDeliveryPointRelationDetailDTO.setDeliveryPoint(productDeliveryPointRelationDetail.getDeliveryPoint());
        return productDeliveryPointRelationDetailDTO;
    }


    public static SearchProductDeliveryPointRelationFilter convertFilter(Long productId, SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointRelationFilterDTO) {
        SearchProductDeliveryPointRelationFilter searchProductDeliveryPointRelationFilter = new SearchProductDeliveryPointRelationFilter();
        searchProductDeliveryPointRelationFilter.setProductId(productId);
        searchProductDeliveryPointRelationFilter.setOffset(searchProductDeliveryPointRelationFilterDTO.getOffset());
        searchProductDeliveryPointRelationFilter.setLimit(searchProductDeliveryPointRelationFilterDTO.getLimit());
        return searchProductDeliveryPointRelationFilter;
    }
}
