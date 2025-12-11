package es.onebox.event.producttickettemplate.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductTicketLiteralsCouchDao;
import es.onebox.event.products.dao.couch.ProductTicketLiterals;
import es.onebox.event.products.dto.ProductTicketLiteralsDTO;
import es.onebox.event.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateFilter;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateLiteralElementFilter;
import es.onebox.event.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.event.producttickettemplate.converter.ProductTicketTemplateConverter;
import es.onebox.event.producttickettemplate.converter.ProductTicketTemplateLanguageConverter;
import es.onebox.event.producttickettemplate.dao.ProductTicketModelDao;
import es.onebox.event.producttickettemplate.dao.ProductTicketTemplateDao;
import es.onebox.event.producttickettemplate.dao.ProductTicketTemplateLanguageDao;
import es.onebox.event.producttickettemplate.domain.dto.CloneProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageRecord;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguagesDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLiteralDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLiteralsDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplatePageDTO;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketTemplateStatus;
import es.onebox.event.tickettemplates.dao.EntityContentLanguageDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketTemplate;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketTemplateLanguage;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateLanguageRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductTicketTemplateService {

    private final ProductTicketTemplateDao productTicketTemplateDao;
    private final ProductTicketLiteralsCouchDao productTicketLiteralsCouchDao;
    private final ProductTicketModelDao productTicketModelDao;
    private final ProductTicketTemplateLanguageDao productTicketTemplateLanguageDao;
    private final EntityContentLanguageDao entityContentLanguageDao;
    private final EntityDao entityDao;

    public ProductTicketTemplateService(ProductTicketTemplateDao productTicketTemplateDao,
                                        ProductTicketModelDao productTicketModelDao,
                                        ProductTicketTemplateLanguageDao productTicketTemplateLanguageDao,
                                        EntityContentLanguageDao entityContentLanguageDao,
                                        EntityDao entityDao,
                                        ProductTicketLiteralsCouchDao productTicketLiteralsCouchDao) {

        this.productTicketTemplateDao = productTicketTemplateDao;
        this.productTicketModelDao = productTicketModelDao;
        this.productTicketTemplateLanguageDao = productTicketTemplateLanguageDao;
        this.productTicketLiteralsCouchDao = productTicketLiteralsCouchDao;
        this.entityContentLanguageDao = entityContentLanguageDao;
        this.entityDao = entityDao;
    }

    public List<ProductTicketModelDTO> getAllModels() {

        return productTicketModelDao.getAllModelsSorted();
    }

    @MySQLRead
    public ProductTicketTemplateDTO getById(Integer id) {

        ProductTicketTemplateDTO productTicketTemplate = productTicketTemplateDao.findOneById(id)
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND));

        return productTicketTemplate.withLanguages(productTicketTemplateDao.getLanguages(
                productTicketTemplate.id()));
    }

    @MySQLWrite
    public Integer create(CreateProductTicketTemplate request) {

        validateRelations(request);

        CpanelProductTicketTemplateRecord createdTemplate = productTicketTemplateDao.insert(
                ProductTicketTemplateConverter.toRecord(request));

        productTicketTemplateLanguageDao.insert(ProductTicketTemplateLanguageConverter
                .toRecord(createdTemplate.getTemplateid(), request.defaultLanguageId()));

        return createdTemplate.getTemplateid();

    }

    @MySQLWrite
    public void update(Integer id, UpdateProductTicketTemplate request) {

        CpanelProductTicketTemplateRecord found = getProductTicketTemplateSimple(id);

        validateModel(request.modelId());
        validateNameNotInUseForEntity(request.name(), found.getEntityid(), found.getName());

        updateLanguages(found.getTemplateid(), found.getEntityid(), request.defaultLanguageId(),
                request.selectedLanguageIds());

        productTicketTemplateDao.update(ProductTicketTemplateConverter.toRecord(request, found));
    }

    private void updateLanguages(Integer productTicketTemplateId, Integer entityId, Integer defaultLanguageId,
                                 List<Integer> selectedLanguageIds) {

        if (!selectedLanguageIds.contains(defaultLanguageId)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE);
        }

        List<Integer> contentLanguageIds = entityContentLanguageDao
                .getEntityContentLanguageIds(entityId);

        productTicketTemplateLanguageDao.deleteByProductTicketTemplateId(productTicketTemplateId);

        Set<CpanelProductTicketTemplateLanguageRecord> updatedLanguages = selectedLanguageIds.stream()
                .filter(contentLanguageIds::contains)
                .map(languageId -> ProductTicketTemplateLanguageConverter
                        .toRecord(productTicketTemplateId, languageId, defaultLanguageId))
                .collect(Collectors.toSet());
        productTicketTemplateLanguageDao
                .insertBatch(updatedLanguages);
    }

    @MySQLWrite
    public void delete(Integer id) {

        CpanelProductTicketTemplateRecord found = getProductTicketTemplateSimple(id);

        if (ProductTicketTemplateStatus.DELETED.getId().equals(found.getStatus().intValue())) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND);
        }

        // TODO: When template gets related to any product, it shouldn't be allowed to
        // delete it.
        // This validation will come in upcoming tasks
        productTicketTemplateDao
                .update(ProductTicketTemplateConverter.toRecord(found, ProductTicketTemplateStatus.DELETED));

    }

    @MySQLRead
    public ProductTicketTemplatePageDTO find(ProductTicketTemplateFilter filter) {

        // No validations will be done as yet no filters have been specified.
        Metadata metadata = MetadataBuilder.build(filter, productTicketTemplateDao.getTotalCount(filter));
        List<ProductTicketTemplateDTO> results = productTicketTemplateDao.find(filter);
        return new ProductTicketTemplatePageDTO(
                results,
                metadata);
    }

    public ProductTicketLiteralsDTO getLiterals(Integer templateId, ProductTicketTemplateLiteralElementFilter filter) {
        validateTemplate(templateId);

        String language = filter.getLanguage();

        if (language == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_LITERALS_LANGUAGE_NOT_FOUND);
        }

        ProductTicketLiterals defaultLiteralsCouch = productTicketLiteralsCouchDao.get(language);
        if (defaultLiteralsCouch == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_DEFAULT_LITERALS_NOT_FOUND);
        }

        ProductTicketLiterals productTicketLiterals = productTicketLiteralsCouchDao.get(templateId.toString(), language);

        return mergeLiterals(defaultLiteralsCouch, productTicketLiterals);
    }

    public void updateLiteral(Integer templateId, ProductTicketTemplateLiteralsDTO literalListDTO) {

        getAndCheckTicketTemplate(templateId);

        if (literalListDTO == null || literalListDTO.isEmpty()) {
            return;
        }

        Map<String, List<ProductTicketTemplateLiteralDTO>> literalsByLanguage = literalListDTO.stream()
                .collect(Collectors.groupingBy(ProductTicketTemplateLiteralDTO::getLanguage));

        Set<String> languages = literalsByLanguage.keySet();

        Map<String, ProductTicketLiterals> existingLiterals = new java.util.HashMap<>();
        for (String language : languages) {
            ProductTicketLiterals literals = productTicketLiteralsCouchDao.get(templateId.toString(), language);
            existingLiterals.put(language, literals != null ? literals : new ProductTicketLiterals());
        }

        Map<String, ProductTicketLiterals> updatedLiterals = new java.util.HashMap<>();
        for (Map.Entry<String, List<ProductTicketTemplateLiteralDTO>> entry : literalsByLanguage.entrySet()) {
            String language = entry.getKey();
            ProductTicketLiterals productTicketLiterals = existingLiterals.get(language);

            entry.getValue().stream()
                    .filter(literal -> literal.getCode() != null && literal.getValue() != null)
                    .forEach(literal -> productTicketLiterals.put(literal.getCode(), literal.getValue()));

            updatedLiterals.put(language, productTicketLiterals);
        }

        for (Map.Entry<String, ProductTicketLiterals> entry : updatedLiterals.entrySet()) {
            productTicketLiteralsCouchDao.upsert(templateId, entry.getKey(), entry.getValue());
        }
    }

    @MySQLRead
    public ProductTicketTemplateLanguagesDTO getProductTicketTemplateLanguages(Long templateId) {
        CpanelProductTicketTemplateRecord productTicketTemplateRecord = productTicketTemplateDao.findById(templateId.intValue());
        if (productTicketTemplateRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND);
        }
        List<ProductTicketTemplateLanguageRecord> productTicketTemplateLanguageRecordRecords
                = productTicketTemplateLanguageDao.findByProductTicketTemplateId(templateId.intValue());
        if (productTicketTemplateLanguageRecordRecords == null || productTicketTemplateLanguageRecordRecords.isEmpty()) {
            return new ProductTicketTemplateLanguagesDTO();
        }
        return ProductTicketTemplateLanguageConverter.toDto(productTicketTemplateLanguageRecordRecords);
    }


    @MySQLWrite
    public IdDTO cloneTemplate(Integer templateId, CloneProductTicketTemplateDTO request) {

        CpanelProductTicketTemplateRecord existingTemplate = getProductTicketTemplateSimple(templateId);
        CpanelProductTicketTemplateRecord cloned = cloneTemplate(
                request, existingTemplate);
        cloneLanguages(existingTemplate.getTemplateid(), cloned.getTemplateid(), cloned.getEntityid());

        return new IdDTO(cloned.getTemplateid().longValue());
    }

    private void cloneLanguages(Integer existingTemplateId, Integer clonedTemplateId, Integer entityId) {

        List<ProductTicketTemplateLanguageRecord> existingLanguages = productTicketTemplateLanguageDao
                .findByProductTicketTemplateId(existingTemplateId);

        if (existingLanguages != null && !existingLanguages.isEmpty()) {
            Set<CpanelProductTicketTemplateLanguageRecord> clonedLanguages = new HashSet<>();
            if (entityId != null) {
                List<Integer> contentLanguageIds = entityContentLanguageDao
                        .getEntityContentLanguageIds(entityId);
                existingLanguages.stream()
                        .filter(templateLang -> contentLanguageIds.contains(templateLang.getLanguageid()))
                        .forEach(r -> convertToCloneLanguage(clonedTemplateId, r, clonedLanguages));
                validateAtLeastOneIsDefault(clonedTemplateId, entityId, clonedLanguages);
            } else {
                existingLanguages.forEach(r -> convertToCloneLanguage(clonedTemplateId, r, clonedLanguages));
            }
            productTicketTemplateLanguageDao.insertBatch(clonedLanguages);
        }
    }

    private void convertToCloneLanguage(Integer clonedTemplateId, CpanelProductTicketTemplateLanguageRecord r,
                                        Set<CpanelProductTicketTemplateLanguageRecord> clonedLanguages) {
        r.setTemplateid(clonedTemplateId);
        r.changed(
                CpanelProductTicketTemplateLanguage.CPANEL_PRODUCT_TICKET_TEMPLATE_LANGUAGE.CREATE_DATE,
                false);
        r.changed(
                CpanelProductTicketTemplateLanguage.CPANEL_PRODUCT_TICKET_TEMPLATE_LANGUAGE.UPDATE_DATE,
                false);
        clonedLanguages.add(r);
    }

    private void validateAtLeastOneIsDefault(Integer clonedTemplateId, Integer entityId,
                                             Set<CpanelProductTicketTemplateLanguageRecord> clonedLanguages) {

        if (clonedLanguages.stream().noneMatch(t -> ConverterUtils.isByteAsATrue(t.getIsdefault()))) {
            Integer entityDefaultLanguage = entityDao.findById(entityId).getIdiomadefecto();
            CpanelProductTicketTemplateLanguageRecord defaultLanguage = clonedLanguages.stream()
                    .filter(templateLang -> templateLang.getLanguageid().equals(entityDefaultLanguage))
                    .findFirst().orElse(null);
            if (defaultLanguage != null) {
                defaultLanguage.setIsdefault((byte) 1);
            } else {
                CpanelProductTicketTemplateLanguageRecord recordDefaultLanguage = ProductTicketTemplateLanguageConverter
                        .toRecord(
                                clonedTemplateId, entityDefaultLanguage);
                clonedLanguages.add(recordDefaultLanguage);
            }
        }
    }

    private CpanelProductTicketTemplateRecord cloneTemplate(CloneProductTicketTemplateDTO request,
                                                            CpanelProductTicketTemplateRecord existingTemplate) {

        CpanelProductTicketTemplateRecord cloned = existingTemplate.copy();

        // The id has to be null to set it as null to increment automatically
        cloned.setTemplateid(null);
        // The audit fields are set false to allow the own jOOq to build the insert
        // statement without those columns, as these are controlled by MySQL already
        cloned.changed(CpanelProductTicketTemplate.CPANEL_PRODUCT_TICKET_TEMPLATE.CREATE_DATE, false);
        cloned.changed(CpanelProductTicketTemplate.CPANEL_PRODUCT_TICKET_TEMPLATE.UPDATE_DATE, false);
        if (request.entityId() != null && request.entityId().intValue() != cloned.getEntityid()) {
            validateEntity(request.entityId());
            cloned.setEntityid(request.entityId());
        }
        validateNameNotInUseForEntity(request.name(), cloned.getEntityid(), null);
        cloned.setName(request.name());

        return productTicketTemplateDao.insert(cloned);
    }

	private void validateRelations(CreateProductTicketTemplate request) {

        validateEntity(request.entityId());
        validateModel(request.modelId());
        validateNameNotInUseForEntity(request.name(), request.entityId(), null);
    }

    private CpanelProductTicketTemplateRecord getProductTicketTemplateSimple(Integer id) {

        try {
            return productTicketTemplateDao.getByIdSimple(id);
        } catch (EntityNotFoundException e) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND);
        }
    }

    private void validateEntity(Integer entityId) {
        if (!entityDao.exists(entityId)) {
            throw new OneboxRestException(MsEventErrorCode.ENTITY_NOT_FOUND);
        }
    }

    private void validateTemplate(Integer templateId) {
        if (productTicketTemplateDao.findOneById(templateId).isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND);
        }
    }

    private void validateModel(Integer modelId) {

        if (!productTicketModelDao.exists(modelId)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_MODEL_NOT_FOUND);
        }
    }

    private ProductTicketLiteralsDTO mergeLiterals(ProductTicketLiterals defaults,
                                                   ProductTicketLiterals overrides) {
        ProductTicketLiteralsDTO mergedLiterals = new ProductTicketLiteralsDTO();
        if (defaults != null) mergedLiterals.putAll(defaults);
        if (overrides != null) mergedLiterals.putAll(overrides);

        return mergedLiterals;
    }

    private void validateNameNotInUseForEntity(String newName, int entityId, String oldName) {
        if (newName != null && (oldName == null || !oldName.equals(newName))
                && productTicketTemplateDao.nameAlreadySetForEntity(newName, entityId)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NAME_ALREADY_SET);
        }
    }

    private void getAndCheckTicketTemplate(Integer templateId) {
        productTicketTemplateDao.findOneById(templateId)
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND));
    }
}
