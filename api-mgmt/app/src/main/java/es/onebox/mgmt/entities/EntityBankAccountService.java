package es.onebox.mgmt.entities;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityBankAccountRepository;
import es.onebox.mgmt.entities.converter.EntityBankAccountConverter;
import es.onebox.mgmt.entities.dto.CreateEntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.EntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityBankAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityBankAccountService {

    private final EntityBankAccountRepository repository;

    @Autowired
    public EntityBankAccountService(EntityBankAccountRepository repository) {
        this.repository = repository;
    }

    public List<EntityBankAccountDTO> getBankAccounts(Long entityId) {
        return repository.getBankAccounts(entityId).stream()
                .map(EntityBankAccountConverter::toDTO)
                .collect(Collectors.toList());
    }

    public EntityBankAccountDTO getBankAccount(Long entityId, Long bankAccountId) {
        EntityBankAccount entity = repository.getBankAccount(entityId, bankAccountId);
        return EntityBankAccountConverter.toDTO(entity);
    }

    public IdDTO createBankAccount(Long entityId, CreateEntityBankAccountDTO dto) {
        EntityBankAccount entity = EntityBankAccountConverter.fromCreateDTO(entityId, dto);
        return repository.createBankAccount(entity);
    }

    public void updateBankAccount(Long entityId, Long bankAccountId, UpdateEntityBankAccountDTO dto) {
        EntityBankAccount entity = EntityBankAccountConverter.fromUpdateDTO(entityId, bankAccountId, dto);
        repository.updateBankAccount(entity);
    }

    public void deleteBankAccount(Long entityId, Long bankAccountId) {
        repository.deleteBankAccount(entityId, bankAccountId);
    }
}
