package es.onebox.mgmt.products.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguages;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.dto.ProductLanguageDTO;
import es.onebox.mgmt.products.dto.ProductLanguagesDTO;
import es.onebox.mgmt.products.dto.UpdateProductLanguageDTO;
import es.onebox.mgmt.products.dto.UpdateProductLanguagesDTO;

import java.util.Map;

public class ProductLanguagesConverter {

    private ProductLanguagesConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static UpdateProductLanguages convert(UpdateProductLanguagesDTO updateProductLanguagesDTO, Map<String, Long> languagesMap) {
        UpdateProductLanguages updateProductLanguages = new UpdateProductLanguages();
        for (UpdateProductLanguageDTO updateProductLanguageDTO : updateProductLanguagesDTO) {
            UpdateProductLanguage updateProductLanguage = new UpdateProductLanguage();
            updateProductLanguage.setCode(updateProductLanguageDTO.getCode());
            updateProductLanguage.setIsDefault(updateProductLanguageDTO.getIsDefault());

            String locale = ConverterUtils.toLocale(updateProductLanguageDTO.getCode());
            if (!languagesMap.containsKey(locale)) {
                throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
            }
            updateProductLanguage.setLanguageId(languagesMap.get(locale));
            updateProductLanguages.add(updateProductLanguage);
        }
        return updateProductLanguages;
    }

    public static ProductLanguagesDTO toDto(ProductLanguages productLanguages) {
        if(productLanguages == null || productLanguages.isEmpty()) {
            return null;
        }
        ProductLanguagesDTO productLanguagesDTO = new ProductLanguagesDTO();
        for (ProductLanguage productLanguage : productLanguages) {
            ProductLanguageDTO productLanguageDTO = new ProductLanguageDTO();
            productLanguageDTO.setCode(productLanguage.getCode());
            productLanguageDTO.setProductId(productLanguage.getProductId());
            productLanguageDTO.setLanguageId(productLanguage.getLanguageId());
            productLanguageDTO.setIsDefault(productLanguage.getIsDefault());
            productLanguagesDTO.add(productLanguageDTO);
        }
        return productLanguagesDTO;
    }
}
