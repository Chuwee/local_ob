package es.onebox.event.products.converter;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.products.dto.ProductLanguageDTO;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductLanguagesDTO;

import java.util.List;

public class ProductLanguagesConverter {

    private ProductLanguagesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductLanguagesDTO toEntity(List<ProductLanguageRecord> productLanguageRecordList) {
        ProductLanguagesDTO productLanguages = new ProductLanguagesDTO();

        for(ProductLanguageRecord productLanguageRecord : productLanguageRecordList) {
            productLanguages.add(toEntity(productLanguageRecord));
        }
        return productLanguages;
    }

    public static ProductLanguageDTO toEntity(ProductLanguageRecord productLanguageRecord) {
        ProductLanguageDTO productLanguage = new ProductLanguageDTO();
        productLanguage.setLanguageId(productLanguageRecord.getLanguageid().longValue());
        productLanguage.setCode(toCode(productLanguageRecord.getCode()));
        productLanguage.setProductId(productLanguageRecord.getProductid().longValue());
        productLanguage.setDefault(ConverterUtils.isByteAsATrue(productLanguageRecord.getDefaultlanguage()));
        return productLanguage;
    }

    public static String toCode(String locale) {
        return locale.replace("_", "-");
    }

}
