package es.onebox.mgmt.entities.profiles;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityProfile;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.profiles.converter.EntityProfileConverter;
import es.onebox.mgmt.entities.profiles.dto.CreateProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfilesDTO;
import es.onebox.mgmt.entities.profiles.dto.UpdateProfileDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityProfilesService {

    @Autowired
    private EntitiesRepository entitiesRepository;

    @Autowired
    private SecurityManager securityManager;

    public ProfilesDTO getProfiles(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        List<EntityProfile> profileList = entitiesRepository.getEntityProfiles(entityId);
        return EntityProfileConverter.fromMs(profileList);
    }

    public ProfileDTO getProfile(Long entityId, Long profileId) {
        securityManager.checkEntityAccessible(entityId);
        EntityProfile profile = entitiesRepository.getEntityProfile(entityId, profileId);
        return EntityProfileConverter.fromMs(profile);
    }

    public IdDTO createProfile(Long entityId, CreateProfileDTO createProfile) {
        securityManager.checkEntityAccessible(entityId);
        EntityProfile entityProfile = EntityProfileConverter.toMs(createProfile);
        return entitiesRepository.createEntityProfile(entityId, entityProfile);
    }

    public void deleteProfile(Long entityId, Long profileId) {
        securityManager.checkEntityAccessible(entityId);
        entitiesRepository.deleteEntityProfile(entityId, profileId);
    }

    public void updateProfile(Long entityId, Long profileId, UpdateProfileDTO updateProfile) {
        securityManager.checkEntityAccessible(entityId);
        EntityProfile entityProfile = EntityProfileConverter.toMs(updateProfile);
        entitiesRepository.updateEntityProfile(entityId, profileId, entityProfile);
    }
}
