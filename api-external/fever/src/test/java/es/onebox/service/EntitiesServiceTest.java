package es.onebox.service;

import es.onebox.common.datasources.ms.entity.dto.Entities;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilterDTO;
import es.onebox.common.datasources.ms.entity.dto.SearchEntitiesResponse;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.entities.dto.Entity;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.fever.service.EntitiesService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntitiesServiceTest {

    private final static Long ENTITY_ID = 1L;
    private final static Long OPERATOR_ID = 2L;

    @Mock
    private EntitiesRepository entitiesRepository;
    @InjectMocks
    private EntitiesService entitiesService;

    private MockedStatic<AuthenticationUtils> authenticationUtils;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
    }

    @AfterEach
    public void tearDown() {
        authenticationUtils.close();
    }

    @Test
    public void getEntities_otherRole_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);
        entity.setAllowFeverZone(Boolean.TRUE);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());
        entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        assertEquals(ENTITY_ID, searchEntitiesResponse.getData().get(0).getId());
    }


    @Test
    public void getEntities_otherRole_multipleEntitiesWithConfig_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);

        Entity secondEntity = new Entity();
        secondEntity.setId(3L);
        secondEntity.setExternalReference("1234");
        secondEntity.setOperator(operator);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);
        entities.getData().add(secondEntity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());
        entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        assertEquals(2, searchEntitiesResponse.getData().size());
    }


    @Test
    public void getEntities_otherRole_multipleEntitiesWithConfig_filterOperator_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);

        Entity secondEntity = new Entity();
        secondEntity.setId(OPERATOR_ID);
        secondEntity.setExternalReference("1234");
        secondEntity.setOperator(operator);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);
        entities.getData().add(secondEntity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());
        entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        assertEquals(1, searchEntitiesResponse.getData().size());
    }

    @Test
    public void getEntities_otherRole_multipleEntitiesWithConfigNotAllow_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);

        Entity secondEntity = new Entity();
        secondEntity.setId(2L);
        secondEntity.setExternalReference("1234");
        secondEntity.setOperator(operator);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);
        entities.getData().add(secondEntity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        assertEquals(ENTITY_ID, searchEntitiesResponse.getData().get(0).getId());
        assertEquals(1, searchEntitiesResponse.getData().size());
    }

    @Test
    public void getEntities_otherRole_noEntities_ok() {
        Entities entities = new Entities();
        entities.setData(new ArrayList<>());

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        assertTrue(CollectionUtils.isEmpty(searchEntitiesResponse.getData()));
    }

    @Test
    public void getEntities_noSysMgrOrSysAns_noOperatorId_whenFilterHaveOperatorId_ko() {
        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        search.setOperatorId(OPERATOR_ID);
        authenticationUtils.when(() -> AuthenticationUtils.hasAnyRole(any(), any())).thenReturn(false);
        OneboxRestException exception = assertThrows(OneboxRestException.class,
                () -> entitiesService.getEntities(search));
        assertEquals(ApiExternalErrorCode.FORBIDDEN_RESOURCE.name(), exception.getErrorCode());
    }

    @Test
    public void getEntities_entityTypesOperator_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);
        entity.setAllowFeverZone(Boolean.TRUE);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());entities.setMetadata(metadata);

        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        authenticationUtils.when(() -> AuthenticationUtils.hasEntityType(any())).thenReturn(true);
        authenticationUtils.when(AuthenticationUtils::getOperatorId).thenReturn(OPERATOR_ID);
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        ArgumentCaptor<EntitySearchFilter> captor = ArgumentCaptor.forClass(EntitySearchFilter.class);
        verify(entitiesRepository, times(1)).getEntities(
                captor.capture()
        );

        EntitySearchFilter capturedEntitySearch = captor.getValue();
        assertEquals(OPERATOR_ID.intValue(), capturedEntitySearch.getOperatorId());
        assertEquals(ENTITY_ID, searchEntitiesResponse.getData().get(0).getId());
        assertEquals(1, searchEntitiesResponse.getData().size());
    }

    @Test
    public void getEntities_roleEntAdmin_ok() {
        Entity operator = new Entity();
        operator.setId(OPERATOR_ID);

        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        entity.setExternalReference("123");
        entity.setOperator(operator);
        entity.setAllowFeverZone(Boolean.TRUE);

        Entities entities = new Entities();
        entities.setData(new ArrayList<>());
        entities.getData().add(entity);

        Metadata metadata = new Metadata();
        metadata.setTotal((long) entities.getData().size());entities.setMetadata(metadata);


        EntitySearchFilterDTO search = new EntitySearchFilterDTO();
        authenticationUtils.when(() -> AuthenticationUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)).thenReturn(true);
        authenticationUtils.when(AuthenticationUtils::getEntityId).thenReturn(ENTITY_ID);
        when(entitiesRepository.getEntities(any())).thenReturn(entities);

        SearchEntitiesResponse searchEntitiesResponse = entitiesService.getEntities(search);

        ArgumentCaptor<EntitySearchFilter> captor = ArgumentCaptor.forClass(EntitySearchFilter.class);
        verify(entitiesRepository, times(1)).getEntities(
                captor.capture()
        );

        EntitySearchFilter capturedEntitySearch = captor.getValue();
        assertEquals(ENTITY_ID.intValue(), capturedEntitySearch.getEntityAdminId());
        assertEquals(ENTITY_ID, searchEntitiesResponse.getData().get(0).getId());
        assertEquals(1, searchEntitiesResponse.getData().size());
    }
}
