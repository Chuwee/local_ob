package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.common.enums.EmailCommunicationElementTagType;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChannelEventImageConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EmailCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class EventChannelContentsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventChannelContentsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }
    
    public List<EventCommunicationElement> getEventCommunicationElements(Long eventId, CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> tagType) {
        ChannelContentsUtils.addEventTagsToFilter(filter, tagType);
        return msEventDatasource.getEventCommunicationElements(eventId, filter);
    }
    
    public void updateEventCommunicationElements(Long eventId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateEventCommunicationElements(eventId, elements);
    }
    
    public List<EmailCommunicationElement> getEventEmailCommunicationElements(Long eventId, CommunicationElementFilter<EmailCommunicationElementTagType> filter) {
        return msEventDatasource.getEventEmailCommunicationElements(eventId, filter);
    }
    
    public void updateEventEmailCommunicationElements(Long eventId, List<EmailCommunicationElement> elements) {
        msEventDatasource.updateEventEmailCommunicationElements(eventId, elements);
    }
    
    public void deleteEventEmailCommunicationElements(Long eventId, String language, EmailCommunicationElementTagType type) {
        msEventDatasource.deleteEventEmailCommunicationElement(eventId, language, type);
    }

    public List<EventCommunicationElement> getSeasonTicketCommunicationElements(Long seasonTicketId, CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> tagType) {
        ChannelContentsUtils.addEventTagsToFilter(filter, tagType);
        return msEventDatasource.getSeasonTicketCommunicationElements(seasonTicketId, filter);
    }

    public void updateSeasonTicketCommunicationElements(Long seasonTicketId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateSeasonTicketCommunicationElements(seasonTicketId, elements);
    }

    public List<EmailCommunicationElement> getSeasonTicketEmailCommunicationElements(Long seasonTicketId, CommunicationElementFilter<EmailCommunicationElementTagType> filter) {
        return msEventDatasource.getSeasonTicketEmailCommunicationElements(seasonTicketId, filter);
    }

    public void updateSeasonTicketEmailCommunicationElements(Long seasonTicketId, List<EmailCommunicationElement> elements) {
        msEventDatasource.updateSeasonTicketEmailCommunicationElements(seasonTicketId, elements);
    }

    public void deleteSeasonTicketEmailCommunicationElements(Long seasonTicketId, String language, EmailCommunicationElementTagType type) {
        msEventDatasource.deleteSeasonTicketEmailCommunicationElement(seasonTicketId, language, type);
    }

    public List<EventCommunicationElement> getChannelEventCommunicationElements(Long eventId, Long channelId, CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> tagType) {
        ChannelContentsUtils.addEventTagsToFilter(filter, tagType);
        return msEventDatasource.getChannelEventCommunicationElements(eventId, channelId, filter);
    }

    public void updateChannelEventCommunicationElements(Long eventId, Long channelId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateChannelEventCommunicationElements(eventId, channelId, elements );
    }

    public List<ChannelEventImageConfigDTO> getChannelEventImageConfig(Long eventId, Long channelId) {
        return msEventDatasource.getChannelEventImageConfig(eventId, channelId);
    }
}
