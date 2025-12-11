package es.onebox.mgmt.terminals.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class TerminalSearchResponseDTO extends BaseResponseCollection<TerminalResponseDTO, Metadata> {

    public TerminalSearchResponseDTO(List<TerminalResponseDTO> data, Metadata metadata) {
        super(data, metadata);
    }

}
