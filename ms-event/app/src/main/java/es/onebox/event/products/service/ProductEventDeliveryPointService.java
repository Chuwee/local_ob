package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductEventDeliveryPointConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.domain.ProductEventDeliveryPointRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.dto.ProductEventDeliveryPointsDTO;
import es.onebox.event.products.dto.UpdateProductEventDeliveryPointDTO;
import es.onebox.event.products.dto.UpdateProductEventDeliveryPointsDTO;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventDeliveryPointRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductEventDeliveryPointService {

    private final ProductDao productDao;
    private final EventDao eventDao;
    private final ProductEventDao productEventDao;
    private final ProductEventDeliveryPointDao productEventDeliveryPointDao;
    private final RefreshDataService refreshDataService;

    @Autowired
    public ProductEventDeliveryPointService(ProductDao productDao, EventDao eventDao,
                                            ProductEventDao productEventDao,
                                            ProductEventDeliveryPointDao productEventDeliveryPointDao,
                                            RefreshDataService refreshDataService) {
        this.productDao = productDao;
        this.eventDao = eventDao;
        this.productEventDao = productEventDao;
        this.productEventDeliveryPointDao = productEventDeliveryPointDao;
        this.refreshDataService = refreshDataService;
    }

    @MySQLRead
    public ProductEventDeliveryPointsDTO getProductEventDeliveryPoints(Long productId, Long eventId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());
        if (eventoRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        ProductEventRecord cpanelProductEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
        if (cpanelProductEventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED);
        }
        List<ProductEventDeliveryPointRecord> productEventDeliveryPointRecords = productEventDeliveryPointDao.findByProductEvent(productId, eventId);
        return ProductEventDeliveryPointConverter.toEntity(productEventDeliveryPointRecords);
    }

    @MySQLWrite
    public ProductEventDeliveryPointsDTO updateProductEventDeliveryPoint(Long productId, Long eventId, UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());
        if (eventoRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        ProductEventRecord cpanelProductEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
        if (cpanelProductEventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED);
        }
        if (updateProductEventDeliveryPointsDTO.stream().filter(pl -> BooleanUtils.isTrue(pl.getDefault())).count() != 1) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_DELIVERY_POINT_DEFAULT_REQUIRED);
        }

        List<ProductEventDeliveryPointRecord> productEventDeliveryPointRecords = productEventDeliveryPointDao.findByProductEvent(productId, eventId);

        for (UpdateProductEventDeliveryPointDTO updateProductEventDeliveryPointDTO : updateProductEventDeliveryPointsDTO) {
            if (productEventDeliveryPointRecords != null && productEventDeliveryPointRecords.stream().noneMatch(pc -> pc.getDeliverypointid().equals(updateProductEventDeliveryPointDTO.getDeliveryPointId().intValue()))) {
                CpanelProductEventDeliveryPointRecord cpanelProductEventDeliveryPointRecord = new CpanelProductEventDeliveryPointRecord();
                cpanelProductEventDeliveryPointRecord.setProducteventid(cpanelProductEventRecord.getProducteventid());
                cpanelProductEventDeliveryPointRecord.setDeliverypointid(updateProductEventDeliveryPointDTO.getDeliveryPointId().intValue());
                cpanelProductEventDeliveryPointRecord.setDefaultdeliverypoint(ConverterUtils.isTrueAsByte(updateProductEventDeliveryPointDTO.getDefault()));
                productEventDeliveryPointDao.insert(cpanelProductEventDeliveryPointRecord);
            }
        }

        if (productEventDeliveryPointRecords != null && !productEventDeliveryPointRecords.isEmpty()) {
            for (ProductEventDeliveryPointRecord productEventDeliveryPointRecord : productEventDeliveryPointRecords) {
                if (updateProductEventDeliveryPointsDTO.stream().noneMatch(r -> r.getDeliveryPointId().equals(productEventDeliveryPointRecord.getDeliverypointid().longValue()))) {
                    productEventDeliveryPointDao.delete(productEventDeliveryPointRecord);
                }
            }
        }
        // TODO update session channel document
        postUpdateProduct(productId);

        return getProductEventDeliveryPoints(productId, eventId);
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }
}
