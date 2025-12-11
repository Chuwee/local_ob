package es.onebox.event.tickettemplates;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.core.utils.file.ImageUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.communicationelements.enums.TicketContentImagePrinterType;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.enums.TicketFormat;
import es.onebox.event.events.enums.TicketTemplateStatus;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.tickettemplates.converter.TicketTemplateCommunicationElementConverter;
import es.onebox.event.tickettemplates.converter.TicketTemplateConverter;
import es.onebox.event.tickettemplates.dao.EntityContentLanguageDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateCommunicationElementDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLanguageDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLiteralDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLiteralTranslationDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateModelDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.tickettemplates.dto.CloneTicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.CommunicationElementDTO;
import es.onebox.event.tickettemplates.dto.TicketCommunicationElementFilter;
import es.onebox.event.tickettemplates.dto.TicketLiteralElementFilter;
import es.onebox.event.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateLiteralDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;
import es.onebox.event.tickettemplates.dto.TicketTemplatesDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplatesFilter;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketTraduccionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelModeloTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaTicketRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;
import static es.onebox.core.exception.CoreErrorCode.NOT_FOUND;
import static es.onebox.event.communicationelements.utils.CommunicationElementsUtils.get200dpiImgPath;
import static es.onebox.event.exception.MsEventErrorCode.INVALID_NAME_CONFLICT;
import static es.onebox.event.exception.MsEventErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE;

@Service
public class TicketTemplateService {

    private static final String DEFAULT_PREFIX = "Copia ";
    private static final List<Integer> DEFAULT_TICKET_TEMPLATES_FORMAT = List.of(TicketFormat.PDF.getFormat(), TicketFormat.ZPL.getFormat()); //PDF, PRINTER

    private final TicketTemplateDao ticketTemplateDao;
    private final TicketTemplateModelDao ticketTemplateModelDao;
    private final TicketTemplateLanguageDao ticketTemplateLanguageDao;
    private final TicketTemplateCommunicationElementDao ticketCommunicationElementDao;
    private final TicketTemplateLiteralDao ticketTemplateLiteralDao;
    private final TicketTemplateLiteralTranslationDao ticketTemplateLiteralTranslationDao;
    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final EntityContentLanguageDao entityContentLanguageDao;
    private final EntityDao entityDao;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public TicketTemplateService(TicketTemplateDao ticketTemplateDao,
                                 TicketTemplateModelDao ticketTemplateModelDao,
                                 TicketTemplateLanguageDao ticketTemplateLanguageDao,
                                 TicketTemplateCommunicationElementDao ticketCommunicationElementDao,
                                 TicketTemplateLiteralDao ticketTemplateLiteralDao,
                                 TicketTemplateLiteralTranslationDao ticketTemplateLiteralTranslationDao,
                                 StaticDataContainer staticDataContainer,
                                 S3BinaryRepository s3OneboxRepository,
                                 ItemDescSequenceDao itemDescSequenceDao,
                                 DescPorIdiomaDao descPorIdiomaDao,
                                 EntityContentLanguageDao entityContentLanguageDao,
                                 EntityDao entityDao, EntitiesRepository entitiesRepository) {
        this.ticketTemplateDao = ticketTemplateDao;
        this.ticketTemplateModelDao = ticketTemplateModelDao;
        this.ticketTemplateLanguageDao = ticketTemplateLanguageDao;
        this.ticketCommunicationElementDao = ticketCommunicationElementDao;
        this.ticketTemplateLiteralDao = ticketTemplateLiteralDao;
        this.ticketTemplateLiteralTranslationDao = ticketTemplateLiteralTranslationDao;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.entityContentLanguageDao = entityContentLanguageDao;
        this.entityDao = entityDao;
        this.entitiesRepository = entitiesRepository;
    }

    @MySQLRead
    public TicketTemplateDTO getTicketTemplate(Long ticketTemplateId) {
        TicketTemplateRecord templateRecord = getAndCheckTicketTemplate(ticketTemplateId);
        if (templateRecord.getModelFormat() != null && TicketFormat.HARD_TICKET_PDF.equals(TicketFormat.byId(templateRecord.getModelFormat()))) {
            validateTicketTemplateFormat(templateRecord.getIdentidad(), templateRecord.getModelFormat());
        }

        List<CpanelIdiomaPlantillaTicketRecord> langs = ticketTemplateDao.getTicketTemplateLanguages(ticketTemplateId);

        return TicketTemplateConverter.convert(templateRecord, langs);
    }

    @MySQLRead
    public TicketTemplatesDTO findTicketTemplates(TicketTemplatesFilter filter) {
        validateTicketTemplateFilter(filter);

        TicketTemplatesDTO templatesDTO = new TicketTemplatesDTO();
        templatesDTO.setData(
                ticketTemplateDao.find(filter).stream()
                        .map(record -> TicketTemplateConverter.convert(record, null))
                        .collect(Collectors.toList()));
        templatesDTO.setMetadata(MetadataBuilder.build(filter, ticketTemplateDao.countByFilter(filter)));
        return templatesDTO;
    }

    private void validateTicketTemplateFilter(TicketTemplatesFilter filter) {
        if (filter.getOperatorId() == null || filter.getOperatorId() <= 0) {
            throw new OneboxRestException(BAD_PARAMETER,
                    "operator Id is mandatory and must be above 0", null);
        }

        if (filter.getEntityId() != null) {
            if (CollectionUtils.isEmpty(filter.getFormat())) {
                filter.setFormat(new ArrayList<>(DEFAULT_TICKET_TEMPLATES_FORMAT));
                EntityDTO entity = entitiesRepository.getEntity(filter.getEntityId().intValue());
                if (BooleanUtils.isTrue(entity.getAllowHardTicketPDF()) && !filter.getFormat().contains(TicketFormat.HARD_TICKET_PDF.getFormat())) {
                    filter.getFormat().add(TicketFormat.HARD_TICKET_PDF.getFormat());
                }
            } else if (filter.getFormat().contains(TicketFormat.HARD_TICKET_PDF.getFormat())) {
                validateVisibilityTicketTemplateFormatHardTicketPdf(filter.getEntityId().intValue());
            }
        }

    }

    @MySQLWrite
    public Integer createTicketTemplate(TicketTemplateDTO templateDTO) {
        Long entityId = templateDTO.getEntity().getId();
        checkTemplateName(templateDTO.getName(), null, entityId.intValue());
        validateTicketTemplateFormat(entityId.intValue(), templateDTO.getDesign());

        CpanelPlantillaTicketRecord template = new CpanelPlantillaTicketRecord();
        template.setNombre(templateDTO.getName());
        template.setIdentidad(entityId.intValue());
        template.setIdmodelo(templateDTO.getDesign().getId().intValue());
        template.setEstado(TicketTemplateStatus.ACTIVE.getId());

        CpanelModeloTicketRecord model = getAndCheckModel(templateDTO.getDesign().getId());
        List<TicketTemplateRecord> entityFormatTemplates = getEntityTemplates(entityId, model.getFormato().intValue());
        template.setAsignacionautomatica(ConverterUtils.isTrueAsByte(entityFormatTemplates.isEmpty()));

        CpanelPlantillaTicketRecord insert = ticketTemplateDao.insert(template);

        CpanelIdiomaPlantillaTicketRecord templateLanguage = new CpanelIdiomaPlantillaTicketRecord();
        templateLanguage.setIdplantilla(insert.getIdplantilla());
        templateLanguage.setIdidioma(templateDTO.getDefaultLanguage());
        templateLanguage.setDefecto((byte) 1);
        ticketTemplateLanguageDao.insert(templateLanguage);

        return insert.getIdplantilla();
    }

    private void validateTicketTemplateFormat(Integer entityId, TicketTemplateDesignDTO design) {
        if (design != null && design.getFormat() != null) {
           validateTicketTemplateFormat(entityId, design.getFormat());
        }
    }

    private void validateTicketTemplateFormat(Integer entityId, Integer format) {
        if (format.equals(TicketFormat.HARD_TICKET_PDF.getFormat())) {
            validateVisibilityTicketTemplateFormatHardTicketPdf(entityId);
        }
    }

    private void validateVisibilityTicketTemplateFormatHardTicketPdf(Integer entityId) {
        EntityDTO entity = entitiesRepository.getEntity(entityId);
        if (BooleanUtils.isFalse(entity.getAllowHardTicketPDF())) {
            throw new OneboxRestException(MsEventErrorCode.TICKET_TEMPLATE_FORMAT_NOT_AVAILABLE);
        }
    }

    @MySQLWrite
    public Long cloneTicketTemplate(Long ticketTemplateId, CloneTicketTemplateDTO body) {
        TicketTemplateRecord templateRecord = getAndCheckTicketTemplate(ticketTemplateId);
        CpanelPlantillaTicketRecord newRecord = cloneTicketTemplateRecord(body, templateRecord);
        Long newTicketTemplateId = newRecord.getIdplantilla().longValue();
        cloneTicketTemplateLanguageRecord(ticketTemplateId, newTicketTemplateId, body.getEntityId());
        // fetch source comElements and take out those not matching the target template languages
        List<CommunicationElementDTO> comElems = this.findCommunicationElements(ticketTemplateId, null);
        comElems = comElems.stream()
                        .filter(comElem-> ticketTemplateDao.getTicketTemplateLanguages(newTicketTemplateId)
                                        .stream()
                                        .map(CpanelIdiomaPlantillaTicketRecord::getIdidioma)
                                        .collect(Collectors.toList())
                                        .contains(staticDataContainer.getLanguageByCode(comElem.getLanguage())))
                        .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(comElems)) {
            this.upsertCommunicationElements(newTicketTemplateId, comElems);
        }
        List<TicketTemplateLiteralDTO> literals = this.findLiterals(ticketTemplateId, null);
        if (CollectionUtils.isNotEmpty(literals)) {
            this.updateLiterals(newTicketTemplateId, literals);
        }
        return newTicketTemplateId;
    }


    @MySQLWrite
    public void updateTicketTemplate(Long ticketTemplateId, TicketTemplateDTO templateDTO) {
        TicketTemplateRecord templateRecord = getAndCheckTicketTemplate(ticketTemplateId);

        Integer entityId = templateRecord.getIdentidad();
        checkTemplateName(templateDTO.getName(), templateRecord.getNombre(), entityId);
        validateTicketTemplateFormat(entityId, templateDTO.getDesign());

        //If changes to default, disable others of this format for the entity
        if (CommonUtils.isTrue(templateDTO.getDefault()) &&
                !CommonUtils.isTrue(templateRecord.getAsignacionautomatica())) {
            List<TicketTemplateRecord> entityFormatTemplates = getEntityTemplates(
                    entityId.longValue(), templateRecord.getModelFormat());
            for (TicketTemplateRecord entityFormatTemplate : entityFormatTemplates) {
                entityFormatTemplate.setAsignacionautomatica((byte) 0);
                ticketTemplateDao.update(entityFormatTemplate);
            }
            templateRecord.setAsignacionautomatica((byte) 1);
        }

        if (templateDTO.getDesign() != null && templateDTO.getDesign().getId() != null) {
            getAndCheckModel(templateDTO.getDesign().getId());
            templateRecord.setIdmodelo(templateDTO.getDesign().getId().intValue());
        }

        updateTicketLanguages(ticketTemplateId, templateDTO.getDefaultLanguage(), templateDTO.getSelectedLanguageIds());

        TicketTemplateConverter.updateRecord(templateRecord, templateDTO);
        if (templateRecord.changed()) {
            ticketTemplateDao.update(templateRecord);
        }
    }

    @MySQLWrite
    public void deleteTicketTemplate(Long ticketTemplateId) {
        TicketTemplateRecord ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        List<TicketTemplateRecord> entityTemplates = getEntityTemplates(ticketTemplate.getIdentidad().longValue(), null);
        if (entityTemplates.size() > 1 && CommonUtils.isTrue(ticketTemplate.getAsignacionautomatica())) {
            throw new OneboxRestException(MsEventErrorCode.TICKET_TEMPLATE_NOT_REMOVABLE);
        }

        ticketTemplate.setEstado(TicketTemplateStatus.DELETED.getId());
        ticketTemplateDao.update(ticketTemplate);
    }

    @MySQLRead
    public List<TicketTemplateDesignDTO> findTicketTemplateDesigns() {
        List<CpanelModeloTicketRecord> modelRecords = ticketTemplateModelDao.findTemplateModels();
        return modelRecords.stream()
                .map(TicketTemplateConverter::convertModel)
                .collect(Collectors.toList());
    }

    public List<CommunicationElementDTO> findCommunicationElements(Long ticketTemplateId, TicketCommunicationElementFilter filter) {
        TicketTemplateRecord ticketRecord = getAndCheckTicketTemplate(ticketTemplateId);

        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records =
                ticketCommunicationElementDao.findCommunicationElements(ticketTemplateId, filter);

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = ticketTemplateDao.getTicketTemplateLanguages(ticketTemplateId)
                .stream().filter(l -> l.getDefecto().equals((byte) 1)).findFirst().orElse(null);
        if (defaultLanguage == null) {
            throw new OneboxRestException(TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE, "Default language not defined", null);
        }

        return TicketTemplateCommunicationElementConverter.fromRecords(records, ticketRecord, defaultLanguage, staticDataContainer);
    }

    @MySQLWrite
    public void upsertCommunicationElements(Long ticketTemplateId, List<CommunicationElementDTO> elements) {
        TicketTemplateRecord ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> recordsByType =
                ticketCommunicationElementDao.findCommunicationElements(ticketTemplateId, null);

        CpanelElementosComTicketRecord comElement = ticketCommunicationElementDao.findById(ticketTemplate.getElementocomticket());

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = ticketTemplateDao.getTicketTemplateLanguages(ticketTemplateId)
                .stream().filter(l -> l.getDefecto().equals((byte) 1)).findFirst().orElse(null);
        if (defaultLanguage == null) {
            throw new OneboxRestException(TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE, "Default language not defined", null);
        }

        boolean initComElement = comElement == null;
        for (CommunicationElementDTO element : elements) {

            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            CpanelDescPorIdiomaRecord record = TicketTemplateCommunicationElementUtils.checkAndGetElement(element, languageId, recordsByType);

            if (record == null) {
                record = insertRecord(recordsByType, comElement, element, languageId);

                // Keep recordsByType map updated to avoid skipping the CpanelItemDescSequence check on multi-language posting (api posting of multiple languages or cloning):
                recordsByType.putIfAbsent(element.getTagType(), new ArrayList<>());
                recordsByType.get(element.getTagType())
                             .add(record);
            }

            String value;
            if (element.getTagType().isImage()) {
                long timestamp = System.currentTimeMillis();
                String oldFilename = record.getDescripcion();

                boolean isPrinterType = Objects.equals(ticketTemplate.getModelFormat(), TicketFormat.ZPL.getFormat());
                value = deleteOrUploadImage(ticketTemplate, defaultLanguage,  element, record, false, timestamp, null, oldFilename, isPrinterType);

                // upload image resized to the 200dpi printer dimension if necessary
                Dimension dimension200dpi = TicketContentImagePrinterType.get200dpiVersion(element.getTagType());
                if (dimension200dpi != null) {
                    deleteOrUploadImage(ticketTemplate, defaultLanguage, element, record, true, timestamp, dimension200dpi, oldFilename, isPrinterType);
                }
                comElement = deleteOrUpdateRecord(comElement, initComElement, element.getTagType(), element.getImageBinary(), record, value, ticketTemplate);
            } else {
                comElement = deleteOrUpdateRecord(comElement, initComElement, element.getTagType(), element.getImageBinary(), record, element.getValue(), ticketTemplate);
            }
            ticketCommunicationElementDao.update(comElement);
        }
    }

    public List<TicketTemplateLiteralDTO> findLiterals(Long ticketTemplateId, TicketLiteralElementFilter filter) {
        getAndCheckTicketTemplate(ticketTemplateId);

        Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> literalWithTranslations =
                ticketTemplateLiteralDao.findLiterals(ticketTemplateId, filter);

        //If literal has no translation for the selected language, search by default value
        if (MapUtils.isEmpty(literalWithTranslations)) {
            literalWithTranslations =
                    ticketTemplateLiteralDao.findLiterals(ticketTemplateId,  new TicketLiteralElementFilter(filter.getCodes(), null));
        }

        List<Integer> languages = getLiteralsRequestLanguages(ticketTemplateId, filter);

        List<TicketTemplateLiteralDTO> literals = new ArrayList<>();
        for (Integer languageId : languages) {
            for (Map.Entry<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> literal : literalWithTranslations.entrySet()) {
                TicketTemplateLiteralDTO literalDTO = null;
                if (!CommonUtils.isEmpty(literal.getValue())) {
                    literalDTO = getLiteralTranslation(languageId, literalDTO, literal);
                }
                //If literal has no translation, load default value
                if (literalDTO == null) {
                    literalDTO = buildLiteral(literal.getKey(), languageId, literal.getKey().getDefecto());
                }
                literals.add(literalDTO);
            }
        }

        return literals;
    }

    public void updateLiterals(Long ticketTemplateId, List<TicketTemplateLiteralDTO> literals) {
        getAndCheckTicketTemplate(ticketTemplateId);

        Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> literalWithTranslations =
                ticketTemplateLiteralDao.findLiterals(ticketTemplateId, null);

        for (TicketTemplateLiteralDTO literal : literals) {
            Integer languageId = staticDataContainer.getLanguageByCode(literal.getLanguage());

            Map.Entry<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> record =
                    checkAndGetLiteral(literal, languageId, literalWithTranslations);

            CpanelLiteralTicketTraduccionRecord translationRecord = record.getValue().stream().
                    filter(Objects::nonNull).filter(tl -> languageId.equals(tl.getIdidioma())).findFirst().orElse(null);
            if (translationRecord != null) {
                translationRecord.setValor(literal.getValue());
                ticketTemplateLiteralTranslationDao.update(translationRecord);
            } else {
                translationRecord = new CpanelLiteralTicketTraduccionRecord();
                translationRecord.setIdplantilla(ticketTemplateId.intValue());
                translationRecord.setIdliteral(record.getKey().getIdliteral());
                translationRecord.setIdidioma(languageId);
                translationRecord.setValor(literal.getValue());
                ticketTemplateLiteralTranslationDao.insert(translationRecord);
            }
        }
    }

    public static Map.Entry<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> checkAndGetLiteral(
            TicketTemplateLiteralDTO element, Integer languageId, Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> records) {
        if (element.getCode() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "tag id is mandatory", null);
        }
        if (element.getLanguage() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "language code id is mandatory", null);
        }
        if (languageId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER_FORMAT, "language code invalid", null);
        }
        Map.Entry<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> record = records.entrySet().stream().
                filter(l -> l.getKey().getCodigo().equals(element.getCode())).
                findFirst().orElse(null);
        if (record == null) {
            throw new OneboxRestException(MsEventErrorCode.TICKET_TEMPLATE_LITERAL_NOT_FOUND);
        }
        return record;
    }

    private CpanelDescPorIdiomaRecord insertRecord(Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> recordsByType,
            CpanelElementosComTicketRecord comElement, CommunicationElementDTO element, Integer languageId) {
        CpanelDescPorIdiomaRecord record;
        if (element.getImageBinary() != null && element.getImageBinary().equals(Optional.empty())) {
            throw new OneboxRestException(NOT_FOUND, "Deleting item not found", null);
        }
        Integer idItem;
        if (recordsByType.containsKey(element.getTagType())) {
            idItem = TicketTemplateCommunicationElementUtils.getItemIdByTag(comElement, element.getTagType());
        } else {
            idItem = itemDescSequenceDao.insertNewRecord();
        }

        CpanelDescPorIdiomaRecord newRecord = new CpanelDescPorIdiomaRecord();
        newRecord.setIdidioma(languageId);
        newRecord.setIditem(idItem);
        newRecord.setDescripcion(StringUtils.EMPTY);
        record = descPorIdiomaDao.insert(newRecord);

        return record;
    }

    private String deleteOrUploadImage(TicketTemplateRecord ticketTemplate, CpanelIdiomaPlantillaTicketRecord defaultLanguage,
                                       CommunicationElementDTO element, CpanelDescPorIdiomaRecord record,
                                       boolean is200dpi, long timestamp, Dimension dimension, String oldFilename, boolean isPrinterType) {
        Optional<String> processedImage = element.getImageBinary();
        ImageFormat imageFormat = ImageFormat.JPG;
        if (isPrinterType) {
            imageFormat = ImageFormat.PNG;
            if (processedImage != null && processedImage.isPresent()) {
                if (dimension != null)
                    processedImage = Optional.of(ImageUtils.resize(processedImage.get(), ImageFormat.PNG, dimension));
                processedImage = Optional.of(ImageUtils.convertToDithering(processedImage.get(), ImageFormat.PNG));
            }

            if (is200dpi && !oldFilename.isEmpty()) {
                oldFilename = get200dpiImgPath(oldFilename);
            }
        }
        String newFilename = CommunicationElementsUtils.getImgFilename(record.getIdidioma(), timestamp, is200dpi, imageFormat);

        return CommunicationElementsUtils.uploadImage(s3OneboxRepository, record, processedImage,
                element.getValue(), defaultLanguage.getIdidioma(), S3URLResolver.S3ImageType.ITEM_IMAGE, ticketTemplate.getOperatorId(),
                ticketTemplate.getIdentidad(), oldFilename, newFilename);
    }

    private CpanelElementosComTicketRecord deleteOrUpdateRecord(CpanelElementosComTicketRecord comElement, boolean initComElement, TicketTemplateTagType tagType,
                                                                Optional<String> imageBinary, CpanelDescPorIdiomaRecord record, String value, CpanelPlantillaTicketRecord ticketTemplate) {
        if (initComElement && comElement == null) {
            comElement = new CpanelElementosComTicketRecord();
            CpanelElementosComTicketRecord insert = ticketCommunicationElementDao.insert(comElement);
            ticketTemplate.setElementocomticket(insert.getIdinstancia());
            ticketTemplateDao.update(ticketTemplate);
        }
        if (!initComElement && (tagType.isImage() && (imageBinary == null || imageBinary.equals(Optional.empty())))) {
            descPorIdiomaDao.delete(record);
        } else {
            record.setDescripcion(value);
            descPorIdiomaDao.update(record);
            TicketTemplateCommunicationElementUtils.setTagRecord(comElement, record.getIditem(), tagType);
        }
        return comElement;
    }

    private TicketTemplateLiteralDTO getLiteralTranslation(Integer languageId, TicketTemplateLiteralDTO literalDTO,
                                                           Map.Entry<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> literal) {
        for (CpanelLiteralTicketTraduccionRecord translation : literal.getValue()) {
            if (languageId.equals(translation.getIdidioma())) {
                literalDTO = buildLiteral(literal.getKey(), translation.getIdidioma(), translation.getValor());
                break;
            }
        }
        return literalDTO;
    }

    TicketTemplateLiteralDTO buildLiteral(CpanelLiteralTicketRecord record, Integer languageId, String value) {
        TicketTemplateLiteralDTO literalDTO = new TicketTemplateLiteralDTO();
        literalDTO.setCode(record.getCodigo());
        literalDTO.setName(record.getNombre());
        literalDTO.setLanguage(staticDataContainer.getLanguage(languageId));
        literalDTO.setValue(value);
        return literalDTO;
    }

    private List<Integer> getLiteralsRequestLanguages(Long ticketTemplateId, TicketLiteralElementFilter filter) {
        List<CpanelIdiomaPlantillaTicketRecord> templateLanguages =
                ticketTemplateDao.getTicketTemplateLanguages(ticketTemplateId);

        List<Integer> langIds = templateLanguages.stream().
                map(CpanelIdiomaPlantillaTicketRecord::getIdidioma).collect(Collectors.toList());

        List<Integer> languageIds;
        if (filter != null && filter.getLanguageId() != null) {
            if (!langIds.contains(filter.getLanguageId())) {
                throw new OneboxRestException(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE);
            }
            languageIds = Collections.singletonList(filter.getLanguageId());
        } else {
            languageIds = langIds;
        }

        return languageIds;
    }

    private CpanelPlantillaTicketRecord cloneTicketTemplateRecord(CloneTicketTemplateDTO body, TicketTemplateRecord templateRecord) {
        String name  = null;

        if(body != null){
            if (body.getEntityId() != null && body.getEntityId().intValue() != templateRecord.getIdentidad()){
                templateRecord.setIdentidad(body.getEntityId().intValue());
            }
            if(StringUtils.isNotBlank(body.getName())) {
                checkTemplateName(body.getName(), null, templateRecord.getIdentidad());
                name = body.getName();
            }
        }
        name = StringUtils.isBlank(name) ? (DEFAULT_PREFIX + templateRecord.getNombre()) : name;

        templateRecord.setIdplantilla(null);
        templateRecord.setElementocomticket(null);
        templateRecord.setAsignacionautomatica(ConverterUtils.isTrueAsByte(Boolean.FALSE));
        templateRecord.setNombre(name);
        return ticketTemplateDao.insert(templateRecord);
    }

    private void cloneTicketTemplateLanguageRecord(Long ticketTemplateId, Long newTicketTemplateId, Long targetEntityId) {
        List<CpanelIdiomaPlantillaTicketRecord> records = ticketTemplateLanguageDao
                .findByTicketTemplateId(ticketTemplateId.intValue());
        if (CollectionUtils.isNotEmpty(records)) {
            if (targetEntityId != null) {
                // clone only languages of the target entity:
                List<Integer> contentLanguageIds = entityContentLanguageDao.getEntityContentLanguageIds(targetEntityId.intValue());
                records.stream()
                        .filter(templateLang -> contentLanguageIds.contains(templateLang.getIdidioma()))
                        .forEach(r -> {
                            r.setIdplantilla(newTicketTemplateId.intValue());
                            ticketTemplateLanguageDao.insert(r);
                        });

                // assert the new template will have a default language
                List<CpanelIdiomaPlantillaTicketRecord> newRecords = ticketTemplateLanguageDao.findByTicketTemplateId(newTicketTemplateId.intValue());
                if(CollectionUtils.isNotEmpty(newRecords) && newRecords.stream().noneMatch(r->r.getDefecto().equals((byte)1))) {
                    Integer entityDefaultLanguage = entityDao.findById(targetEntityId.intValue()).getIdiomadefecto();
                    CpanelIdiomaPlantillaTicketRecord defaultLanguage =
                            newRecords.stream()
                                    .filter(templateLang -> templateLang.getIdidioma().equals(entityDefaultLanguage))
                                    .findFirst().orElse(null);
                    if (defaultLanguage != null) {
                        defaultLanguage.setDefecto((byte)1);
                        ticketTemplateLanguageDao.update(defaultLanguage);
                    } else {
                        CpanelIdiomaPlantillaTicketRecord recordDefaultLanguage = new CpanelIdiomaPlantillaTicketRecord();
                        recordDefaultLanguage.setIdplantilla(newTicketTemplateId.intValue());
                        recordDefaultLanguage.setIdidioma(entityDefaultLanguage);
                        recordDefaultLanguage.setDefecto((byte)1);
                        ticketTemplateLanguageDao.insert(recordDefaultLanguage);
                    }
                }
            } else {
                records.forEach(r -> {
                            r.setIdplantilla(newTicketTemplateId.intValue());
                            ticketTemplateLanguageDao.insert(r);
                });
            }
        }
    }

    private TicketTemplateRecord getAndCheckTicketTemplate(Long ticketTemplateId) {
        TicketTemplateRecord templateRecord = ticketTemplateDao.find(ticketTemplateId.intValue());
        if (templateRecord == null || templateRecord.getEstado().equals(TicketTemplateStatus.DELETED.getId())) {
            throw new OneboxRestException(MsEventErrorCode.TICKET_TEMPLATE_NOT_FOUND);
        }
        return templateRecord;
    }

    private CpanelModeloTicketRecord getAndCheckModel(Long modelId) {
        CpanelModeloTicketRecord model = ticketTemplateModelDao.findById(modelId.intValue());
        if (model == null) {
            throw new OneboxRestException(MsEventErrorCode.TICKET_TEMPLATE_MODEL_NOT_FOUND);
        }
        return model;
    }

    private void checkTemplateName(String newName, String oldName, Integer entityId) {
        if (newName != null && (oldName == null || !oldName.equals(newName)) &&
                ticketTemplateDao.countByNameAndEntity(newName, entityId) > 0) {
            throw new OneboxRestException(INVALID_NAME_CONFLICT);
        }
    }

    private List<TicketTemplateRecord> getEntityTemplates(Long entityId, Integer format) {
        TicketTemplatesFilter entityFilter = new TicketTemplatesFilter();
        entityFilter.setEntityId(entityId);
        if (format != null) {
            entityFilter.setFormat(List.of(format));
        }
        return ticketTemplateDao.find(entityFilter);
    }

    private void updateTicketLanguages(Long ticketTemplateId, Integer defaultLanguage, List<Integer> selectedLanguageIds) {
        if (!CommonUtils.isEmpty(selectedLanguageIds)) {
            if (!selectedLanguageIds.contains(defaultLanguage)) {
                throw new OneboxRestException(TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE);
            }
            ticketTemplateLanguageDao.deleteByTicketTemplate(ticketTemplateId);
            for (Integer selectedLanguageId : selectedLanguageIds) {
                CpanelIdiomaPlantillaTicketRecord templateLanguage = new CpanelIdiomaPlantillaTicketRecord();
                templateLanguage.setIdplantilla(ticketTemplateId.intValue());
                templateLanguage.setIdidioma(selectedLanguageId);
                templateLanguage.setDefecto(ConverterUtils.isTrueAsByte(selectedLanguageId.equals(defaultLanguage)));
                ticketTemplateLanguageDao.insert(templateLanguage);
            }
        }
    }

}
