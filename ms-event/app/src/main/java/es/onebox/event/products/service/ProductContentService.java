package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductContentsConverter;
import es.onebox.event.products.dao.ProductAttributeContentsCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.dao.couch.ProductContentDocumentValue;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductLiteralDTO;
import es.onebox.event.products.dto.ProductLiteralsDTO;
import es.onebox.event.products.dto.ProductValueLiteralDTO;
import es.onebox.event.products.dto.ProductValueLiteralsDTO;
import es.onebox.event.products.lock.HazelcastLockRepository;
import es.onebox.event.tags.utils.LanguageUtils;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProductContentService {

    private final ProductAttributeContentsCouchDao productAttributeContentsCouchDao;
    private final ProductDao productDao;
    private final ProductLanguageDao productLanguageDao;
    private final RefreshDataService refreshDataService;
    private final HazelcastLockRepository hazelcastLockRepository;

    @Autowired
    public ProductContentService(ProductAttributeContentsCouchDao productAttributeContentsCouchDao,
                                 ProductDao productDao,
                                 ProductLanguageDao productLanguageDao,
                                 RefreshDataService refreshDataService,
                                 HazelcastLockRepository hazelcastLockRepository) {
        this.productAttributeContentsCouchDao = productAttributeContentsCouchDao;
        this.productDao = productDao;
        this.productLanguageDao = productLanguageDao;
        this.refreshDataService = refreshDataService;
        this.hazelcastLockRepository = hazelcastLockRepository;
    }

    @MySQLRead
    public ProductLiteralsDTO getProductAttributeLiterals(Long productId, Long attributeId, String language) {
        validations(productId);

        ProductContentDocument productCatalogDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productCatalogDocument == null) {
            return new ProductLiteralsDTO();
        }
        return ProductContentsConverter.toAttributesDto(productCatalogDocument, LanguageUtils.toCpanelIdiomaCode(language));
    }

    @MySQLWrite
    public void createOrUpdateProductAttributeLiterals(Long productId, Long attributeId, ProductLiteralsDTO productLiteralsDTO) {
        validations(productId);
        try {
            hazelcastLockRepository.lockedExecutionProductContents(() -> changeProductAttributeContents(productId, attributeId, productLiteralsDTO), productId, attributeId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private ProductContentDocument changeProductAttributeContents(Long productId, Long attributeId, ProductLiteralsDTO productLiteralsDTO) {
        ProductContentDocument productContentDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productContentDocument == null) {
            productContentDocument = new ProductContentDocument(productId, attributeId);
        }
        if (productContentDocument.getLanguageElements() == null) {
            productContentDocument.setLanguageElements(new HashMap<>());
        }
        for (ProductLiteralDTO productLiteralDTO : productLiteralsDTO) {
            if (!productContentDocument.getLanguageElements().containsKey(productLiteralDTO.getLanguageCode())) {
                productContentDocument.getLanguageElements().put(productLiteralDTO.getLanguageCode(), new ProductCommunicationElement());
                productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).setTexts(new ArrayList<>());
                productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).setImages(new ArrayList<>());
            }
            ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
            productCommunicationElementDetail.setType(productLiteralDTO.getKey());
            productCommunicationElementDetail.setValue(productLiteralDTO.getValue());
            if (productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().stream().anyMatch(el -> el.getType().equals(productLiteralDTO.getKey()))) {
                ProductCommunicationElementDetail currentProductCommunicationElementDetail = productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().stream().filter(el -> el.getType().equals(productLiteralDTO.getKey())).findFirst().get();
                productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().remove(currentProductCommunicationElementDetail);
            }
            productContentDocument.getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().add(productCommunicationElementDetail);

        }
        String key = productAttributeContentsCouchDao.getKey(productId, attributeId);
        productAttributeContentsCouchDao.upsert(key, productContentDocument);

        postUpdateProduct(productId);

        return productContentDocument;
    }

    @MySQLRead
    public ProductLiteralsDTO getProductValueLiterals(Long productId, Long attributeId, Long valueId, String language) {
        validations(productId);

        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            return null;
        }
        ProductContentDocument productCatalogDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productCatalogDocument == null) {
            return null;
        }
        return ProductContentsConverter.toValuesDto(productCatalogDocument, LanguageUtils.toCpanelIdiomaCode(language), valueId);
    }

    @MySQLRead
    public ProductValueLiteralsDTO getProductBulkValueLiterals(Long productId, Long attributeId, String language) {
        validations(productId);

        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            return null;
        }
        ProductContentDocument productCatalogDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productCatalogDocument == null) {
            return null;
        }
        return ProductContentsConverter.toValuesDto(productCatalogDocument, language);
    }

    @MySQLWrite
    public void createOrUpdateProductValueLiterals(Long productId, Long attributeId, Long valueId, ProductLiteralsDTO productLiteralsDTO) {
        validations(productId);

        try {
            hazelcastLockRepository.lockedExecutionProductContents(() -> changeProductAttributeValueContents(productId, attributeId, valueId, productLiteralsDTO), productId, attributeId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private ProductContentDocument changeProductAttributeValueContents(Long productId, Long attributeId, Long valueId, ProductLiteralsDTO productLiteralsDTO) {
        ProductContentDocument productContentDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productContentDocument == null) {
            productContentDocument = new ProductContentDocument(productId, attributeId);
        }
        if (productContentDocument.getValues() == null) {
            productContentDocument.setValues(new HashMap<>());
        }
        if (!productContentDocument.getValues().containsKey(valueId.toString())) {
            productContentDocument.getValues().put(valueId.toString(), new ProductContentDocumentValue());
            productContentDocument.getValues().get(valueId.toString()).setLanguageElements(new HashMap<>());
        }

        for (ProductLiteralDTO productLiteralDTO : productLiteralsDTO) {
            if (!productContentDocument.getValues().get(valueId.toString()).getLanguageElements().containsKey(productLiteralDTO.getLanguageCode())) {
                productContentDocument.getValues().get(valueId.toString()).getLanguageElements().put(productLiteralDTO.getLanguageCode(), new ProductCommunicationElement());
                productContentDocument.getValues().get(valueId.toString()).getLanguageElements().get(productLiteralDTO.getLanguageCode()).setTexts(new ArrayList<>());
            }
            ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
            productCommunicationElementDetail.setType(productLiteralDTO.getKey());
            productCommunicationElementDetail.setValue(productLiteralDTO.getValue());
            if (productContentDocument.getValues().get(valueId.toString()).getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().stream().anyMatch(el -> el.getType().equals(productLiteralDTO.getKey()))) {
                ProductCommunicationElementDetail currentProductCommunicationElementDetail = productContentDocument.getValues().get(valueId.toString()).getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().stream().filter(el -> el.getType().equals(productLiteralDTO.getKey())).findFirst().get();
                productContentDocument.getValues().get(valueId.toString()).getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().remove(currentProductCommunicationElementDetail);
            }
            productContentDocument.getValues().get(valueId.toString()).getLanguageElements().get(productLiteralDTO.getLanguageCode()).getTexts().add(productCommunicationElementDetail);
        }
        productAttributeContentsCouchDao.upsert(productId + "_" + attributeId, productContentDocument);
        postUpdateProduct(productId);

        return productContentDocument;
    }

    @MySQLWrite
    public void createOrUpdateProductBulkValueLiterals(Long productId, Long attributeId, ProductValueLiteralsDTO productValueLiteralsDTO) {
        validations(productId);

        try {
            hazelcastLockRepository.lockedExecutionProductContents(() -> changeProductAttributeValueContents(productId, attributeId, productValueLiteralsDTO), productId, attributeId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private ProductContentDocument changeProductAttributeValueContents(Long productId, Long attributeId, ProductValueLiteralsDTO productValueLiteralsDTO) {
        ProductContentDocument productContentDocument = productAttributeContentsCouchDao.get(productId, attributeId);
        if (productContentDocument == null) {
            productContentDocument = new ProductContentDocument(productId, attributeId);
        }
        if (productContentDocument.getValues() == null) {
            productContentDocument.setValues(new HashMap<>());
        }

        for (ProductValueLiteralDTO productValueLiteralDTO : productValueLiteralsDTO) {
            if (!productContentDocument.getValues().containsKey(productValueLiteralDTO.getValueId().toString())) {
                productContentDocument.getValues().put(productValueLiteralDTO.getValueId().toString(), new ProductContentDocumentValue());
                productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).setLanguageElements(new HashMap<>());
            }
            if (!productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().containsKey(productValueLiteralDTO.getLanguageCode())) {
                productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().put(productValueLiteralDTO.getLanguageCode(), new ProductCommunicationElement());
                productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().get(productValueLiteralDTO.getLanguageCode()).setTexts(new ArrayList<>());
            }
            ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
            productCommunicationElementDetail.setType(productValueLiteralDTO.getKey());
            productCommunicationElementDetail.setValue(productValueLiteralDTO.getValue());
            if (productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().get(productValueLiteralDTO.getLanguageCode()).getTexts().stream().anyMatch(el -> el.getType().equals(productValueLiteralDTO.getKey()))) {
                ProductCommunicationElementDetail currentProductCommunicationElementDetail = productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().get(productValueLiteralDTO.getLanguageCode()).getTexts().stream().filter(el -> el.getType().equals(productValueLiteralDTO.getKey())).findFirst().get();
                productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().get(productValueLiteralDTO.getLanguageCode()).getTexts().remove(currentProductCommunicationElementDetail);
            }
            productContentDocument.getValues().get(productValueLiteralDTO.getValueId().toString()).getLanguageElements().get(productValueLiteralDTO.getLanguageCode()).getTexts().add(productCommunicationElementDetail);
        }
        String key = productAttributeContentsCouchDao.getKey(productId, attributeId);
        productAttributeContentsCouchDao.upsert(key, productContentDocument);
        postUpdateProduct(productId);

        return productContentDocument;
    }

    private void validations(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductLanguageRecord> productLanguages = productLanguageDao.findByProductId(productId);
        if (productLanguages == null || productLanguages.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }

}

