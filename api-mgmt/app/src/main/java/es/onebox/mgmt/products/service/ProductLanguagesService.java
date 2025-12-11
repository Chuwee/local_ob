package es.onebox.mgmt.products.service;

import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguages;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.products.converter.ProductLanguagesConverter;
import es.onebox.mgmt.products.dto.ProductLanguagesDTO;
import es.onebox.mgmt.products.dto.UpdateProductLanguagesDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductLanguagesService {
    private final ProductsRepository productsRepository;
    private final MasterdataRepository masterdataRepository;
    private final ValidationService validationService;

    @Autowired
    public ProductLanguagesService(ProductsRepository productsRepository,
                                   MasterdataRepository masterdataRepository, ValidationService validationService) {
        this.productsRepository = productsRepository;
        this.masterdataRepository = masterdataRepository;
        this.validationService = validationService;
    }

    public ProductLanguagesDTO getProductLanguages(Long productId) {
        validationService.getAndCheckProduct(productId);
        ProductLanguages productLanguages = productsRepository.getProductLanguages(productId);

        return ProductLanguagesConverter.toDto(productLanguages);
    }

    public ProductLanguagesDTO updateProductLanguages(Long productId, UpdateProductLanguagesDTO updateProductLanguagesDTO) {
        validationService.getAndCheckProduct(productId);

        Map<String, Long> languagesMap = masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getCode, MasterdataValue::getId));
        UpdateProductLanguages updateProductLanguages = ProductLanguagesConverter.convert(updateProductLanguagesDTO, languagesMap);
        ProductLanguages productLanguages = productsRepository.updateProductLanguages(productId, updateProductLanguages);
        return ProductLanguagesConverter.toDto(productLanguages);
    }

}
