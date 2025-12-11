package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductLanguagesConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductLanguagesDTO;
import es.onebox.event.products.dto.UpdateProductLanguageDTO;
import es.onebox.event.products.dto.UpdateProductLanguagesDTO;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductLanguageRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductLanguageService {

    private final ProductDao productDao;
    private final ProductLanguageDao productLanguageDao;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    @Autowired
    public ProductLanguageService(ProductDao productDao, ProductLanguageDao productLanguageDao,
                                  RefreshDataService refreshDataService, WebhookService webhookService) {
        this.productDao = productDao;
        this.productLanguageDao = productLanguageDao;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }

    @MySQLRead
    public ProductLanguagesDTO getProductLanguages(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            return null;
        }
        return ProductLanguagesConverter.toEntity(productLanguageRecords);
    }

    @MySQLWrite
    public ProductLanguagesDTO updateProductLanguages(Long productId, UpdateProductLanguagesDTO updateProductLanguagesDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        if (updateProductLanguagesDTO.stream().filter(pl -> BooleanUtils.isTrue(pl.getDefault())).count() != 1) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGE_DEFAULT_REQUIRED);
        }


        productLanguageDao.deleteByProduct(productId);
        for (UpdateProductLanguageDTO updateProductLanguageDTO : updateProductLanguagesDTO) {
            CpanelProductLanguageRecord cpanelProductLanguageRecord = new CpanelProductLanguageRecord();
            cpanelProductLanguageRecord.setProductid(productId.intValue());
            cpanelProductLanguageRecord.setLanguageid(updateProductLanguageDTO.getLanguageId().intValue());
            cpanelProductLanguageRecord.setDefaultlanguage(ConverterUtils.isTrueAsByte(updateProductLanguageDTO.getDefault()));
            productLanguageDao.insert(cpanelProductLanguageRecord);
        }
        postUpdateProduct(productId);
        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_LANGUAGES);
        return getProductLanguages(productId);
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }
}

