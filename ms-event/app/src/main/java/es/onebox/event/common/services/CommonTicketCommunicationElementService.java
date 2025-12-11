package es.onebox.event.common.services;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.core.utils.file.ImageUtils;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.TicketType;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketContentImagePrinterType;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.converter.TicketCommunicationElementConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.EventTicketCommunicationElementDao;
import es.onebox.event.events.dao.SessionTicketCommunicationElementDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventPassbookConfig;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.products.dao.ProductTicketContentsDao;
import es.onebox.event.products.dao.couch.ProductTicketContent;
import es.onebox.event.products.dao.couch.ProductTicketContentImageDetail;
import es.onebox.event.products.dao.couch.ProductTicketContentValue;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductTicketContentImageDTO;
import es.onebox.event.products.dto.ProductTicketContentListImageDTO;
import es.onebox.event.products.enums.TicketContentImageType;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionPassbookConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.TableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static es.onebox.event.communicationelements.utils.CommunicationElementsUtils.get200dpiImgPath;
import static es.onebox.event.communicationelements.utils.CommunicationElementsUtils.getImgFilename;

@Service
public class CommonTicketCommunicationElementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTicketCommunicationElementService.class);

    private final EventTicketCommunicationElementDao ticketCommunicationElementDao;
    private final SessionTicketCommunicationElementDao sessionticketCommunicationElementDao;
    private final ProductTicketContentsDao productTicketContentsDao;
    private final EventDao eventDao;
    private final SessionDao sessionDao;
    private final StaticDataContainer staticDataContainer;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final S3BinaryRepository s3OneboxRepository;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public CommonTicketCommunicationElementService(EventTicketCommunicationElementDao ticketCommunicationElementDao,
                                                   EventDao eventDao,
                                                   SessionDao sessionDao,
                                                   StaticDataContainer staticDataContainer,
                                                   DescPorIdiomaDao descPorIdiomaDao,
                                                   S3BinaryRepository s3OneboxRepository,
                                                   ItemDescSequenceDao itemDescSequenceDao,
                                                   EventConfigCouchDao eventConfigCouchDao,
                                                   SessionTicketCommunicationElementDao sessionticketCommunicationElementDao,
                                                   SessionConfigCouchDao sessionConfigCouchDao,
                                                   ProductTicketContentsDao productTicketContentsDao,
                                                   EntitiesRepository entitiesRepository) {
        this.ticketCommunicationElementDao = ticketCommunicationElementDao;
        this.eventDao = eventDao;
        this.sessionDao = sessionDao;
        this.staticDataContainer = staticDataContainer;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.s3OneboxRepository = s3OneboxRepository;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.sessionticketCommunicationElementDao = sessionticketCommunicationElementDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.productTicketContentsDao = productTicketContentsDao;
        this.entitiesRepository = entitiesRepository;
    }

    public List<TicketCommunicationElementDTO> getCommunicationElements(TicketCommunicationElementFilter filter, TicketCommunicationElementCategory type, EventRecord event) {
        Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = findCommunicationElements(event.getIdevento(), filter, type);
        return TicketCommunicationElementConverter.fromRecords(records, event.getOperatorId().longValue(), staticDataContainer);
    }

    public List<PassbookCommunicationElementDTO> getEventPassbookCommunicationElements(PassbookCommunicationElementFilter filter,
                                                                                       Long eventId) {
        Set<PassbookCommunicationElementTagType> filterTags = filter != null && filter.getTags() != null ? filter.getTags() : new HashSet<>();
        String filterLang = filter != null && filter.getLanguage() != null ? filter.getLanguage() : "";

        EventConfig ec = eventConfigCouchDao.get(eventId.toString());
        EventPassbookConfig epc = ec != null && ec.getEventPassbookConfig() != null ? ec.getEventPassbookConfig() : new EventPassbookConfig();
        List<PassbookCommunicationElementDTO> result = new ArrayList<>(getTagValues(filterTags, filterLang, epc.getStripImage(), PassbookCommunicationElementTagType.STRIP));
        result.addAll(getTagValues(filterTags, filterLang, epc.getTitle(), PassbookCommunicationElementTagType.TITLE));
        result.addAll(getTagValues(filterTags, filterLang, epc.getAdditionalData1(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_1));
        result.addAll(getTagValues(filterTags, filterLang, epc.getAdditionalData2(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_2));
        result.addAll(getTagValues(filterTags, filterLang, epc.getAdditionalData3(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_3));
        return result;
    }

    public void updateTicketCommElements(Set<TicketCommunicationElementDTO> elements,
                                         TicketCommunicationElementCategory type, EventRecord event) {
        TicketCommunicationElementFilter filter = TicketCommunicationElementConverter.buildFilter(elements);
        final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = findCommunicationElements(event.getIdevento(), filter, type);
        TableField<CpanelEventoRecord, Integer> field = CommunicationElementsUtils.checkJoinField(type);
        Integer commElementId = event.getValue(field);
        CpanelElementosComTicketRecord comElements = null;
        if (commElementId != null) {
            comElements = this.ticketCommunicationElementDao.findById(commElementId);
        }
        if (comElements == null) {
            comElements = ticketCommunicationElementDao.insertNew();
            this.eventDao.updateField(event.getIdevento(), field, comElements.getIdinstancia());
        }
        updateTicketCommElements(elements, type, event.getOperatorId(), records, comElements);
    }

    public void updatePassbookCommElements(Set<PassbookCommunicationElementDTO> elements, EventRecord event) {
        Integer operatorId = event.getOperatorId();
        Integer entityId = event.getIdentidad();
        Integer eventId = event.getIdevento();
        EventConfig ec = eventConfigCouchDao.getOrInitEventConfig(Long.valueOf(eventId));
        EventPassbookConfig epc = ec.getEventPassbookConfig();
        epc = epc == null ? new EventPassbookConfig() : epc;

        for (PassbookCommunicationElementDTO el : elements) {
            switch (el.getTag()) {
                case STRIP:
                    epc.setStripImage(addPassBookValue(operatorId, entityId, eventId, null, el, epc.getStripImage(), null));
                    break;
                case TITLE:
                    epc.setTitle(addPassBookValue(operatorId, entityId, eventId, null, el, epc.getTitle(), 50));
                    break;
                case ADDITIONAL_DATA_1:
                    epc.setAdditionalData1(addPassBookValue(operatorId, entityId, eventId, null, el, epc.getAdditionalData1(), 200));
                    break;
                case ADDITIONAL_DATA_2:
                    epc.setAdditionalData2(addPassBookValue(operatorId, entityId, eventId, null, el, epc.getAdditionalData2(), 200));
                    break;
                case ADDITIONAL_DATA_3:
                    epc.setAdditionalData3(addPassBookValue(operatorId, entityId, eventId, null, el, epc.getAdditionalData3(), 200));
                    break;
            }
        }
        ec.setEventPassbookConfig(epc);
        eventConfigCouchDao.upsert(eventId.toString(), ec);
    }

    public void deleteCommunicationElement(TicketCommunicationElementTagType tag, String language, TicketCommunicationElementCategory type, EventRecord event) {
        final Integer languageId = this.staticDataContainer.getLanguageByCode(language);
        final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = findCommunicationElements(event.getIdevento(),
                TicketCommunicationElementConverter.buildFilter(tag, languageId), type);
        if (CollectionUtils.isEmpty(records)) {
            throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Deleting event: " + event.getIdevento() + " ticket item not found", null);
        }
        records.get(tag).stream().findFirst().ifPresent(record -> {
            this.descPorIdiomaDao.delete(record);
            CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), record.getDescripcion(), event.getOperatorId());

            // delete 200 dpi version if present
            String filename200dpi = get200dpiImgPath(record.getDescripcion());
            CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), filename200dpi, event.getOperatorId());
        });
    }

    public void deleteEventPassbookCommElement(PassbookCommunicationElementTagType tag, String language, Long eventId) {
        EventConfig ec = eventConfigCouchDao.get(eventId.toString());
        if (ec == null || ec.getEventPassbookConfig() == null) {
            return;
        }
        switch (tag) {
            case STRIP:
                removePassbookValue(tag, language, ec.getEventPassbookConfig().getStripImage());
                break;
            case TITLE:
                removePassbookValue(tag, language, ec.getEventPassbookConfig().getTitle());
                break;
            case ADDITIONAL_DATA_1:
                removePassbookValue(tag, language, ec.getEventPassbookConfig().getAdditionalData1());
                break;
            case ADDITIONAL_DATA_2:
                removePassbookValue(tag, language, ec.getEventPassbookConfig().getAdditionalData2());
                break;
            case ADDITIONAL_DATA_3:
                removePassbookValue(tag, language, ec.getEventPassbookConfig().getAdditionalData3());
                break;
        }
        eventConfigCouchDao.upsert(eventId.toString(), ec);
    }

    public void deleteSessionPassbookCommElement(PassbookCommunicationElementTagType tag, String language, Long sessionId) {
        SessionConfig sc = sessionConfigCouchDao.get(sessionId.toString());
        if (sc == null || sc.getSessionPassbookConfig() == null) {
            return;
        }
        switch (tag) {
            case STRIP:
                removePassbookValue(tag, language, sc.getSessionPassbookConfig().getStripImage());
                break;
            case TITLE:
                removePassbookValue(tag, language, sc.getSessionPassbookConfig().getTitle());
                break;
            case ADDITIONAL_DATA_1:
                removePassbookValue(tag, language, sc.getSessionPassbookConfig().getAdditionalData1());
                break;
            case ADDITIONAL_DATA_2:
                removePassbookValue(tag, language, sc.getSessionPassbookConfig().getAdditionalData2());
                break;
            case ADDITIONAL_DATA_3:
                removePassbookValue(tag, language, sc.getSessionPassbookConfig().getAdditionalData3());
                break;
        }
        sessionConfigCouchDao.upsert(sessionId.toString(), sc);
    }

    public void deleteAllSessionPassbookImages(String language, Long sessionId) {
        SessionConfig sc = sessionConfigCouchDao.get(sessionId.toString());
        if (sc == null || sc.getSessionPassbookConfig() == null || sc.getSessionPassbookConfig().getStripImage() == null) {
            return;
        }

        removePassbookValue(PassbookCommunicationElementTagType.STRIP, language, sc.getSessionPassbookConfig().getStripImage());

        sessionConfigCouchDao.upsert(sessionId.toString(), sc);
    }

    public void updateSessionPassbookCommElements(Set<PassbookCommunicationElementDTO> elements, SessionRecord session) {
        Integer operatorId = session.getOperatorId();
        Integer entityId = session.getEntityId();
        Integer eventId = session.getIdevento();
        Integer sessionId = session.getIdsesion();
        SessionConfig sc = sessionConfigCouchDao.getOrInitSessionConfig(sessionId.longValue());
        SessionPassbookConfig epc = sc.getSessionPassbookConfig();
        epc = epc == null ? new SessionPassbookConfig() : epc;

        for (PassbookCommunicationElementDTO el : elements) {
            switch (el.getTag()) {
                case STRIP:
                    epc.setStripImage(addPassBookValue(operatorId, entityId, eventId, sessionId, el, epc.getStripImage(), null));
                    break;
                case TITLE:
                    epc.setTitle(addPassBookValue(operatorId, entityId, eventId, sessionId, el, epc.getTitle(), 50));
                    break;
                case ADDITIONAL_DATA_1:
                    epc.setAdditionalData1(addPassBookValue(operatorId, entityId, eventId, sessionId, el, epc.getAdditionalData1(), 200));
                    break;
                case ADDITIONAL_DATA_2:
                    epc.setAdditionalData2(addPassBookValue(operatorId, entityId, eventId, sessionId, el, epc.getAdditionalData2(), 200));
                    break;
                case ADDITIONAL_DATA_3:
                    epc.setAdditionalData3(addPassBookValue(operatorId, entityId, eventId, sessionId, el, epc.getAdditionalData3(), 200));
                    break;
            }
        }
        sc.setSessionPassbookConfig(epc);
        sessionConfigCouchDao.upsert(sessionId.toString(), sc);
    }

    public List<PassbookCommunicationElementDTO> getSessionPassbookCommunicationElements(PassbookCommunicationElementFilter filter,
                                                                                         Long sessionId) {
        Set<PassbookCommunicationElementTagType> filterTags = filter != null && filter.getTags() != null ? filter.getTags() : new HashSet<>();
        String filterLang = filter != null && filter.getLanguage() != null ? filter.getLanguage() : "";

        SessionConfig sc = sessionConfigCouchDao.get(sessionId.toString());
        SessionPassbookConfig spc = sc != null && sc.getSessionPassbookConfig() != null ? sc.getSessionPassbookConfig() : new SessionPassbookConfig();
        List<PassbookCommunicationElementDTO> result = new ArrayList<>(getTagValues(filterTags, filterLang, spc.getStripImage(), PassbookCommunicationElementTagType.STRIP));
        result.addAll(getTagValues(filterTags, filterLang, spc.getTitle(), PassbookCommunicationElementTagType.TITLE));
        result.addAll(getTagValues(filterTags, filterLang, spc.getAdditionalData1(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_1));
        result.addAll(getTagValues(filterTags, filterLang, spc.getAdditionalData2(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_2));
        result.addAll(getTagValues(filterTags, filterLang, spc.getAdditionalData3(), PassbookCommunicationElementTagType.ADDITIONAL_DATA_3));
        return result;
    }

    public List<TicketCommunicationElementDTO> getSessionCommunicationElements(SessionRecord session, TicketCommunicationElementCategory type,
                                                                               TicketCommunicationElementFilter filter) {
        Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = sessionticketCommunicationElementDao.findSessionTicketCommunicationElements(session.getIdsesion().longValue(), type, filter);
        return TicketCommunicationElementConverter.fromRecords(records, session.getOperatorId().longValue(), staticDataContainer);
    }

    public void updateSessionCommunicationElements(SessionRecord session, TicketCommunicationElementCategory type,
                                                   Set<TicketCommunicationElementDTO> elements) {
        TicketCommunicationElementFilter filter = TicketCommunicationElementConverter.buildFilter(elements);
        final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = sessionticketCommunicationElementDao.findSessionTicketCommunicationElements(session.getIdsesion().longValue(), type, filter);
        TableField<CpanelSesionRecord, Integer> field = CommunicationElementsUtils.checkSessionJoinField(type);
        Integer commElementId = session.getValue(field);
        CpanelElementosComTicketRecord comElements = null;
        if (Objects.nonNull(commElementId)) {
            comElements = this.ticketCommunicationElementDao.findById(commElementId);
        }
        if (Objects.isNull(comElements)) {
            comElements = ticketCommunicationElementDao.insertNew();
            sessionDao.updateField(session.getIdsesion(), field, comElements.getIdinstancia());
        }
        updateTicketCommElements(elements, type, session.getOperatorId(), records, comElements);

    }

    public void deleteSessionCommunicationElements(SessionRecord session, TicketCommunicationElementCategory type,
                                                   String language, TicketCommunicationElementTagType tag) {
        Integer languageId = Optional.ofNullable(staticDataContainer.getLanguageByCode(language)).orElseThrow(() ->
                ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
        TicketCommunicationElementFilter filter = TicketCommunicationElementConverter.buildFilter(tag, languageId);
        final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records =
                sessionticketCommunicationElementDao.findSessionTicketCommunicationElements(session.getIdsesion().longValue(), type, filter);
        if (CollectionUtils.isEmpty(records)) {
            throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Session ticket item not found", null);
        }
        records.get(tag).stream().findFirst().ifPresent(record -> {
            descPorIdiomaDao.delete(record);
            CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), record.getDescripcion(), session.getOperatorId());

            // delete 200 dpi version if present
            String filename200dpi = get200dpiImgPath(record.getDescripcion());
            CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), filename200dpi, session.getOperatorId());
        });
    }

    public void deleteSessionCommunicationElementsBulk(Collection<SessionRecord> sessions, TicketCommunicationElementCategory type,
                                                       String language, TicketCommunicationElementTagType tag) {
        Integer languageId = Optional.ofNullable(staticDataContainer.getLanguageByCode(language)).orElseThrow(() ->
                ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
        TicketCommunicationElementFilter filter = TicketCommunicationElementConverter.buildFilter(tag, languageId);
        sessions.forEach(session -> {
            final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records =
                    sessionticketCommunicationElementDao.findSessionTicketCommunicationElements(session.getIdsesion().longValue(), type, filter);
            if (records.containsKey(tag)) {
                records.get(tag).stream().findFirst().ifPresent(record -> {
                    descPorIdiomaDao.delete(record);
                    CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), record.getDescripcion(), session.getOperatorId());
                });
            }
        });
    }

    public void deleteAllSessionCommunicationImageElements(SessionRecord session, TicketCommunicationElementCategory type,
                                                           Integer languageId) {
        TicketCommunicationElementFilter filter = TicketCommunicationElementConverter.buildFilter(languageId);
        final Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records =
                sessionticketCommunicationElementDao.findSessionTicketCommunicationElements(session.getIdsesion().longValue(), type, filter);
        if (CollectionUtils.isEmpty(records)) {
            throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Session ticket item not found", null);
        }
        TicketCommunicationElementTagType.getImageTags().forEach(tag -> {
            if (records.containsKey(tag)) {
                records.get(tag).stream().findFirst().ifPresent(record -> {
                    descPorIdiomaDao.delete(record);
                    CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(),
                            record.getDescripcion(), session.getOperatorId());
                });
            }
        });
    }

    private List<PassbookCommunicationElementDTO> getTagValues(Set<PassbookCommunicationElementTagType> filterTags,
                                                               String filterLang, Map<String, String> tagValues,
                                                               PassbookCommunicationElementTagType tagType) {
        List<PassbookCommunicationElementDTO> result = new ArrayList<>();

        if ((!filterTags.isEmpty() && !filterTags.contains(tagType)) || CollectionUtils.isEmpty(tagValues)) {
            return result;
        }
        for (Map.Entry<String, String> entry : tagValues.entrySet()) {
            if (StringUtils.isNotEmpty(filterLang) && !entry.getKey().equals(filterLang)) {
                continue;
            }
            PassbookCommunicationElementDTO e = new PassbookCommunicationElementDTO();
            e.setLanguage(entry.getKey());
            if (tagType.isImage()) {
                e.setValue(staticDataContainer.getS3Repository() + entry.getValue());
            } else {
                e.setValue(entry.getValue());
            }
            e.setTag(tagType);
            result.add(e);
        }
        return result;
    }

    private Map<String, String> addPassBookValue(Integer operatorId, Integer entityId, Integer eventId, Integer sessionId,
                                                 PassbookCommunicationElementDTO el, Map<String, String> dictionary, Integer maxLength) {
        Optional.ofNullable(staticDataContainer.getLanguageByCode(el.getLanguage()))
                .orElseThrow(() -> ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
        if (dictionary == null) {
            dictionary = new HashMap<>();
        }
        if (el.getTag().isImage()) {
            String imagePath = uploadPassbookFile(operatorId, entityId, eventId, sessionId, el.getLanguage(), el.getTag().name(), el.getImageBinary());
            dictionary.put(el.getLanguage(), imagePath);
        } else {
            if (el.getValue().length() > maxLength) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_STRING_LENGTH);
            }
            dictionary.put(el.getLanguage(), el.getValue());
        }
        return dictionary;
    }

    private void removePassbookValue(PassbookCommunicationElementTagType tag, String language, Map<String, String> dictionary) {
        if (dictionary == null || !dictionary.containsKey(language)) {
            return;
        }
        if (tag.isImage()) {
            CommunicationElementsUtils.deletePassbookImage(s3OneboxRepository, dictionary.get(language));
        }
        dictionary.remove(language);
    }

    private void checkStringLength(String value, int maxLength) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (value.length() > maxLength) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_STRING_LENGTH);
        }
    }

    private Integer resolveItemId(CpanelElementosComTicketRecord comElements, TicketCommunicationElementTagType tag) {
        Integer itemId = TicketCommunicationElementConverter.getTagItemId(comElements, tag);
        if (itemId == null) {
            itemId = this.itemDescSequenceDao.insertNewRecord();
            TicketCommunicationElementConverter.setTagRecord(comElements, itemId, tag);
            this.ticketCommunicationElementDao.update(comElements);
        }
        return itemId;
    }


    private void updateTicketCommElements(Set<TicketCommunicationElementDTO> elements, TicketCommunicationElementCategory type,
                                          Integer operatorId, Map<TicketCommunicationElementTagType,
            List<CpanelDescPorIdiomaRecord>> records, CpanelElementosComTicketRecord comElements) {

        for (TicketCommunicationElementDTO el : elements) {
            if (CommunicationElementsUtils.isValueNotInformed(el)) {
                throw ExceptionBuilder.build(MsEventErrorCode.COMMUNICATION_ELEMENT_UPDATE_REQUIRED);
            }
            String value;
            Integer languageId = Optional.ofNullable(staticDataContainer.getLanguageByCode(el.getLanguage()))
                    .orElseThrow(() -> ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
            CpanelDescPorIdiomaRecord desc = CommunicationElementsUtils.checkAndGetElement(el, records, languageId);
            TicketCommunicationElementTagType tag = el.getTag();
            Integer itemId = resolveItemId(comElements, tag);
            String imageBinary = el.getImageBinary();
            String altText = Optional.ofNullable(el.getAltText()).orElse(desc != null ? desc.getAlttext() : null);
            if (StringUtils.isNotBlank(imageBinary)) {
                uploadImageVariants(type, operatorId, languageId, desc, tag, itemId, imageBinary, altText);
            } else {
                value = el.getValue();
                if (StringUtils.isNotBlank(value)) {
                    this.descPorIdiomaDao.upsert(itemId, languageId, value, altText);
                } else {
                    this.descPorIdiomaDao.delete(itemId, languageId);
                }
            }
        }
    }

    private void uploadImageVariants(TicketCommunicationElementCategory type, Integer operatorId, Integer languageId,
                                     CpanelDescPorIdiomaRecord desc, TicketCommunicationElementTagType tag,
                                     Integer itemId, String imageBinary, String altText) {
        String value;
        long timestamp = System.currentTimeMillis();
        String filename;
        ImageFormat format = ImageFormat.JPG;

        if (isPrinter(type)) {
            format = ImageFormat.PNG;
            // upload image resized to the 200dpi printer dimension if necessary
            Dimension dimension200dpi = TicketContentImagePrinterType.get200dpiVersion(tag);
            if (dimension200dpi != null) {
                String resized200dpiImg = ImageUtils.resize(imageBinary, format, dimension200dpi);
                resized200dpiImg = ImageUtils.convertToDithering(resized200dpiImg, format);
                uploadFile(operatorId, resized200dpiImg, itemId, languageId, desc,
                        getImgFilename(languageId, timestamp, true, format));
            }
            // dither the original image too
            imageBinary = ImageUtils.convertToDithering(imageBinary, format);
        }
        filename = getImgFilename(languageId, timestamp, false, format);
        value = uploadFile(operatorId, imageBinary, itemId, languageId, desc, filename);
        this.descPorIdiomaDao.upsert(itemId, languageId, value, altText);
    }

    private String uploadFile(final Integer operatorId, String imageBinary, Integer itemId, Integer languageId,
                              CpanelDescPorIdiomaRecord desc, String filename) {
        String previousFile = desc != null && desc.getDescripcion() != null ? desc.getDescripcion() : null;
        return CommunicationElementsUtils.uploadItemImage(s3OneboxRepository, previousFile, languageId, itemId,
                imageBinary, operatorId, filename);
    }

    private String uploadPassbookFile(final Integer operatorId, final Integer entityId, final Integer eventId, final Integer sessionId,
                                      String langCode, String fileType, String imageBinary) {
        return CommunicationElementsUtils.uploadPassbookImage(operatorId, entityId, eventId, sessionId, langCode, fileType,
                imageBinary, s3OneboxRepository);
    }

    private Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> findCommunicationElements(Integer eventId,
                                                                                                              TicketCommunicationElementFilter filter, TicketCommunicationElementCategory type) {
        return this.ticketCommunicationElementDao.findEventTicketCommunicationElements(eventId, filter, type);
    }


    private static boolean isPrinter(TicketCommunicationElementCategory type) {
        return TicketCommunicationElementCategory.TICKET_OFFICE.equals(type)
                || TicketCommunicationElementCategory.INVITATION_TICKET_OFFICE.equals(type);
    }

    public void updateTicketCommElementsWithRetries(SessionRecord sessionRecord, TicketCommunicationElementCategory type,
                                                    Set<TicketCommunicationElementDTO> values, int maxRetries) {
        int attempt = 0;
        boolean updated = false;
        do {
            try {
                updateSessionCommunicationElements(sessionRecord, type, values);
                updated = true;
            } catch (PessimisticLockingFailureException e) {
                attempt++;
                if (attempt == maxRetries) {
                    LOGGER.error("EventId: {} - SessionId: {} - Type: {} - Error updating ticket communication element.",
                            sessionRecord.getIdevento(), sessionRecord.getSessionId(), type, e);
                    throw new OneboxRestException(MsEventErrorCode.COMMUNICATION_ELEMENT_BULK_UPDATE_LOCKED, e);
                }
            }
        } while (!updated);
    }

    public ProductTicketContent createOrUpdateTicketContentsImage(Long productId, Integer entityId,
                                                                  ProductTicketContentListImageDTO ticketContentListImageDTO,
                                                                  TicketType ticketType, List<ProductLanguageRecord> productLanguageRecordList) {
        Integer operatorId = entitiesRepository.getEntity(entityId).getOperator().getId();
        ProductTicketContent ticketContentDocument = productTicketContentsDao.getOrCreate(productId);

        if (ticketContentDocument.getTicketContentByType() == null) {
            ticketContentDocument.setTicketContentByType(new HashMap<>());
        }

        if (!ticketContentDocument.getTicketContentByType().containsKey(ticketType)) {
            ticketContentDocument.getTicketContentByType().put(ticketType, new HashMap<>());
        }

        for (ProductTicketContentImageDTO imageDTO : ticketContentListImageDTO) {
            String language = imageDTO.getLanguage();

            if (!ticketContentDocument.getTicketContentByType().get(ticketType).containsKey(language)) {
                ticketContentDocument.getTicketContentByType().get(ticketType).put(language, new ProductTicketContentValue());
            }

            productLanguageRecordList.stream()
                    .filter(el -> el.getCode().equals(imageDTO.getLanguage()))
                    .findAny()
                    .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.TICKET_CONTENT_LANGUAGE_NOT_MATCH));

            ProductTicketContentValue productTicketContentValue = ticketContentDocument.getTicketContentByType().get(ticketType).get(language);

            if (productTicketContentValue.getImages() == null) {
                productTicketContentValue.setImages(new ArrayList<>());
            }

            Optional<ProductTicketContentImageDetail> contentImageDetail =  productTicketContentValue.getImages().stream()
                    .filter(el -> el.getType().equals(imageDTO.getType()))
                    .findAny();

            if (contentImageDetail.isPresent()){
                ProductTicketContentImageDetail imageDetail =  contentImageDetail.get();
                CommunicationElementsUtils.deleteProductTicketImage(s3OneboxRepository, imageDetail.getValue());
                setImageValue(operatorId, entityId, productId, productTicketContentValue, imageDTO);
            } else {
                ProductTicketContentImageDetail imageDetail = new ProductTicketContentImageDetail(imageDTO.getType(), imageDTO.getImageUrl());
                productTicketContentValue.getImages().add(imageDetail);
                setImageValue(operatorId, entityId, productId, productTicketContentValue, imageDTO);
            }
        }

        productTicketContentsDao.upsert(productId.toString(), ticketContentDocument);
        return ticketContentDocument;
    }

    private void setImageValue(Integer operatorId, Integer entityId, Long productId, ProductTicketContentValue productTicketContentValue, ProductTicketContentImageDTO imageDTO
    ) {
        productTicketContentValue.getImages().stream()
                .filter(el -> el.getType().equals(imageDTO.getType()))
                .findFirst()
                .ifPresent(el -> el.setValue(addImageValue(operatorId, entityId, productId, imageDTO)));
    }

    private String addImageValue(Integer operatorId, Integer entityId, Long productId, ProductTicketContentImageDTO imageDTO) {
        Optional.ofNullable(staticDataContainer.getLanguageByCode(imageDTO.getLanguage()))
                .orElseThrow(() -> ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));

        if (imageDTO.getType() != null) {
            return uploadImageFile(operatorId, entityId, productId, imageDTO.getLanguage(), imageDTO.getType().name(), imageDTO.getImageUrl());
        }
        throw new OneboxRestException(MsEventErrorCode.PRODUCT_IMAGE_TYPE_NOT_FOUND);
    }

    private String uploadImageFile(final Integer operatorId, final Integer entityId, final Long productId,
                                   String langCode, String fileType, String imageBinary) {
        return CommunicationElementsUtils.uploadProductTicketImage(operatorId, entityId, productId, langCode, fileType,
                imageBinary, s3OneboxRepository);
    }

    public ProductTicketContent deleteTicketContentsImage(Long productId, TicketType ticketType, TicketContentImageType imageType, String langCode) {
        ProductTicketContent ticketContentDocument = productTicketContentsDao.get(productId.toString());
        Map<String, ProductTicketContentValue> ticketContentValueMap = ticketContentDocument.getTicketContentByType().getOrDefault(ticketType, Map.of());
        ProductTicketContentValue ticketContentValue = ticketContentValueMap.get(langCode);

        if (ticketContentValue != null) {
            Optional<ProductTicketContentImageDetail> imageDetailOptional = ticketContentValue
                    .getImages()
                    .stream()
                    .filter(img -> img.getType().equals(imageType))
                    .findFirst();

            imageDetailOptional.ifPresent(imageDetail -> {
                ticketContentDocument.getTicketContentByType()
                        .get(ticketType)
                        .get(langCode)
                        .getImages().remove(imageDetail);

                CommunicationElementsUtils.deleteProductTicketImage(s3OneboxRepository, imageDetailOptional.get().getValue());
                productTicketContentsDao.upsert(productId.toString(), ticketContentDocument);

            });
            return ticketContentDocument;
        } else {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND);
        }
    }
}
