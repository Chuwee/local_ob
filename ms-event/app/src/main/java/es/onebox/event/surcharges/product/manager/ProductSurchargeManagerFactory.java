package es.onebox.event.surcharges.product.manager;

import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargeDao;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargePromotionDao;
import es.onebox.event.surcharges.product.dto.ProductSurchargeDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSurchargeManagerFactory {
    private final RangeDao rangeDao;
    private final RangeProductSurchargeDao rangeProductSurchargeDao;
    private final RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao;
    private final ProductDao productDao;

    @Autowired
    public ProductSurchargeManagerFactory(RangeDao rangeDao,
                                          RangeProductSurchargeDao rangeProductSurchargeDao,
                                          RangeProductSurchargePromotionDao rangeProductSurchargePromotionDao,
                                          ProductDao productDao) {
        this.rangeDao = rangeDao;
        this.rangeProductSurchargeDao = rangeProductSurchargeDao;
        this.rangeProductSurchargePromotionDao = rangeProductSurchargePromotionDao;
        this.productDao = productDao;
    }

    public ProductSurchargeManager create(ProductSurchargeDTO productSurchargeDTO) {
        ProductSurchargeManager productSurchargeManager = switch (productSurchargeDTO.getType()) {
            case GENERIC -> new ProductSurchargeManagerGeneric(rangeDao, productDao, rangeProductSurchargeDao);
            case PROMOTION -> new ProductSurchargeManagerPromotion(rangeDao, productDao, rangeProductSurchargePromotionDao);
        };

        List<Range> ranges = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(productSurchargeDTO.getRanges())) {
            productSurchargeDTO.getRanges().stream()
                    .map(rangeDTO -> new Range(
                            rangeDTO.getFrom(),
                            rangeDTO.getValues().getFixed(),
                            rangeDTO.getValues().getPercentage(),
                            rangeDTO.getValues().getMin(),
                            rangeDTO.getValues().getMax(),
                            rangeDTO.getCurrencyId()))
                    .forEach(ranges::add);

            productSurchargeManager.setRanges(ranges);
        }
        return productSurchargeManager;
    }
}