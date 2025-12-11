package es.onebox.mgmt.events;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityExternalBarcodeConfigRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.ExternalBarcodeEventConfig;
import es.onebox.mgmt.datasources.ms.event.repository.EventExternalBarcodeConfigRepository;
import es.onebox.mgmt.entities.entityexternalbarcodes.EntityExternalBarcodesRepository;
import es.onebox.mgmt.events.converter.EventExternalBarcodeConverter;
import es.onebox.mgmt.events.dto.EventExternalBarcodesConfigDTO;
import es.onebox.mgmt.events.enums.EventExternalBarcodesConfigProperties;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventExternalBarcodesConfigService {

    private final EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository;
    private final EventExternalBarcodeConfigRepository externalBarcodeEventRepository;
    private final EntityExternalBarcodesRepository entityExternalBarcodesRepository;
    private final ValidationService validationService;

    public EventExternalBarcodesConfigService(EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository,
            EventExternalBarcodeConfigRepository externalBarcodeEventRepository,
            EntityExternalBarcodesRepository entityExternalBarcodesRepository, ValidationService validationService) {
        this.entityExternalBarcodeConfigRepository = entityExternalBarcodeConfigRepository;
        this.externalBarcodeEventRepository = externalBarcodeEventRepository;
        this.entityExternalBarcodesRepository = entityExternalBarcodesRepository;
        this.validationService = validationService;
    }

    public EventExternalBarcodesConfigDTO getExternalBarcodeEventConfig(Long eventId) {
        validateRequest(eventId);
        ExternalBarcodeEventConfig externalBarcodeEventConfig = externalBarcodeEventRepository.getExternalBarcodeEntityConfig(eventId);
        return EventExternalBarcodeConverter.toDTO(externalBarcodeEventConfig);

    }

    public void updateExternalBarcodesConfig(Long eventId, EventExternalBarcodesConfigDTO externalBarcodesConfig) {
        Event event = validateRequest(eventId, externalBarcodesConfig);
        ExternalBarcodeEventConfig newConfig = EventExternalBarcodeConverter.toMs(externalBarcodesConfig, eventId, event.getEntityId());
        externalBarcodeEventRepository.updateExternalBarcodeEntityConfig(eventId, newConfig);
    }

    public Event validateRequest(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);
        ExternalBarcodeEntityConfig externalBarcodeEntityConfig = entityExternalBarcodeConfigRepository.getExternalBarcodeEntityConfig(event.getEntityId());
        if (BooleanUtils.isNotTrue(externalBarcodeEntityConfig.getAllowExternalBarcode())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_UNSUPPORTED_OPERATION, "The use of external barcodes is disabled for event entity", null);
        }
        return event;
    }

    public Event validateRequest(Long eventId, EventExternalBarcodesConfigDTO externalBarcodesConfig) {
        Event event = validateRequest(eventId);
        if (BooleanUtils.isTrue(externalBarcodesConfig.getAllowed())) {
            ExternalBarcodeConfig entityBarcodeConfigs = entityExternalBarcodesRepository.getEntityExternalBarcodeConfig(event.getEntityId());

            if (externalBarcodesConfig.getFairCode() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "fair_code can not be null", null);
            }
            validateAgainstEntityConfigurations(
                entityBarcodeConfigs,
                EventExternalBarcodesConfigProperties.FAIR_CODE,
                externalBarcodesConfig.getFairCode()
            );
            if (externalBarcodesConfig.getFairEdition() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "fair_edition can not be null", null);
            }
            validateAgainstEntityConfigurations(
                entityBarcodeConfigs,
                EventExternalBarcodesConfigProperties.FAIR_EDITION,
                externalBarcodesConfig.getFairEdition()
            );
        }
        return event;
    }

    private void validateAgainstEntityConfigurations(ExternalBarcodeConfig entityBarcodeConfigs,
                                                     EventExternalBarcodesConfigProperties configType, String configValue) {
        List<String> validValues = (List<String>) entityBarcodeConfigs.getProperties().get(configType.getProperty());
        if(!validValues.contains(configValue)){
            throw new OneboxRestException(
                        ApiMgmtErrorCode.INVALID_EXTERNAL_BARCODE_CONFIG_VALUE,
                        configValue+" is not among the valid values "+configType+" can have.",
                        null);
        }
    }
}
