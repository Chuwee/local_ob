package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductAttributeConverter;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductAttributeValueDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dto.CreateProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributesDTO;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.event.products.dto.UpdateProductAttributeDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.products.helper.ProductLanguageHelper;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeService {
    private final ProductAttributeDao productAttributeDao;
    private final ProductHelper productHelper;
    private final ProductAttributeValueDao productAttributeValueDao;
    private final ProductVariantDao productVariantDao;
    private final ProductDao productDao;
    private final ProductLanguageHelper productLanguageHelper;
    private final RefreshDataService refreshDataService;

    public ProductAttributeService(ProductAttributeDao productAttributeDao, ProductDao productDao,
                                   RefreshDataService refreshDataService, ProductHelper productHelper,
                                   ProductVariantDao productVariantDao, ProductAttributeValueDao productAttributeValueDao,
                                   ProductLanguageHelper productLanguageHelper) {
        this.productAttributeDao = productAttributeDao;
        this.productDao = productDao;
        this.refreshDataService = refreshDataService;
        this.productHelper = productHelper;
        this.productVariantDao = productVariantDao;
        this.productAttributeValueDao = productAttributeValueDao;
        this.productLanguageHelper = productLanguageHelper;
    }

    @MySQLWrite
    public Long createProductAttribute(Long productId, CreateProductAttributeDTO createProductAttributeDTO) {
        checkProduct(productId);

        if (createProductAttributeDTO.getPosition() == null) {
            findDefaultPosition(productId, createProductAttributeDTO);
        }

        CpanelProductAttributeRecord productAttributeRecord =
                ProductAttributeConverter.toRecord(productId, createProductAttributeDTO);

        if (productAttributeDao.getTotalAttributes(productId) >= 2) {
            throw new OneboxRestException(MsEventErrorCode.MAX_ATTRIBUTES_REACHED);
        }

        try {
            CpanelProductAttributeRecord newProductAttribute = productAttributeDao.insert(productAttributeRecord);
            productLanguageHelper.modifyProductAttributeContents(productId, newProductAttribute.getAttributeid().longValue(), createProductAttributeDTO.getName(), false);
            postUpdateProduct(productId);

            return newProductAttribute.getAttributeid().longValue();
        } catch (DuplicateKeyException e) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_ATTRIBUTE_NAME_DUPLICATED);
        }
    }

    private void findDefaultPosition(Long productId, CreateProductAttributeDTO createProductAttributeDTO) {
        List<CpanelProductAttributeRecord> productAttributeRecords = productAttributeDao.findByProductId(productId);
        if (productAttributeRecords != null && !productAttributeRecords.isEmpty()) {
            CpanelProductAttributeRecord maxPositionAttribute = Collections.max(productAttributeRecords, Comparator.comparing(s -> s.getPosition()));
            createProductAttributeDTO.setPosition(maxPositionAttribute.getPosition() + 1);
        } else {
            createProductAttributeDTO.setPosition(0);
        }
    }

    @MySQLWrite
    public void updateProductAttribute(Long productId, Long attributeId, UpdateProductAttributeDTO updateProductAttributeDTO) {
        //Check product exists
        checkProduct(productId);

        //Check product attribute exists
        CpanelProductAttributeRecord productAttributeRecord = getAndCheckProductAttribute(attributeId);

        checkProductSales(productId);

        if (updateProductAttributeDTO.getName() != null) {
            productAttributeRecord.setName(updateProductAttributeDTO.getName());
        }
        if (updateProductAttributeDTO.getPosition() != null) {
            if (updateProductAttributeDTO.getPosition() < 0) {
                throw new OneboxRestException(MsEventErrorCode.POSITION_MUST_BE_POSITIVE);
            }
            productAttributeRecord.setPosition(updateProductAttributeDTO.getPosition());
        }
        productAttributeDao.update(productAttributeRecord);

        if (updateProductAttributeDTO.getName() != null) {
            productLanguageHelper.modifyProductAttributeContents(productId, attributeId, productAttributeRecord.getName(), true);
            productLanguageHelper.modifyProductAttributeContents(productId, attributeId, updateProductAttributeDTO.getName(), false);
        }
        postUpdateProduct(productId);
    }

    @MySQLRead
    public ProductAttributesDTO getProductAttributes(Long productId) {
        checkProduct(productId);
        List<CpanelProductAttributeRecord> productAttributeRecords = productAttributeDao.findByProductId(productId);

        return ProductAttributeConverter.toEntity(productAttributeRecords);
    }

    @MySQLRead
    public ProductAttributeDTO getProductAttribute(Long productId, Long attributeId) {
        //Check product exists
        checkProduct(productId);

        //Check product attribute exists
        CpanelProductAttributeRecord productAttributeRecord = getAndCheckProductAttribute(attributeId);

        return ProductAttributeConverter.fromRecord(productAttributeRecord);
    }

    @MySQLWrite
    public void deleteProductAttribute(Long productId, Long attributeId) {
        //Check product exists
        checkProduct(productId);

        checkProductSales(productId);

        CpanelProductAttributeRecord cpanelProductAttributeRecord = productAttributeDao.findById(attributeId.intValue());

        productAttributeValueDao.getProductAttributeValues(cpanelProductAttributeRecord.getAttributeid().longValue(),
                        new SearchProductAttributeValueFilterDTO())
                .forEach(productAttributeValueDao::delete);
        productAttributeDao.delete(cpanelProductAttributeRecord);
        productLanguageHelper.modifyProductAttributeContents(productId, attributeId, cpanelProductAttributeRecord.getName(), true);

        postUpdateProduct(productId);
    }

    private void checkProduct(Long productId) {
        CpanelProductRecord product = productDao.findById(productId.intValue());
        if (product == null || product.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private CpanelProductAttributeRecord getAndCheckProductAttribute(Long attributeId) {
        CpanelProductAttributeRecord productAttributeRecord = productAttributeDao.findById(attributeId.intValue());
        if (productAttributeRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
        }

        return productAttributeRecord;
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }

    private void checkProductSales(Long productId) {
        CpanelProductRecord productRecord = productDao.findById(productId.intValue());
        if (productRecord.getState().equals(ProductState.ACTIVE.getId())) {
            List<CpanelProductVariantRecord> productVariants = productVariantDao.getProductVariantsByProductId(productId);
            if (productVariants != null && !productVariants.isEmpty()) {
                List<Integer> variantIds = productVariants.stream().map(CpanelProductVariantRecord::getVariantid).collect(Collectors.toList());
                List<Long> variantIdsLng = variantIds.stream().map(Long::valueOf).collect(Collectors.toList());
                boolean hasSales = productHelper.checkProductOrVariantSales(variantIdsLng);
                if (hasSales) {
                    throw new OneboxRestException(MsEventErrorCode.PRODUCT_HAS_SALES);
                }
            }
        }
    }

}
