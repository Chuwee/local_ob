package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductCommunicationElementsConverter;
import es.onebox.mgmt.products.dto.CreateProductCommunicationElementImageDTO;
import es.onebox.mgmt.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.CreateProductContentTextListDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.mgmt.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.mgmt.products.dto.ProductContentTextDTO;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsTextsType;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductCommunicationElementsService {
    private final ProductsRepository productsRepository;
    private final MasterdataRepository masterdataRepository;
    private final ValidationService validationService;

    public ProductCommunicationElementsService(ProductsRepository productsRepository,
                                               MasterdataRepository masterdataRepository,
                                               ValidationService validationService) {
        this.productsRepository = productsRepository;
        this.masterdataRepository = masterdataRepository;
        this.validationService = validationService;
    }

    private void checkLanguages(Long productId, String language, Map<String, Long> languagesMap) {
        if (language != null) {
            Locale locale = Locale.forLanguageTag(language);
            if (!languagesMap.containsKey(locale.toString())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG);
            }
        }

        ProductLanguages productLanguages = productsRepository.getProductLanguages(productId);
        if (productLanguages == null || productLanguages.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_LANGUAGE_NOT_RELATED);
        }

        if (language != null && productLanguages.stream().noneMatch(pl -> pl.getCode().equals(language))) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_LANGUAGE_NOT_RELATED);
        }
    }

    public ProductCommunicationElementsTextsDTO createProductCommunicationElementsText(Long productId, CreateProductContentTextListDTO createProductContentTextListDTO) {
        Map<String, Long> languagesMap = masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getCode, MasterdataValue::getId));
        validationService.getAndCheckProduct(productId);

        for (ProductContentTextDTO<ProductCommunicationElementsTextsType> productContentTextDTO : createProductContentTextListDTO.getTexts()) {
            checkLanguages(productId, productContentTextDTO.getLanguage(), languagesMap);
        }

        CreateProductCommunicationElementsText createProductCommunicationElementsText = ProductCommunicationElementsConverter.convertTexts(createProductContentTextListDTO, languagesMap);
        ProductCommunicationElementsText productCommunicationElementsText = productsRepository.createProductCommunicationElementsText(productId, createProductCommunicationElementsText);
        return ProductCommunicationElementsConverter.toDtoTexts(productCommunicationElementsText);
    }

    public ProductCommunicationElementsTextsDTO getProductCommunicationElementsText(Long productId) {
        validationService.getAndCheckProduct(productId);
        ProductCommunicationElementsText productCommunicationElementsText = productsRepository.getProductCommunicationElementsText(productId);
        return ProductCommunicationElementsConverter.toDtoTexts(productCommunicationElementsText);
    }


    public void createProductCommunicationElementsImages(Long productId, CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO) {
        Map<String, Long> languagesMap = masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getCode, MasterdataValue::getId));
        validationService.getAndCheckProduct(productId);

        for (CreateProductCommunicationElementImageDTO<ProductCommunicationElementsImagesType> createProductCommunicationElementImageDTO : createProductCommunicationElementsImagesDTO) {
            checkLanguages(productId, createProductCommunicationElementImageDTO.getLanguage(), languagesMap);
        }
        List<CreateProductCommunicationElementImage> productCommunicationElementImageList = ProductCommunicationElementsConverter.convertImages(createProductCommunicationElementsImagesDTO, languagesMap);
        productsRepository.createProductCommunicationElementsImage(productId, productCommunicationElementImageList);
    }

    public ProductCommunicationElementsImagesDTO<ProductCommunicationElementsImagesType> getProductCommunicationElementsImages(Long productId) {
        validationService.getAndCheckProduct(productId);
        checkLanguages(productId, null, null);
        ProductCommunicationElementsImage productCommunicationElementsImage = productsRepository.getProductCommunicationElementsImages(productId);
        return ProductCommunicationElementsConverter.toDtoImages(productCommunicationElementsImage);
    }

    public void deleteProductCommunicationElementsImages(Long productId, String language, ProductCommunicationElementsImagesType type, Integer position) {
        Map<String, Long> languagesMap = masterdataRepository.getLanguages(null, null).stream()
                .collect(Collectors.toMap(MasterdataValue::getCode, MasterdataValue::getId));
        validationService.getAndCheckProduct(productId);
        checkLanguages(productId, language, languagesMap);

        productsRepository.deleteProductCommunicationElementImage(productId, ConverterUtils.toLocale(language), type, position);

    }
}
