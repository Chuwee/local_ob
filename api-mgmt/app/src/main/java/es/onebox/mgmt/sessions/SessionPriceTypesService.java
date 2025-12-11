package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.converters.SessionConverter;
import es.onebox.mgmt.sessions.dto.PriceTypeDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeRequestDTO;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.utils.VenueTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionPriceTypesService {

    @Autowired
    private ValidationService validationService;
    @Autowired
    private SessionsRepository sessionsRepository;
    @Autowired
    private VenuesRepository venuesRepository;

    public List<PriceTypeDTO> getSessionPriceTypes(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);

        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(session.getVenueConfigId());

        return sessionsRepository.getPriceTypes(eventId, sessionId)
                .getData().stream()
                .map(priceType ->  SessionConverter.fromMsEvent(priceType, venueTemplate))
                .collect(Collectors.toList());
    }

    public void upsert(Long eventId, Long sessionId, Long priceTypeId, PriceTypeRequestDTO request) {
        Session session = validationService.getAndCheckSessionExternal(eventId, sessionId);

        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(session.getVenueConfigId());
        if (!VenueTemplateUtils.isVisitOrThemePark(venueTemplate.getTemplateType()) &&
                request.getAdditionalConfig() != null &&
                request.getAdditionalConfig().getGateId() != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACTIVITY_SESSION_MANDATORY, "sessionId: " + sessionId + ", venueTemplateType: " + venueTemplate.getTemplateType().name(), null);
        }

        sessionsRepository.updatePriceTypes(eventId, sessionId, priceTypeId, SessionConverter.toMsEvent(request));
    }
}
