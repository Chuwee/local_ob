package es.onebox.event.surcharges.product.manager;

import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.common.converters.CommonRangeConverter;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProductSurchargeManager {
    protected List<Range> ranges = new ArrayList<>();
    protected RangeDao rangeDao;
    protected ProductDao productDao;

    public ProductSurchargeManager(RangeDao rangeDao, ProductDao productDao) {
        this.rangeDao = rangeDao;
        this.productDao = productDao;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    public RangeDao getRangeDao() {
        return rangeDao;
    }

    public void setRangeDao(RangeDao rangeDao) {
        this.rangeDao = rangeDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    protected abstract void insertSurcharges(Long productId, List<Integer> rangeIds);


    public abstract void deleteSurchargesAndRanges(Long productId);

    public void insert(Long productId) {
        this.generateMaxRange();

        List<Integer> insertedRangesIds = this.insertRanges().stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        this.insertSurcharges(productId, insertedRangesIds);
    }

    public List<CpanelRangoRecord> insertRanges() {
        List<CpanelRangoRecord> insertedRangoRecords = new ArrayList<>();

        List<Range> rangesList = this.ranges;

        CommonRangeConverter.fromProductSurchargeManager(rangesList)
                .forEach(cpanelRangoRecord -> {
                    CpanelRangoRecord record = rangeDao.insert(cpanelRangoRecord);
                    insertedRangoRecords.add(record);
                });

        return insertedRangoRecords;
    }

    public void sortRanges() {
        Collections.sort(ranges);
    }

    public void generateMaxRange() {
        this.sortRanges();
        int totalRanges = ranges.size() - 1;
        int index = 0;

        for (Range range : this.ranges) {
            if (index == totalRanges) {
                range.setTo(0D);
            } else {
                range.setTo(this.ranges.get(++index).getFrom());
            }
        }
    }

    public boolean isEmpty() {
        return this.ranges == null || this.ranges.isEmpty();
    }

    public boolean isInitialRangeDuplicated() {
        Set<Double> uniqueRanges = new HashSet<>();
        for (Range range : this.ranges) {
            if (!uniqueRanges.add(range.getFrom())) {
                return true;
            }
        }
        return false;
    }
}