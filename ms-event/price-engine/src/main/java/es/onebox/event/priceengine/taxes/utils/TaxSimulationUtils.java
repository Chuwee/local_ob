package es.onebox.event.priceengine.taxes.utils;

import es.onebox.core.order.utils.tax.TaxUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.priceengine.taxes.domain.CapacityRangeType;
import es.onebox.event.priceengine.taxes.domain.TaxAmount;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;

public class TaxSimulationUtils {

    private TaxSimulationUtils() {
        throw new UnsupportedOperationException();
    }

    // When tax data is missing from the database, it defaults to 0%
    public static <T extends TaxInfo> T createTaxInfo(Long id, Double value, String name, Supplier<T> constructor) {
        T taxInfo = constructor.get();
        taxInfo.setId(id);
        taxInfo.setValue(value != null ? value : DOUBLE_ZERO);
        taxInfo.setName(name);
        return taxInfo;
    }

    public static <T extends TaxInfo> T createTaxInfo(Long id, Double value, String name, Double minRange, Double maxRange,
                                                      Boolean progressive, Double progressiveMin, Double progressiveMax,
                                                      CapacityRangeType capacityType, Integer capacityMin, Integer capacityMax,
                                                      LocalDateTime startDate, LocalDateTime endDate, Supplier<T> constructor) {
        T taxInfo = constructor.get();
        taxInfo.setId(id);
        taxInfo.setValue(value != null ? value : DOUBLE_ZERO);
        taxInfo.setName(name);
        taxInfo.setMinRange(minRange);
        taxInfo.setMaxRange(maxRange);
        taxInfo.setProgressive(progressive);
        taxInfo.setProgressiveMin(progressiveMin);
        taxInfo.setProgressiveMax(progressiveMax);
        taxInfo.setCapacityTypeId(capacityType);
        taxInfo.setCapacityMin(capacityMin);
        taxInfo.setCapacityMax(capacityMax);
        taxInfo.setStartDate(startDate);
        taxInfo.setEndDate(endDate);
        return taxInfo;
    }

    public static <T extends TaxAmount> List<T> createTaxAmount(Double totalTaxes, Double netPrice, List<? extends TaxInfo> taxes, Supplier<T> constructor) {

        if (totalTaxes == null || netPrice == null || CollectionUtils.isEmpty(taxes)) {
            return null;
        }

        List<? extends TaxInfo> sortedTaxes = taxes.stream()
                .sorted(Comparator.comparingDouble(TaxInfo::getValue).thenComparingLong(TaxInfo::getId))
                .toList();

        List<T> taxAmounts = new ArrayList<>();
        TaxInfo lastTax = sortedTaxes.get(sortedTaxes.size() - 1);
        double acc = 0;

        for (TaxInfo taxInfo : sortedTaxes) {

            T taxAmount = constructor.get();
            taxAmount.setId(taxInfo.getId());

            if (taxInfo != lastTax) {
                Double amount = TaxUtils.calculateTaxAmountGivenNetPrice(netPrice, taxInfo.getValue());
                taxAmount.setAmount(amount);
                acc += amount;
            } else {
                taxAmount.setAmount(NumberUtils.minus(totalTaxes, acc));
            }

            taxAmounts.add(taxAmount);
        }

        taxAmounts.sort(Comparator.comparingLong(TaxAmount::getId));

        return taxAmounts;
    }
}
