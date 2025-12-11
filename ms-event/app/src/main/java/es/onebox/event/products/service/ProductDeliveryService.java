package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductDeliveryConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dto.ProductDeliveryDTO;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDeliveryService {

    private final ProductDao productDao;
    private final RefreshDataService refreshDataService;

    @Autowired
    public ProductDeliveryService(ProductDao productDao, RefreshDataService refreshDataService) {
        this.productDao = productDao;
        this.refreshDataService = refreshDataService;
    }

    @MySQLRead
    public ProductDeliveryDTO getProductDelivery(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return ProductDeliveryConverter.toEntity(cpanelProductRecord);
    }

    @MySQLWrite
    public ProductDeliveryDTO updateProductDelivery(Long productId, ProductDeliveryDTO productDeliveryDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        if (cpanelProductRecord.getState().equals(ProductState.ACTIVE.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_NOT_UPDATABLE);
        }

        cpanelProductRecord.setDeliverytype(productDeliveryDTO.getDeliveryType().getId());

        if (productDeliveryDTO.getDeliveryType().equals(ProductDeliveryType.FIXED_DATES)) {
            checkDeliveryDates(productDeliveryDTO);

            cpanelProductRecord.setDeliverydatefrom(CommonUtils.zonedDateTimeToTimestamp(productDeliveryDTO.getDeliveryDateFrom()));
            cpanelProductRecord.setDeliverydateto(CommonUtils.zonedDateTimeToTimestamp(productDeliveryDTO.getDeliveryDateTo()));
        } else {
            cpanelProductRecord.setDeliverystarttimeunit(productDeliveryDTO.getStartTimeUnit().getId());
            cpanelProductRecord.setDeliverystarttimevalue(productDeliveryDTO.getStartTimeValue().intValue());
            cpanelProductRecord.setDeliveryendtimeunit(productDeliveryDTO.getEndTimeUnit().getId());
            cpanelProductRecord.setDeliveryendtimevalue(productDeliveryDTO.getEndTimeValue().intValue());
        }

        CpanelProductRecord updatedProduct = productDao.update(cpanelProductRecord);
        postUpdateProduct(productId);

        return ProductDeliveryConverter.toEntity(updatedProduct);
    }

    private void checkDeliveryDates(ProductDeliveryDTO productDeliveryDTO) {
        if (productDeliveryDTO.getDeliveryDateFrom() == null || productDeliveryDTO.getDeliveryDateTo() == null) {
            throw new OneboxRestException(MsEventErrorCode.DELIVERY_DATES_REQUIRED);
        }

        if (productDeliveryDTO.getDeliveryDateFrom().isAfter(productDeliveryDTO.getDeliveryDateTo())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_DELIVERY_DATES);
        }
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }

}
