package es.onebox.mgmt.realms.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.AdditionalProperties;
import es.onebox.mgmt.datasources.ms.entity.dto.Permission;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.Roles;
import es.onebox.mgmt.realms.dto.AdditionalPropertiesDTO;
import es.onebox.mgmt.realms.dto.RoleDTO;
import es.onebox.mgmt.realms.dto.RolesDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoleConverter {

    private RoleConverter() {
    }

    public static List<RoleDTO> convertRoles(List<Role> source) {
        List<RoleDTO> target = new ArrayList<>();
        if (!CommonUtils.isEmpty(source)) {
            for (Role sourceRole : source) {
                RoleDTO targetRole = new RoleDTO(sourceRole.getCode());
                List<String> permisions = new ArrayList<>();
                if (!CollectionUtils.isEmpty(sourceRole.getPermissions())) {
                    for (Permission targetPermission : sourceRole.getPermissions()) {
                        permisions.add(targetPermission.getCode());
                    }
                }
                if (!CollectionUtils.isEmpty(permisions)) {
                    targetRole.setPermissions(permisions);
                }
                if (sourceRole.getAdditionalProperties() != null
                        && !CommonUtils.isEmpty(sourceRole.getAdditionalProperties().getProducerIds())) {
                    targetRole.setAdditionalProperties(new AdditionalPropertiesDTO());
                    targetRole.getAdditionalProperties().setProducerIds(
                            sourceRole.getAdditionalProperties().getProducerIds()
                    );
                }
                target.add(targetRole);
            }
        }
        return target;
    }

    public static Roles toMs(RolesDTO in) {
        return new Roles(in.stream().map(RoleConverter::roleDTO2Role).collect(Collectors.toSet()));
    }


    public static Role roleDTO2Role(RoleDTO roleDTO) {
        Role role = new Role();
        role.setCode(roleDTO.getCode());
        if (!CollectionUtils.isEmpty(roleDTO.getPermissions())) {
            role.setPermissions(roleDTO.getPermissions().stream().map(Permission::new).collect(Collectors.toList()));
        }
        if (roleDTO.getAdditionalProperties() != null) {
            role.setAdditionalProperties(new AdditionalProperties());
            if (!CommonUtils.isEmpty(roleDTO.getAdditionalProperties().getProducerIds())) {
                role.getAdditionalProperties().setProducerIds(
                    roleDTO.getAdditionalProperties().getProducerIds()
                );
            }
        }
        return role;
    }

}
