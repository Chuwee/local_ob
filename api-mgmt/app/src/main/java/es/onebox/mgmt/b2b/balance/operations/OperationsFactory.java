package es.onebox.mgmt.b2b.balance.operations;

import es.onebox.mgmt.b2b.balance.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OperationsFactory {

    private final Map<OperationType, Operation> operations;

    @Autowired
    public OperationsFactory(Deposit deposit, EditCreditLimit editCreditLimit, CashAdjustment cashAdjustment) {
        operations = Map.of(OperationType.DEPOSIT, deposit,
                OperationType.CREDIT_LIMIT, editCreditLimit,
                OperationType.CASH_ADJUSTMENT, cashAdjustment
        );
    }

    public Operation get(OperationType operation) {
        return operations.get(operation);
    }
}
