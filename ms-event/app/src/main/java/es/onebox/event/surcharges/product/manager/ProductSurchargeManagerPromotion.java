package es.onebox.event.surcharges.product.manager;

import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargePromotionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSurchargeRangePromotionProductRecord;

import java.util.List;
import java.util.stream.Collectors;

public class ProductSurchargeManagerPromotion extends ProductSurchargeManager {

    RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao;

    public ProductSurchargeManagerPromotion(RangeDao rangeDao, ProductDao productDao,
                                            RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao) {
        super(rangeDao, productDao);
        this.rangeProductSurchargePromotionDao = rangeProductSurchargePromotionDao;
    }


    @Override
    public void insertSurcharges(Long productId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelSurchargeRangePromotionProductRecord(productId.intValue(), rangeId))
                .forEach(record -> rangeProductSurchargePromotionDao.insert(record));
    }

    @Override
    public void deleteSurchargesAndRanges(Long productId) {
        List<Integer> rangeIds = rangeProductSurchargePromotionDao.getByProductId(productId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        rangeProductSurchargePromotionDao.deleteByProductId(productId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }
}
