package es.onebox.atm.events.service;

import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.event.dto.PreSaleConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SessionConfigDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtmEventService {

    private final MsEventRepository msEventRepository;
    private final CatalogRepository catalogRepository;
    @Autowired
    public AtmEventService(MsEventRepository msEventRepository,
                           CatalogRepository catalogRepository) {
        this.msEventRepository = msEventRepository;
        this.catalogRepository = catalogRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmEventService.class);

    public PreSaleConfigDTO getSessionPresaleInformation(Long eventId, Long sessionId) {
        LOGGER.info("[ATM EVENTS] Starting getting " +
                "presale information for event: [{}] and session: [{}]", eventId, sessionId);

        validateChannelVisibility(eventId, sessionId);
        SessionConfigDTO sessionConfigDTO = msEventRepository.getSessionConfig(sessionId.intValue());

        validateSessionConfig(sessionConfigDTO, sessionId);

        return sessionConfigDTO.getPreSaleConfig();
    }

    private void validateChannelVisibility(Long eventId, Long sessionId) {
        String token = AuthContextUtils.getToken();

        //Make sure the channel has visibility through the session and event, the returned value is not needed
        if (catalogRepository.getSession(token, eventId, sessionId) == null) {
            LOGGER.error("[ATM EVENTS] The channel has no visibility of the session: {}", sessionId);

            throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
        }
    }

    private void validateSessionConfig(SessionConfigDTO sessionConfigDTO, Long sessionId) {
        if(sessionConfigDTO == null) {
            LOGGER.error("[ATM EVENTS] Session not found for id: {}", sessionId);
            throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
        }
        if(sessionConfigDTO.getPreSaleConfig() == null) {
            LOGGER.error("[ATM EVENTS] Presale not found for session id: {}", sessionId);
            throw new OneboxRestException(ApiExternalErrorCode.PRESALE_NOT_FOUND);
        }
    }
}
