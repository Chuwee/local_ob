package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductVariantConverter;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductAttributeValueDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.ProductVariantDTO;
import es.onebox.event.products.dto.ProductVariantsDTO;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.event.products.dto.SearchProductVariantsFilterDTO;
import es.onebox.event.products.dto.UpdateProductVariantDTO;
import es.onebox.event.products.dto.UpdateProductVariantPricesDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductVariantService {

    private final ProductDao productDao;
    private final ProductVariantDao productVariantDao;
    private final ProductVariantStockCouchDao productVariantStockCouchDao;
    private final RefreshDataService refreshDataService;
    private final ProductAttributeDao productAttributeDao;
    private final ProductAttributeValueDao productAttributeValueDao;
    private final WebhookService webhookService;

    @Autowired
    public ProductVariantService(ProductDao productDao, ProductVariantDao productVariantDao,
                                 ProductVariantStockCouchDao productVariantStockCouchDao,
                                 RefreshDataService refreshDataService,
                                 ProductAttributeDao productAttributeDao,
                                 ProductAttributeValueDao productAttributeValueDao,
                                 WebhookService webhookService) {
        this.productDao = productDao;
        this.productVariantDao = productVariantDao;
        this.productVariantStockCouchDao = productVariantStockCouchDao;
        this.refreshDataService = refreshDataService;
        this.productAttributeDao = productAttributeDao;
        this.productAttributeValueDao = productAttributeValueDao;
        this.webhookService = webhookService;
    }

    @MySQLRead
    public ProductVariantsDTO searchProductVariants(Long productId,
                                                    SearchProductVariantsFilterDTO searchProductVariantsFilterDTO) {
        //Check product exists
        CpanelProductRecord productRecord = getAndCheckProduct(productId);

        if (searchProductVariantsFilterDTO.getStock() != null && productRecord.getStocktype() == 2) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_FILTER_INVALID);
        }

        List<ProductVariantRecord> records = productVariantDao.searchProductVariants(productId,
                searchProductVariantsFilterDTO);
        List<ProductVariantDTO> productVariants = records.stream()
                .map(r -> {
                    Long stock = productVariantStockCouchDao.get(productId, r.getVariantid().longValue());
                    return new AbstractMap.SimpleEntry<>(r, stock);
                })
                .filter(entry -> {
                    Long stock = entry.getValue();
                    if (searchProductVariantsFilterDTO.getStock() != null) {
                        if (searchProductVariantsFilterDTO.getStock() == 1) {
                            return stock > 0;
                        } else if (searchProductVariantsFilterDTO.getStock() == 0) {
                            return stock == 0;
                        }
                    }
                    return true;
                })
                .map(entry -> ProductVariantConverter.fromRecord(entry.getKey(), entry.getValue(),
                        ProductType.get(productRecord.getType())))
                .toList();

        Long total = productVariantDao.getTotalProductVariants(productId, searchProductVariantsFilterDTO);

        return new ProductVariantsDTO(productVariants,
                MetadataBuilder.build(searchProductVariantsFilterDTO, total));
    }

    @MySQLRead
    public ProductVariantDTO getProductVariant(Long productId, Long variantId) {
        //Check product exists
        CpanelProductRecord product = getAndCheckProduct(productId);

        //Check product variant exists
        ProductVariantRecord productVariantRecord = getAndCheckProductVariant(productId, variantId);

        //Get stock
        Long stock = productVariantStockCouchDao.get(productId, variantId);

        return ProductVariantConverter.fromRecord(productVariantRecord, stock, ProductType.get(product.getType()));
    }

    @MySQLWrite
    public void updateProductVariant(Long productId, Long variantId, UpdateProductVariantDTO updateProductVariantDTO) {
        //Check product exists
        CpanelProductRecord product = getAndCheckProduct(productId);

        //Check product variant exists
        ProductVariantRecord productVariant = getAndCheckProductVariant(productId, variantId);

        //Check price and stock are not being modified on active products
        checkProductVariantUpdatable(product, updateProductVariantDTO, productVariant);

        if (updateProductVariantDTO.getPrice() != null) {
            productVariant.setPrice(updateProductVariantDTO.getPrice());
        }
        if (updateProductVariantDTO.getSku() != null) {
            productVariant.setSku(updateProductVariantDTO.getSku());
        }
        if (updateProductVariantDTO.getStock() != null) {
            productVariantStockCouchDao.updateStock(productId, variantId, updateProductVariantDTO.getStock());
            if (!ProductStockType.UNBOUNDED.equals(ProductStockType.get(product.getStocktype()))) {
                productVariant.setStock(updateProductVariantDTO.getStock().intValue());
            }
        }
        if (updateProductVariantDTO.getStatus() != null) {
            productVariant.setStatus(updateProductVariantDTO.getStatus().getId());
        }

        productVariantDao.update(productVariant);
        postUpdateProduct(productId);
        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_CONFIGURATION);
    }

    @MySQLWrite
    public void updateProductVariantPrices(Long productId, UpdateProductVariantPricesDTO updateProductVariantPricesDTO) {
        //Check product exists
        getAndCheckProduct(productId);

        Set<Long> updateVariants = new HashSet<>();

        for (Long variantId :
                updateProductVariantPricesDTO.getVariants()) {
            //Check product variant exists
            ProductVariantRecord productVariant = getAndCheckProductVariant(productId, variantId);

            updateVariants.add(productVariant.getVariantid().longValue());

        }

        productVariantDao.updateProductVariantPrices(updateVariants, updateProductVariantPricesDTO.getPrice());
    }

    @MySQLWrite
    public List<IdNameDTO> createVariantProductVariants(Long productId) {
        //Check product exists
        CpanelProductRecord product = getAndCheckProduct(productId);

        if (!product.getState().equals(ProductState.INACTIVE.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_STATE_INVALID);
        }

        if (product.getType().equals(ProductType.SIMPLE.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_PRODUCT_TYPE_INVALID);
        }

        List<CpanelProductAttributeRecord> productAttributeRecords = productAttributeDao.findByProductId(productId);

        //No attributes
        if (productAttributeRecords == null || productAttributeRecords.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NUMBER_ATTRIBUTES_INVALID);
        }

        // Get the attribute values for both attributes
        List<CpanelProductAttributeValueRecord> valuesForFirstAttribute = productAttributeValueDao
                .getProductAttributeValues(productAttributeRecords.get(0).getAttributeid().longValue(),
                        new SearchProductAttributeValueFilterDTO());

        List<CpanelProductAttributeValueRecord> valuesForSecondAttribute = null;
        if (productAttributeRecords.size() > 1) {
            valuesForSecondAttribute = productAttributeValueDao
                    .getProductAttributeValues(productAttributeRecords.get(1).getAttributeid().longValue(),
                            new SearchProductAttributeValueFilterDTO());
        }

        int attributeId1 = productAttributeRecords.get(0).getAttributeid();
        Integer attributeId2 = null;
        if (productAttributeRecords.size() > 1) {
            attributeId2 = productAttributeRecords.get(1).getAttributeid();
        }

        //Get pre-existent variants to check new insertions
        Set<String> existingVariantKeys = productVariantDao.getExistingVariantKeys(productId, attributeId1, valuesForFirstAttribute, attributeId2, valuesForSecondAttribute);

        //Get new variants to insert
        List<CpanelProductVariantRecord> newVariants = new ArrayList<>();
        for (CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord1 : valuesForFirstAttribute) {
            if (valuesForSecondAttribute != null) {
                for (CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord2 : valuesForSecondAttribute) {
                    String variantKey = attributeId1 + ":" + cpanelProductAttributeValueRecord1.getValueid() + ":" + attributeId2 + ":" + cpanelProductAttributeValueRecord2.getValueid();
                    if (!existingVariantKeys.contains(variantKey)) {
                        newVariants.add(ProductVariantConverter.createProductVariantRecord(productId, cpanelProductAttributeValueRecord1, cpanelProductAttributeValueRecord2, attributeId1, attributeId2));
                    }
                }
            } else {
                String variantKey = attributeId1 + ":" + cpanelProductAttributeValueRecord1.getValueid();
                if (!existingVariantKeys.contains(variantKey)) {
                    newVariants.add(ProductVariantConverter.createProductVariantRecord(productId, cpanelProductAttributeValueRecord1, attributeId1));
                }
            }
        }

        //Batch insertion of new variants
        productVariantDao.insertBatch(new HashSet<>(newVariants));

        List<CpanelProductVariantRecord> variantsCreated = productVariantDao.getProductVariantsByAttributes(productId, attributeId1, valuesForFirstAttribute, attributeId2, valuesForSecondAttribute);

        variantsCreated.stream()
                .filter(variant -> ProductStockType.BOUNDED.getId() == product.getStocktype())
                .forEach(variant -> {
                    Long stock = productVariantStockCouchDao.get(productId, variant.getVariantid().longValue());
                    if (stock == null) {
                        productVariantStockCouchDao.insert(productId, variant.getVariantid().longValue());
                    }
                });

        //Return values
        return variantsCreated.stream()
                .map(variant -> new IdNameDTO(variant.getVariantid().longValue(), variant.getName()))
                .toList();
    }

    private CpanelProductRecord getAndCheckProduct(Long productId) {
        CpanelProductRecord product = productDao.findById(productId.intValue());
        if (product == null || product.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        return product;
    }

    private ProductVariantRecord getAndCheckProductVariant(Long productId, Long variantId) {
        ProductVariantRecord productVariantRecord = productVariantDao.findById(productId, variantId);
        if (productVariantRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND);
        }

        return productVariantRecord;
    }

    private void checkProductVariantUpdatable(CpanelProductRecord product,
                                              UpdateProductVariantDTO updateProductVariantDTO,
                                              ProductVariantRecord currentVariant) {
        if (updateProductVariantDTO.getStatus() != null
                && product.getType().equals(ProductType.SIMPLE.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_STATUS_NOT_UPDATABLE);
        }

        Long requestedStock = updateProductVariantDTO.getStock();
        Double requestedPrice = updateProductVariantDTO.getPrice();

        if ((requestedStock != null || requestedPrice != null) &&
                (product.getState().equals(ProductState.ACTIVE.getId())
                        &&
                        (currentVariant.getStatus().equals(ProductVariantStatus.ACTIVE.getId())
                                &&
                                (updateProductVariantDTO.getStatus() == null || updateProductVariantDTO.getStatus().equals(ProductVariantStatus.ACTIVE))
                        )
                )) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE);
        }
    }

    private void postUpdateProduct(Long productId) {
        refreshDataService.refreshProduct(productId);
    }



    public ProductVariantDTO getProductVariantSession(Long productId, Long variantId, Long sessionId) {
        //Check product exists
        CpanelProductRecord product = getAndCheckProduct(productId);

        //Check product variant exists
        ProductVariantRecord productVariantRecord = getAndCheckProductVariant(productId, variantId);

        //Get stock
        Long stock = productVariantStockCouchDao.get(productId, variantId);

        return ProductVariantConverter.fromRecord(productVariantRecord, stock, ProductType.get(product.getType()));
    }

}
