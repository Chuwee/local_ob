package es.onebox.event.sessions;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.converter.ExternalSessionConfigConverter;
import es.onebox.event.sessions.domain.ExternalSessionConfig;
import es.onebox.event.sessions.dto.ExternalSessionConfigDTO;
import es.onebox.event.sessions.service.ExternalSessionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/external-sessions")
public class ExternalSessionConfigController {

    private final ExternalSessionConfigService externalSessionConfigService;

    @Autowired
    public ExternalSessionConfigController(ExternalSessionConfigService externalSessionConfigService) {
        this.externalSessionConfigService = externalSessionConfigService;
    }

    @PostMapping("/{sessionId}/config")
    public void createExternalSessionConfig(@PathVariable(value = "sessionId") Long sessionId,
                                            @RequestBody ExternalSessionConfigDTO session) {
        if (session == null) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).build();
        }
        externalSessionConfigService.createExternalSessionConfig(sessionId, ExternalSessionConfigConverter.toEntity(session));
    }

    @GetMapping("/{sessionId}/config")
    public ExternalSessionConfigDTO getExternalSessionConfig(@PathVariable(value = "sessionId") Long sessionId) {
        ExternalSessionConfig externalSessionConfig = externalSessionConfigService.getExternalSessionConfig(sessionId);
        return ExternalSessionConfigConverter.toDTO(externalSessionConfig);
    }

    @PutMapping("/{sessionId}/config")
    public void upsertExternalSessionConfig(@PathVariable(value = "sessionId") Long sessionId,
                                            @RequestBody ExternalSessionConfigDTO externalSessionConfigDTO) {
        externalSessionConfigService.updateExternalSessionConfig(sessionId,
                ExternalSessionConfigConverter.toEntity(externalSessionConfigDTO));
    }

    @DeleteMapping("/{sessionId}/config")
    public void deleteExternalSessionConfig(@PathVariable(value = "sessionId") Long sessionId) {
        externalSessionConfigService.deleteExternalSessionConfig(sessionId);
    }


}
