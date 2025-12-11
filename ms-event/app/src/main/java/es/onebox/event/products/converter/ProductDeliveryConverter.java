package es.onebox.event.products.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.products.dto.ProductDeliveryDTO;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;

public class ProductDeliveryConverter {

    private ProductDeliveryConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductDeliveryDTO toEntity(CpanelProductRecord productRecord) {
        ProductDeliveryDTO productDeliveryDTO = new ProductDeliveryDTO();
        if (productRecord.getDeliverytype() == null) {
            return null;
        }

        if (productRecord.getDeliverytype().equals(ProductDeliveryType.FIXED_DATES.getId())) {
            productDeliveryDTO.setDeliveryType(ProductDeliveryType.get(productRecord.getDeliverytype()));
            productDeliveryDTO.setDeliveryDateFrom(CommonUtils.timestampToZonedDateTime(productRecord.getDeliverydatefrom()));
            productDeliveryDTO.setDeliveryDateTo(CommonUtils.timestampToZonedDateTime(productRecord.getDeliverydateto()));

            return productDeliveryDTO;
        }

        if (productRecord.getDeliverystarttimeunit() == null && productRecord.getDeliverystarttimevalue() == null
                && productRecord.getDeliveryendtimeunit() == null && productRecord.getDeliveryendtimevalue() == null) {
            return null;
        }

        productDeliveryDTO.setDeliveryType(ProductDeliveryType.get(productRecord.getDeliverytype()));
        productDeliveryDTO.setStartTimeUnit(ProductDeliveryTimeUnitType.get(productRecord.getDeliverystarttimeunit()));
        productDeliveryDTO.setStartTimeValue(toValue(productRecord.getDeliverystarttimevalue()));
        productDeliveryDTO.setEndTimeUnit(ProductDeliveryTimeUnitType.get(productRecord.getDeliveryendtimeunit()));
        productDeliveryDTO.setEndTimeValue(toValue(productRecord.getDeliveryendtimevalue()));

        return productDeliveryDTO;
    }

    private static Long toValue(Integer value) {
        return value != null ? value.longValue() : null;
    }

}
