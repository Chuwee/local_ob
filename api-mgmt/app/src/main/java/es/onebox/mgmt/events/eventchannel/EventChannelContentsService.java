package es.onebox.mgmt.events.eventchannel;

import com.google.common.collect.Sets;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.common.enums.EmailCommunicationElementTagType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EmailCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventChannelContentsService {

    private final EventChannelContentsRepository eventCommunicationElementRepository;
    private final EventsRepository eventsRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;

    @Autowired
    public EventChannelContentsService(EventChannelContentsRepository eventCommunicationElementRepository,
            EventsRepository eventsRepository, MasterdataService masterdataService, SecurityManager securityManager,
                                       SaleRequestsRepository saleRequestsRepository) {
        this.eventCommunicationElementRepository = eventCommunicationElementRepository;
        this.eventsRepository = eventsRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
        this.saleRequestsRepository = saleRequestsRepository;
    }

    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentTexts(Long eventId, EventChannelContentTextFilter filter) {
        getAndCheckEventAndChannel(eventId);

        CommunicationElementFilter<EventTagType> communicationElementFilter
        = ChannelContentConverter.fromEventFilter(filter, masterdataService);

        List<EventCommunicationElement> comElements = eventCommunicationElementRepository.getEventCommunicationElements(eventId,
                communicationElementFilter, EventTagType::isText);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).thenComparing(EventCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsEventText(comElements);
    }

    public void updateChannelContentTexts(Long eventId, List<ChannelContentTextDTO<EventChannelContentTextType>> texts) {
        Event event = getAndCheckEvent(eventId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentTextDTO<EventChannelContentTextType> element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage()));
        }

        eventCommunicationElementRepository.updateEventCommunicationElements(eventId, ChannelContentConverter.toMsEventText(texts));
    }

    public ChannelContentImageListDTO<EventChannelContentImageType> getChannelContentImages(Long eventId, ChannelContentImageFilter<EventChannelContentImageType> filter) {
        getAndCheckEventAndChannel(eventId);

        if (filter != null && filter.getType() != null) {
            if (EventChannelContentImageType.PROMOTER_BANNER.name().equals(filter.getType().name())) {
                return getEventEmailCommunicationElements(eventId, filter);
            }
            return getEventCommunicationElements(eventId, filter);
        }
        ChannelContentImageListDTO<EventChannelContentImageType> result = getEventCommunicationElements(eventId, filter);
        result.addAll(getEventEmailCommunicationElements(eventId, filter));
        return result;
    }

    private ChannelContentImageListDTO<EventChannelContentImageType> getEventEmailCommunicationElements(Long eventId, ChannelContentImageFilter<EventChannelContentImageType> filter) {
        CommunicationElementFilter<EmailCommunicationElementTagType> emailFilter = new CommunicationElementFilter<>();
        emailFilter.setTags(Sets.newHashSet(EmailCommunicationElementTagType.PROMOTER_BANNER));
        if (filter != null && filter.getLanguage() != null) {
            emailFilter.setLanguageId(masterdataService.getLanguageByCode(ConverterUtils.toLocale(filter.getLanguage())));
        }
        List<EmailCommunicationElement> emailComElements = eventCommunicationElementRepository.getEventEmailCommunicationElements(eventId, emailFilter);
       return ChannelContentConverter.fromMsEventImage(emailComElements);
    }

    private ChannelContentImageListDTO<EventChannelContentImageType> getEventCommunicationElements(Long eventId, ChannelContentImageFilter<EventChannelContentImageType> filter) {
        CommunicationElementFilter<EventTagType> communicationElementFilter
                = ChannelContentConverter.fromEventFilter(filter, masterdataService);
        List<EventCommunicationElement> comElements = eventCommunicationElementRepository.getEventCommunicationElements(
                eventId, communicationElementFilter, EventTagType::isImage);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).
                thenComparing(EventCommunicationElement::getTagId).
                thenComparing(EventCommunicationElement::getPosition));

        return ChannelContentConverter.fromMsEventImage(comElements);
    }

    public void updateChannelContentImages(Long eventId, List<ChannelContentImageDTO<EventChannelContentImageType>> images) {
        Event event = getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        Map<Boolean, List<ChannelContentImageDTO<EventChannelContentImageType>>> results = images.stream()
                .peek(element -> element.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage())))
                .collect(Collectors.partitioningBy(el -> EventChannelContentImageType.PROMOTER_BANNER.equals(el.getType())));

        if (CollectionUtils.isNotEmpty(results.get(Boolean.FALSE))) {
            eventCommunicationElementRepository.updateEventCommunicationElements(eventId, ChannelContentConverter.toMsEventImageList(results.get(Boolean.FALSE)));
        }
        if (CollectionUtils.isNotEmpty(results.get(Boolean.TRUE))) {
            eventCommunicationElementRepository.updateEventEmailCommunicationElements(eventId, ChannelContentConverter.toMsEventEmailImageList(results.get(Boolean.TRUE)));
        }
    }

    public void deleteChannelContentImages(Long eventId, String language, EventChannelContentImageType type, Integer position) {
        getAndCheckEvent(eventId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        if (EventChannelContentImageType.PROMOTER_BANNER.equals(type)) {
            String languageCode = ConverterUtils.checkLanguage(language, languages);
            eventCommunicationElementRepository.deleteEventEmailCommunicationElements(eventId, languageCode, EmailCommunicationElementTagType.PROMOTER_BANNER);
        } else {
            EventCommunicationElement dto = ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages);
            eventCommunicationElementRepository.updateEventCommunicationElements(eventId, Collections.singletonList(dto));
        }
    }

    private Event getAndCheckEvent(Long eventId) {
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || event.getStatus().equals(EventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND, "No event found with id: " + eventId, null);
        }
        securityManager.checkEntityAccessible(event.getEntityId());

        return event;
    }

    private Event getAndCheckEventAndChannel(Long eventId) {
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || event.getStatus().equals(EventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND, "No event found with id: " + eventId, null);
        }

        if (!securityManager.isEntityAccessible(event.getEntityId(), false)) {
            MsSaleRequestsFilter filter = toFilter(eventId);
            MsSaleRequestsResponseDTO saleRequests = saleRequestsRepository.searchSaleRequests(filter);
            if (saleRequests.getMetadata().getTotal() <= 0L) {
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
            }
        }

        return event;
    }

    private static MsSaleRequestsFilter toFilter(Long eventId) {
        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setLimit(10L);
        filter.setOffset(0L);
        filter.setEventId(Arrays.asList(eventId));
        filter.setChannelEntityId(Arrays.asList(SecurityUtils.getUserEntityId()));
        filter.setIncludeArchived(Boolean.FALSE);
        return filter;
    }
}
