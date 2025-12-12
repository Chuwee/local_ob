package es.onebox.circuitcat.venues.service;

import es.onebox.circuitcat.venues.converter.VenueConverter;
import es.onebox.circuitcat.venues.dto.VenueConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VenueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VenueService.class);

    private MsEventRepository eventRepository;
    private VenueTemplateRepository venueTemplateRepository;

    @Autowired
    public VenueService(MsEventRepository eventRepository, VenueTemplateRepository venueTemplateRepository) {
        this.eventRepository = eventRepository;
        this.venueTemplateRepository = venueTemplateRepository;
    }

    public VenueConfigDTO getVenueConfig(List<Long> sessionIds) {
        VenueConfigDTO venueConfigDTO = new VenueConfigDTO();
        Map<Long, VenueTemplate> venueConfigIds = new HashMap<>();

        for (Long sessionId : sessionIds) {
            SessionDTO sessionDTO;
            try {
                sessionDTO = eventRepository.getSession(sessionId);
            } catch (OneboxRestException e) {
                LOGGER.error("[CIRCUIT CAT SECTOR] Session {} not found", sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
            }

            if (!venueConfigIds.containsKey(sessionDTO.getVenueConfigId())) {
                VenueTemplate venueTemplate = venueTemplateRepository.getVenueTemplate(sessionDTO.getVenueConfigId());
                venueConfigIds.put(sessionDTO.getVenueConfigId(), venueTemplate);
                if (venueConfigDTO.getVenues() == null) {
                    venueConfigDTO.setVenues(new ArrayList<>());
                }
                venueConfigDTO.getVenues().add(VenueConverter.convert(venueTemplate));
            }
        }

        return venueConfigDTO;
    }
}
