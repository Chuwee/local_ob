package es.onebox.mgmt.entities.profiles.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityProfile;
import es.onebox.mgmt.entities.profiles.dto.CreateProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfilesDTO;
import es.onebox.mgmt.entities.profiles.dto.UpdateProfileDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class EntityProfileConverter {

    private EntityProfileConverter() {
    }

    public static ProfilesDTO fromMs(List<EntityProfile> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        List<ProfileDTO> profiles = source.stream().map(EntityProfileConverter::fromMs).collect(Collectors.toList());
        return new ProfilesDTO(profiles);
    }

    public static ProfileDTO fromMs(EntityProfile source) {
        if (source == null) {
            return null;
        }
        ProfileDTO target = new ProfileDTO();
        target.setDefaultProfile(source.getDefaultProfile());
        target.setId(source.getId());
        target.setName(source.getName());
        return target;
    }

    public static EntityProfile toMs(UpdateProfileDTO source) {
        EntityProfile target = new EntityProfile();
        target.setDefaultProfile(source.getDefaultProfile());
        target.setName(source.getName());
        return target;
    }

    public static EntityProfile toMs(CreateProfileDTO source) {
        EntityProfile target = new EntityProfile();
        target.setDefaultProfile(source.getDefaultProfile());
        target.setName(source.getName());
        return target;
    }
}
