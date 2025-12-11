package es.onebox.event.products.helper;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductAttributeContentsCouchDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.dao.couch.ProductContentDocumentValue;
import es.onebox.event.products.domain.ProductLanguageRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProductLanguageHelper {
    private final ProductAttributeContentsCouchDao productAttributeContentsCouchDao;
    private final ProductLanguageDao productLanguageDao;

    @Autowired
    public ProductLanguageHelper(ProductAttributeContentsCouchDao productAttributeContentsCouchDao, ProductLanguageDao productLanguageDao) {
        this.productAttributeContentsCouchDao = productAttributeContentsCouchDao;
        this.productLanguageDao = productLanguageDao;
    }

    public void modifyProductAttributeContents(Long productId, Long attributeId, String attributeName, boolean isDeletion) {
        List<ProductLanguageRecord> productLanguages = productLanguageDao.findByProductId(productId);
        if (productLanguages == null || productLanguages.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }

        productLanguages.forEach(productLanguage -> {
            String languageCode = productLanguage.getCode();
            String documentId = productAttributeContentsCouchDao.getKey(productId, attributeId);

            ProductContentDocument productContentMap =
                    Optional.ofNullable(productAttributeContentsCouchDao.get(documentId))
                            .orElse(new ProductContentDocument(productId, attributeId));
            if(productContentMap.getLanguageElements() == null) {
                productContentMap.setLanguageElements(new HashMap<>());
            }
            if(!productContentMap.getLanguageElements().containsKey(languageCode)) {
                productContentMap.getLanguageElements().put(languageCode, new ProductCommunicationElement());
                productContentMap.getLanguageElements().get(languageCode).setTexts(new ArrayList<>());
            }
            if(CollectionUtils.isNotEmpty(productContentMap.getLanguageElements().get(languageCode).getTexts())) {
                if (productContentMap.getLanguageElements().get(languageCode).getTexts().stream().noneMatch(el -> el.getValue() != null && el.getType().equals(attributeName))) {
                    if (!isDeletion) {
                        ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
                        productCommunicationElementDetail.setType("ATTRIBUTE_NAME");
                        productCommunicationElementDetail.setValue(attributeName);
                        productContentMap.getLanguageElements().get(languageCode).getTexts().add(productCommunicationElementDetail);
                    }
                } else {
                    ProductCommunicationElementDetail currentProductCommunicationElementDetail = productContentMap.getLanguageElements().get(languageCode).getTexts().stream().filter(el -> el.getValue() != null && el.getValue().equals(attributeName)).findFirst().get();
                    productContentMap.getLanguageElements().get(languageCode).getTexts().remove(currentProductCommunicationElementDetail);
                }
            }
            productAttributeContentsCouchDao.upsert(documentId, productContentMap);
        });

    }

    public void modifyProductValueContents(Long productId, Long attributeId, Long valueId, String valueName, boolean isDeletion) {
        List<ProductLanguageRecord> productLanguages = productLanguageDao.findByProductId(productId);
        if (productLanguages == null || productLanguages.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }

        productLanguages.forEach(productLanguage -> {
            String languageCode = productLanguage.getCode();
            String documentId = productAttributeContentsCouchDao.getKey(productId, attributeId);

            ProductContentDocument productContentMap =
                    Optional.ofNullable(productAttributeContentsCouchDao.get(documentId))
                            .orElse(new ProductContentDocument(productId, attributeId));
            if(productContentMap.getValues() == null) {
                productContentMap.setValues(new HashMap<>());
            }
            if(!productContentMap.getValues().containsKey(valueId.toString())) {
                productContentMap.getValues().put(valueId.toString(), new ProductContentDocumentValue());
            }
            if(productContentMap.getValues().get(valueId.toString()).getLanguageElements() == null) {
                productContentMap.getValues().get(valueId.toString()).setLanguageElements(new HashMap<>());
            }
            if(!productContentMap.getValues().get(valueId.toString()).getLanguageElements().containsKey(languageCode)) {
                productContentMap.getValues().get(valueId.toString()).getLanguageElements().put(languageCode, new ProductCommunicationElement());
                productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).setTexts(new ArrayList<>());
            }
            if(CollectionUtils.isNotEmpty(productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).getTexts())) {
                if (productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).getTexts().stream().noneMatch(el -> el.getValue() != null && el.getValue().equals(valueName))) {
                    if (!isDeletion) {
                        ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
                        productCommunicationElementDetail.setType("VALUE_NAME");
                        productCommunicationElementDetail.setValue(valueName);
                        productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).getTexts().add(productCommunicationElementDetail);
                    }
                } else {
                    Optional<ProductCommunicationElementDetail> currentProductCommunicationElementDetailOpt = productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).getTexts().stream().filter(el -> el.getValue() != null && el.getValue().equals(valueName)).findFirst();
                    if(currentProductCommunicationElementDetailOpt.isPresent()) {
                        productContentMap.getValues().get(valueId.toString()).getLanguageElements().get(languageCode).getTexts().remove(currentProductCommunicationElementDetailOpt.get());
                    }
                }
            }
            productAttributeContentsCouchDao.upsert(documentId, productContentMap);
        });

    }
}
