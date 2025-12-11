package es.onebox.mgmt.terminals.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalResponse;
import es.onebox.mgmt.datasources.ms.entity.repository.TerminalsRepository;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.terminals.dto.TerminalCreateRequestDTO;
import es.onebox.mgmt.terminals.dto.TerminalResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchFilterDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalUpdateRequestDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import static es.onebox.mgmt.terminals.converter.TerminalConverter.toDTO;
import static es.onebox.mgmt.terminals.converter.TerminalConverter.toMs;

@Service
public class TerminalsService {

    private final TerminalsRepository terminalsRepository;
    private final SecurityManager securityManager;

    public TerminalsService(TerminalsRepository terminalsRepository, SecurityManager securityManager) {
        this.terminalsRepository = terminalsRepository;
        this.securityManager = securityManager;
    }

    public IdDTO createTerminal(TerminalCreateRequestDTO terminalCreateRequestDTO) {
        securityManager.checkEntityAccessibleWithVisibility(terminalCreateRequestDTO.entityId());
        return terminalsRepository.createTerminal(toMs(terminalCreateRequestDTO));
    }

    public TerminalSearchResponseDTO searchTerminals(TerminalSearchFilterDTO terminalSearchFilterDTO) {
        if (terminalSearchFilterDTO.getEntityId() != null) {
            securityManager.checkEntityAccessibleWithVisibility(terminalSearchFilterDTO.getEntityId());
        }
        return toDTO(terminalsRepository.searchTerminals(toMs(terminalSearchFilterDTO)));
    }

    public TerminalResponseDTO getTerminal(Integer terminalId) {
        return toDTO(validateAndGetTerminal(terminalId));
    }

    public void updateTerminal(Integer terminalId, TerminalUpdateRequestDTO terminalUpdateRequestDTO) {
        if (terminalUpdateRequestDTO.entityId() != null) {
            securityManager.checkEntityAccessibleWithVisibility(terminalUpdateRequestDTO.entityId());
        }
        validateAndGetTerminal(terminalId);
        terminalsRepository.updateTerminal(terminalId, toMs(terminalUpdateRequestDTO));
    }

    public void deleteTerminal(Integer terminalId) {
        validateAndGetTerminal(terminalId);
        terminalsRepository.deleteTerminal(terminalId);
    }

    public void regenerateTerminalLicense(Integer terminalId) {
        validateAndGetTerminal(terminalId);
        terminalsRepository.regenerateTerminalLicense(terminalId);
    }

    private TerminalResponse validateAndGetTerminal(Integer terminalId) {
        TerminalResponse terminalResponse = terminalsRepository.getTerminal(terminalId);
        try {
            securityManager.checkEntityAccessibleWithVisibility(terminalResponse.getEntityId());
        } catch (AccessDeniedException accessDeniedException) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.TERMINAL_NOT_FOUND, accessDeniedException);
        }
        return terminalResponse;
    }

}
