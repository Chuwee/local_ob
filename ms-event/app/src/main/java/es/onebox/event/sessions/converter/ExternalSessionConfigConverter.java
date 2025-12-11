package es.onebox.event.sessions.converter;

import es.onebox.event.sessions.domain.ExternalSessionConfig;
import es.onebox.event.sessions.dto.ExternalSessionConfigDTO;

public class ExternalSessionConfigConverter {

    private ExternalSessionConfigConverter() {
    }

    public static ExternalSessionConfigDTO toDTO(ExternalSessionConfig entity) {
        if (entity == null) {
            return null;
        }
        ExternalSessionConfigDTO externalSessionConfigDTO = new ExternalSessionConfigDTO();
        externalSessionConfigDTO.setSessionId(entity.getSessionId());
        externalSessionConfigDTO.setGeneralAdmission(entity.getGeneralAdmission());
        return externalSessionConfigDTO;
    }

    public static ExternalSessionConfig toEntity(ExternalSessionConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        ExternalSessionConfig entity = new ExternalSessionConfig();
        entity.setSessionId(dto.getSessionId());
        entity.setGeneralAdmission(dto.getGeneralAdmission());
        return entity;
    }

}
