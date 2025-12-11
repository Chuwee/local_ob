package es.onebox.mgmt.seasontickets.service;

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
import es.onebox.mgmt.datasources.ms.event.dto.event.EmailCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeasonTicketChannelContentsService {

    private final EventChannelContentsRepository eventCommunicationElementRepository;
    private final SeasonTicketRepository seasonTicketRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;

    @Autowired
    public SeasonTicketChannelContentsService(EventChannelContentsRepository eventCommunicationElementRepository,
                                              SeasonTicketRepository seasonTicketRepository,
                                              MasterdataService masterdataService,
                                              SecurityManager securityManager) {
        this.eventCommunicationElementRepository = eventCommunicationElementRepository;
        this.seasonTicketRepository = seasonTicketRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
    }

    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentTexts(Long seasonTicketId, EventChannelContentTextFilter filter) {
        getAndCheckSeasonTicket(seasonTicketId);

        CommunicationElementFilter<EventTagType> communicationElementFilter
        = ChannelContentConverter.fromEventFilter(filter, masterdataService);

        List<EventCommunicationElement> comElements = eventCommunicationElementRepository.getSeasonTicketCommunicationElements(
                seasonTicketId, communicationElementFilter, EventTagType::isText);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).thenComparing(EventCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsEventText(comElements);
    }

    public void updateChannelContentTexts(Long seasonTicketId, List<ChannelContentTextDTO<EventChannelContentTextType>> texts) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentTextDTO<EventChannelContentTextType> element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForSeasonTicket(seasonTicket, languages, element.getLanguage()));
        }

        eventCommunicationElementRepository.updateSeasonTicketCommunicationElements(seasonTicketId, ChannelContentConverter.toMsEventText(texts));
    }

    public ChannelContentImageListDTO<EventChannelContentImageType> getChannelContentImages(Long seasonTicketId, ChannelContentImageFilter<EventChannelContentImageType> filter) {
        getAndCheckSeasonTicket(seasonTicketId);

        CommunicationElementFilter<EventTagType> communicationElementFilter
        = ChannelContentConverter.fromEventFilter(filter, masterdataService);

        List<EventCommunicationElement> comElements = eventCommunicationElementRepository.getSeasonTicketCommunicationElements(
                seasonTicketId, communicationElementFilter, EventTagType::isImage);

        comElements.sort(Comparator.comparing(EventCommunicationElement::getLanguage).
                thenComparing(EventCommunicationElement::getTagId).
                thenComparing(EventCommunicationElement::getPosition));

        CommunicationElementFilter<EmailCommunicationElementTagType> emailFilter = new CommunicationElementFilter<>();
        emailFilter.setTags(Sets.newHashSet(EmailCommunicationElementTagType.PROMOTER_BANNER));
        List<EmailCommunicationElement> emailComElements = eventCommunicationElementRepository.getSeasonTicketEmailCommunicationElements(seasonTicketId, emailFilter);

        ChannelContentImageListDTO<EventChannelContentImageType> result = ChannelContentConverter.fromMsEventImage(comElements);
        result.addAll(ChannelContentConverter.fromMsEventImage(emailComElements));

        return result;
    }

    public void updateChannelContentImages(Long seasonTicketId, List<ChannelContentImageDTO<EventChannelContentImageType>> images) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        Map<Boolean, List<ChannelContentImageDTO<EventChannelContentImageType>>> results = images.stream()
                .peek(element -> element.setLanguage(ChannelContentsUtils.checkElementLanguageForSeasonTicket(seasonTicket, languages, element.getLanguage())))
                .collect(Collectors.partitioningBy(el -> EventChannelContentImageType.PROMOTER_BANNER.equals(el.getType())));

        if (CollectionUtils.isNotEmpty(results.get(Boolean.FALSE))) {
            eventCommunicationElementRepository.updateSeasonTicketCommunicationElements(seasonTicketId, ChannelContentConverter.toMsEventImageList(results.get(Boolean.FALSE)));
        }
        if (CollectionUtils.isNotEmpty(results.get(Boolean.TRUE))) {
            eventCommunicationElementRepository.updateSeasonTicketEmailCommunicationElements(seasonTicketId, ChannelContentConverter.toMsEventEmailImageList(results.get(Boolean.TRUE)));
        }
    }

    public void deleteChannelContentImages(Long seasonTicketId, String language, EventChannelContentImageType type, Integer position) {
        getAndCheckSeasonTicket(seasonTicketId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        if (EventChannelContentImageType.PROMOTER_BANNER.equals(type)) {
            String languageCode = ConverterUtils.checkLanguage(language, languages);
            eventCommunicationElementRepository.deleteSeasonTicketEmailCommunicationElements(seasonTicketId, languageCode, EmailCommunicationElementTagType.PROMOTER_BANNER);
        } else {
            EventCommunicationElement dto = ChannelContentConverter.buildEventCommunicationElementToDelete(language, type, position, languages);
            eventCommunicationElementRepository.updateSeasonTicketCommunicationElements(seasonTicketId, Collections.singletonList(dto));
        }
    }

    private SeasonTicket getAndCheckSeasonTicket(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND, "No season ticket found with id: " + seasonTicketId, null);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());

        return seasonTicket;
    }
}
