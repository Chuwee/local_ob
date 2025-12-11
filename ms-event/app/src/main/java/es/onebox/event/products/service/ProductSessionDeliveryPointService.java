package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductSessionConverter;
import es.onebox.event.products.converter.ProductSessionDeliveryPointConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductSessionDeliveryPointDao;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionDeliveryPointRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointDTO;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointDetailDTO;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointsDTO;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionDeliveryPointRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSessionDeliveryPointService {

    private final ProductDao productDao;
    private final EventDao eventDao;
    private final ProductEventDao productEventDao;
    private final RefreshDataService refreshDataService;
    private final ProductSessionDeliveryPointDao productSessionDeliveryPointDao;
    private final ProductSessionDao productSessionDao;

    @Autowired
    public ProductSessionDeliveryPointService(ProductDao productDao,
                                              EventDao eventDao,
                                              ProductEventDao productEventDao,
                                              ProductSessionDeliveryPointDao productSessionDeliveryPointDao,
                                              RefreshDataService refreshDataService,
                                              ProductSessionDao productSessionDao) {
        this.productDao = productDao;
        this.eventDao = eventDao;
        this.productEventDao = productEventDao;
        this.productSessionDeliveryPointDao = productSessionDeliveryPointDao;
        this.refreshDataService = refreshDataService;
        this.productSessionDao = productSessionDao;
    }

    @MySQLRead
    public ProductSessionDeliveryPointsDTO getProductSessionDeliveryPoints(Long productId, Long eventId,
                                                                           ProductSessionDeliveryPointsFilterDTO filterDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());
        if (eventoRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        ProductEventRecord productEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
        if (productEventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED);
        }

        SelectionType selectionType = SelectionType.get(productEventRecord.getSessionsselectiontype());

        SessionSearchFilter sessionFilter = prepareSessionSearchFilter(productId, eventId, filterDTO, selectionType);

        Long total = SelectionType.RESTRICTED.equals(selectionType) ?
                productSessionDeliveryPointDao.countRestrictedByProductEvent(productId, eventId, sessionFilter) :
                productSessionDeliveryPointDao.countUnrestrictedByEvent(eventId, sessionFilter);

        List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords =
                productSessionDeliveryPointDao.findByProductEvent(productId, eventId, sessionFilter, selectionType);

        return ProductSessionDeliveryPointConverter.toEntity(productSessionDeliveryPointRecords, sessionFilter, total);
    }

    @MySQLWrite
    public ProductSessionDeliveryPointsDTO updateProductSessionDeliveryPoint(Long productId, Long eventId, UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointsDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());
        if (eventoRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        ProductEventRecord productEventRecord = productEventDao.findByProductIdAndEventId(productId, eventId);
        if (productEventRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED);
        }
        for (UpdateProductSessionDeliveryPointDTO updateProductSessionDeliveryPointDTO : updateProductSessionDeliveryPointsDTO) {
            if (updateProductSessionDeliveryPointDTO.getDeliveryPoints().stream().noneMatch(pl -> BooleanUtils.isTrue(pl.getDefault()))) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_SESSION_DELIVERY_POINT_DEFAULT_REQUIRED);
            }
        }

        for (UpdateProductSessionDeliveryPointDTO updateProductSessionDeliveryPointDTO : updateProductSessionDeliveryPointsDTO) {
            List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords = productSessionDeliveryPointDao.findByProductSession(productId, updateProductSessionDeliveryPointDTO.getId());
            // create new product session delivery point
            for (UpdateProductSessionDeliveryPointDetailDTO updateProductSessionDeliveryPointDetailDTO : updateProductSessionDeliveryPointDTO.getDeliveryPoints()) {
                if (productSessionDeliveryPointRecords != null && productSessionDeliveryPointRecords.stream().noneMatch(pc -> pc.getDeliverypointid().equals(updateProductSessionDeliveryPointDetailDTO.getDeliveryPointId().intValue()) && pc.getSessionid().equals(updateProductSessionDeliveryPointDTO.getId().intValue()))) {
                    CpanelProductSessionDeliveryPointRecord cpanelProductSessionDeliveryPointRecord = new CpanelProductSessionDeliveryPointRecord();
                    cpanelProductSessionDeliveryPointRecord.setProducteventid(productEventRecord.getProducteventid());
                    cpanelProductSessionDeliveryPointRecord.setSessionid(updateProductSessionDeliveryPointDTO.getId().intValue());
                    cpanelProductSessionDeliveryPointRecord.setDeliverypointid(updateProductSessionDeliveryPointDetailDTO.getDeliveryPointId().intValue());
                    cpanelProductSessionDeliveryPointRecord.setDefaultdeliverypoint(ConverterUtils.isTrueAsByte(updateProductSessionDeliveryPointDetailDTO.getDefault()));
                    productSessionDeliveryPointDao.insert(cpanelProductSessionDeliveryPointRecord);
                }
            }

            // delete
            if (productSessionDeliveryPointRecords != null && !productSessionDeliveryPointRecords.isEmpty()) {
                for (ProductSessionDeliveryPointRecord productSessionDeliveryPointRecord : productSessionDeliveryPointRecords) {
                    if (updateProductSessionDeliveryPointDTO.getDeliveryPoints().stream().noneMatch(dp -> dp.getDeliveryPointId().equals(productSessionDeliveryPointRecord.getDeliverypointid().longValue()))) {
                        productSessionDeliveryPointDao.delete(productSessionDeliveryPointRecord);
                    }
                }
            }
        }

        // TODO update session channel document
        postUpdateProduct(productId);

        return getProductSessionDeliveryPoints(productId, eventId, null);
    }

    private SessionSearchFilter prepareSessionSearchFilter(Long productId, Long eventId,
                                                           ProductSessionDeliveryPointsFilterDTO filter,
                                                           SelectionType selectionType) {
        if (filter == null) {
            return null;
        }

        SessionSearchFilter sessionFilter = ProductSessionDeliveryPointConverter.convertToSessionFilter(filter);
        if (SelectionType.RESTRICTED.equals(selectionType)) {
            List<ProductSessionRecord> productSessions = productSessionDao.findProductSessionsByProductId(productId.intValue(), eventId.intValue());
            if (CollectionUtils.isNotEmpty(productSessions)) {
                sessionFilter.setIds(productSessions.stream().map(CpanelProductSessionRecord::getSessionid).map(Integer::longValue).toList());
            }
        } else {
            sessionFilter.setEventId(List.of(eventId));
        }
        return sessionFilter;
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }
}
