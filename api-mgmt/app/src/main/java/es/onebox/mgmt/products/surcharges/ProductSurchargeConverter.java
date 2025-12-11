package es.onebox.mgmt.products.surcharges;

import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.products.surcharges.dto.ProductSurchargeDTO;
import es.onebox.mgmt.products.surcharges.enums.ProductSurchargeType;
import es.onebox.mgmt.datasources.common.dto.Range;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSurcharge;

import java.util.List;
import java.util.stream.Collectors;

public class ProductSurchargeConverter {

    private ProductSurchargeConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductSurcharge toProductSurcharge(ProductSurchargeDTO productSurchargeDTO, List<Currency> currencies, Currency defaultCurrency) {

        ProductSurcharge productSurcharge = new ProductSurcharge();

        productSurcharge.setType(ProductSurchargeType.valueOf(productSurchargeDTO.getType().name()));
        List<Range> targetRanges = productSurchargeDTO.getRanges().stream()
                .map(range -> SurchargeConverter.fromDTO(range, currencies, defaultCurrency))
                .collect(Collectors.toList());

        productSurcharge.setRanges(targetRanges);

        return productSurcharge;
    }

    public static List<ProductSurchargeDTO> toProductSurchargeDTO(List<ProductSurcharge> productSurcharges,
                                                                  List<Currency> currencies, Currency defaultCurrency) {
        return productSurcharges.stream()
                .map(ps -> toRangeDTO(ps, new ProductSurchargeDTO(), currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    private static ProductSurchargeDTO toRangeDTO(ProductSurcharge productSurcharge, ProductSurchargeDTO productSurchargeDTO,
                                                  List<Currency> currencies, Currency defaultCurrency) {
        List<RangeDTO> ranges = productSurcharge.getRanges().stream()
                .map(range -> SurchargeConverter.toSurchargeRangeDTO(range, currencies, defaultCurrency))
                .collect(Collectors.toList());

        productSurchargeDTO.setRanges(ranges);
        productSurchargeDTO.setType(ProductSurchargeType.valueOf(productSurcharge.getType().name()));

        return productSurchargeDTO;
    }
}
