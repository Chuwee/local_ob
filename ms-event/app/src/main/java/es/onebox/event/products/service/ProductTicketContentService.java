package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.TicketType;
import es.onebox.event.common.services.CommonTicketCommunicationElementService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductTicketContentsConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.ProductTicketContentsDao;
import es.onebox.event.products.dao.couch.ProductTicketContent;
import es.onebox.event.products.dao.couch.ProductTicketContentTextDetail;
import es.onebox.event.products.dao.couch.ProductTicketContentValue;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductTicketContentListImageDTO;
import es.onebox.event.products.dto.ProductTicketContentListTextDTO;
import es.onebox.event.products.dto.ProductTicketContentTextDTO;
import es.onebox.event.products.enums.TicketContentImageType;
import es.onebox.event.products.lock.HazelcastLockRepository;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductTicketContentService {

    private final ProductTicketContentsDao productTicketContentsDao;
    private final ProductDao productDao;
    private final ProductLanguageDao productLanguageDao;
    private final CommonTicketCommunicationElementService commonTicketCommunicationElementService;
    private final StaticDataContainer staticDataContainer;
    private final HazelcastLockRepository hazelcastLockRepository;

    @Autowired
    public ProductTicketContentService(ProductTicketContentsDao productTicketContentsDao,
                                       ProductDao productDao,
                                       ProductLanguageDao productLanguageDao,
                                       CommonTicketCommunicationElementService commonTicketCommunicationElementService,
                                       StaticDataContainer staticDataContainer,
                                       HazelcastLockRepository hazelcastLockRepository) {
        this.productTicketContentsDao = productTicketContentsDao;
        this.productDao = productDao;
        this.productLanguageDao = productLanguageDao;
        this.commonTicketCommunicationElementService = commonTicketCommunicationElementService;

        this.staticDataContainer = staticDataContainer;
        this.hazelcastLockRepository = hazelcastLockRepository;
    }

    public void createOrUpdateText(Long productId, TicketType ticketType, ProductTicketContentListTextDTO productTicketContentListTextDTO) {
        validateValueLength(productTicketContentListTextDTO);
        validateProduct(productId);
        List<ProductLanguageRecord> productLanguageRecordList = validateLanguage(productId);

        try {
            hazelcastLockRepository.lockedExecutionProductTicketContents(() -> createOrUpdateTicketContentsText(productId, productTicketContentListTextDTO, ticketType, productLanguageRecordList)
                    , productId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    public ProductTicketContentListTextDTO getTexts(Long productId, TicketType ticketType) {
        validateProduct(productId);
        validateLanguage(productId);
        ProductTicketContent document = productTicketContentsDao.get(productId.toString());

        if (document == null || document.getTicketContentByType() == null || document.getTicketContentByType().isEmpty()) {
            return new ProductTicketContentListTextDTO();
        }

        Map<String, ProductTicketContentValue> ticketContentValueMap = document.getTicketContentByType().getOrDefault(ticketType, Map.of());

        return ProductTicketContentsConverter.toTextsDto(ticketContentValueMap);
    }

    public void createOrUpdateImage(Long productId, TicketType ticketType, ProductTicketContentListImageDTO productTicketContentListImageDTO) {
        CpanelProductRecord cpanelProductRecord = validateProduct(productId);
        validateLanguage(productId);
        List<ProductLanguageRecord> productLanguageRecordList = validateLanguage(productId);
        Integer entityId = cpanelProductRecord.getEntityid();

        try {
            hazelcastLockRepository.lockedExecutionProductTicketContents(() -> commonTicketCommunicationElementService.createOrUpdateTicketContentsImage(productId, entityId,
                    productTicketContentListImageDTO, ticketType, productLanguageRecordList), productId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    public ProductTicketContentListImageDTO getImages(Long productId, TicketType ticketType) {
        validateProduct(productId);
        ProductTicketContent document = productTicketContentsDao.get(productId.toString());

        if (document == null || document.getTicketContentByType() == null || document.getTicketContentByType().isEmpty()) {
            return new ProductTicketContentListImageDTO();
        }

        Map<String, ProductTicketContentValue> ticketContentValueMap = document.getTicketContentByType().getOrDefault(ticketType, Map.of());

        return ProductTicketContentsConverter.toImagesDto(ticketContentValueMap, staticDataContainer.getS3Repository());
    }

    public void deleteImage(Long productId, String language, TicketType ticketType, TicketContentImageType imageType) {
        validateProduct(productId);
        validateLanguage(productId);

        try {
            hazelcastLockRepository.lockedExecutionProductTicketContents(() ->
                    commonTicketCommunicationElementService.deleteTicketContentsImage(productId, ticketType, imageType, language), productId);
        } catch (OneboxRestException e) {
            throw e;
        } catch (Exception e) {
            throw new OneboxRestException(MsEventErrorCode.GENERIC_ERROR, e);
        }
    }

    private List<ProductLanguageRecord> validateLanguage(Long productId) {
        List<ProductLanguageRecord> productLanguages = productLanguageDao.findByProductId(productId);
        if (productLanguages == null || productLanguages.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }
        return productLanguages;
    }

    private CpanelProductRecord validateProduct(Long productId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        return cpanelProductRecord;
    }

    private ProductTicketContent createOrUpdateTicketContentsText(Long productId,
                                                                  ProductTicketContentListTextDTO productTicketContentListTextDTO,
                                                                  TicketType ticketType, List<ProductLanguageRecord> productLanguageRecordList) {

        ProductTicketContent ticketContentDocument = productTicketContentsDao.getOrCreate(productId);
        initializeContentType(ticketContentDocument, ticketType);

        for (ProductTicketContentTextDTO ticketContentTextDTO : productTicketContentListTextDTO) {
            String language = ticketContentTextDTO.getLanguage();

            if (!ticketContentDocument.getTicketContentByType().get(ticketType).containsKey(language)) {
                ticketContentDocument.getTicketContentByType().get(ticketType).put(language, new ProductTicketContentValue());
            }

            productLanguageRecordList.stream()
                    .filter(el -> el.getCode().equals(ticketContentTextDTO.getLanguage()))
                    .findAny()
                    .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.TICKET_CONTENT_LANGUAGE_NOT_MATCH));

            ProductTicketContentValue productTicketContentValue = ticketContentDocument.getTicketContentByType().get(ticketType).get(language);

            if (productTicketContentValue.getTexts() == null) {
                productTicketContentValue.setTexts(new ArrayList<>());
            }

            Optional<ProductTicketContentTextDetail> ticketContentTextDetail = productTicketContentValue.getTexts().stream()
                    .filter(el -> el.getType().equals(ticketContentTextDTO.getType()))
                    .findAny();

            if (ticketContentTextDetail.isPresent()) {
                ticketContentTextDetail.get().setValue(ticketContentTextDTO.getValue());
            } else {
                ProductTicketContentTextDetail productTicketContentTextDetail = new ProductTicketContentTextDetail();
                productTicketContentTextDetail.setType(ticketContentTextDTO.getType());
                productTicketContentTextDetail.setValue(ticketContentTextDTO.getValue());
                productTicketContentValue.getTexts().add(productTicketContentTextDetail);
            }
        }

        productTicketContentsDao.upsert(productId.toString(), ticketContentDocument);
        return ticketContentDocument;
    }

    private void initializeContentType(ProductTicketContent productTicketContent, TicketType ticketType) {
        if (productTicketContent.getTicketContentByType() == null) {
            productTicketContent.setTicketContentByType(new HashMap<>());
        }

        if (!productTicketContent.getTicketContentByType().containsKey(ticketType)) {
            productTicketContent.getTicketContentByType().put(ticketType, new HashMap<>());
        }
    }


    private void validateValueLength(ProductTicketContentListTextDTO productTicketContentListTextDTO) {
        int maxLength;
        for (ProductTicketContentTextDTO ticketContentTextDTO : productTicketContentListTextDTO) {
            String value = ticketContentTextDTO.getValue();
            switch (ticketContentTextDTO.getType()) {
                case NAME -> maxLength = 50;
                case DELIVERY_DETAIL -> maxLength = 500;
                default -> throw new OneboxRestException(MsEventErrorCode.TEXT_VALUE_EXCEEDS_MAX_LENGTH);
            }

            if (value.length() > maxLength) {
                throw new OneboxRestException(MsEventErrorCode.TEXT_VALUE_EXCEEDS_MAX_LENGTH);
            }
        }
    }
}


