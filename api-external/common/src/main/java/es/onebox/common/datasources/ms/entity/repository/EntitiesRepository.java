package es.onebox.common.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.common.dto.Category;
import es.onebox.common.datasources.ms.entity.MsEntityDatasource;
import es.onebox.common.datasources.ms.entity.dto.AuthConfigDTO;
import es.onebox.common.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.common.datasources.ms.entity.dto.Entities;
import es.onebox.common.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.common.datasources.ms.entity.dto.EntityConfig;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.common.datasources.ms.entity.dto.Operator;
import es.onebox.common.datasources.ms.entity.dto.Producer;
import es.onebox.common.datasources.ms.entity.dto.RequestEntityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntitiesRepository {

    private static final int CACHE_OPERATOR_TTL = 600;
    private static final String CACHE_OPERATOR_KEY = "entities.operator";

    @Qualifier("msEntityDataSource")
    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntitiesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    @Cached(key = "entity", expires = 30 * 60)
    public EntityDTO getByIdCached(@CachedArg Long id) {
        return msEntityDatasource.getEntity(id);
    }

    public EntityDTO getById(Long id) {
        return msEntityDatasource.getEntity(id);
    }

    @Cached(key = "promoter", expires = 60 * 60)
    public Producer getProducerById(@CachedArg Long producerId) {
        return msEntityDatasource.getProducer(producerId);
    }

    @Cached(key = "personalizedCategories", expires = 5 * 60)
    public List<Category> getPersonalizedCategories(@CachedArg Long entityId) {
        return msEntityDatasource.getPersonalizedCategories(entityId);
    }


    @Cached(key = CACHE_OPERATOR_KEY, expires = CACHE_OPERATOR_TTL)
    public Operator getCachedOperator(@CachedArg Long entityId) {
        EntityDTO entity = getByIdCached(entityId);
        return msEntityDatasource.getOperator(entity.getOperator().getId());
    }

    @Cached(key = "entityCustomerTypes", expires = 5 * 60)
    public CustomerTypes getCustomerTypes(@CachedArg Long entityId) {
        return msEntityDatasource.getCustomerTypes(entityId);
    }

    public void updateEntity(Long entityId, RequestEntityDTO entity) {
        msEntityDatasource.updateEntity(entityId, entity);
    }

    public Entities getEntities(EntitySearchFilter entitySearchFilter) {
        return msEntityDatasource.getEntities(entitySearchFilter);
    }


    public List<EntityConfig> getEntitiesConfigs(List<Long> entities) {
        return msEntityDatasource.getEntitiesConfigs(entities);
    }

    public EntityBankAccount getEntityBankAccount(Long entityId, Long bankAccountId) {
        return msEntityDatasource.getEntityBankAccount(entityId, bankAccountId);
    }

    @Cached(key = "authConfig", expires = 10 * 60)
    public AuthConfigDTO getEntityAuthConfig(@CachedArg Long entityId) {
        return msEntityDatasource.getAuthConfig(entityId);
    }
}
