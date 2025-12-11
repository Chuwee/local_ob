package es.onebox.mgmt.sessions;

import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.ticket.dto.WhitelistSearchResponse;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.sessions.converters.SessionWhitelistConverter;
import es.onebox.mgmt.sessions.dto.SessionWhitelistDTO;
import es.onebox.mgmt.sessions.dto.WhitelistFilterDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionWhitelistService {

    private final ValidationService validationService;
    private final TicketsRepository ticketsRepository;

    private final VenuesRepository venuesRepository;

    @Autowired
    public SessionWhitelistService(ValidationService validationService, TicketsRepository ticketsRepository,
                                   VenuesRepository venuesRepository) {
        this.validationService = validationService;
        this.ticketsRepository = ticketsRepository;
        this.venuesRepository = venuesRepository;
    }

    public SessionWhitelistDTO getWhitelist(Long eventId, Long sessionId, WhitelistFilterDTO filter) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        Event event = validationService.getAndCheckEvent(eventId);
        VenueTemplate venue = venuesRepository.getVenueTemplate(session.getVenueConfigId());
        List<PriceType> priceTypes = venuesRepository.getPriceTypes(session.getVenueConfigId());

        WhitelistSearchResponse response = ticketsRepository.getWhitelist(sessionId, SessionWhitelistConverter.toMs(filter));
        return SessionWhitelistConverter.toDTO(response, event, session, venue, priceTypes);
    }
}
