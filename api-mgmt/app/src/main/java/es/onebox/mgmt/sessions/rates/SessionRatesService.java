package es.onebox.mgmt.sessions.rates;

import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.events.converter.RateConverter;
import es.onebox.mgmt.events.converter.RatesConverter;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.sessions.dto.SessionRateDTO;
import es.onebox.mgmt.validation.RateRestrictionsValidator;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionRatesService {

    private final EventsRepository eventsRepository;
    private final SessionsRepository sessionsRepository;
    private final ValidationService validationService;

    protected SessionRatesService(EventsRepository eventsRepository, SessionsRepository sessionsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.sessionsRepository = sessionsRepository;
        this.validationService = validationService;
    }

    public List<SessionRateDTO> getRates(Long eventId, Long sessionId) {

        validationService.getAndCheckSession(eventId, sessionId);

        EventRates rates = eventsRepository.getSessionRates(eventId, sessionId);
        return RatesConverter.fromMsSessionRate(rates);
    }

    public RatesRestrictedDTO getRestrictedRates(Long eventId, Long sessionId) {
        validationService.getAndCheckSession(eventId, sessionId);
        return RateConverter.fromMsEvent(sessionsRepository.getSessionRatesRestrictions(eventId, sessionId));
    }

    public void upsertSessionRatesRestrictions(Long eventId, Long sessionId, Long rateId, RateRestrictionDTO restrictionDTO) {
        validationService.getAndCheckSession(eventId, sessionId);
        RateRestrictionsValidator.validateRestrictions(eventsRepository.getEvent(eventId), restrictionDTO);

        sessionsRepository.upsertSessionRatesRestrictions(eventId, sessionId, rateId, RateConverter.toMsEvent(restrictionDTO));
    }

    public void deleteSessionRate(Long eventId, Long sessionId, Long rateId) {
        validationService.getAndCheckSession(eventId, sessionId);
        sessionsRepository.deleteSessionRateRestrictions(eventId, sessionId, rateId);
    }

}
