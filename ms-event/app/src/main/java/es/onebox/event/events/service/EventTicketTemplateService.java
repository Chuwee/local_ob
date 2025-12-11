package es.onebox.event.events.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.onebox.event.events.converter.TicketTemplateConverter;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dto.TicketTemplateDTO;

@Service
public class EventTicketTemplateService {

    private final EventDao eventDao;

    @Autowired
    public EventTicketTemplateService(final EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<TicketTemplateDTO> findTicketTemplatesByEventId(Integer eventId) {
        return eventDao.getTicketTemplates(eventId).stream().map(TicketTemplateConverter::convert).collect(Collectors.toList());
    }
}
