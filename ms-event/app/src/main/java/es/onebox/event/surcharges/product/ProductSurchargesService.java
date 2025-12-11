package es.onebox.event.surcharges.product;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.common.converters.CommonRangeConverter;
import es.onebox.event.surcharges.dao.RangeSurchargeEntityDao;
import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargeDao;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargePromotionDao;
import es.onebox.event.surcharges.product.dto.ProductSurchargeDTO;
import es.onebox.event.surcharges.product.dto.ProductSurchargeListDTO;
import es.onebox.event.surcharges.product.enums.ProductSurchargeType;
import es.onebox.event.surcharges.product.manager.ProductSurchargeManager;
import es.onebox.event.surcharges.product.manager.ProductSurchargeManagerFactory;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSurchargeRangeProductRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static es.onebox.event.common.utils.RangesUtils.defaultRanges;

@Service
public class ProductSurchargesService {

    private final ProductSurchargeManagerFactory productSurchargeManagerFactory;
    private final ProductDao productDao;
    private final RangeProductSurchargeDao rangeProductSurchargeDao;
    private final RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao;
    private final RangeSurchargeEntityDao rangeSurchargeEntityDao;
    private final SurchargeRangeDao surchargeRangeDao;
    private final WebhookService webhookService;


    @Autowired
    public ProductSurchargesService(ProductSurchargeManagerFactory productSurchargeManagerFactory,
                                    ProductDao productDao, RangeProductSurchargeDao rangeProductSurchargeDao, RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao, RangeSurchargeEntityDao rangeSurchargeEntityDao, SurchargeRangeDao surchargeRangeDao, WebhookService webhookService) {
        this.productSurchargeManagerFactory = productSurchargeManagerFactory;
        this.productDao = productDao;
        this.rangeProductSurchargeDao = rangeProductSurchargeDao;
        this.rangeProductSurchargePromotionDao = rangeProductSurchargePromotionDao;
        this.rangeSurchargeEntityDao = rangeSurchargeEntityDao;
        this.surchargeRangeDao = surchargeRangeDao;
        this.webhookService = webhookService;
    }

    public List<ProductSurchargeDTO> getRanges(Long productId, List<ProductSurchargeType> types) {
        List<ProductSurchargeDTO> productSurcharges = new ArrayList<>();
        CpanelProductRecord productRecord = productDao.getById(productId.intValue());

        if (productRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        if (types == null || types.isEmpty()) {
            types = Arrays.asList(ProductSurchargeType.GENERIC, ProductSurchargeType.PROMOTION);
        }

        for (ProductSurchargeType type : types) {
            switch (type) {
                case GENERIC, PROMOTION -> getProductSurcharges(productRecord, type, productSurcharges);
                default -> throw new OneboxRestException(MsEventErrorCode.PRODUCT_SURCHARGE_TYPE_NOT_SUPPORTED);
            }
        }

        return productSurcharges;
    }

    @MySQLWrite
    public void setSurcharges(Long productId, ProductSurchargeListDTO productSurchargeListDTO) {
        if (productSurchargeListDTO == null || productSurchargeListDTO.isEmpty()) {
            return;
        }

        CpanelProductRecord productRecord = productDao.getById(productId.intValue());

        for (ProductSurchargeDTO surchargeDTO : productSurchargeListDTO) {

            ProductSurchargeManager productSurchargeManager = productSurchargeManagerFactory.create(surchargeDTO);
            validateSetSurcharge(productRecord, productSurchargeManager);

            productSurchargeManager.deleteSurchargesAndRanges(productId);
            productSurchargeManager.insert(productId);
        }
        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_SURCHARGES);
    }

    @MySQLRead
    public void getProductSurcharges(CpanelProductRecord productRecord, ProductSurchargeType type, List<ProductSurchargeDTO> productSurchargeDTOList) {
        switch (type) {
            case GENERIC -> {
                List<CpanelRangoRecord> rangoRecordList = rangeProductSurchargeDao.getByProductId(productRecord.getProductid());

                if (CollectionUtils.isEmpty(rangoRecordList)) {
                    if (productRecord.getIdcurrency() != null) {
                        List<CpanelRangoRecord> entityRangeRecords =
                                rangeSurchargeEntityDao.getByEntityIdAndCurrencyId(productRecord.getEntityid(), productRecord.getIdcurrency());
                        rangoRecordList = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(productRecord.getIdcurrency()));
                    } else {
                        List<CpanelRangoRecord> entityRangeRecords = rangeSurchargeEntityDao.getByEntityId(productRecord.getEntityid());
                        rangoRecordList = CollectionUtils.isNotEmpty(entityRangeRecords)
                                ? entityRangeRecords
                                : Arrays.asList(defaultRanges(null));
                    }
                }

                ProductSurchargeDTO productSurchargeDTO = getProductSurchargeByType(rangoRecordList, ProductSurchargeType.GENERIC);
                productSurchargeDTOList.add(productSurchargeDTO);
            }

            case PROMOTION -> {
                List<CpanelRangoRecord> promotionRangeRecordsList = rangeProductSurchargePromotionDao.getByProductId(productRecord.getProductid());

                if (CollectionUtils.isEmpty(promotionRangeRecordsList)) {
                    promotionRangeRecordsList = Arrays.asList(defaultRanges(productRecord.getIdcurrency()));
                }

                ProductSurchargeDTO productSurchargeDTO = getProductSurchargeByType(promotionRangeRecordsList, ProductSurchargeType.PROMOTION);
                productSurchargeDTOList.add(productSurchargeDTO);
            }
        }
    }

    public void initProductSurcharges(CpanelProductRecord productRecord) {
        List<CpanelRangoRecord> entityRanges = rangeSurchargeEntityDao.getByEntityIdAndCurrencyId(productRecord.getEntityid(),
                productRecord.getIdcurrency());

        if (entityRanges.isEmpty()) {
            entityRanges = rangeSurchargeEntityDao.getByEntityId(productRecord.getEntityid());
            entityRanges.removeIf(range -> range.getIdcurrency() != null && !range.getIdcurrency().equals(productRecord.getIdcurrency()));
        }

        if (entityRanges.isEmpty()) {
            CpanelRangoRecord newProductRange = insertRangeRecord(productRecord.getIdcurrency());
            CpanelSurchargeRangeProductRecord cpanelSurchargeRangeProductRecord = new CpanelSurchargeRangeProductRecord();
            cpanelSurchargeRangeProductRecord.setProductid(productRecord.getProductid());
            cpanelSurchargeRangeProductRecord.setRangeid(newProductRange.getIdrango());
            rangeProductSurchargeDao.insert(cpanelSurchargeRangeProductRecord);

        } else {
            entityRanges.forEach(entityRange -> {
                entityRange.setIdrango(null);
                Integer rangeId = surchargeRangeDao.insertInto(entityRange);

                CpanelSurchargeRangeProductRecord cpanelSurchargeRangeProductRecord = new CpanelSurchargeRangeProductRecord();
                cpanelSurchargeRangeProductRecord.setProductid(productRecord.getProductid());
                cpanelSurchargeRangeProductRecord.setRangeid(rangeId);
                rangeProductSurchargeDao.insert(cpanelSurchargeRangeProductRecord);
            });
        }
    }

    private ProductSurchargeDTO getProductSurchargeByType(List<CpanelRangoRecord> rangeRecords, ProductSurchargeType type) {
        ProductSurchargeDTO productSurcharges = new ProductSurchargeDTO();
        List<RangeDTO> ranges = CommonRangeConverter.fromRecords(rangeRecords);
        productSurcharges.setRanges(ranges);
        productSurcharges.setType(type);
        return productSurcharges;
    }

    private void validateSetSurcharge(CpanelProductRecord productRecord, ProductSurchargeManager productSurchargeManager) {
        if (productRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        if (productSurchargeManager.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.AT_LEAST_ONE_RANGE);
        }

        if (productSurchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFrom()))) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_FROM_RANGE_MANDATORY);
        }

        if (productSurchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFixed()) && Objects.isNull(range.getPercentage()))) {
            throw new OneboxRestException(MsEventErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (productSurchargeManager.isInitialRangeDuplicated()) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_DUPLICATED_FROM_RANGE);
        }

        if (productSurchargeManager.getRanges().stream().noneMatch(range -> range.getFrom() == 0)) {
            throw new OneboxRestException(MsEventErrorCode.FROM_RANGE_ZERO_MANDATORY);
        }

        if (isMinGreaterThanMax(productSurchargeManager)) {
            throw new OneboxRestException(MsEventErrorCode.MIN_SURCHARGE_GREATER_THAN_MAX);
        }
    }

    private boolean isMinGreaterThanMax(ProductSurchargeManager productSurchargeManager) {
        return productSurchargeManager.getRanges().stream()
                .filter(range -> range.getMin() != null && range.getMax() != null)
                .anyMatch(range -> range.getMin() > range.getMax());
    }

    private CpanelRangoRecord insertRangeRecord(Integer currencyId) {
        CpanelRangoRecord productRange = new CpanelRangoRecord();
        productRange.setValor(0d);
        productRange.setRangominimo(0d);
        productRange.setRangomaximo(0d);
        productRange.setNombrerango(CommonRangeConverter.getRangeName(productRange));
        productRange.setIdcurrency(currencyId);
        productRange.setIdrango(surchargeRangeDao.insertInto(productRange));

        return productRange;
    }
}
