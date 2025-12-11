package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.mgmt.entities.dto.CreateEntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.EntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityBankAccountDTO;

public class EntityBankAccountConverter {

    public static EntityBankAccountDTO toDTO(EntityBankAccount entity) {
        if (entity == null) return null;
        EntityBankAccountDTO dto = new EntityBankAccountDTO();
        dto.setId(entity.getId());
        dto.setIban(entity.getIban());
        dto.setBic(entity.getBic());
        dto.setCc(entity.getCc());
        dto.setName(entity.getName());
        return dto;
    }

    public static EntityBankAccount fromCreateDTO(Long entityId, CreateEntityBankAccountDTO dto) {
        if (dto == null) return null;
        EntityBankAccount entity = new EntityBankAccount();
        entity.setEntityId(entityId);
        entity.setIban(dto.getIban());
        entity.setBic(dto.getBic());
        entity.setCc(dto.getCc());
        entity.setName(dto.getName());
        return entity;
    }

    public static EntityBankAccount fromUpdateDTO(Long entityId, Long bankAccountId, UpdateEntityBankAccountDTO dto) {
        if (dto == null) return null;
        EntityBankAccount entity = new EntityBankAccount();
        entity.setId(bankAccountId);
        entity.setEntityId(entityId);
        entity.setIban(dto.getIban());
        entity.setBic(dto.getBic());
        entity.setCc(dto.getCc());
        entity.setName(dto.getName());
        return entity;
    }
}

