package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.ProductVariantDTO;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;

public class ProductVariantConverter {

    private ProductVariantConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static CpanelProductVariantRecord createDefaultVariant(Integer productId, String productName) {
        CpanelProductVariantRecord productVariantRecord = new CpanelProductVariantRecord();
        productVariantRecord.setProductid(productId);
        productVariantRecord.setName(productName);
        productVariantRecord.setStatus(1);
        return productVariantRecord;
    }

    public static ProductVariantDTO fromRecord(ProductVariantRecord r, Long stock, ProductType productType) {
        ProductVariantDTO result = new ProductVariantDTO();
        result.setId(r.getVariantid().longValue());
        result.setName(r.getName());
        result.setSku(r.getSku());
        result.setPrice(r.getPrice());
        result.setStock(stock != null ? stock.intValue() : null);
        result.setCreateDate(CommonUtils.timestampToZonedDateTime(r.getCreateDate()));
        result.setUpdateDate(CommonUtils.timestampToZonedDateTime(r.getUpdateDate()));
        result.setProduct(new IdNameDTO(r.getProductid().longValue(), r.getProductName()));

        if (productType.equals(ProductType.VARIANT)) {
            result.setVariantOption1(new IdNameDTO(r.getVariantoption1().longValue(), r.getProductFirstAttributeName()));
            result.setVariantValue1(new IdNameDTO(r.getVariantvalue1().longValue(), r.getProductFirstValueName()));
            if(r.getVariantoption2() != null) {
                result.setVariantOption2(new IdNameDTO(r.getVariantoption2().longValue(), r.getProductSecondAttributeName()));
                result.setVariantValue2(new IdNameDTO(r.getVariantvalue2().longValue(), r.getProductSecondValueName()));
            }
            result.setProductVariantStatus(ProductVariantStatus.get(r.getStatus()));
        }

        return result;
    }

    public static CpanelProductVariantRecord createProductVariantRecord(Long productId,
                                                                        CpanelProductAttributeValueRecord value1,
                                                                        CpanelProductAttributeValueRecord value2,
                                                                        Integer attributeId1,
                                                                        Integer attributeId2) {
        CpanelProductVariantRecord productVariantRecord = new CpanelProductVariantRecord();

        productVariantRecord.setProductid(productId.intValue());
        productVariantRecord.setName(value1.getName() + " / " + value2.getName());
        productVariantRecord.setVariantoption1(attributeId1);
        productVariantRecord.setVariantoption2(attributeId2);
        productVariantRecord.setVariantvalue1(value1.getValueid());
        productVariantRecord.setVariantvalue2(value2.getValueid());
        productVariantRecord.setPrice(0D);
        productVariantRecord.setStatus(ProductVariantStatus.ACTIVE.getId());

        return productVariantRecord;
    }

    public static CpanelProductVariantRecord createProductVariantRecord(Long productId,
                                                                        CpanelProductAttributeValueRecord value1,
                                                                        Integer attributeId1) {
        CpanelProductVariantRecord productVariantRecord = new CpanelProductVariantRecord();

        productVariantRecord.setProductid(productId.intValue());
        productVariantRecord.setName(value1.getName());
        productVariantRecord.setVariantoption1(attributeId1);
        productVariantRecord.setVariantvalue1(value1.getValueid());
        productVariantRecord.setPrice(0D);
        productVariantRecord.setStatus(ProductVariantStatus.ACTIVE.getId());

        return productVariantRecord;
    }

}
