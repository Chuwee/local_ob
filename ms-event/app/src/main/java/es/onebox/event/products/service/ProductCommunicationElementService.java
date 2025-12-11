package es.onebox.event.products.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductCommunicationElementsConverter;
import es.onebox.event.products.dao.ProductCommunicationElementCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.CreateProductCommunicationElementImageDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementTextDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementsTextsDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.lock.HazelcastLockRepository;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductCommunicationElementService {

    private final ProductDao productDao;
    private final ProductLanguageDao productLanguageDao;
    private final ProductCommunicationElementCouchDao productCommunicationElementCouchDao;
    private final S3BinaryRepository s3OneboxRepository;
    private final StaticDataContainer staticDataContainer;
    private final EntitiesRepository entitiesRepository;
    private final RefreshDataService refreshDataService;
    private final HazelcastLockRepository hazelcastLockRepository;
    private final WebhookService webhookService;

    @Autowired
    public ProductCommunicationElementService(ProductDao productDao,
                                              ProductLanguageDao productLanguageDao,
                                              ProductCommunicationElementCouchDao productCommunicationElementCouchDao,
                                              S3BinaryRepository s3OneboxRepository,
                                              StaticDataContainer staticDataContainer,
                                              EntitiesRepository entitiesRepository,
                                              RefreshDataService refreshDataService,
                                              HazelcastLockRepository hazelcastLockRepository, WebhookService webhookService) {
        this.productDao = productDao;
        this.productLanguageDao = productLanguageDao;
        this.productCommunicationElementCouchDao = productCommunicationElementCouchDao;
        this.s3OneboxRepository = s3OneboxRepository;
        this.staticDataContainer = staticDataContainer;
        this.entitiesRepository = entitiesRepository;
        this.refreshDataService = refreshDataService;
        this.hazelcastLockRepository = hazelcastLockRepository;
        this.webhookService = webhookService;
    }

    public ProductCommunicationElementsTextsDTO createProductCommunicationElementsTexts(Long productId, CreateProductCommunicationElementsTextsDTO createProductCommunicationElementsTexts) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null || cpanelProductRecord.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        try {
            return hazelcastLockRepository.lockedExecutionProductCommunicationElements(() -> changeProductCommunicationElementsTexts(productId, createProductCommunicationElementsTexts), productId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private ProductCommunicationElementsTextsDTO changeProductCommunicationElementsTexts(Long productId,
                                                                                         CreateProductCommunicationElementsTextsDTO createProductCommunicationElementsTextsDTO) {
        ProductCommunicationElementDocument document = productCommunicationElementCouchDao.get(productId.toString());
        if (document == null) {
            document = new ProductCommunicationElementDocument();
            document.setProductId(productId);
        }
        if (document.getLanguageElements() == null) {
            document.setLanguageElements(new HashMap<>());
        }

        //If there are old languages set, replace them
        Map<String, ProductCommunicationElement> fixedLanguageElements = new HashMap<>();
        document.getLanguageElements().forEach((key, value) -> {
            String correctedKey = key.replace("-", "_");
            fixedLanguageElements.put(correctedKey, value);
        });
        document.setLanguageElements(fixedLanguageElements);

        for (CreateProductCommunicationElementTextDTO createProductCommunicationElementTextDTO : createProductCommunicationElementsTextsDTO) {
            String languageKey = createProductCommunicationElementTextDTO.getLanguage();

            if (!document.getLanguageElements().containsKey(languageKey)) {
                document.getLanguageElements().put(languageKey, new ProductCommunicationElement());
            }

            if (document.getLanguageElements().get(languageKey).getTexts() == null) {
                document.getLanguageElements().get(languageKey).setTexts(new ArrayList<>());
            }

            if (document.getLanguageElements().get(languageKey).getTexts().stream()
                    .noneMatch(el -> el.getType().equals(createProductCommunicationElementTextDTO.getType().name()))) {
                ProductCommunicationElementDetail productCommunicationElementDetail =
                        new ProductCommunicationElementDetail(createProductCommunicationElementTextDTO.getType().name(),
                                createProductCommunicationElementTextDTO.getValue());
                document.getLanguageElements().get(languageKey).getTexts().add(productCommunicationElementDetail);
            } else {
                document.getLanguageElements().get(languageKey).getTexts().stream()
                        .filter(el -> el.getType().equals(createProductCommunicationElementTextDTO.getType().name()))
                        .findFirst()
                        .get()
                        .setValue(createProductCommunicationElementTextDTO.getValue());
            }
        }
        productCommunicationElementCouchDao.upsert(productId.toString(), document);

        document = productCommunicationElementCouchDao.get(productId.toString());
        postUpdateProduct(productId, NotificationSubtype.PRODUCT_CHANNEL_LITERALS);

        return ProductCommunicationElementsConverter.toTextsDto(document);
    }

    public ProductCommunicationElementsTextsDTO getProductCommunicationElementsTexts(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null || cpanelProductRecord.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }

        ProductCommunicationElementDocument document = productCommunicationElementCouchDao.get(productId.toString());
        if (document == null || document.getLanguageElements() == null || document.getLanguageElements().isEmpty()) {
            return null;
        }
        return ProductCommunicationElementsConverter.toTextsDto(document);
    }

    public void createProductCommunicationElementsImages(Long productId, CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null || cpanelProductRecord.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }

        try {
            hazelcastLockRepository.lockedExecutionProductCommunicationElements(() -> changeProductCommunicationElementsImages(productId, createProductCommunicationElementsImagesDTO, productLanguageRecords, cpanelProductRecord), productId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private boolean changeProductCommunicationElementsImages(Long productId, CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImagesDTO, List<ProductLanguageRecord> productLanguageRecords, CpanelProductRecord cpanelProductRecord) {
        ProductCommunicationElementDocument document = productCommunicationElementCouchDao.get(productId.toString());
        Map<Long, String> langaugesMap = entitiesRepository.getAllIdAndCodeLanguages();

        if (document == null) {
            document = new ProductCommunicationElementDocument();
            document.setProductId(productId);
        }
        if (document.getLanguageElements() == null) {
            document.setLanguageElements(new HashMap<>());
        }

        for (CreateProductCommunicationElementImageDTO createProductCommunicationElementImageDTO : createProductCommunicationElementsImagesDTO) {
            String languageCode = createProductCommunicationElementImageDTO.getLanguage();
            if (productLanguageRecords.stream().noneMatch(el -> el.getCode().equals(languageCode))) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGE_NOT_RELATED);
            }

            if (!document.getLanguageElements().containsKey(createProductCommunicationElementImageDTO.getLanguage())) {
                document.getLanguageElements().put(createProductCommunicationElementImageDTO.getLanguage(), new ProductCommunicationElement());
            }
            if (document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()) == null) {
                document.getLanguageElements().put(createProductCommunicationElementImageDTO.getLanguage(), new ProductCommunicationElement());
            }
            if (document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()).getImages() == null) {
                document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()).setImages(new ArrayList<>());
            }

            EntityDTO entityDTO = entitiesRepository.getEntity(cpanelProductRecord.getEntityid());
            Optional<Map.Entry<Long, String>> langOpt = langaugesMap.entrySet().stream().filter(la -> la.getValue().equals(languageCode)).findFirst();
            if (langOpt.isEmpty()) {
                throw new OneboxRestException(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE);
            }
            String path = uploadFile(productId, langOpt.get().getKey(), createProductCommunicationElementImageDTO.getType().name(), createProductCommunicationElementImageDTO.getValue(), entityDTO.getOperator().getId(), cpanelProductRecord.getEntityid(), createProductCommunicationElementImageDTO.getPosition());

            if (document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()).getImages().stream().noneMatch(el -> el.getPosition() != null && el.getPosition().equals(createProductCommunicationElementImageDTO.getPosition()))) {
                ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
                productCommunicationElementDetail.setType(createProductCommunicationElementImageDTO.getType().name());
                productCommunicationElementDetail.setValue(path);
                productCommunicationElementDetail.setPosition(createProductCommunicationElementImageDTO.getPosition());
                productCommunicationElementDetail.setAltText(createProductCommunicationElementImageDTO.getAltText());
                document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()).getImages().add(productCommunicationElementDetail);
            } else {
                Optional<ProductCommunicationElementDetail> productCommunicationElementDetail = document.getLanguageElements().get(createProductCommunicationElementImageDTO.getLanguage()).getImages().stream().filter(el -> el.getPosition().equals(createProductCommunicationElementImageDTO.getPosition())).findFirst();
                ProductCommunicationElementDetail communicationElementDetail = productCommunicationElementDetail.orElse(null);
                if (Objects.nonNull(communicationElementDetail)) {
                    communicationElementDetail.setValue(path);
                    communicationElementDetail.setAltText(createProductCommunicationElementImageDTO.getAltText());
                }
            }
        }
        productCommunicationElementCouchDao.upsert(productId.toString(), document);
        postUpdateProduct(productId, NotificationSubtype.PRODUCT_CHANNEL_IMAGES);
        return true;
    }

    public ProductCommunicationElementsImagesDTO getProductCommunicationElementsImages(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null || cpanelProductRecord.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }

        ProductCommunicationElementDocument document = productCommunicationElementCouchDao.get(productId.toString());
        if (document == null || document.getLanguageElements() == null || document.getLanguageElements().isEmpty()) {
            return null;
        }
        return ProductCommunicationElementsConverter.toImagesDto(document, staticDataContainer.getS3Repository());
    }

    private String buildFileName(Long languageId, String type, Integer position, ImageFormat format) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));
        return new StringBuilder().append(type)
                .append("_").append(languageId)
                .append("_").append(position)
                .append("_").append(timestamp)
                .append(".").append(format).toString();
    }

    private String uploadFile(final Long productId, Long languageId, String type, String content, Integer operatorId, Integer entityId, Integer position) {
        String fileName = buildFileName(languageId, type, position, ImageFormat.JPG);
        return CommunicationElementsUtils.uploadProductImage(s3OneboxRepository, fileName, languageId.intValue(),
                productId.intValue(), content, operatorId, entityId, fileName);
    }

    private void deleteFile(final Long productId, Long languageId, String type, Long operatorId, Long entityId, Integer position) {
        String fileName = buildFileName(languageId, type, position, ImageFormat.JPG);
        CommunicationElementsUtils.deleteProductImage(s3OneboxRepository, languageId, productId, operatorId, entityId, fileName);
    }

    public void deleteProductCommunicationElementsImages(Long productId, String language, ProductCommunicationElementsImagesType type, Long position) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null || cpanelProductRecord.getState().equals(ProductState.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductLanguageRecord> productLanguageRecords = productLanguageDao.findByProductId(productId);
        if (productLanguageRecords == null || productLanguageRecords.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        } else if (productLanguageRecords.stream().noneMatch(pl -> pl.getCode().equals(language))) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGE_NOT_RELATED);
        }

        ProductCommunicationElementDocument document = productCommunicationElementCouchDao.get(productId.toString());
        if (document == null || document.getLanguageElements() == null || document.getLanguageElements().isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_COMMUNICATIONS_ELEMENTS_NOT_FOUND);
        }

        if (document.getLanguageElements().get(language) == null || document.getLanguageElements().get(language).getImages().isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_COMMUNICATIONS_ELEMENTS_LANGUAGE_NOT_FOUND);
        }

        Optional<ProductCommunicationElementDetail> productCommunicationElementDetailOpt = document.getLanguageElements().get(language).getImages().stream().filter(el -> el.getType().equals(type.name()) && el.getPosition().equals(position.intValue())).findFirst();
        if (productCommunicationElementDetailOpt.isPresent()) {
            Map<Long, String> langaugesMap = entitiesRepository.getAllIdAndCodeLanguages();
            Optional<Map.Entry<Long, String>> languageIdOpt = langaugesMap.entrySet().stream().filter(la -> la.getValue().equals(language)).findFirst();
            if (languageIdOpt.isEmpty()) {
                throw new OneboxRestException(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE);
            }

            EntityDTO entityDTO = entitiesRepository.getEntity(cpanelProductRecord.getEntityid());

            ProductCommunicationElementDetail productCommunicationElementDetail = productCommunicationElementDetailOpt.get();
            document.getLanguageElements().get(language).getImages().remove(productCommunicationElementDetail);
            productCommunicationElementCouchDao.upsert(productId.toString(), document);

            deleteFile(productId, languageIdOpt.get().getKey(), productCommunicationElementDetail.getType(), entityDTO.getOperator().getId().longValue(), cpanelProductRecord.getEntityid().longValue(), productCommunicationElementDetail.getPosition());
        }
        postUpdateProduct(productId, NotificationSubtype.PRODUCT_CHANNEL_IMAGES);
    }

    private void postUpdateProduct(Long productId, NotificationSubtype notificationSubtype) {
        refreshDataService.refreshProduct(productId);
        webhookService.sendProductNotification(productId, notificationSubtype);
    }
}
