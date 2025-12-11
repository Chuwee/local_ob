package es.onebox.mgmt.products.service;

import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpsertProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelations;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductDeliveryPointRelationFilter;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.products.converter.ProductDeliveryPointRelationConverter;
import es.onebox.mgmt.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.mgmt.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.mgmt.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDeliveryPointRelationService {
    private final SecurityManager securityManager;
    private final ProductsRepository productsRepository;

    @Autowired
    public ProductDeliveryPointRelationService(SecurityManager securityManager, ProductsRepository productsRepository) {
        this.securityManager = securityManager;
        this.productsRepository = productsRepository;
    }

    public void upsertProductDeliveryPointRelation(Long productId, UpsertProductDeliveryPointRelationDTO upsertProductDeliveryPointRelationDTO) {
        Product product = productsRepository.getProduct(productId);
        securityManager.checkEntityAccessible(product.getEntity().getId());
        UpsertProductDeliveryPointRelation upsertProductDeliveryPointRelation = ProductDeliveryPointRelationConverter.convert(upsertProductDeliveryPointRelationDTO);
        productsRepository.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelation);
    }

    public ProductDeliveryPointRelationDTO getProductDeliveryPointRelation(Long productId, Long deliveryPointId) {
        ProductDeliveryPointRelation productDeliveryPointRelation = productsRepository.getProductDeliveryPointRelation(productId, deliveryPointId);
        Product product = productsRepository.getProduct(productDeliveryPointRelation.getProduct().getId());
        securityManager.checkEntityAccessible(product.getEntity().getId());
        return ProductDeliveryPointRelationConverter.toDto(productDeliveryPointRelation);
    }

    public ProductDeliveryPointsRelationsDTO searchProductDeliveryPointRelation(Long productId, SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointRelationFilterDTO) {
        SearchProductDeliveryPointRelationFilter filter = ProductDeliveryPointRelationConverter.convertFilter(productId, searchProductDeliveryPointRelationFilterDTO);
        ProductDeliveryPointRelations productDeliveryPointRelations = productsRepository.searchProductDeliveryPointRelations(productId, filter);
        return ProductDeliveryPointRelationConverter.toDtoList(productDeliveryPointRelations);
    }

}
