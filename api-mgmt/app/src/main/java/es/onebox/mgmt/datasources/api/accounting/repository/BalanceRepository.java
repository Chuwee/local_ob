package es.onebox.mgmt.datasources.api.accounting.repository;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.mgmt.datasources.api.accounting.ApiAccountingDatasource;
import es.onebox.mgmt.datasources.api.accounting.dto.BalanceRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.ClientTransactionsExportFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.DepositRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.ProviderClient;
import es.onebox.mgmt.datasources.api.accounting.dto.SearchTransactionsFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BalanceRepository {

    private final ApiAccountingDatasource apiAccountingDatasource;

    @Autowired
    public BalanceRepository(ApiAccountingDatasource apiAccountingDatasource) {
        this.apiAccountingDatasource = apiAccountingDatasource;
    }


    public void createProviderClientAssociation(Long entityId, Long clientId) {
        apiAccountingDatasource.createProviderClientAssociation(entityId, clientId);
    }

    public void deactivateProviderClientAssociation(Long entityId, Long clientId) {
        apiAccountingDatasource.deactivateProviderClientAssociation(entityId, clientId);
    }

    public ProviderClient getClientBalance(Long entityId, Long clientId) {
        return apiAccountingDatasource.getBalance(entityId, clientId);
    }

    public TransactionAudits searchTransactions(SearchTransactionsFilter filter) {
        return apiAccountingDatasource.getTransactions(filter);
    }

    public void deposit(DepositRequest depositRequest) {
        apiAccountingDatasource.deposit(depositRequest);
    }

    public void cashAdjustment(BalanceRequest cashAdjustmentRequest) {
        apiAccountingDatasource.cashAdjustment(cashAdjustmentRequest);
    }

    public void editMaxCreditLimit(BalanceRequest editMaxCreditLimitRequest) {
        apiAccountingDatasource.editCreditLimit(editMaxCreditLimitRequest);
    }

    public ExportProcess exportTransactions(ClientTransactionsExportFilter filter) {
        return apiAccountingDatasource.exportTransactions(filter);
    }

    public ExportProcess exportTransactionsStatus(String exportId, Long userId) {
        return apiAccountingDatasource.exportTransactionsStatus(exportId, userId);
    }
}
