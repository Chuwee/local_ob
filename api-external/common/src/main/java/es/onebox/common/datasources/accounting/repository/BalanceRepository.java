package es.onebox.common.datasources.accounting.repository;

import es.onebox.common.datasources.accounting.ApiAccountingDatasource;
import es.onebox.common.datasources.accounting.dto.TransactionAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BalanceRepository {

    private final ApiAccountingDatasource apiAccountingDatasource;

    @Autowired
    public BalanceRepository(ApiAccountingDatasource apiAccountingDatasource) {
        this.apiAccountingDatasource = apiAccountingDatasource;
    }

    public List<TransactionAudit> getTransaction(String movementId) {
        return apiAccountingDatasource.getTransaction(movementId);
    }

}
