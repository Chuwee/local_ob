package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityBankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityBankAccountRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityBankAccountRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public List<EntityBankAccount> getBankAccounts(Long entityId) {
        return msEntityDatasource.getBankAccounts(entityId);
    }

    public EntityBankAccount getBankAccount(Long entityId, Long bankAccountId) {
        return msEntityDatasource.getBankAccount(entityId, bankAccountId);
    }

    public IdDTO createBankAccount(EntityBankAccount bankAccount) {
        return msEntityDatasource.createBankAccount(bankAccount);
    }

    public void updateBankAccount(EntityBankAccount bankAccount) {
        msEntityDatasource.updateBankAccount(bankAccount);
    }

    public void deleteBankAccount(Long entityId, Long bankAccountId) {
        msEntityDatasource.deleteBankAccount(entityId, bankAccountId);
    }
}
