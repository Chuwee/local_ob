package es.onebox.mgmt.collectives.converter;

import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.collectives.dto.CollectiveAssignedEntitiesDTO;
import es.onebox.mgmt.collectives.dto.CollectiveAssignedEntityDTO;
import es.onebox.mgmt.collectives.dto.CollectiveDTO;
import es.onebox.mgmt.collectives.dto.CollectiveDetailDTO;
import es.onebox.mgmt.collectives.dto.CollectiveValidatorAuthentication;
import es.onebox.mgmt.collectives.dto.ExternalValidatorClassDTO;
import es.onebox.mgmt.collectives.dto.ExternalValidatorDTO;
import es.onebox.mgmt.collectives.dto.Status;
import es.onebox.mgmt.collectives.dto.Type;
import es.onebox.mgmt.collectives.dto.ValidationMethod;
import es.onebox.mgmt.collectives.dto.request.CollectiveCreateDTO;
import es.onebox.mgmt.collectives.dto.request.CollectivesRequest;
import es.onebox.mgmt.collectives.dto.request.EntitiesAssignationRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveExternalValidatorsRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveStatus;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveType;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveValidationMethod;
import es.onebox.mgmt.datasources.ms.collective.dto.EntitiesCollective;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsExternalValidatorDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCreateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveUpdateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsEntitiesAssignationRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectiveConverter {

    private static final Set<CollectiveStatus> DEFAULT_STATUS = Stream.of(CollectiveStatus.ACTIVE, CollectiveStatus.INACTIVE)
            .collect(Collectors.toSet());
    private static final Set<CollectiveType> DEFAULT_TYPE = Stream.of(CollectiveType.INTERNAL, CollectiveType.EXTERNAL)
            .collect(Collectors.toSet());
    private static final Set<CollectiveValidationMethod> DEFAULT_VALIDATION_METHOD = Stream.of(
            CollectiveValidationMethod.USER,
            CollectiveValidationMethod.USER_PASSWORD,
            CollectiveValidationMethod.PROMOTIONAL_CODE,
            CollectiveValidationMethod.GIFT_TICKET,
            CollectiveValidationMethod.SHOPPING_CART,
            CollectiveValidationMethod.USER_CODE_PASSWORD)
            .collect(Collectors.toSet());

    private CollectiveConverter() {
    }

    public static CollectiveDTO fromMsCollectiveDTO(MsCollectiveDTO msCollectiveDTO) {
        CollectiveDTO collectiveDTO = new CollectiveDTO();
        collectiveDTO.setId(msCollectiveDTO.getId());
        collectiveDTO.setName(msCollectiveDTO.getName());
        collectiveDTO.setDescription(msCollectiveDTO.getDescription());
        collectiveDTO.setStatus(Status.valueOf(msCollectiveDTO.getStatus().name()));
        collectiveDTO.setType(Type.valueOf(msCollectiveDTO.getType().name()));
        collectiveDTO.setValidationMethod(ValidationMethod.valueOf(msCollectiveDTO.getValidationMethod().name()));
        collectiveDTO.setShowUsages(msCollectiveDTO.getShowUsages());
        if (msCollectiveDTO.getOwnerEntityId() != null) {
            collectiveDTO.setEntity(new IdNameDTO());
            collectiveDTO.getEntity().setId(msCollectiveDTO.getOwnerEntityId());
            collectiveDTO.getEntity().setName(msCollectiveDTO.getOwnerEntityName());
        }
        return collectiveDTO;
    }

    public static List<CollectiveDTO> fromMsCollectivesDTO(List<MsCollectiveDTO> msCollectivesDTO) {
        return msCollectivesDTO.stream()
                .map(CollectiveConverter::fromMsCollectiveDTO)
                .collect(Collectors.toList());
    }

    public static MsCollectiveRequest toMsCollectiveRequest(CollectivesRequest request) {
        MsCollectiveRequest msCollectiveRequest = new MsCollectiveRequest();

        if (SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_OPR_ANS) && SecurityUtils.isOperatorEntity()) {
            msCollectiveRequest.setOperatorId(Set.of(SecurityUtils.getUserOperatorId()));
            msCollectiveRequest.setEntityId(request.getEntityId());
            msCollectiveRequest.setEntityAdminId(request.getEntityAdminId());
        } else if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)){
            msCollectiveRequest.setEntityAdminId(SecurityUtils.getUserEntityId());
            msCollectiveRequest.setEntityId(request.getEntityId());
        } else  {
            msCollectiveRequest.setEntityId(Set.of(SecurityUtils.getUserEntityId()));
        }

        msCollectiveRequest.setStatus(toCollectiveStatusOrDefault(request.getStatus()));
        msCollectiveRequest.setType(toCollectiveTypeOrDefault(request.getType()));
        msCollectiveRequest.setValidationMethod(toCollectiveValidationMethodOrDefault(request.getValidationMethod()));
        msCollectiveRequest.setId(request.getId());
        msCollectiveRequest.setSort(request.getSort());
        msCollectiveRequest.setQ(request.getQ());
        msCollectiveRequest.setLimit(request.getLimit());
        msCollectiveRequest.setOffset(request.getOffset());
        return msCollectiveRequest;
    }

    public static CollectiveDetailDTO fromMsCollectiveDetailDTO(MsCollectiveDetailDTO msCollectiveDetailDTO) {
        CollectiveDetailDTO collectiveDetailDTO = new CollectiveDetailDTO();

        if(Objects.nonNull(msCollectiveDetailDTO)) {
            collectiveDetailDTO.setId(msCollectiveDetailDTO.getId());
            collectiveDetailDTO.setName(msCollectiveDetailDTO.getName());
            collectiveDetailDTO.setDescription(msCollectiveDetailDTO.getDescription());
            collectiveDetailDTO.setStatus(Status.valueOf(msCollectiveDetailDTO.getStatus().name()));
            collectiveDetailDTO.setType(Type.valueOf(msCollectiveDetailDTO.getType().name()));
            collectiveDetailDTO.setValidationMethod(ValidationMethod.valueOf(msCollectiveDetailDTO.getValidationMethod().name()));
            collectiveDetailDTO.setMaxUserLength(msCollectiveDetailDTO.getUserMaxLength());
            collectiveDetailDTO.setShowUsages(msCollectiveDetailDTO.getShowUsages());
            if(msCollectiveDetailDTO.getCipherPolicy() != null) {
                collectiveDetailDTO.setCipherPolicy(msCollectiveDetailDTO.getCipherPolicy());
            }
            if (msCollectiveDetailDTO.getOwnerEntityId() != null) {
                collectiveDetailDTO.setEntity(new IdNameDTO());
                collectiveDetailDTO.getEntity().setId(msCollectiveDetailDTO.getOwnerEntityId());
                collectiveDetailDTO.getEntity().setName(msCollectiveDetailDTO.getOwnerEntityName());
                collectiveDetailDTO.setGeneric(msCollectiveDetailDTO.getOwnerEntityId().equals(SecurityUtils.getUserOperatorId()) ? true : false);
            } else {
                collectiveDetailDTO.setEntity(new IdNameDTO());
                collectiveDetailDTO.setGeneric(true);
            }
            if (CollectiveType.EXTERNAL.equals(msCollectiveDetailDTO.getType())) {
                ExternalValidatorDTO externalValidator = new ExternalValidatorDTO();
                externalValidator.setExternalValidatorName(msCollectiveDetailDTO.getExternalValidator());
                CollectiveValidatorAuthentication authentication = msCollectiveDetailDTO.getExternalValidatorAuthentication() == null ?
                        null : CollectiveValidatorAuthentication.getByName(msCollectiveDetailDTO.getExternalValidatorAuthentication().name());
                externalValidator.setExternalValidatorAuthentication(authentication);
                externalValidator.setExternalValidatorProperties(msCollectiveDetailDTO.getExternalValidatorProperties());
                collectiveDetailDTO.setExternalValidator(externalValidator);
            }
        }
        return collectiveDetailDTO;
    }

    public static MsCollectiveUpdateDTO toMsCollectiveUpdate (UpdateCollectiveRequest request, CollectiveDetailDTO collective){
        MsCollectiveUpdateDTO msCollectiveUpdateDTO = new MsCollectiveUpdateDTO();
        if(SecurityUtils.isOperatorEntity() || !Type.EXTERNAL.equals(collective.getType())) {
            msCollectiveUpdateDTO.setName(request.getName());
        } else {
            msCollectiveUpdateDTO.setName(collective.getName());
        }
        if (request.getDescription() != null) {
            msCollectiveUpdateDTO.setDescription(request.getDescription());
        }
        if(request.getCipherPolicy() != null) {
            msCollectiveUpdateDTO.setCipherPolicy(request.getCipherPolicy().toString());
        }
        msCollectiveUpdateDTO.setShowUsages(request.getShowUsages());
        msCollectiveUpdateDTO.setUserMaxLength(request.getUserMaxLength());

        return  msCollectiveUpdateDTO;
    }

    public static MsCollectiveUpdateDTO toMsCollectiveUpdateExternalValidator (UpdateCollectiveExternalValidatorsRequest request, CollectiveDetailDTO collective, Long entityId){
        MsCollectiveUpdateDTO msCollectiveUpdateDTO = new MsCollectiveUpdateDTO();

        msCollectiveUpdateDTO.setName(collective.getName());
        msCollectiveUpdateDTO.setEntityId(entityId);
        msCollectiveUpdateDTO.setExternalValidatorProperties(request.getExternalValidatorProperties());
        msCollectiveUpdateDTO.setDescription(collective.getDescription());
        msCollectiveUpdateDTO.setCipherPolicy(collective.getCipherPolicy().toString());
        msCollectiveUpdateDTO.setUserMaxLength(collective.getMaxUserLength());

        return  msCollectiveUpdateDTO;
    }

    public static ExternalValidatorClassDTO fromMsExternalValidator(MsExternalValidatorDTO msExternalValidator){
        ExternalValidatorClassDTO externalValidator = new ExternalValidatorClassDTO();

        externalValidator.setName(msExternalValidator.getName());
        externalValidator.setExecutionClass(msExternalValidator.getExecutionClass());
        CollectiveValidatorAuthentication authentication = CollectiveValidatorAuthentication.getByName(msExternalValidator.getAuthentication().name());
        externalValidator.setAuthentication(authentication);

        return externalValidator;
    }

    public static CollectiveAssignedEntitiesDTO toCollectiveAssignedEntitiesDTO (Set<Entity> entities, EntitiesCollective entitiesAssignedToCollective) {
        CollectiveAssignedEntitiesDTO collectiveAssignedEntitiesDTO = new CollectiveAssignedEntitiesDTO();
        if (CollectionUtils.isNotEmpty(entities)) {
            collectiveAssignedEntitiesDTO.addAll(entities.stream()
                    .sorted(Comparator.comparing(Entity::getName))
                    .map(entity -> CollectiveConverter.getCollectiveAssignedEntityDTO(entity, entitiesAssignedToCollective)).collect(Collectors.toList()));
        }
        return collectiveAssignedEntitiesDTO;
    }

    public static MsCollectiveCreateDTO toMsCollectiveCreateDTO(CollectiveCreateDTO in, Long userEntityId) {
        MsCollectiveCreateDTO out = new MsCollectiveCreateDTO();
        out.setName(in.getName());
        out.setDescription(in.getDescription());
        if (Objects.isNull(in.getEntityId())) {
            out.setEntityId(userEntityId);
        } else {
            out.setEntityId(in.getEntityId());
        }
        out.setType(CollectiveType.valueOf(in.getType().name()));
        out.setValidationMethod(CollectiveValidationMethod.valueOf(in.getValidationMethod().name()));
        out.setExternalValidator(in.getExternalValidator());
        return out;
    }

    public static MsEntitiesAssignationRequest toMsEntitiesAssignationRequest(EntitiesAssignationRequest in, Long userOperatorId) {
        MsEntitiesAssignationRequest out = new MsEntitiesAssignationRequest();
        out.setEntities(in.getEntities());
        out.setOperatorId(userOperatorId);
        out.setEntityAdminId(in.getEntityAdminId());
        return out;
    }

    private static CollectiveAssignedEntityDTO getCollectiveAssignedEntityDTO(Entity entity, EntitiesCollective entitiesAssignedToCollective) {
        CollectiveAssignedEntityDTO out  = new CollectiveAssignedEntityDTO();
        out.setId(entity.getId());
        out.setName(entity.getName());
        out.setEnabled(false);
        if (entitiesAssignedToCollective.containsKey(entity.getId())) {
            out.setEnabled(true);
            out.setExternalValidatorProperties(entitiesAssignedToCollective.get(entity.getId()).getExternalValidatorProperties());
        }
        return out;
    }

    private static Set<CollectiveStatus> toCollectiveStatus(Set<Status> status) {
        return status.stream()
                .map(s -> CollectiveStatus.valueOf(s.name()))
                .collect(Collectors.toSet());
    }

    private static Set<CollectiveStatus> toCollectiveStatusOrDefault(Set<Status> status) {
        return CollectionUtils.isEmpty(status) ? DEFAULT_STATUS : toCollectiveStatus(status);
    }

    private static Set<CollectiveType> toCollectiveType(Set<Type> type) {
        return type.stream()
                .map(s -> CollectiveType.valueOf(s.name()))
                .collect(Collectors.toSet());
    }

    private static Set<CollectiveType> toCollectiveTypeOrDefault(Set<Type> type) {
        return CollectionUtils.isEmpty(type) ? DEFAULT_TYPE : toCollectiveType(type);
    }

    private static Set<CollectiveValidationMethod> toCollectiveValidationMethod(Set<ValidationMethod> validationMethod) {
        return validationMethod.stream()
                .map(s -> CollectiveValidationMethod.valueOf(s.name()))
                .collect(Collectors.toSet());
    }

    private static Set<CollectiveValidationMethod> toCollectiveValidationMethodOrDefault(Set<ValidationMethod> validationMethods) {
        return CollectionUtils.isEmpty(validationMethods) ? DEFAULT_VALIDATION_METHOD : toCollectiveValidationMethod(validationMethods);
    }
}
