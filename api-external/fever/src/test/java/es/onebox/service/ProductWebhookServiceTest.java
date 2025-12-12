package es.onebox.service;

import es.onebox.common.datasources.ms.event.dto.CategoryDTO;
import es.onebox.common.datasources.ms.event.dto.ChannelEntityDTO;
import es.onebox.common.datasources.ms.event.dto.ChannelSubtype;
import es.onebox.common.datasources.ms.event.dto.ProductChannelDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelInfoDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementImageDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementTextDTO;
import es.onebox.common.datasources.ms.event.dto.ProductDTO;
import es.onebox.common.datasources.ms.event.dto.ProductEvent;
import es.onebox.common.datasources.ms.event.dto.ProductEventData;
import es.onebox.common.datasources.ms.event.dto.ProductEvents;
import es.onebox.common.datasources.ms.event.dto.ProductLanguage;
import es.onebox.common.datasources.ms.event.dto.ProductLanguages;
import es.onebox.common.datasources.ms.event.dto.ProductPublishingSessions;
import es.onebox.common.datasources.ms.event.dto.ProductSessionBase;
import es.onebox.common.datasources.ms.event.dto.ProductSurchargeDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.ProductVariants;
import es.onebox.common.datasources.ms.event.dto.RangeDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDateDTO;
import es.onebox.common.datasources.ms.event.enums.ProductEventStatus;
import es.onebox.common.datasources.ms.event.enums.ProductState;
import es.onebox.common.datasources.ms.event.enums.ProductStockType;
import es.onebox.common.datasources.ms.event.enums.ProductSurchargeType;
import es.onebox.common.datasources.ms.event.enums.ProductCommunicationElementTextsType;
import es.onebox.common.datasources.ms.event.enums.ProductCommunicationElementsImagesType;
import es.onebox.common.datasources.ms.event.enums.ProductType;
import es.onebox.common.datasources.ms.event.enums.ProductVariantStatus;
import es.onebox.common.datasources.ms.event.enums.SaleRequestsStatus;
import es.onebox.common.datasources.ms.event.enums.SelectionType;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductChannelFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementImageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementTextFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductEventFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductSessionFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductSurchargeFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductVariantFeverDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.fever.service.ProductWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import es.onebox.common.datasources.ms.event.dto.RangeValueDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

public class ProductWebhookServiceTest {

    private static final Long PRODUCT_ID = 1001L;
    private static final String PRODUCT_NAME = "Test Product";
    private static final Long CURRENCY_ID = 1L;
    private static final Long TAX_ID = 10L;
    private static final String TAX_NAME = "VAT";
    private static final Long SURCHARGE_TAX_ID = 20L;
    private static final String SURCHARGE_TAX_NAME = "Surcharge Tax";
    private static final Long VARIANT_ID = 2001L;
    private static final String VARIANT_NAME = "Test Variant";
    private static final String VARIANT_SKU = "TEST-SKU-001";
    private static final Double VARIANT_PRICE = 25.99;
    private static final Long EVENT_ID = 3001L;
    private static final String EVENT_NAME = "Test Event";
    private static final Long SESSION_ID = 4001L;
    private static final String SESSION_NAME = "Test Session";
    private static final Long CHANNEL_ID = 5001L;
    private static final String CHANNEL_NAME = "Test Channel";
    private static final Long ENTITY_ID = 6001L;
    private static final String ENTITY_NAME = "Test Entity";

    @Mock
    private MsEventRepository msEventRepository;

    @InjectMocks
    private ProductWebhookService productWebhookService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendProductGeneralData_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);

        WebhookFeverDTO result = productWebhookService.sendProductGeneralData(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductDetails());
        Assertions.assertEquals(PRODUCT_NAME, result.getFeverMessage().getProductUpdate().getProductDetails().getName());
        Assertions.assertEquals(ProductType.SIMPLE, result.getFeverMessage().getProductUpdate().getProductDetails().getProductType());
        Assertions.assertEquals(ProductStockType.BOUNDED, result.getFeverMessage().getProductUpdate().getProductDetails().getStockType());
        Assertions.assertEquals(ProductState.ACTIVE, result.getFeverMessage().getProductUpdate().getProductDetails().getProductState());
        Assertions.assertEquals(CURRENCY_ID, result.getFeverMessage().getProductUpdate().getProductDetails().getCurrencyId());
        Assertions.assertEquals(TAX_ID, result.getFeverMessage().getProductUpdate().getProductDetails().getTax().getId());
        Assertions.assertEquals(TAX_NAME, result.getFeverMessage().getProductUpdate().getProductDetails().getTax().getName());
        Assertions.assertEquals(SURCHARGE_TAX_ID, result.getFeverMessage().getProductUpdate().getProductDetails().getSurchargeTax().getId());
        Assertions.assertEquals(SURCHARGE_TAX_NAME, result.getFeverMessage().getProductUpdate().getProductDetails().getSurchargeTax().getName());
    }

    @Test
    public void sendProductGeneralData_withCategories_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        
        CategoryDTO category = generateCategoryDTO(1L, "Main Category");
        CategoryDTO customCategory = generateCategoryDTO(2L, "Custom Category");
        product.setCategory(category);
        product.setCustomCategory(customCategory);
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);

        WebhookFeverDTO result = productWebhookService.sendProductGeneralData(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductDetails());
        Assertions.assertEquals(PRODUCT_NAME, result.getFeverMessage().getProductUpdate().getProductDetails().getName());
        Assertions.assertEquals(category, result.getFeverMessage().getProductUpdate().getProductDetails().getCategory());
        Assertions.assertEquals(customCategory, result.getFeverMessage().getProductUpdate().getProductDetails().getCustomCategory());
    }

    @Test
    public void sendProductGeneralData_withoutTaxes_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        product.setTax(null);
        product.setSurchargeTax(null);
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);

        WebhookFeverDTO result = productWebhookService.sendProductGeneralData(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductDetails());
        Assertions.assertEquals(PRODUCT_NAME, result.getFeverMessage().getProductUpdate().getProductDetails().getName());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductDetails().getTax());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductDetails().getSurchargeTax());
    }

    @Test
    public void sendProductSurcharges_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        List<ProductSurchargeDTO> surcharges = generateProductSurcharges();
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);
        when(msEventRepository.getProductSurcharges(PRODUCT_ID)).thenReturn(surcharges);

        WebhookFeverDTO result = productWebhookService.sendProductSurcharges(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSurcharges());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductSurcharges().size());
        Assertions.assertEquals(ProductSurchargeType.GENERIC, result.getFeverMessage().getProductUpdate().getProductSurcharges().get(0).getType());
        Assertions.assertEquals(ProductSurchargeType.PROMOTION, result.getFeverMessage().getProductUpdate().getProductSurcharges().get(1).getType());
    }

    @Test
    public void sendProductSurcharges_emptySurcharges_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        List<ProductSurchargeDTO> emptySurcharges = new ArrayList<>();
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);
        when(msEventRepository.getProductSurcharges(PRODUCT_ID)).thenReturn(emptySurcharges);

        WebhookFeverDTO result = productWebhookService.sendProductSurcharges(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductSurcharges().isEmpty());
    }

    @Test
    public void sendProductSurcharges_nullSurcharges_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);
        when(msEventRepository.getProductSurcharges(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductSurcharges(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductSurcharges());
    }

    @Test
    public void sendProductSurcharges_multipleSurchargeTypes_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductDTO product = generateProductDTO();
        List<ProductSurchargeDTO> surcharges = generateMultipleTypeSurcharges();
        
        when(msEventRepository.getProduct(PRODUCT_ID)).thenReturn(product);
        when(msEventRepository.getProductSurcharges(PRODUCT_ID)).thenReturn(surcharges);

        WebhookFeverDTO result = productWebhookService.sendProductSurcharges(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSurcharges());
        Assertions.assertEquals(3, result.getFeverMessage().getProductUpdate().getProductSurcharges().size());
        
        List<ProductSurchargeType> types = result.getFeverMessage().getProductUpdate().getProductSurcharges().stream()
                .map(ProductSurchargeFeverDTO::getType)
                .toList();
        Assertions.assertTrue(types.contains(ProductSurchargeType.GENERIC));
        Assertions.assertTrue(types.contains(ProductSurchargeType.PROMOTION));
    }

    private WebhookFeverDTO generateWebhookMessage() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId(PRODUCT_ID.toString());
        
        FeverMessageDTO message = new FeverMessageDTO();
        return new WebhookFeverDTO(notificationMessage, req, message);
    }

    private ProductDTO generateProductDTO() {
        ProductDTO product = new ProductDTO();
        product.setProductId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setProductType(ProductType.SIMPLE);
        product.setStockType(ProductStockType.BOUNDED);
        product.setProductState(ProductState.ACTIVE);
        product.setCurrencyId(CURRENCY_ID);
        product.setCreateDate(ZonedDateTime.now());
        product.setUpdateDate(ZonedDateTime.now());
        
        IdNameDTO tax = new IdNameDTO();
        tax.setId(TAX_ID);
        tax.setName(TAX_NAME);
        product.setTax(tax);
        
        IdNameDTO surchargeTax = new IdNameDTO();
        surchargeTax.setId(SURCHARGE_TAX_ID);
        surchargeTax.setName(SURCHARGE_TAX_NAME);
        product.setSurchargeTax(surchargeTax);
        
        return product;
    }

    private CategoryDTO generateCategoryDTO(Long id, String description) {
        CategoryDTO category = new CategoryDTO();
        category.setId(id.intValue());
        category.setDescription(description);
        category.setCode("CAT_" + id);
        return category;
    }

    private List<ProductSurchargeDTO> generateProductSurcharges() {
        List<ProductSurchargeDTO> surcharges = new ArrayList<>();
        
        ProductSurchargeDTO fixedSurcharge = new ProductSurchargeDTO();
        fixedSurcharge.setType(ProductSurchargeType.GENERIC);
        fixedSurcharge.setRanges(generateRanges(5.0, null));
        surcharges.add(fixedSurcharge);
        
        ProductSurchargeDTO percentageSurcharge = new ProductSurchargeDTO();
        percentageSurcharge.setType(ProductSurchargeType.PROMOTION);
        percentageSurcharge.setRanges(generateRanges(null, 10.0));
        surcharges.add(percentageSurcharge);
        
        return surcharges;
    }

    private List<ProductSurchargeDTO> generateMultipleTypeSurcharges() {
        List<ProductSurchargeDTO> surcharges = new ArrayList<>();
        
        ProductSurchargeDTO fixedSurcharge = new ProductSurchargeDTO();
        fixedSurcharge.setType(ProductSurchargeType.GENERIC);
        fixedSurcharge.setRanges(generateRanges(5.0, null));
        surcharges.add(fixedSurcharge);
        
        ProductSurchargeDTO percentageSurcharge = new ProductSurchargeDTO();
        percentageSurcharge.setType(ProductSurchargeType.PROMOTION);
        percentageSurcharge.setRanges(generateRanges(null, 10.0));
        surcharges.add(percentageSurcharge);
        
        ProductSurchargeDTO progressiveSurcharge = new ProductSurchargeDTO();
        progressiveSurcharge.setType(ProductSurchargeType.GENERIC);
        progressiveSurcharge.setRanges(generateRanges(null, 15.0));
        surcharges.add(progressiveSurcharge);
        
        return surcharges;
    }

    private List<RangeDTO> generateRanges(Double fixedValue, Double percentageValue) {
        List<RangeDTO> ranges = new ArrayList<>();
        RangeDTO range = new RangeDTO();
        range.setFrom(0.0);
        range.setTo(100.0);
        
        RangeValueDTO values = new RangeValueDTO();
        if (fixedValue != null) {
            values.setFixed(fixedValue);
        }
        if (percentageValue != null) {
            values.setPercentage(percentageValue);
        }
        values.setMin(0.0);
        values.setMax(100.0);
        
        range.setValues(values);
        ranges.add(range);
        return ranges;
    }

    @Test
    public void sendProductConfiguration_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductVariants productVariants = generateProductVariants();
        
        when(msEventRepository.getProductVariants(PRODUCT_ID)).thenReturn(productVariants);

        WebhookFeverDTO result = productWebhookService.sendProductConfiguration(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductVariants());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductVariants().getData().size());
        Assertions.assertEquals(VARIANT_NAME, result.getFeverMessage().getProductUpdate().getProductVariants().getData().get(0).getName());
        Assertions.assertEquals(VARIANT_SKU, result.getFeverMessage().getProductUpdate().getProductVariants().getData().get(0).getSku());
        Assertions.assertEquals(VARIANT_PRICE, result.getFeverMessage().getProductUpdate().getProductVariants().getData().get(0).getPrice());
    }

    @Test
    public void sendProductConfiguration_emptyVariants_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductVariants emptyVariants = new ProductVariants(new ArrayList<>(), new Metadata());
        
        when(msEventRepository.getProductVariants(PRODUCT_ID)).thenReturn(emptyVariants);

        WebhookFeverDTO result = productWebhookService.sendProductConfiguration(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductVariants());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductVariants().getData().isEmpty());
    }

    @Test
    public void sendProductConfiguration_nullVariants_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductVariants(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductConfiguration(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductVariants());
    }

    @Test
    public void sendProductConfiguration_variantsWithDifferentStatuses_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductVariants productVariants = generateProductVariantsWithDifferentStatuses();
        
        when(msEventRepository.getProductVariants(PRODUCT_ID)).thenReturn(productVariants);

        WebhookFeverDTO result = productWebhookService.sendProductConfiguration(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductVariants());
        Assertions.assertEquals(3, result.getFeverMessage().getProductUpdate().getProductVariants().getData().size());
        
        List<ProductVariantStatus> statuses = result.getFeverMessage().getProductUpdate().getProductVariants().getData().stream()
                .map(ProductVariantFeverDTO::getProductVariantStatus)
                .toList();
        Assertions.assertTrue(statuses.contains(ProductVariantStatus.ACTIVE));
        Assertions.assertTrue(statuses.contains(ProductVariantStatus.INACTIVE));
    }

    private ProductVariants generateProductVariants() {
        List<ProductVariant> variants = new ArrayList<>();
        
        ProductVariant variant1 = new ProductVariant();
        variant1.setId(VARIANT_ID);
        variant1.setName(VARIANT_NAME);
        variant1.setSku(VARIANT_SKU);
        variant1.setPrice(VARIANT_PRICE);
        variant1.setStock(100);
        variant1.setProductVariantStatus(ProductVariantStatus.ACTIVE);
        variant1.setCreateDate(ZonedDateTime.now());
        variant1.setUpdateDate(ZonedDateTime.now());
        
        IdNameDTO product = new IdNameDTO();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        variant1.setProduct(product);
        
        IdNameDTO variantOption1 = new IdNameDTO();
        variantOption1.setId(1L);
        variantOption1.setName("Size");
        variant1.setVariantOption1(variantOption1);
        
        IdNameDTO variantValue1 = new IdNameDTO();
        variantValue1.setId(1L);
        variantValue1.setName("Large");
        variant1.setVariantValue1(variantValue1);
        
        variants.add(variant1);
        
        ProductVariant variant2 = new ProductVariant();
        variant2.setId(VARIANT_ID + 1);
        variant2.setName("Test Variant 2");
        variant2.setSku("TEST-SKU-002");
        variant2.setPrice(35.99);
        variant2.setStock(50);
        variant2.setProductVariantStatus(ProductVariantStatus.ACTIVE);
        variant2.setCreateDate(ZonedDateTime.now());
        variant2.setUpdateDate(ZonedDateTime.now());
        variant2.setProduct(product);
        
        IdNameDTO variantValue2 = new IdNameDTO();
        variantValue2.setId(2L);
        variantValue2.setName("Medium");
        variant2.setVariantValue1(variantValue2);
        variant2.setVariantOption1(variantOption1);
        
        variants.add(variant2);
        
        return new ProductVariants(variants, new Metadata());
    }

    private ProductVariants generateProductVariantsWithDifferentStatuses() {
        List<ProductVariant> variants = new ArrayList<>();
        
        ProductVariant activeVariant = new ProductVariant();
        activeVariant.setId(VARIANT_ID);
        activeVariant.setName("Active Variant");
        activeVariant.setSku("ACTIVE-SKU");
        activeVariant.setPrice(25.99);
        activeVariant.setStock(100);
        activeVariant.setProductVariantStatus(ProductVariantStatus.ACTIVE);
        activeVariant.setCreateDate(ZonedDateTime.now());
        activeVariant.setUpdateDate(ZonedDateTime.now());
        
        IdNameDTO product = new IdNameDTO();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        activeVariant.setProduct(product);
        
        variants.add(activeVariant);
        
        ProductVariant inactiveVariant = new ProductVariant();
        inactiveVariant.setId(VARIANT_ID + 1);
        inactiveVariant.setName("Inactive Variant");
        inactiveVariant.setSku("INACTIVE-SKU");
        inactiveVariant.setPrice(15.99);
        inactiveVariant.setStock(0);
        inactiveVariant.setProductVariantStatus(ProductVariantStatus.INACTIVE);
        inactiveVariant.setCreateDate(ZonedDateTime.now());
        inactiveVariant.setUpdateDate(ZonedDateTime.now());
        inactiveVariant.setProduct(product);
        
        variants.add(inactiveVariant);
        
        ProductVariant anotherActiveVariant = new ProductVariant();
        anotherActiveVariant.setId(VARIANT_ID + 2);
        anotherActiveVariant.setName("Another Active Variant");
        anotherActiveVariant.setSku("ANOTHER-ACTIVE-SKU");
        anotherActiveVariant.setPrice(45.99);
        anotherActiveVariant.setStock(75);
        anotherActiveVariant.setProductVariantStatus(ProductVariantStatus.ACTIVE);
        anotherActiveVariant.setCreateDate(ZonedDateTime.now());
        anotherActiveVariant.setUpdateDate(ZonedDateTime.now());
        anotherActiveVariant.setProduct(product);
        
        variants.add(anotherActiveVariant);
        
        return new ProductVariants(variants, new Metadata());
    }

    @Test
    public void sendProductLanguages_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductLanguages productLanguages = generateProductLanguages();
        
        when(msEventRepository.productLanguages(PRODUCT_ID)).thenReturn(productLanguages);

        WebhookFeverDTO result = productWebhookService.sendProductLanguages(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductLanguages());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductLanguages().size());
        Assertions.assertEquals(productLanguages, result.getFeverMessage().getProductUpdate().getProductLanguages());
    }

    @Test
    public void sendProductLanguages_nullLanguages_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.productLanguages(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductLanguages(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductLanguages());
    }

    private ProductLanguages generateProductLanguages() {
        ProductLanguages productLanguages = new ProductLanguages();
        
        ProductLanguage language1 = new ProductLanguage();
        language1.setProductId(PRODUCT_ID);
        language1.setCode("en");
        language1.setLanguageId(1L);
        language1.setIsDefault(true);
        
        ProductLanguage language2 = new ProductLanguage();
        language2.setProductId(PRODUCT_ID);
        language2.setCode("es");
        language2.setLanguageId(2L);
        language2.setIsDefault(false);
        
        productLanguages.add(language1);
        productLanguages.add(language2);
        
        return productLanguages;
    }

    @Test
    public void sendProductEvents_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductEvents productEvents = generateProductEvents();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID)).thenReturn(productEvents);

        WebhookFeverDTO result = productWebhookService.sendProductEvents(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductEvents());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductEvents().size());
        
        ProductEventFeverDTO firstEvent = result.getFeverMessage().getProductUpdate().getProductEvents().get(0);
        Assertions.assertEquals(PRODUCT_ID, firstEvent.getProduct().getId());
        Assertions.assertEquals(PRODUCT_NAME, firstEvent.getProduct().getName());
        Assertions.assertEquals(EVENT_ID, firstEvent.getEvent().getId());
        Assertions.assertEquals(EVENT_NAME, firstEvent.getEvent().getName());
        Assertions.assertEquals(ProductEventStatus.ACTIVE, firstEvent.getStatus());
        Assertions.assertEquals(SelectionType.ALL, firstEvent.getSessionsSelectionType());
    }

    @Test
    public void sendProductEvents_emptyEvents_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductEvents emptyEvents = new ProductEvents();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID)).thenReturn(emptyEvents);

        WebhookFeverDTO result = productWebhookService.sendProductEvents(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductEvents());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductEvents().isEmpty());
    }

    @Test
    public void sendProductEvents_nullEvents_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductEvents(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductEvents());
    }

    @Test
    public void sendProductEvents_multipleEventsWithDifferentStatuses_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductEvents productEvents = generateProductEventsWithDifferentStatuses();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID)).thenReturn(productEvents);

        WebhookFeverDTO result = productWebhookService.sendProductEvents(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductEvents());
        Assertions.assertEquals(3, result.getFeverMessage().getProductUpdate().getProductEvents().size());
        
        List<ProductEventStatus> statuses = result.getFeverMessage().getProductUpdate().getProductEvents().stream()
                .map(ProductEventFeverDTO::getStatus)
                .toList();
        Assertions.assertTrue(statuses.contains(ProductEventStatus.ACTIVE));
        Assertions.assertTrue(statuses.contains(ProductEventStatus.INACTIVE));
        Assertions.assertTrue(statuses.contains(ProductEventStatus.DELETED));
    }

    @Test
    public void sendProductSessions_validProductAndEvent_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        ProductPublishingSessions productSessions = generateProductPublishingSessions();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(productSessions);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSessions());
        Assertions.assertEquals(SelectionType.RESTRICTED, result.getFeverMessage().getProductUpdate().getProductSessions().getType());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().size());
        
        Set<ProductSessionFeverDTO> sessions = result.getFeverMessage().getProductUpdate().getProductSessions().getSessions();
        List<Long> sessionIds = sessions.stream().map(ProductSessionFeverDTO::getId).toList();
        List<String> sessionNames = sessions.stream().map(ProductSessionFeverDTO::getName).toList();
        
        Assertions.assertTrue(sessionIds.contains(SESSION_ID));
        Assertions.assertTrue(sessionIds.contains(SESSION_ID + 1));
        Assertions.assertTrue(sessionNames.contains(SESSION_NAME));
        Assertions.assertTrue(sessionNames.contains("Test Session 2"));
        
        // Verify all sessions have dates
        sessions.forEach(session -> Assertions.assertNotNull(session.getDates()));
    }

    @Test
    public void sendProductSessions_allSelectionType_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        ProductPublishingSessions productSessions = generateProductPublishingSessionsAllType();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(productSessions);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSessions());
        Assertions.assertEquals(SelectionType.ALL, result.getFeverMessage().getProductUpdate().getProductSessions().getType());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().isEmpty());
    }

    @Test
    public void sendProductSessions_nullSessions_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductSessions());
    }

    @Test
    public void sendProductSessions_emptySessions_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        ProductPublishingSessions emptySessions = new ProductPublishingSessions();
        emptySessions.setType(SelectionType.RESTRICTED);
        emptySessions.setSessions(new HashSet<>());
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(emptySessions);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSessions());
        Assertions.assertEquals(SelectionType.RESTRICTED, result.getFeverMessage().getProductUpdate().getProductSessions().getType());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().isEmpty());
    }

    private WebhookFeverDTO generateWebhookMessageWithEventId() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId(PRODUCT_ID.toString());
        notificationMessage.setEventId(EVENT_ID);
        
        FeverMessageDTO message = new FeverMessageDTO();
        return new WebhookFeverDTO(notificationMessage, req, message);
    }

    private ProductEvents generateProductEvents() {
        ProductEvents productEvents = new ProductEvents();
        
        ProductEvent event1 = new ProductEvent();
        IdNameDTO product1 = new IdNameDTO();
        product1.setId(PRODUCT_ID);
        product1.setName(PRODUCT_NAME);
        event1.setProduct(product1);
        
        ProductEventData eventData1 = new ProductEventData();
        eventData1.setId(EVENT_ID);
        eventData1.setName(EVENT_NAME);
        eventData1.setStartDate(ZonedDateTime.now().plusDays(1));
        event1.setEvent(eventData1);
        
        event1.setStatus(ProductEventStatus.ACTIVE);
        event1.setSessionsSelectionType(SelectionType.ALL);
        
        productEvents.add(event1);
        
        ProductEvent event2 = new ProductEvent();
        IdNameDTO product2 = new IdNameDTO();
        product2.setId(PRODUCT_ID);
        product2.setName(PRODUCT_NAME);
        event2.setProduct(product2);
        
        ProductEventData eventData2 = new ProductEventData();
        eventData2.setId(EVENT_ID + 1);
        eventData2.setName("Test Event 2");
        eventData2.setStartDate(ZonedDateTime.now().plusDays(7));
        event2.setEvent(eventData2);
        
        event2.setStatus(ProductEventStatus.INACTIVE);
        event2.setSessionsSelectionType(SelectionType.RESTRICTED);
        
        productEvents.add(event2);
        
        return productEvents;
    }

    private ProductEvents generateProductEventsWithDifferentStatuses() {
        ProductEvents productEvents = new ProductEvents();
        
        ProductEvent activeEvent = new ProductEvent();
        IdNameDTO product = new IdNameDTO();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        activeEvent.setProduct(product);
        
        ProductEventData activeEventData = new ProductEventData();
        activeEventData.setId(EVENT_ID);
        activeEventData.setName("Active Event");
        activeEventData.setStartDate(ZonedDateTime.now().plusDays(1));
        activeEvent.setEvent(activeEventData);
        activeEvent.setStatus(ProductEventStatus.ACTIVE);
        activeEvent.setSessionsSelectionType(SelectionType.ALL);
        
        productEvents.add(activeEvent);
        
        ProductEvent inactiveEvent = new ProductEvent();
        inactiveEvent.setProduct(product);
        
        ProductEventData inactiveEventData = new ProductEventData();
        inactiveEventData.setId(EVENT_ID + 1);
        inactiveEventData.setName("Inactive Event");
        inactiveEventData.setStartDate(ZonedDateTime.now().plusDays(2));
        inactiveEvent.setEvent(inactiveEventData);
        inactiveEvent.setStatus(ProductEventStatus.INACTIVE);
        inactiveEvent.setSessionsSelectionType(SelectionType.RESTRICTED);
        
        productEvents.add(inactiveEvent);
        
        ProductEvent deletedEvent = new ProductEvent();
        deletedEvent.setProduct(product);
        
        ProductEventData deletedEventData = new ProductEventData();
        deletedEventData.setId(EVENT_ID + 2);
        deletedEventData.setName("Deleted Event");
        deletedEventData.setStartDate(ZonedDateTime.now().plusDays(3));
        deletedEvent.setEvent(deletedEventData);
        deletedEvent.setStatus(ProductEventStatus.DELETED);
        deletedEvent.setSessionsSelectionType(SelectionType.ALL);
        
        productEvents.add(deletedEvent);
        
        return productEvents;
    }

    private ProductPublishingSessions generateProductPublishingSessions() {
        ProductPublishingSessions productSessions = new ProductPublishingSessions();
        productSessions.setType(SelectionType.RESTRICTED);
        
        Set<ProductSessionBase> sessions = new HashSet<>();
        
        ProductSessionBase session1 = new ProductSessionBase();
        session1.setId(SESSION_ID);
        session1.setName(SESSION_NAME);
        session1.setDates(generateSessionDates());
        sessions.add(session1);
        
        ProductSessionBase session2 = new ProductSessionBase();
        session2.setId(SESSION_ID + 1);
        session2.setName("Test Session 2");
        session2.setDates(generateSessionDates());
        sessions.add(session2);
        
        productSessions.setSessions(sessions);
        
        return productSessions;
    }

    private ProductPublishingSessions generateProductPublishingSessionsAllType() {
        ProductPublishingSessions productSessions = new ProductPublishingSessions();
        productSessions.setType(SelectionType.ALL);
        productSessions.setSessions(new HashSet<>());
        
        return productSessions;
    }

    private SessionDateDTO generateSessionDates() {
        SessionDateDTO dates = new SessionDateDTO();
        dates.setStart(ZonedDateTime.now().plusDays(1));
        dates.setEnd(ZonedDateTime.now().plusDays(1).plusHours(2));
        
        return dates;
    }

    // Edge case and error scenario tests
    
    @Test
    public void sendProductEvents_repositoryThrowsException_exceptionPropagated() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID))
                .thenThrow(new RuntimeException("Database connection error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            productWebhookService.sendProductEvents(webhookFever);
        });
    }

    @Test
    public void sendProductSessions_repositoryThrowsException_exceptionPropagated() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID))
                .thenThrow(new RuntimeException("Database connection error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            productWebhookService.sendProductSessions(webhookFever);
        });
    }

    @Test
    public void sendProductEvents_invalidProductId_exceptionThrown() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        webhookFever.getNotificationMessage().setId("invalid-id");

        Assertions.assertThrows(NumberFormatException.class, () -> {
            productWebhookService.sendProductEvents(webhookFever);
        });
    }

    @Test
    public void sendProductSessions_invalidProductId_exceptionThrown() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        webhookFever.getNotificationMessage().setId("invalid-id");

        Assertions.assertThrows(NumberFormatException.class, () -> {
            productWebhookService.sendProductSessions(webhookFever);
        });
    }

    @Test
    public void sendProductSessions_nullEventId_throwsOneboxRestException() {
        WebhookFeverDTO webhookFever = generateWebhookMessage(); // No event ID set

        OneboxRestException exception = Assertions.assertThrows(OneboxRestException.class, () -> {
            productWebhookService.sendProductSessions(webhookFever);
        });
        
        Assertions.assertEquals(ApiExternalErrorCode.EVENT_ID_REQUIRED.name(), exception.getErrorCode());
    }

    @Test
    public void sendProductSessions_validEventId_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        ProductPublishingSessions productSessions = generateProductPublishingSessions();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(productSessions);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSessions());
        Assertions.assertEquals(SelectionType.RESTRICTED, result.getFeverMessage().getProductUpdate().getProductSessions().getType());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().size());
    }

    @Test
    public void sendProductEvents_eventWithNullFields_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductEvents productEventsWithNulls = generateProductEventsWithNullFields();
        
        when(msEventRepository.getProductEvents(PRODUCT_ID)).thenReturn(productEventsWithNulls);

        WebhookFeverDTO result = productWebhookService.sendProductEvents(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductEvents());
        Assertions.assertEquals(1, result.getFeverMessage().getProductUpdate().getProductEvents().size());
        
        ProductEventFeverDTO event = result.getFeverMessage().getProductUpdate().getProductEvents().get(0);
        Assertions.assertNull(event.getStatus());
        Assertions.assertNull(event.getSessionsSelectionType());
    }

    @Test
    public void sendProductSessions_sessionWithNullFields_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithEventId();
        ProductPublishingSessions sessionsWithNulls = generateProductPublishingSessionsWithNullFields();
        
        when(msEventRepository.geProductSessions(PRODUCT_ID, EVENT_ID)).thenReturn(sessionsWithNulls);

        WebhookFeverDTO result = productWebhookService.sendProductSessions(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductSessions());
        Assertions.assertEquals(1, result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().size());
        
        ProductSessionFeverDTO session = result.getFeverMessage().getProductUpdate().getProductSessions().getSessions().iterator().next();
        Assertions.assertNull(session.getName());
        Assertions.assertNull(session.getDates());
    }

    private ProductEvents generateProductEventsWithNullFields() {
        ProductEvents productEvents = new ProductEvents();
        
        ProductEvent event = new ProductEvent();
        IdNameDTO product = new IdNameDTO();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        event.setProduct(product);
        
        ProductEventData eventData = new ProductEventData();
        eventData.setId(EVENT_ID);
        eventData.setName(EVENT_NAME);
        eventData.setStartDate(ZonedDateTime.now().plusDays(1));
        event.setEvent(eventData);
        
        event.setStatus(null);
        event.setSessionsSelectionType(null);
        
        productEvents.add(event);
        
        return productEvents;
    }

    private ProductPublishingSessions generateProductPublishingSessionsWithNullFields() {
        ProductPublishingSessions productSessions = new ProductPublishingSessions();
        productSessions.setType(SelectionType.RESTRICTED);
        
        Set<ProductSessionBase> sessions = new HashSet<>();
        
        ProductSessionBase session = new ProductSessionBase();
        session.setId(SESSION_ID);
        session.setName(null);
        session.setDates(null);
        sessions.add(session);
        
        productSessions.setSessions(sessions);
        
        return productSessions;
    }

    @Test
    public void sendProductChannelsUpdate_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductChannelsDTO productChannels = generateProductChannelsDTO();
        
        when(msEventRepository.getProductChannels(PRODUCT_ID)).thenReturn(productChannels);

        WebhookFeverDTO result = productWebhookService.sendProductChannelsUpdate(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductChannels());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductChannels().size());
        
        ProductChannelFeverDTO firstChannel = result.getFeverMessage().getProductUpdate().getProductChannels().get(0);
        Assertions.assertEquals(PRODUCT_ID, firstChannel.getProductId());
        Assertions.assertEquals(SaleRequestsStatus.ACCEPTED, firstChannel.getSaleRequestsStatus());
        Assertions.assertTrue(firstChannel.getCheckoutSuggestionEnabled());
        Assertions.assertTrue(firstChannel.getStandaloneEnabled());
    }

    @Test
    public void sendProductChannelsUpdate_emptyChannels_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductChannelsDTO emptyChannels = new ProductChannelsDTO();
        
        when(msEventRepository.getProductChannels(PRODUCT_ID)).thenReturn(emptyChannels);

        WebhookFeverDTO result = productWebhookService.sendProductChannelsUpdate(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductChannels().isEmpty());
    }

    @Test
    public void sendProductChannelsUpdate_nullChannels_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductChannels(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductChannelsUpdate(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductChannels());
    }

    @Test
    public void sendProductChannelUpdateSale_validProductAndChannel_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithChannelId();
        ProductChannelDTO productChannel = generateProductChannel();
        
        when(msEventRepository.getProductChannel(PRODUCT_ID, CHANNEL_ID)).thenReturn(productChannel);

        WebhookFeverDTO result = productWebhookService.sendProductChannelUpdateSale(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertEquals(PRODUCT_ID, result.getFeverMessage().getProductUpdate().getProductChannel().getProductId());
        Assertions.assertEquals(CHANNEL_ID, result.getFeverMessage().getProductUpdate().getProductChannel().getChannelId());
        Assertions.assertEquals(SaleRequestsStatus.ACCEPTED, result.getFeverMessage().getProductUpdate().getProductChannel().getSaleRequestsStatus());
    }

    @Test
    public void sendProductChannelUpdateSale_nullChannelId_throwsOneboxRestException() {
        WebhookFeverDTO webhookFever = generateWebhookMessage(); // No channel ID set

        OneboxRestException exception = Assertions.assertThrows(OneboxRestException.class, () -> {
            productWebhookService.sendProductChannelUpdateSale(webhookFever);
        });
        
        Assertions.assertEquals(ApiExternalErrorCode.CHANNEL_ID_REQUIRED.name(), exception.getErrorCode());
    }

    @Test
    public void sendProductChannelUpdateSale_nullChannel_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessageWithChannelId();
        
        when(msEventRepository.getProductChannel(PRODUCT_ID, CHANNEL_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductChannelUpdateSale(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductChannel());
    }

    private WebhookFeverDTO generateWebhookMessageWithChannelId() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        NotificationMessageDTO notificationMessage = new NotificationMessageDTO();
        notificationMessage.setId(PRODUCT_ID.toString());
        notificationMessage.setChannelId(CHANNEL_ID);
        
        FeverMessageDTO message = new FeverMessageDTO();
        return new WebhookFeverDTO(notificationMessage, req, message);
    }

    private ProductChannelsDTO generateProductChannelsDTO() {
        ProductChannelsDTO channels = new ProductChannelsDTO();
        
        ProductChannelDTO channel1 = generateProductChannel();
        channels.add(channel1);
        
        ProductChannelDTO channel2 = new ProductChannelDTO();
        IdNameDTO product2 = new IdNameDTO();
        product2.setId(PRODUCT_ID);
        product2.setName(PRODUCT_NAME);
        channel2.setProduct(product2);
        
        ProductChannelInfoDTO channelInfo2 = new ProductChannelInfoDTO();
        channelInfo2.setId(CHANNEL_ID + 1);
        channelInfo2.setName("Test Channel 2");
        channelInfo2.setType(ChannelSubtype.BOX_OFFICE);
        
        ChannelEntityDTO entity2 = new ChannelEntityDTO();
        entity2.setId(ENTITY_ID + 1);
        entity2.setName("Test Entity 2");
        entity2.setLogo("test-logo-2.png");
        channelInfo2.setEntity(entity2);
        channel2.setChannel(channelInfo2);
        
        channel2.setSaleRequestsStatus(SaleRequestsStatus.REJECTED);
        channel2.setCheckoutSuggestionEnabled(false);
        channel2.setStandaloneEnabled(false);
        channels.add(channel2);
        
        return channels;
    }

    private ProductChannelDTO generateProductChannel() {
        ProductChannelDTO channel = new ProductChannelDTO();
        IdNameDTO product = new IdNameDTO();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        channel.setProduct(product);
        
        ProductChannelInfoDTO channelInfo = generateProductChannelInfo();
        channel.setChannel(channelInfo);
        
        channel.setSaleRequestsStatus(SaleRequestsStatus.ACCEPTED);
        channel.setCheckoutSuggestionEnabled(true);
        channel.setStandaloneEnabled(true);

        return channel;
    }

    private ProductChannelInfoDTO generateProductChannelInfo() {
        ProductChannelInfoDTO channelInfo = new ProductChannelInfoDTO();
        channelInfo.setId(CHANNEL_ID);
        channelInfo.setName(CHANNEL_NAME);
        channelInfo.setType(ChannelSubtype.WEB);
        
        ChannelEntityDTO entity = new ChannelEntityDTO();
        entity.setId(ENTITY_ID);
        entity.setName(ENTITY_NAME);
        entity.setLogo("test-logo.png");
        channelInfo.setEntity(entity);
        
        return channelInfo;
    }

    @Test
    public void sendProductChannelLiterals_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductCommunicationElementsTextsDTO productTexts = generateProductTexts();
        
        when(msEventRepository.getProductCommunicationElementsTexts(PRODUCT_ID)).thenReturn(productTexts);

        WebhookFeverDTO result = productWebhookService.sendProductChannelLiterals(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductCommunicationElementTexts());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductCommunicationElementTexts().size());
        
        ProductCommunicationElementTextFeverDTO firstText = result.getFeverMessage().getProductUpdate().getProductCommunicationElementTexts().get(0);
        Assertions.assertEquals(ProductCommunicationElementTextsType.PRODUCT_NAME, firstText.getType());
        Assertions.assertEquals(1L, firstText.getLanguageId());
        Assertions.assertEquals("Test Title", firstText.getValue());
    }

    @Test
    public void sendProductChannelLiterals_emptyTexts_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductCommunicationElementsTextsDTO emptyTexts = new ProductCommunicationElementsTextsDTO();
        
        when(msEventRepository.getProductCommunicationElementsTexts(PRODUCT_ID)).thenReturn(emptyTexts);

        WebhookFeverDTO result = productWebhookService.sendProductChannelLiterals(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductCommunicationElementTexts().isEmpty());
    }

    @Test
    public void sendProductChannelLiterals_nullTexts_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductCommunicationElementsTexts(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductChannelLiterals(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductCommunicationElementTexts());
    }

    @Test
    public void sendProductChannelImages_validProduct_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductCommunicationElementsImagesDTO productImages = generateProductImages();
        
        when(msEventRepository.getProductCommunicationElementImages(PRODUCT_ID)).thenReturn(productImages);

        WebhookFeverDTO result = productWebhookService.sendProductChannelImages(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate().getProductCommunicationElementImages());
        Assertions.assertEquals(2, result.getFeverMessage().getProductUpdate().getProductCommunicationElementImages().size());
        
        ProductCommunicationElementImageFeverDTO firstImage = result.getFeverMessage().getProductUpdate().getProductCommunicationElementImages().get(0);
        Assertions.assertEquals(1L, firstImage.getId());
        Assertions.assertEquals(ProductCommunicationElementsImagesType.LANDSCAPE, firstImage.getType());
        Assertions.assertEquals(Integer.valueOf(1), firstImage.getPosition());
        Assertions.assertEquals("Test Alt Text", firstImage.getAltText());
    }

    @Test
    public void sendProductChannelImages_emptyImages_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        ProductCommunicationElementsImagesDTO emptyImages = new ProductCommunicationElementsImagesDTO();
        
        when(msEventRepository.getProductCommunicationElementImages(PRODUCT_ID)).thenReturn(emptyImages);

        WebhookFeverDTO result = productWebhookService.sendProductChannelImages(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertTrue(result.getFeverMessage().getProductUpdate().getProductCommunicationElementImages().isEmpty());
    }

    @Test
    public void sendProductChannelImages_nullImages_ok() {
        WebhookFeverDTO webhookFever = generateWebhookMessage();
        
        when(msEventRepository.getProductCommunicationElementImages(PRODUCT_ID)).thenReturn(null);

        WebhookFeverDTO result = productWebhookService.sendProductChannelImages(webhookFever);

        Assertions.assertNotNull(result.getFeverMessage());
        Assertions.assertNotNull(result.getFeverMessage().getProductUpdate());
        Assertions.assertNull(result.getFeverMessage().getProductUpdate().getProductCommunicationElementImages());
    }

    private ProductCommunicationElementsTextsDTO generateProductTexts() {
        ProductCommunicationElementsTextsDTO texts = new ProductCommunicationElementsTextsDTO();
        
        ProductCommunicationElementTextDTO text1 = new ProductCommunicationElementTextDTO();
        text1.setType(ProductCommunicationElementTextsType.PRODUCT_NAME);
        text1.setLanguageId(1L);
        text1.setValue("Test Title");
        text1.setLanguage("English");
        
        texts.add(text1);
        
        ProductCommunicationElementTextDTO text2 = new ProductCommunicationElementTextDTO();
        text2.setType(ProductCommunicationElementTextsType.DESCRIPTION);
        text2.setLanguageId(2L);
        text2.setValue("Test Description");
        text2.setLanguage("Spanish");
        
        texts.add(text2);
        
        return texts;
    }

    private ProductCommunicationElementsImagesDTO generateProductImages() {
        ProductCommunicationElementsImagesDTO images = new ProductCommunicationElementsImagesDTO();
        
        ProductCommunicationElementImageDTO image1 = new ProductCommunicationElementImageDTO();
        image1.setId(1L);
        image1.setType(ProductCommunicationElementsImagesType.LANDSCAPE);
        image1.setPosition(Integer.valueOf(1));
        image1.setTagId(Integer.valueOf(1));
        image1.setValue("image1.jpg");
        image1.setAltText("Test Alt Text");
        image1.setTag("Main Image");
        image1.setLanguage("English");
        
        images.add(image1);
        
        ProductCommunicationElementImageDTO image2 = new ProductCommunicationElementImageDTO();
        image2.setId(2L);
        image2.setType(ProductCommunicationElementsImagesType.LANDSCAPE);
        image2.setPosition(Integer.valueOf(2));
        image2.setTagId(Integer.valueOf(2));
        image2.setValue("image2.jpg");
        image2.setAltText("Secondary Image");
        image2.setTag("Secondary Image");
        image2.setLanguage("English");
        
        images.add(image2);
        
        return images;
    }
}
