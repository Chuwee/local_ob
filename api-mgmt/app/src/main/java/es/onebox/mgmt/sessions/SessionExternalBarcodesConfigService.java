package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.ExternalBarcodeEventConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalBarcodeSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.EventExternalBarcodeConfigRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionExternalBarcodesConfigRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.entities.entityexternalbarcodes.EntityExternalBarcodesRepository;
import es.onebox.mgmt.events.EventExternalBarcodesConfigService;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.converters.SessionExternalBarcodeConverter;
import es.onebox.mgmt.sessions.dto.SessionExternalBarcodeConfigDTO;
import es.onebox.mgmt.sessions.enums.SessionPassType;
import es.onebox.mgmt.sessions.enums.SessionsExternalBarcodeConfigProperties;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionExternalBarcodesConfigService {

    private final ValidationService validationService;
    private final EventExternalBarcodesConfigService eventExternalBarcodesConfigService;
    private final EventExternalBarcodeConfigRepository eventExternalBarcodeConfigRepository;
    private final SessionExternalBarcodesConfigRepository sessionExternalBarcodesConfigRepository;
    private final EntityExternalBarcodesRepository entityExternalBarcodesRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    public SessionExternalBarcodesConfigService(ValidationService validationService,
                                                EventExternalBarcodesConfigService eventExternalBarcodesConfigService,
                                                EventExternalBarcodeConfigRepository eventExternalBarcodeConfigRepository,
                                                SessionExternalBarcodesConfigRepository sessionExternalBarcodesConfigRepository,
                                                EntityExternalBarcodesRepository entityExternalBarcodesRepository, OrdersRepository ordersRepository) {
        this.validationService = validationService;
        this.eventExternalBarcodesConfigService = eventExternalBarcodesConfigService;
        this.eventExternalBarcodeConfigRepository = eventExternalBarcodeConfigRepository;
        this.sessionExternalBarcodesConfigRepository = sessionExternalBarcodesConfigRepository;
        this.entityExternalBarcodesRepository = entityExternalBarcodesRepository;
        this.ordersRepository = ordersRepository;
    }

    public SessionExternalBarcodeConfigDTO getSessionExternalBarcodeConfig(Long eventId, Long sessionId) {
        validateRequest(eventId, sessionId);

        ExternalBarcodeSessionConfig barcodeSessionConfig = sessionExternalBarcodesConfigRepository.getSessionExternalBarcodeConfig(sessionId);
        return SessionExternalBarcodeConverter.toDTO(barcodeSessionConfig);
    }

    public void updateSessionExternalBarcodeConfig(Long eventId, Long sessionId, SessionExternalBarcodeConfigDTO sessionExternalBarcodeConfig) {
        Session session = validateRequest(eventId, sessionId);

        boolean hasSales = ordersRepository.sessionHasOrders(sessionId);
        if (!hasSales) {
            ExternalBarcodeConfig entityBarcodeConfigs = entityExternalBarcodesRepository.getEntityExternalBarcodeConfig(session.getEntityId());

            if (sessionExternalBarcodeConfig.getPersonType() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "person_type can not be null", null);
            }else{
                validateAgainstEntityConfigurations(
                    entityBarcodeConfigs,
                    SessionsExternalBarcodeConfigProperties.PERSON_TYPES,
                    sessionExternalBarcodeConfig.getPersonType()
                );
            }
            if (sessionExternalBarcodeConfig.getVariableCode() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "variable_code can not be null", null);
            }else{
                validateAgainstEntityConfigurations(
                    entityBarcodeConfigs,
                    SessionsExternalBarcodeConfigProperties.VARIABLE_CODES,
                    sessionExternalBarcodeConfig.getVariableCode()
                );
            }
        }

        if (SessionPassType.USES.equals(sessionExternalBarcodeConfig.getPassType())
                && sessionExternalBarcodeConfig.getUses() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid external barcode config, uses can not be null", null);
        }

        if (SessionPassType.DAYS.equals(sessionExternalBarcodeConfig.getPassType())
                && sessionExternalBarcodeConfig.getDays() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid external barcode config, days can not be null", null);
        }

        ExternalBarcodeSessionConfig body = SessionExternalBarcodeConverter.toMs(eventId, sessionId, sessionExternalBarcodeConfig, session, hasSales);
        sessionExternalBarcodesConfigRepository.updateSessionExternalBarcodeConfig(sessionId, body);
    }

    private Session validateRequest(Long eventId, Long sessionId) {
        eventExternalBarcodesConfigService.validateRequest(eventId);
        Session session = validationService.getAndCheckSession(eventId, sessionId);

        ExternalBarcodeEventConfig externalBarcodeEventConfig = eventExternalBarcodeConfigRepository.getExternalBarcodeEntityConfig(eventId);
        if (externalBarcodeEventConfig == null || BooleanUtils.isNotTrue(externalBarcodeEventConfig.getAllow())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SESSION_UNSUPPORTED_OPERATION, "The use of external barcodes is disabled for the event", null);
        }
        return session;
    }

    private void validateAgainstEntityConfigurations(ExternalBarcodeConfig entityBarcodeConfigs,
                                                     SessionsExternalBarcodeConfigProperties configType, String configValue) {
        List<String> validValues = (List<String>) entityBarcodeConfigs.getProperties().get(configType.getProperty());
        if(!validValues.contains(configValue)){
            throw new OneboxRestException(
                    ApiMgmtErrorCode.INVALID_EXTERNAL_BARCODE_CONFIG_VALUE,
                    configValue+" is not among the valid values "+configType+" can have.",
                    null);
        }
    }
}
