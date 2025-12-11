package es.onebox.event.common.services;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.events.converter.EmailCommunicationElementConverter;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEmailRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommonEmailCommunicationElementService {

    private final EmailCommunicationElementDao emailCommunicationElementDao;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;
    private final EventDao eventDao;


    @Autowired
    public CommonEmailCommunicationElementService(
            EmailCommunicationElementDao emailCommunicationElementDao,
            ItemDescSequenceDao itemDescSequenceDao, DescPorIdiomaDao descPorIdiomaDao,
            StaticDataContainer staticDataContainer,
            S3BinaryRepository s3OneboxRepository, EventDao eventDao) {
        this.emailCommunicationElementDao = emailCommunicationElementDao;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
        this.eventDao = eventDao;
    }


    public List<EmailCommunicationElementDTO> findCommunicationElements(final EmailCommunicationElementFilter filter, final EventRecord event) {
        final Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = this.emailCommunicationElementDao
                .findEventCommunicationElements(event.getIdevento(), convertLangCodeToLangIdIfNeeded(filter));
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return EmailCommunicationElementConverter.fromRecords(records, event.getOperatorId().longValue(), staticDataContainer);
    }

    public void updateEventCommunicationElements(Set<EmailCommunicationElementDTO> elements, final EventRecord event) {
        final Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = this.emailCommunicationElementDao
                .findEventCommunicationElements(event.getIdevento(), buildFilter(elements));
        Integer commElementId = event.getElementocomemail();
        CpanelElementosComEmailRecord comElements = null;
        if (commElementId != null) {
            comElements = emailCommunicationElementDao.findById(commElementId);
        }
        if (comElements == null) {
            comElements = emailCommunicationElementDao.insertNew();
            this.eventDao.updateField(event.getIdevento(), Tables.CPANEL_EVENTO.ELEMENTOCOMEMAIL, comElements.getIdinstancia());
        }
        for (EmailCommunicationElementDTO el : elements) {
            if (isValueNotInformed(el)) {
                throw new OneboxRestException(MsEventErrorCode.COMMUNICATION_ELEMENT_UPDATE_REQUIRED,
                        "No value to update for event: " + event.getIdevento(), null);
            }
            String value;
            Integer languageId = staticDataContainer.getLanguageByCode(el.getLanguage());
            CpanelDescPorIdiomaRecord desc = checkAndGetElement(el, records, languageId);
            Integer itemId = resolveItemId(comElements, el.getTag());
            if (el.getImageBinary() != null && StringUtils.isNotBlank(el.getImageBinary())) {
                value = uploadFile(event, el, itemId, languageId, desc);
            } else {
                value = el.getValue();
            }
            this.descPorIdiomaDao.upsert(itemId, languageId, value);
        }
    }

    public void deleteCommunicationElement(EmailCommunicationElementTagType tag, String language, final EventRecord event) {
        final Integer languageId = this.staticDataContainer.getLanguageByCode(language);
        final Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records = this.emailCommunicationElementDao
                .findEventCommunicationElements(event.getIdevento(), buildFilter(tag, languageId));
        if (CollectionUtils.isEmpty(records)) {
            throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Deleting event: " + event.getIdevento() + " email item not found", null);
        }
        records.get(tag).stream().findFirst().ifPresent(record -> {
            this.descPorIdiomaDao.delete(record);
            CommunicationElementsUtils.deleteItemImage(s3OneboxRepository, languageId, record.getIditem(), record.getDescripcion(), event.getOperatorId());
        });
    }

    private String uploadFile(final EventRecord event, EmailCommunicationElementDTO el, Integer itemId, Integer languageId, CpanelDescPorIdiomaRecord desc) {
        String previousFile = desc != null && desc.getDescripcion() != null ? desc.getDescripcion() : null;
        return CommunicationElementsUtils.uploadItemImage(s3OneboxRepository, previousFile, languageId, itemId, el.getImageBinary(),
                event.getOperatorId(), ImageFormat.JPG);
    }

    private Integer resolveItemId(CpanelElementosComEmailRecord comElements, EmailCommunicationElementTagType tag) {
        Integer itemId = getTagItemId(comElements, tag);
        if (itemId == null) {
            itemId = this.itemDescSequenceDao.insertNewRecord();
            setTagRecord(comElements, itemId, tag);
            this.emailCommunicationElementDao.update(comElements);
        }
        return itemId;
    }

    private static CpanelDescPorIdiomaRecord checkAndGetElement(EmailCommunicationElementDTO element,
                                                                Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> records, Integer languageId) {
        if (records.containsKey(element.getTag())) {
            return records.get(element.getTag()).stream()
                    .filter(desc -> desc.getIdidioma().equals(languageId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private static void setTagRecord(CpanelElementosComEmailRecord record, Integer itemId, EmailCommunicationElementTagType tag) {
        switch (tag) {
            case PROMOTER_BANNER:
                record.setBannerpromotor(itemId);
                break;
            case CHANNEL_BANNER:
                record.setBannercanal(itemId);
                break;
            case CHANNEL_BANNER_LINK:
                record.setLinkbannercanal(itemId);
                break;
            case CHANNEL_HEADER_BANNER:
                record.setBannercabeceracanal(itemId);
                break;
            case CHANNEL_HEADER_BANNER_LINK:
                record.setLinkbannercabeceracanal(itemId);
                break;
            default:
                break;
        }
    }

    private static Integer getTagItemId(CpanelElementosComEmailRecord record, EmailCommunicationElementTagType tag) {
        switch (tag) {
            case PROMOTER_BANNER:
                return record.getBannerpromotor();
            case CHANNEL_BANNER:
                return record.getBannercanal();
            case CHANNEL_BANNER_LINK:
                return record.getLinkbannercanal();
            case CHANNEL_HEADER_BANNER:
                return record.getBannercabeceracanal();
            case CHANNEL_HEADER_BANNER_LINK:
                return record.getLinkbannercabeceracanal();
            default:
                return null;
        }
    }

    private boolean isValueNotInformed(EmailCommunicationElementDTO el) {
        return (el.getImageBinary() == null && el.getValue() == null) || (StringUtils.isBlank(el.getImageBinary()) && el.getValue() == null);
    }

    private EmailCommunicationElementFilter buildFilter(Set<EmailCommunicationElementDTO> elements) {
        EmailCommunicationElementFilter filter = new EmailCommunicationElementFilter();
        Set<EmailCommunicationElementTagType> tags = elements.stream().map(EmailCommunicationElementDTO::getTag)
                .collect(Collectors.toSet());
        filter.setTags(EnumSet.copyOf(tags));
        return filter;
    }

    private EmailCommunicationElementFilter buildFilter(EmailCommunicationElementTagType tag, Integer languageId) {
        EmailCommunicationElementFilter filter = new EmailCommunicationElementFilter();
        filter.setTags(EnumSet.of(tag));
        filter.setLanguageId(languageId);
        return filter;
    }

    private EmailCommunicationElementFilter convertLangCodeToLangIdIfNeeded(final EmailCommunicationElementFilter filter) {
        if (StringUtils.isNotBlank(filter.getLanguage()) && filter.getLanguageId() == null) {
            filter.setLanguageId(staticDataContainer.getLanguageByCode(filter.getLanguage()));
        }
        return filter;
    }
}
