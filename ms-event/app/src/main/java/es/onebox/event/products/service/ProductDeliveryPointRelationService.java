package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductDeliveryPointRecordRelationConverter;
import es.onebox.event.products.dao.DeliveryPointDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductDeliveryPointRelationDao;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.event.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.event.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.event.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductDeliveryPointRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDeliveryPointRelationService {

    private final ProductDeliveryPointRelationDao productDeliveryPointRelationDao;
    private final ProductDao productDao;
    private final DeliveryPointDao deliveryPointDao;
    private final RefreshDataService refreshDataService;

    @Autowired
    public ProductDeliveryPointRelationService(ProductDeliveryPointRelationDao productDeliveryPointRelationDao,
                                               ProductDao productDao, DeliveryPointDao deliveryPointDao,
                                               RefreshDataService refreshDataService) {
        this.productDeliveryPointRelationDao = productDeliveryPointRelationDao;
        this.productDao = productDao;
        this.deliveryPointDao = deliveryPointDao;
        this.refreshDataService = refreshDataService;
    }

    @MySQLWrite
    public void upsertProductDeliveryPointRelation(Long productId, UpsertProductDeliveryPointRelationDTO upsertProductDeliveryPointRelationDTO) {
        CpanelProductRecord productRecord = validateProduct(productId);

        List<ProductDeliveryPointRelationRecord> productDeliveryPointsRelations = productDeliveryPointRelationDao.getProductDeliveryPointsRelations(productId, null);

        if (!productDeliveryPointsRelations.isEmpty() && productRecord.getState().equals(ProductState.ACTIVE.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_NOT_UPDATABLE);
        }

        List<ProductDeliveryPointRelationRecord> deliveryPointsRelationsToRemove = productDeliveryPointsRelations.stream().filter(dp -> !upsertProductDeliveryPointRelationDTO.getDeliveryPointIds().contains(dp.getDeliverypointid().longValue())).collect(Collectors.toList());

        for (ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord : deliveryPointsRelationsToRemove) {
            productDeliveryPointRelationDao.delete(productDeliveryPointRelationRecord);
        }

        for (Long deliveryPointId : upsertProductDeliveryPointRelationDTO.getDeliveryPointIds()) {
            CpanelDeliveryPointRecord cpanelDeliveryPointRecord = deliveryPointDao.getById(deliveryPointId.intValue());
            if (cpanelDeliveryPointRecord == null) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND);
            }
            ProductDeliveryPointRelationRecord productDeliveryPointRelationDaoByRelationId = productDeliveryPointRelationDao.findByRelationId(productId, deliveryPointId);
            if (productDeliveryPointRelationDaoByRelationId == null) {
                CpanelProductDeliveryPointRecord cpanelProductDeliveryPointRecord = ProductDeliveryPointRecordRelationConverter.toRecord(productId, deliveryPointId);
                productDeliveryPointRelationDao.insert(cpanelProductDeliveryPointRecord);
            }
        }
        postUpdateProduct(productId);
    }

    @MySQLRead
    public ProductDeliveryPointRelationDTO getProductDeliveryPointRelation(Long productId, Long deliveryPointId) {
        ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord = productDeliveryPointRelationDao.findByRelationId(productId, deliveryPointId);
        if (productDeliveryPointRelationRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_RELATION_NOT_FOUND);
        }
        return ProductDeliveryPointRecordRelationConverter.toDto(productDeliveryPointRelationRecord);
    }

    @MySQLRead
    public ProductDeliveryPointsRelationsDTO searchProductDeliveryPoinRelations(Long productId, SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointFilterRelationFilterDTO) {
        ProductDeliveryPointsRelationsDTO productDeliveryPointsRelationsDTO = new ProductDeliveryPointsRelationsDTO();
        List<ProductDeliveryPointRelationRecord> productDeliveryPointsRelations = productDeliveryPointRelationDao.getProductDeliveryPointsRelations(productId, searchProductDeliveryPointFilterRelationFilterDTO);
        if (productDeliveryPointsRelations != null) {
            productDeliveryPointsRelationsDTO.setData(ProductDeliveryPointRecordRelationConverter.toDtoList(productDeliveryPointsRelations));
            Long total = productDeliveryPointRelationDao.getTotalProductDeliveryPointsRelations(productId, searchProductDeliveryPointFilterRelationFilterDTO);
            productDeliveryPointsRelationsDTO.setMetadata(MetadataBuilder.build(searchProductDeliveryPointFilterRelationFilterDTO, total));
            return productDeliveryPointsRelationsDTO;
        }
        return null;
    }

    private CpanelProductRecord validateProduct(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return cpanelProductRecord;
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }
}
