package es.onebox.mgmt.b2b.balance.operations;

import es.onebox.mgmt.b2b.balance.converter.OperationsConverter;
import es.onebox.mgmt.b2b.balance.dto.OperationRequestDTO;
import es.onebox.mgmt.datasources.api.accounting.dto.DepositRequest;
import es.onebox.mgmt.datasources.api.accounting.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Deposit implements Operation {

    private final BalanceRepository balanceRepository;

    @Autowired
    public Deposit(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public void execute(Long clientId, OperationRequestDTO operationRequest) {
        DepositRequest request = OperationsConverter.toMsDeposit(operationRequest, clientId);
        balanceRepository.deposit(request);
    }
}
