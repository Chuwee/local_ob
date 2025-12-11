package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductAttributeValueConverter;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductAttributeValueDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.CreateProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValuesDTO;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.event.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.event.products.dto.UpdateProductAttributeValueDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.products.helper.ProductLanguageHelper;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeValueService {
    private final ProductAttributeValueDao productAttributeValueDao;
    private final ProductVariantDao productVariantDao;
    private final ProductHelper productHelper;
    private final ProductDao productDao;
    private final ProductLanguageHelper productLanguageHelper;
    private final ProductAttributeDao productAttributeDao;
    private final RefreshDataService refreshDataService;
    private final ProductVariantStockCouchDao productVariantStockCouchDao;

    public ProductAttributeValueService(ProductAttributeValueDao productAttributeValueDao, ProductVariantDao productVariantDao,
                                        ProductHelper productHelper,
                                        ProductDao productDao, ProductAttributeDao productAttributeDao,
                                        RefreshDataService refreshDataService,
                                        ProductLanguageHelper productLanguageHelper,
                                        ProductVariantStockCouchDao productVariantStockCouchDao) {
        this.productAttributeValueDao = productAttributeValueDao;
        this.productVariantDao = productVariantDao;
        this.productHelper = productHelper;
        this.productDao = productDao;
        this.productAttributeDao = productAttributeDao;
        this.refreshDataService = refreshDataService;
        this.productLanguageHelper = productLanguageHelper;
        this.productVariantStockCouchDao = productVariantStockCouchDao;
    }

    @MySQLWrite
    public Long createProductAttributeValue(Long productId, Long attributeId,
                                            CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        CpanelProductRecord product = checkProduct(productId);
        checkAttribute(attributeId);

        if (createProductAttributeValueDTO.getPosition() == null) {
            findDefaultPosition(attributeId, createProductAttributeValueDTO);
        }

        CpanelProductAttributeValueRecord productAttributeValueRecord =
                ProductAttributeValueConverter.toRecord(attributeId, createProductAttributeValueDTO);

        Long currentVariants = productVariantDao.getCountProductVariantsByProductId(productId);
        if (currentVariants > 25) {
            throw new OneboxRestException(MsEventErrorCode.MAX_ATTRIBUTE_VALUES_BY_PRODUCT_REACHED);
        }

        //Product has one or two attributes
        List<CpanelProductAttributeRecord> productAttributeRecords = productAttributeDao.findByProductId(productId);
        List<CpanelProductAttributeRecord> otherProductAttribute = productAttributeRecords.stream()
                .filter(pa -> !pa.getAttributeid().equals(attributeId.intValue()))
                .collect(Collectors.toList());

        //Check variants limit if there are two attributes
        if (!otherProductAttribute.isEmpty()) {
            Integer otherAttributeId = otherProductAttribute.get(0).getAttributeid();
            Long otherAttributeValues = productAttributeValueDao.getTotalAttributeValues(productId, otherAttributeId.longValue());
            if (currentVariants + otherAttributeValues > 25) {
                throw new OneboxRestException(MsEventErrorCode.MAX_ATTRIBUTE_VALUES_BY_PRODUCT_REACHED);
            }
        }

        //Get variants
        SearchProductVariantsFilterDTO searchProductVariantsFilterDTO = new SearchProductVariantsFilterDTO();
        searchProductVariantsFilterDTO.setOffset(0L);
        searchProductVariantsFilterDTO.setLimit(1L);
        List<ProductVariantRecord> productVariants = productVariantDao.searchProductVariants(productId, searchProductVariantsFilterDTO);

        try {
            CpanelProductAttributeValueRecord newProductAttributeValue = productAttributeValueDao.insert(productAttributeValueRecord);
            productLanguageHelper.modifyProductValueContents(productId, attributeId, newProductAttributeValue.getValueid().longValue(), createProductAttributeValueDTO.getName(), false);

            //There are two attributes
            if (!otherProductAttribute.isEmpty() && productVariants != null && !productVariants.isEmpty()) {
                ProductVariantRecord productVariantRecord = productVariants.get(0);

                Integer existingOption1 = productVariantRecord.getVariantoption1();
                Integer otherAttributeId = otherProductAttribute.get(0).getAttributeid();
                List<CpanelProductAttributeValueRecord> otherAttributeValues = productAttributeValueDao.getProductAttributeValues(otherAttributeId.longValue(), new SearchProductAttributeValueFilterDTO());

                for (CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord : otherAttributeValues) {
                    CpanelProductVariantRecord cpanelProductVariantRecord = new CpanelProductVariantRecord();

                    cpanelProductVariantRecord.setProductid(productId.intValue());
                    cpanelProductVariantRecord.setStatus(ProductVariantStatus.ACTIVE.getId());
                    cpanelProductVariantRecord.setPrice(0d);

                    String newName;
                    if (existingOption1.equals(attributeId.intValue())) {
                        cpanelProductVariantRecord.setVariantoption1(attributeId.intValue());
                        cpanelProductVariantRecord.setVariantvalue1(newProductAttributeValue.getValueid());
                        cpanelProductVariantRecord.setVariantoption2(otherAttributeId);
                        cpanelProductVariantRecord.setVariantvalue2(cpanelProductAttributeValueRecord.getValueid());
                        newName = newProductAttributeValue.getName() + " / " + cpanelProductAttributeValueRecord.getName();
                    } else {
                        cpanelProductVariantRecord.setVariantoption1(otherAttributeId);
                        cpanelProductVariantRecord.setVariantvalue1(cpanelProductAttributeValueRecord.getValueid());
                        cpanelProductVariantRecord.setVariantoption2(attributeId.intValue());
                        cpanelProductVariantRecord.setVariantvalue2(newProductAttributeValue.getValueid());
                        newName = cpanelProductAttributeValueRecord.getName() + " / " + newProductAttributeValue.getName();
                    }

                    cpanelProductVariantRecord.setName(newName);
                    CpanelProductVariantRecord variant = productVariantDao.insert(cpanelProductVariantRecord);

                    if (product.getStocktype().equals(ProductStockType.BOUNDED.getId())) {
                        productVariantStockCouchDao.insert(productId, variant.getVariantid().longValue());
                    }
                }
                //There is only one attribute
            } else if (productVariants != null && !productVariants.isEmpty()) {
                CpanelProductVariantRecord cpanelProductVariantRecord = new CpanelProductVariantRecord();

                cpanelProductVariantRecord.setProductid(productId.intValue());
                cpanelProductVariantRecord.setStatus(ProductVariantStatus.ACTIVE.getId());
                cpanelProductVariantRecord.setPrice(0d);

                cpanelProductVariantRecord.setVariantoption1(attributeId.intValue());
                cpanelProductVariantRecord.setVariantvalue1(newProductAttributeValue.getValueid());
                cpanelProductVariantRecord.setName(createProductAttributeValueDTO.getName());

                CpanelProductVariantRecord variant = productVariantDao.insert(cpanelProductVariantRecord);

                if (product.getStocktype().equals(ProductStockType.BOUNDED.getId())) {
                    productVariantStockCouchDao.insert(productId, variant.getVariantid().longValue());
                }
            }

            postUpdateProduct(productId);

            return newProductAttributeValue.getValueid().longValue();
        } catch (DuplicateKeyException e) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_ATTRIBUTE_VALUE_NAME_DUPLICATED);
        }
    }

    private void findDefaultPosition(Long attributeId, CreateProductAttributeValueDTO createProductAttributeValueDTO) {
        SearchProductAttributeValueFilterDTO filter = new SearchProductAttributeValueFilterDTO();
        filter.setOffset(0L);
        filter.setLimit(30L);
        List<CpanelProductAttributeValueRecord> productAttributeValueRecords = productAttributeValueDao.getProductAttributeValues(attributeId, filter);
        if (productAttributeValueRecords != null && !productAttributeValueRecords.isEmpty()) {
            CpanelProductAttributeValueRecord maxPositionAttributeValue = Collections.max(productAttributeValueRecords, Comparator.comparing(s -> s.getPosition()));
            createProductAttributeValueDTO.setPosition(maxPositionAttributeValue.getPosition() + 1);
        } else {
            createProductAttributeValueDTO.setPosition(0);
        }
    }

    private boolean findPosition(List<CpanelProductAttributeValueRecord> productAttributeValueRecords, final Integer position) {
        return productAttributeValueRecords.stream().noneMatch(at -> at.getPosition().equals(position));
    }

    @MySQLWrite
    public void updateProductAttributeValue(Long productId, Long attributeId, Long valueId,
                                            UpdateProductAttributeValueDTO updateProductAttributeValueDTO) {
        checkProduct(productId);
        checkAttribute(attributeId);

        //Check product attribute exists
        CpanelProductAttributeValueRecord productAttributeValueRecord = getAndCheckProductAttributeValue(valueId);

        checkProductSales(productId);

        if (updateProductAttributeValueDTO.getName() != null) {
            productAttributeValueRecord.setName(updateProductAttributeValueDTO.getName());
        }
        if (updateProductAttributeValueDTO.getPosition() != null) {
            if (updateProductAttributeValueDTO.getPosition() < 0) {
                throw new OneboxRestException(MsEventErrorCode.POSITION_MUST_BE_POSITIVE);
            }
            productAttributeValueRecord.setPosition(updateProductAttributeValueDTO.getPosition());
        }
        productAttributeValueDao.update(productAttributeValueRecord);

        if (updateProductAttributeValueDTO.getName() != null) {
            updateVariantsNames(productId, valueId, updateProductAttributeValueDTO.getName());
        }

        productLanguageHelper.modifyProductValueContents(productId, attributeId, valueId, productAttributeValueRecord.getName(), true);
        productLanguageHelper.modifyProductValueContents(productId, attributeId, valueId, updateProductAttributeValueDTO.getName(), false);
        postUpdateProduct(productId);
    }

    @MySQLRead
    public ProductAttributeValuesDTO getProductAttributeValues(Long productId, Long attributeId,
                                                               SearchProductAttributeValueFilterDTO filter) {
        checkProduct(productId);
        checkAttribute(attributeId);
        ProductAttributeValuesDTO productAttributeValuesDTO = new ProductAttributeValuesDTO();

        List<CpanelProductAttributeValueRecord> productAttributeValueRecords =
                productAttributeValueDao.getProductAttributeValues(attributeId, filter);

        productAttributeValuesDTO.setMetadata(new Metadata());
        if (productAttributeValueRecords != null) {
            productAttributeValuesDTO.setData(ProductAttributeValueConverter.toDTOs(productAttributeValueRecords));
            Long total = productAttributeValueDao.getTotalAttributeValues(productId, attributeId);
            productAttributeValuesDTO.setMetadata(MetadataBuilder.build(filter, total));

            return productAttributeValuesDTO;
        }

        return productAttributeValuesDTO;
    }

    @MySQLRead
    public ProductAttributeValueDTO getProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        checkProduct(productId);
        checkAttribute(attributeId);

        //Check product attribute exists
        CpanelProductAttributeValueRecord productAttributeValueRecord = getAndCheckProductAttributeValue(valueId);

        return ProductAttributeValueConverter.fromRecord(productAttributeValueRecord);
    }

    @MySQLWrite
    public void deleteProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        checkProduct(productId);
        checkAttribute(attributeId);

        CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord = productAttributeValueDao.findById(valueId.intValue());

        checkProductSales(productId);

        // delete variants
        Long currentVariants = productVariantDao.getCountProductVariantsByProductId(productId);
        if (currentVariants > 0) {
            productVariantDao.deleteByValue(productId, attributeId, valueId);
        }

        // delete value
        productAttributeValueDao.delete(cpanelProductAttributeValueRecord);
        productLanguageHelper.modifyProductValueContents(productId, attributeId, valueId, cpanelProductAttributeValueRecord.getName(), true);
        postUpdateProduct(productId);
    }

    private CpanelProductRecord checkProduct(Long productId) {
        CpanelProductRecord product = productDao.findById(productId.intValue());
        if (product == null || product.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        return product;
    }

    private void checkAttribute(Long attributeId) {
        CpanelProductAttributeRecord productAttributeRecord =
                productAttributeDao.findById(attributeId.intValue());
        if (productAttributeRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
        }

    }

    private CpanelProductAttributeValueRecord getAndCheckProductAttributeValue(Long valueId) {
        CpanelProductAttributeValueRecord productAttributeValueRecord = productAttributeValueDao.findById(valueId.intValue());
        if (productAttributeValueRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_ATTRIBUTE_VALUE_NOT_FOUND);
        }

        return productAttributeValueRecord;
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }

    private void updateVariantsNames(Long productId, Long valueId, String name) {
        List<CpanelProductVariantRecord> productVariants =
                productVariantDao.getProductVariantsByProductIdAndValueId(productId, valueId);

        for (CpanelProductVariantRecord productVariantRecord : productVariants) {

            //Update the first part of the name Example: Color / Red --> Tone / Red
            String currentName = productVariantRecord.getName();
            if (productVariantRecord.getVariantvalue1() != null && productVariantRecord.getVariantvalue2() != null) {
                String[] nameParts = currentName.split("/", 2);
                if (productVariantRecord.getVariantvalue1().equals(valueId.intValue())) {
                    String updatedName = name + " / " + nameParts[1].trim();
                    productVariantRecord.setName(updatedName);
                } else if (productVariantRecord.getVariantvalue2().equals(valueId.intValue())) {
                    String updatedName = nameParts[0].trim() + " / " + name;
                    productVariantRecord.setName(updatedName);
                }
            } else if (productVariantRecord.getVariantvalue1() != null && productVariantRecord.getVariantvalue1().equals(valueId.intValue())) {
                productVariantRecord.setName(name);
            }
            productVariantDao.update(productVariantRecord);
        }
    }

    private void checkProductSales(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord.getState().equals(ProductState.ACTIVE.getId())) {
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
