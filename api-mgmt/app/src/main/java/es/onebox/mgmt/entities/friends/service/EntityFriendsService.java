package es.onebox.mgmt.entities.friends.service;

import es.onebox.mgmt.datasources.ms.entity.repository.EntityFriendsRepository;
import es.onebox.mgmt.entities.EntitiesService;
import es.onebox.mgmt.entities.friends.converter.EntityFriendsConverter;
import es.onebox.mgmt.entities.friends.dto.EntityFriendsConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityFriendsService {

    private final EntitiesService entitiesService;
    private final EntityFriendsRepository entityFriendsRepository;

    @Autowired
    public EntityFriendsService(EntitiesService entitiesService, EntityFriendsRepository entityFriendsRepository) {
        this.entitiesService = entitiesService;
        this.entityFriendsRepository = entityFriendsRepository;
    }

    public EntityFriendsConfigDTO getConfig(Long entityId) {
        entitiesService.getEntity(entityId);
        return EntityFriendsConverter.toDTO(entityFriendsRepository.getConfig(entityId));
    }

    public void updateConfig(Long entityId, EntityFriendsConfigDTO config) {
        entitiesService.getEntity(entityId);
        entityFriendsRepository.updateConfig(entityId, EntityFriendsConverter.toMs(config));
    }
}
