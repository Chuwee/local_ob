package es.onebox.mgmt.b2b.balance.operations;

import es.onebox.mgmt.b2b.balance.dto.OperationRequestDTO;

public interface Operation {
    void execute(Long clientId, OperationRequestDTO operationRequest);
}
