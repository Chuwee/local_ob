package es.onebox.mgmt.terminals.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalState;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalUpdateRequest;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.terminals.dto.LicenseDTO;
import es.onebox.mgmt.terminals.dto.TerminalCreateRequestDTO;
import es.onebox.mgmt.terminals.dto.TerminalResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchFilterDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalUpdateRequestDTO;

import java.util.List;

import static es.onebox.mgmt.common.ConverterUtils.updateField;

public class TerminalConverter {

    private TerminalConverter() {
    }

    public static TerminalCreateRequest toMs(TerminalCreateRequestDTO terminalCreateRequestDTO) {
        TerminalCreateRequest terminalCreateRequest = new TerminalCreateRequest();
        terminalCreateRequest.setName(terminalCreateRequestDTO.name());
        terminalCreateRequest.setCode(terminalCreateRequestDTO.code());
        terminalCreateRequest.setEntityId(terminalCreateRequestDTO.entityId());
        terminalCreateRequest.setType(terminalCreateRequestDTO.type());
        terminalCreateRequest.setLicenseEnabled(terminalCreateRequestDTO.licenseEnabled());
        return terminalCreateRequest;
    }

    public static TerminalSearchFilter toMs(TerminalSearchFilterDTO terminalSearchFilterDTO) {
        TerminalSearchFilter terminalSearchFilter = new TerminalSearchFilter();
        if (terminalSearchFilterDTO.getEntityId() != null) {
            terminalSearchFilter.setEntityId(List.of(terminalSearchFilterDTO.getEntityId()));
        } else {
            terminalSearchFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        }
        if (terminalSearchFilterDTO.getType() != null) {
            terminalSearchFilter.setType(List.of(terminalSearchFilterDTO.getType()));
        }
        updateField(terminalSearchFilter::setLicenseEnabled, terminalSearchFilterDTO.getLicenseEnabled());
        updateField(terminalSearchFilter::setFreeSearch, terminalSearchFilterDTO.getQ());
        updateField(terminalSearchFilter::setLimit, terminalSearchFilterDTO.getLimit());
        updateField(terminalSearchFilter::setOffset, terminalSearchFilterDTO.getOffset());
        terminalSearchFilter.setState(TerminalState.ACTIVE);
        return terminalSearchFilter;
    }

    public static TerminalSearchResponseDTO toDTO(TerminalSearchResponse terminalSearchResponse) {
        List<TerminalResponseDTO> terminalResponseDTOList = terminalSearchResponse
                .getData()
                .stream()
                .map(TerminalConverter::toDTO)
                .toList();
        return new TerminalSearchResponseDTO(terminalResponseDTOList, terminalSearchResponse.getMetadata());
    }

    public static TerminalResponseDTO toDTO(TerminalResponse terminalResponse) {
        TerminalResponseDTO terminalResponseDTO = new TerminalResponseDTO();
        terminalResponseDTO.setId(terminalResponse.getId());
        terminalResponseDTO.setCode(terminalResponse.getCode());
        terminalResponseDTO.setName(terminalResponse.getName());
        terminalResponseDTO.setEntity(new IdNameDTO(terminalResponse.getEntityId(), terminalResponse.getEntityName()));
        terminalResponseDTO.setOnline(terminalResponse.getOnline());
        terminalResponseDTO.setLicense(toLicenseDTO(terminalResponse));
        terminalResponseDTO.setType(terminalResponse.getType());
        return terminalResponseDTO;
    }

    private static LicenseDTO toLicenseDTO(TerminalResponse terminalResponse) {
        LicenseDTO licenseDTO = new LicenseDTO();
        licenseDTO.setCode(terminalResponse.getLicense());
        licenseDTO.setEnabled(TerminalState.ACTIVE.equals(terminalResponse.getLicenseState()));
        licenseDTO.setActivationDate(terminalResponse.getLicenseActivationDate());
        licenseDTO.setExpirationDate(terminalResponse.getLicenseExpirationDate());
        return licenseDTO;
    }

    public static TerminalUpdateRequest toMs(TerminalUpdateRequestDTO terminalUpdateRequestDTO) {
        TerminalUpdateRequest terminalUpdateRequest = new TerminalUpdateRequest();
        updateField(terminalUpdateRequest::setName, terminalUpdateRequestDTO.name());
        updateField(terminalUpdateRequest::setEntityId, terminalUpdateRequestDTO.entityId());
        updateField(terminalUpdateRequest::setLicenseEnabled, terminalUpdateRequestDTO.licenseEnabled());
        return terminalUpdateRequest;
    }


}
