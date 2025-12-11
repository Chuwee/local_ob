package es.onebox.mgmt.collectives;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.collectives.converter.CollectiveConverter;
import es.onebox.mgmt.collectives.dto.CollectiveAssignedEntitiesDTO;
import es.onebox.mgmt.collectives.dto.CollectiveDetailDTO;
import es.onebox.mgmt.collectives.dto.CollectivesDTO;
import es.onebox.mgmt.collectives.dto.ExternalValidatorClassDTO;
import es.onebox.mgmt.collectives.dto.Type;
import es.onebox.mgmt.collectives.dto.request.CollectiveCreateDTO;
import es.onebox.mgmt.collectives.dto.request.CollectivesRequest;
import es.onebox.mgmt.collectives.dto.request.EntitiesAssignationRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveExternalValidatorsRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveStatusRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveStatus;
import es.onebox.mgmt.datasources.ms.collective.dto.EntitiesCollective;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectivesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsExternalValidatorDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsExternalValidatorsDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveUpdateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveStatusRequest;
import es.onebox.mgmt.datasources.ms.collective.repository.CollectivesRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entities;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtCollectivesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;

@Service
public class CollectivesService {

    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private final CollectivesRepository collectivesRepository;
    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;

    @Autowired
    public CollectivesService(CollectivesRepository collectivesRepository,
                              EntitiesRepository entitiesRepository,
                              SecurityManager securityManager) {
        this.collectivesRepository = collectivesRepository;
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public CollectivesDTO getCollectives(CollectivesRequest request) {

        if (!SecurityUtils.isOperatorEntity() && !SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN) && CollectionUtils.isNotEmpty(request.getEntityId())) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("entity_id is not applicable for your scope")
                    .build();
        }

        if (CollectionUtils.isNotEmpty(request.getEntityId())) {
            securityManager.checkEntityAccessible(request.getEntityId());
        }

        if (request.getEntityAdminId() != null) {
            securityManager.checkEntityAccessibleIncludeEntityAdmin(request.getEntityAdminId());
        }

        MsCollectiveRequest msCollectiveRequest = CollectiveConverter.toMsCollectiveRequest(request);

        MsCollectivesDTO collectives = this.collectivesRepository.getCollectives(msCollectiveRequest);
        CollectivesDTO collectivesDTO = new CollectivesDTO();
        collectivesDTO.setMetadata(collectives.getMetadata());
        collectivesDTO.setData(CollectiveConverter.fromMsCollectivesDTO(collectives.getData()));

        return collectivesDTO;
    }

    public CollectiveDetailDTO getCollective(Long collectiveId) {
        MsCollectiveDetailDTO collective = getAndCheckCollective(collectiveId);

        return CollectiveConverter.fromMsCollectiveDetailDTO(collective);
    }

    public MsCollectiveDetailDTO getAndCheckCollective(Long collectiveId) {

        if (SecurityUtils.isOperatorEntity()) {
            return this.collectivesRepository.getCollective(collectiveId, SecurityUtils.getUserOperatorId(), null, null);
        } else if (SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)) {
            return this.collectivesRepository.getCollective(collectiveId, null, null, SecurityUtils.getUserEntityId());
        } else {
            return this.collectivesRepository.getCollective(collectiveId, null, SecurityUtils.getUserEntityId(), null);
        }
    }

    public IdDTO createCollective(CollectiveCreateDTO request) {
        if (Objects.isNull(request)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).build();
        }
        validateCollectiveCreateDTO(request);
        if (Objects.nonNull(request.getEntityId())) {
            securityManager.checkEntityAccessible(request.getEntityId());
        }
        return collectivesRepository.createCollective(CollectiveConverter.toMsCollectiveCreateDTO(request, SecurityUtils.getUserEntityId()));
    }

    public void updateCollective(Long collectiveId, UpdateCollectiveRequest request) {
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);
        if (Objects.nonNull(collectiveDetailDTO)) {
            securityManager.checkEntityAccessible(collectiveDetailDTO.getEntity().getId());
            MsCollectiveUpdateDTO msCollectiveUpdateDTO = CollectiveConverter.toMsCollectiveUpdate(request, collectiveDetailDTO);
            this.collectivesRepository.updateCollective(collectiveId, msCollectiveUpdateDTO);
        }
    }

    public void updateCollectiveExternalValidators(Long collectiveId, UpdateCollectiveExternalValidatorsRequest request) {
        validateEntityIdParam(request.getEntityId());
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);
        if (Objects.nonNull(collectiveDetailDTO)) {
            Long entityId = request.getEntityId() == null ? SecurityUtils.getUserEntityId() : request.getEntityId();
            checkEntitiesAssignedToCollective(collectiveId, entityId);
            checkExternalValidators(collectiveDetailDTO.getExternalValidator().getExternalValidatorProperties(), request.getExternalValidatorProperties());
            MsCollectiveUpdateDTO msCollectiveUpdateDTO = CollectiveConverter.toMsCollectiveUpdateExternalValidator(request, collectiveDetailDTO, entityId);
            this.collectivesRepository.updateCollective(collectiveId, msCollectiveUpdateDTO);
        }
    }

    private void checkExternalValidators(Map<String, Object> oldExternalValidator, Map<String, Object> externalValidator) {
        if (oldExternalValidator != null){
            if ((oldExternalValidator.get(USER) != null && (externalValidator.get(USER) == null || StringUtils.isBlank(externalValidator.get(USER).toString())))
                    || (oldExternalValidator.get(PASSWORD) != null && (externalValidator.get(PASSWORD) == null || StringUtils.isBlank(externalValidator.get(PASSWORD).toString())))){
                throw OneboxRestException.builder(ApiMgmtCollectivesErrorCode.COLLECTIVE_EXTERNAL_VALIDATOR_PROPERTY_EMPTY).build();
            }
        }
    }

    private void checkEntitiesAssignedToCollective(Long collectiveId, Long entityId) {
        Set<Long> entitiesAssignedToCollective = this.collectivesRepository.getEntitiesAssignedToCollective(collectiveId).keySet();
        if (!entitiesAssignedToCollective.contains(entityId)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.NOT_FOUND).setMessage("entity not found").build();
        }
    }

    public void deleteCollective(Long collectiveId) {
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);
        if (Objects.nonNull(collectiveDetailDTO)) {
            securityManager.checkEntityAccessible(collectiveDetailDTO.getEntity().getId());
            collectivesRepository.deleteCollective(collectiveId);
        }
    }

    public void updateCollectiveStatus(Long collectiveId, UpdateCollectiveStatusRequest request) {
        if (Objects.isNull(request)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .build();
        }
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);
        if (Objects.nonNull(collectiveDetailDTO)) {
            securityManager.checkEntityAccessible(collectiveDetailDTO.getEntity().getId());
            MsUpdateCollectiveStatusRequest msRequest = new MsUpdateCollectiveStatusRequest();
            msRequest.setStatus(CollectiveStatus.valueOf(request.getStatus().name()));
            collectivesRepository.updateCollectiveStatus(collectiveId, msRequest);
        }
    }
    private void validateCollectiveDetailDTO(CollectiveDetailDTO collectiveDetailDTO) {

        if (Objects.nonNull(collectiveDetailDTO.getEntity()) && Objects.nonNull(collectiveDetailDTO.getEntity().getId())) {
            securityManager.checkEntityAccessibleIncludeEntityAdmin(collectiveDetailDTO.getEntity().getId());
        } else {
            throw OneboxRestException.builder(ApiMgmtErrorCode.ENTITY_NOT_FOUND)
                    .build();
        }
    }

    public CollectiveAssignedEntitiesDTO getEntitiesAssignedToCollective(Long collectiveId) {
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);
        if (Objects.nonNull(collectiveDetailDTO)) {
            validateCollectiveDetailDTO(collectiveDetailDTO);
            EntitiesCollective entitiesCollective = this.collectivesRepository.getEntitiesAssignedToCollective(collectiveId);
            long offset = 0L;
            long limit = 999L;
            EntitySearchFilter filter = new EntitySearchFilter();
            filter.setLimit(limit);
            filter.setOffset(offset);
            filter.setOperatorId(SecurityUtils.getUserOperatorId());
            Entities msEntities = entitiesRepository.getEntities(filter);
            Set<Entity> entities = msEntities.getData().stream()
                    .filter(entity -> entity.getTypes().contains(EntityTypes.EVENT_ENTITY) || entity.getTypes().contains(EntityTypes.CHANNEL_ENTITY)).collect(Collectors.toSet());
            while (msEntities.getMetadata().getTotal() > offset + limit) {
                offset += limit;
                msEntities = entitiesRepository.getEntities(filter);
                entities.addAll(msEntities.getData().stream()
                        .filter(entity -> entity.getTypes().contains(EntityTypes.EVENT_ENTITY) || entity.getTypes().contains(EntityTypes.CHANNEL_ENTITY))
                        .collect(Collectors.toSet()));
            }
            return CollectiveConverter.toCollectiveAssignedEntitiesDTO(entities, entitiesCollective);
        }
        return new CollectiveAssignedEntitiesDTO();
    }

    public void assignEntitiesToCollective(Long collectiveId, EntitiesAssignationRequest request) {
        if (Objects.isNull(request)) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .build();
        }
        CollectiveDetailDTO collectiveDetailDTO = this.getCollective(collectiveId);

        validateCollectiveDetailDTO(collectiveDetailDTO);
        request.getEntities().forEach(securityManager::checkEntityAccessible);

        if (Objects.nonNull(collectiveDetailDTO)) {
            this.collectivesRepository.assignEntitiesToCollective(collectiveId,
                    CollectiveConverter.toMsEntitiesAssignationRequest(request, SecurityUtils.getUserOperatorId()));
        }
    }

    public List<ExternalValidatorClassDTO> getExternalValidators() {
        List<ExternalValidatorClassDTO> externalValidators = new ArrayList<>();

        MsExternalValidatorsDTO msExternalValidators = this.collectivesRepository.getExternalValidators();
        if (Objects.nonNull(msExternalValidators) && CollectionUtils.isNotEmpty(msExternalValidators.getValidators())) {
            externalValidators.addAll(msExternalValidators.getValidators().stream()
                    .sorted(Comparator.comparing(MsExternalValidatorDTO::getName))
                    .map(CollectiveConverter::fromMsExternalValidator).toList());
        }

        return externalValidators;
    }

    private void validateCollectiveCreateDTO(CollectiveCreateDTO request) {

        validateEntityIdParam(request.getEntityId());
        if ((!SecurityUtils.isOperatorEntity() && !SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN) || Type.EXTERNAL.equals(request.getType())) && Objects.nonNull(request.getEntityId())) {
            throw OneboxRestException.builder(ApiMgmtCollectivesErrorCode.COLLECTIVE_ENTITY_NOT_ALLOWED).build();
        }
        if (Type.INTERNAL.equals(request.getType()) && request.getExternalValidator() != null) {
            throw OneboxRestException.builder(ApiMgmtCollectivesErrorCode.COLLECTIVE_INTERNAL_TYPES_CANNOT_HAVE_EXTERNAL_VALIDATORS).build();
        }
    }

    private void validateEntityIdParam(Long entityId) {
        if (SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN) && entityId == null ) {
           throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                   .setMessage("entity_id mandatory").build();

        } else if ((!SecurityUtils.isOperatorEntity() && !SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)) && entityId != null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                .setMessage("entityId parameter can only be used by operator/admininistrator users").build();
        }
    }
}

