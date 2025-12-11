package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalSearchResponse;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.TerminalUpdateRequest;
import org.springframework.stereotype.Repository;

@Repository
public class TerminalsRepository {

    private final MsEntityDatasource msEntityDatasource;

    public TerminalsRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public IdDTO createTerminal(TerminalCreateRequest terminalCreateRequest) {
        return msEntityDatasource.createTerminal(terminalCreateRequest);
    }

    public TerminalSearchResponse searchTerminals(TerminalSearchFilter terminalSearchFilter) {
        return msEntityDatasource.searchTerminals(terminalSearchFilter);
    }

    public TerminalResponse getTerminal(Integer terminalId) {
        return msEntityDatasource.getTerminal(terminalId);
    }

    public void updateTerminal(Integer terminalId, TerminalUpdateRequest terminalUpdateRequest) {
        msEntityDatasource.updateTerminal(terminalId, terminalUpdateRequest);
    }

    public void deleteTerminal(Integer terminalId) {
        msEntityDatasource.deleteTerminal(terminalId);
    }

    public void regenerateTerminalLicense(Integer terminalId) {
        msEntityDatasource.regenerateTerminalLicense(terminalId);
    }

}
