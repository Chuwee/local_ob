package es.onebox.fever.service;

import es.onebox.common.datasources.ms.entity.dto.Entities;
import es.onebox.common.datasources.ms.entity.dto.EntityConfig;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilterDTO;
import es.onebox.common.datasources.ms.entity.dto.SearchEntitiesResponse;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.entities.dto.Entity;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntitiesService {
    private final EntitiesRepository entitiesRepository;

    public EntitiesService(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public SearchEntitiesResponse getEntities(EntitySearchFilterDTO filter) {

        EntitySearchFilter entitySearchFilter = buildEntitiesFilter(filter);
        checkRoles(filter, entitySearchFilter);

        entitySearchFilter.setAllowFeverZone(Boolean.TRUE);
        entitySearchFilter.setOperatorAllowFeverZone(Boolean.TRUE);

        List<Entity> entities = new ArrayList<>();
        Long limit = filter.getLimit();
        Long total;
        do {
            Entities entitiesData = entitiesRepository.getEntities(entitySearchFilter);
            List<Entity> currentEntities = entitiesData.getData();
            if (CollectionUtils.isNotEmpty(currentEntities)) {
                entities.addAll(currentEntities);
            }
            total = entitiesData.getMetadata().getTotal();
            entitySearchFilter.setOffset(entitySearchFilter.getOffset() + limit);
        } while (total > entitySearchFilter.getOffset());

        SearchEntitiesResponse response = new SearchEntitiesResponse();
        response.setData(entities.stream().filter(entity -> !entity.getId().equals(entity.getOperator().getId()))
                .map(EntitiesService::fromMsEntity).collect(Collectors.toList()));
        return response;
    }

    private void checkRoles(EntitySearchFilterDTO filter, EntitySearchFilter entitySearchFilter) {
        if (!AuthenticationUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)
                && filter.getOperatorId() != null && !filter.getOperatorId().equals(AuthenticationUtils.getOperatorId())) {
            throw new OneboxRestException(ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        } else if (!AuthenticationUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS) &&
                AuthenticationUtils.hasEntityType(EntityTypes.OPERATOR)) {
            Long operatorId = AuthenticationUtils.getOperatorId();
            entitySearchFilter.setOperatorId(operatorId == null ? null : operatorId.intValue());
        } else if (AuthenticationUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            Long entityId = AuthenticationUtils.getEntityId();
            entitySearchFilter.setEntityAdminId(entityId == null ? null : entityId.intValue());
        } else {
            Long entityId = AuthenticationUtils.getEntityId();
            entitySearchFilter.setId(entityId == null ? null : entityId.intValue());
        }
    }

    private boolean checkAvailable(Entity entity, EntityConfig config) {
        return config.getEntityId().equals(entity.getId().intValue()) &&
                Boolean.TRUE.equals(config.getAllowFeverZone());
    }

    private EntitySearchFilter buildEntitiesFilter(EntitySearchFilterDTO source) {
        EntitySearchFilter target = new EntitySearchFilter();
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        target.setAllowAvetIntegration(source.getAllowAvetIntegration());
        target.setAllowMembers(source.getAllowMembers());
        target.setB2bEnabled(source.getB2bEnabled());
        target.setAllowDigitalSeasonTicket(source.getAllowDigitalSeasonTicket());
        target.setAllowMassiveEmail(source.getAllowMassiveEmail());
        target.setFreeSearch(source.getFreeSearch());
        target.setIncludeEntityAdmin(source.getIncludeEntityAdmin());
        target.setSort(source.getSort());
        target.setFields(source.getFields());
        target.setOperatorId(source.getOperatorId() != null ? source.getOperatorId().intValue() : null);
        target.setType(source.getType());
        if (source.getStatus() != null) {
            target.setStatus(FilterWithOperator.build(Operator.EQUALS, source.getStatus()));
        }
        return target;
    }

    private static EntityDTO fromMsEntity(Entity source) {
        if (source == null) {
            return null;
        }

        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setId(source.getId());
        entityDTO.setName(source.getName());
        entityDTO.setShortName(source.getShortName());
        entityDTO.setNif(source.getNif());
        entityDTO.setSocialReason(source.getSocialReason());
        if (source.getOperator() != null) {
            EntityDTO operator = new EntityDTO();
            operator.setId(source.getOperator().getId());
            operator.setName(source.getOperator().getName());
            entityDTO.setOperator(operator);
        }
        entityDTO.setExternalReference(source.getExternalReference());
        entityDTO.setAllowFeverZone(source.getAllowFeverZone());
        return entityDTO;
    }
}
