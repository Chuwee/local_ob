package es.onebox.mgmt.users.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Permissions;
import es.onebox.core.security.ResourceId;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigCreateDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.user.realm.UserRealmConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.RolesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.realms.converter.RoleConverter;
import es.onebox.mgmt.realms.dto.AdditionalPropertiesDTO;
import es.onebox.mgmt.realms.dto.RoleDTO;
import es.onebox.mgmt.realms.dto.RolesDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.dto.realms.AvailableResourceCreateDTO;
import es.onebox.mgmt.users.dto.realms.AvailableResourceDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.FORBIDDEN_RESOURCE;

@Service
public class UserRolesService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public UserRolesService(UsersRepository usersRepository, RolesRepository rolesRepository,
                            EntitiesRepository entitiesRepository) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public List<RoleDTO> getRolesAvailable(Long userId) {
        User user = usersRepository.getById(userId);
        validateAccess(user);

        Entity entity = entitiesRepository.getCachedEntity(user.getEntityId());
        Operator operator = entitiesRepository.getCachedOperator(user.getOperatorId());

        List<Role> availableRoles = new ArrayList<>(rolesRepository.getAllRoles());

        if (!Boolean.TRUE.equals(operator.getAllowFeverZone()) || !Boolean.TRUE.equals(entity.getAllowFeverZone()) ||
                StringUtils.isEmpty(entity.getExternalReference())) {
            availableRoles.remove(new Role(Roles.Codes.ROLE_FV_REPORTING));
        }

        List<EntityTypes> types = entity.getTypes();

        return RoleConverter.convertRoles(availableRoles.stream()
                .filter(role -> Roles.valueOf(role.getCode()).getTipoEntidad().stream().anyMatch(types::contains))
                .collect(Collectors.toList()));
    }

    public List<RoleDTO> getRoles(Long userId) {
        User userToUpdate = usersRepository.getById(userId);
        validateAccess(userToUpdate);
        return RoleConverter.convertRoles(usersRepository.getUserRoles(userId));
    }

    public void setRole(Long userId, RoleDTO roleDTO) {
        User userToUpdate = usersRepository.getById(userId);
        checkSettingRole(userToUpdate, Collections.singleton(roleDTO));

        Entity entity = entitiesRepository.getEntity(userToUpdate.getEntityId());

        if (!isRoleValidByEntity(roleDTO.getCode(), entity)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.FORBIDDEN_RESOURCE).build();
        }

        usersRepository.setRole(userId, RoleConverter.roleDTO2Role(roleDTO));
    }

    public List<AvailableResourceDTO> availableResourceServers(Long userId) {
        var user = usersRepository.getById(userId);
        validateAccess(user);
        var availableResources = this.usersRepository.getAvailableResourceServers(userId);
        var resources = availableResources.stream()
                .filter(r -> !(ResourceId.API_GATEWAY.value().equals(r.getName()) || ResourceId.API_MONITORING.value().equals(r.getName())))
                .map(r -> {
                    var resource = new AvailableResourceDTO();
                    resource.setName(r.getName());
                    resource.setEnabled(Boolean.TRUE);

                    return resource;
                }).collect(Collectors.toList());
        UserRealmConfigDTO currentResources = this.usersRepository.getUserRealmConfig(userId);
        if (currentResources != null) {
            resources.forEach(r -> {
                if (!currentResources.getResources().contains(r.getName())) {
                    r.setEnabled(Boolean.FALSE);
                }
            });
        }

        return resources;
    }

    public void upsertResourceServers(Long userId, AvailableResourceCreateDTO upsert) {
        User user = usersRepository.getById(userId);
        validateAccess(user);
        UserRealmConfigCreateDTO upsertRealm = new UserRealmConfigCreateDTO();
        upsert.resources().add(ResourceId.API_GATEWAY.value());
        upsert.resources().add(ResourceId.API_MONITORING.value());
        upsertRealm.setResources(upsert.resources());
        usersRepository.upsertUserRealConfig(userId, upsertRealm);
    }

    public void setRoles(Long userId, RolesDTO roles) {
        User userToUpdate = usersRepository.getById(userId);
        checkSettingRole(userToUpdate, roles);
        Entity entity = entitiesRepository.getEntity(userToUpdate.getEntityId());

        if (roles.stream().map(RoleDTO::getCode).anyMatch(c -> !isRoleValidByEntity(c, entity))) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.FORBIDDEN_RESOURCE).build();
        }
        usersRepository.setRoles(userId, RoleConverter.toMs(roles));
    }

    public void unsetRole(Long userId, String roleCode) {
        User userToUpdate = usersRepository.getById(userId);
        checkSettingRole(userToUpdate, Collections.singleton(new RoleDTO(roleCode)));
        usersRepository.unsetRole(userId, roleCode);
    }

    public void addPermission(Long userId, String roleCode, String permissionCode) {
        if ((permissionCode.equals(Permissions.BI_MOBILE.name()) || permissionCode.equals(Permissions.BI_IMPERSONATION.name()))
                && !SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.PERMISSION_ONLY_MANAGED_BY_SYS_ADMIN);
        }
        usersRepository.addPermission(userId, roleCode, permissionCode);
    }

    public void deletePermission(Long userId, String roleCode, String permissionCode) {
        if (permissionCode.equals(Permissions.BI_MOBILE.name()) && !SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.PERMISSION_ONLY_MANAGED_BY_SYS_ADMIN);
        }
        usersRepository.deletePermission(userId, roleCode, permissionCode);
    }

    private static boolean isRoleValidByEntity(String roleCode, Entity entity) {
        boolean roleValid = false;
        for (EntityTypes entityType : EntityTypes.values()) {
            if (SecurityUtils.hasAnyEntityType(entity.getTypes(), entityType)) {
                List<Roles> roles = SecurityUtils.getRoleByTypeEntity(entityType);
                roleValid = roleValid || roles.stream().map(Roles::getRol).filter(role -> role.equals(roleCode)).findFirst().orElse(null) != null;
            }
        }
        return roleValid;
    }


    private void checkSettingRole(User userToUpdate, Set<RoleDTO> role) {
        validateAccess(userToUpdate);
        User authUser = usersRepository.getUser(SecurityUtils.getUsername(), SecurityUtils.getUserOperatorId(), SecurityUtils.getApiKey());
        if (authUser.getId().equals(userToUpdate.getId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_ROLE_CHANGE);
        }

        //TODO: Delete this condition when integration roles are protected in cpanel
        if (userToUpdate.getEmail().startsWith("int_")
                && CollectionUtils.isNotEmpty(userToUpdate.getRoles())
                && userToUpdate.getRoles().size() == 1
                && Roles.ROLE_CNL_INT.getRol().equals(userToUpdate.getRoles().get(0).getCode())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_ROLE_CHANGE);
        }

        if (role != null) {
            // The SYS roles cannot be updated
            List<Roles> superOperatorRoles = SecurityUtils.getRoleByTypeEntity(EntityTypes.SUPER_OPERATOR);
            for (Roles superOperatorRole : superOperatorRoles) {
                if (role.stream().map(RoleDTO::getCode).anyMatch(r -> r.equals(superOperatorRole.getRol()))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
                }
            }
            // ROLE_PRD_ANS can only be set to ProducerUsers & cannot have roles other than BI
            validateProducerRoles(role, userToUpdate.getProducerId());

            role.forEach(this::checkSettingPermissions);
        }
    }


    private void checkSettingPermissions(RoleDTO role) {
        if (CollectionUtils.isNotEmpty(role.getPermissions()) &&
                (role.getPermissions().contains(Permissions.BI_MOBILE.name()) || role.getPermissions().contains(Permissions.BI_IMPERSONATION.name())) &&
                !SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.PERMISSION_ONLY_MANAGED_BY_SYS_ADMIN);
        }
    }

    private void validateAccess(User user) {
        if (SecurityUtils.notAccessibleResource(user.getEntityId(), user.getOperatorId(),
                entitiesRepository.getCachedEntityAdminEntities(SecurityUtils.getUserEntityId()), ROLE_OPR_MGR, ROLE_ENT_MGR)) {
            throw new OneboxRestException(FORBIDDEN_RESOURCE);
        }
    }

    private void validateProducerRoles(Set<RoleDTO> requestedRoles, Long producerId) {

        boolean isRequestingPrdAns = requestedRoles.stream().anyMatch(role -> Roles.ROLE_PRD_ANS.getRol().equals(role.getCode()));
        boolean isRequestingValidRoles = requestedRoles.stream().allMatch(role ->
                Roles.ROLE_PRD_ANS.getRol().equals(role.getCode()) || Roles.ROLE_BI_USR.getRol().equals(role.getCode())
        );

        if (isRequestingPrdAns) {
            if (!isRequestingValidRoles) {
                throw new OneboxRestException(ApiMgmtEntitiesErrorCode.PRD_ANS_CAN_ONLY_HAVE_BI_ROLE);
            }
            Optional<List<Long>> requestedProducers = requestedRoles.stream()
                                                .filter(r -> r.getCode().equals(Roles.ROLE_PRD_ANS.getRol()))
                                                .map(RoleDTO::getAdditionalProperties)
                                                .filter(Objects::nonNull)
                                                .map(AdditionalPropertiesDTO::getProducerIds)
                                                .filter(producers -> !CommonUtils.isEmpty(producers))
                                                .findAny();
            if (requestedProducers.isEmpty()) {
                throw new OneboxRestException(ApiMgmtEntitiesErrorCode.PRD_ANS_MUST_BE_LINKED_TO_PRODUCER);
            } else {
                requestedProducers.get().forEach(reqProducer -> {
                    if (!reqProducer.equals(producerId)) {
                        Producer producer = entitiesRepository.getProducer(reqProducer);
                        Entity producerEntity = entitiesRepository.getCachedEntity(producer.getEntity().getId());
                        if (!SecurityUtils.accessibleResource(producer.getEntity().getId(), producerEntity.getOperator().getId())) {
                            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCER_NOT_FOUND);
                        }
                    }
                });
            }
        }
    }

}