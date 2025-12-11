package es.onebox.event.surcharges.product.manager;

import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargeDao;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSurchargeRangeProductRecord;

import java.util.List;
import java.util.stream.Collectors;

public class ProductSurchargeManagerGeneric extends ProductSurchargeManager {

    RangeProductSurchargeDao rangeProductSurchargeDao;

    public ProductSurchargeManagerGeneric(RangeDao rangeDao, ProductDao productDao, RangeProductSurchargeDao rangeProductSurchargeDao) {
        super(rangeDao, productDao);
        this.rangeProductSurchargeDao = rangeProductSurchargeDao;
    }

    @Override
    protected void insertSurcharges(Long productId, List<Integer> rangeIds) {
        rangeIds.stream()
                .map(rangeId -> new CpanelSurchargeRangeProductRecord(productId.intValue(), rangeId))
                .forEach(record -> rangeProductSurchargeDao.insert(record));
    }

    @Override
    public void deleteSurchargesAndRanges(Long productId) {
        List<Integer> rangeIds = rangeProductSurchargeDao.getByProductId(productId.intValue())
                .stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());
        rangeProductSurchargeDao.deleteByProductId(productId.intValue());
        rangeDao.deleteByIds(rangeIds);
    }
}