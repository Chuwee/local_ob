package es.onebox.mgmt.datasources.queueit.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityQueueProvider;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.queueit.QueueItDatasource;
import es.onebox.mgmt.queueit.QueueItCustomerDescriptor;
import es.onebox.mgmt.queueit.QueueItProperties;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class QueueItRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueItRepository.class);
    private static final String CACHE_QUEUE_IT_INTEGRATION_CONFIG = "queueit.config";

    private final QueueItDatasource queueItDataSource;
    @Autowired
    private QueueItProperties queueItProperties;
    @Autowired
    private EntitiesRepository entitiesRepository;

    private Map<Long, QueueItCustomerDescriptor> customerQueueItData = new HashMap<>();

    public QueueItRepository(QueueItDatasource queueItDataSource) {
        this.queueItDataSource = queueItDataSource;
    }

    @PostConstruct
    private void initConfig() {
        if (CollectionUtils.isNotEmpty(queueItProperties.getCustomerAccounts())) {
            customerQueueItData = queueItProperties.getCustomerAccounts().stream()
                    .collect(Collectors.toMap(QueueItCustomerDescriptor::getEntityId, Function.identity()));
        }
    }

    @Cached(key = CACHE_QUEUE_IT_INTEGRATION_CONFIG, expires = 1, timeUnit = TimeUnit.MINUTES)
    public String getCustomerIntegrationConfiguration(@CachedArg Long entityId) {
        try {
            String customerId = getCustomerId(entityId);
            String host = getQueueItHost(entityId);
            String apiKey = getApiKey(entityId);
            return queueItDataSource.getEventQueueItConfig(customerId, apiKey, host);
        } catch (Exception e) {
            LOGGER.error("[QUEUEIT] Error retrieving customer integration configuration.", e);
        }
        return null;
    }

    private String getCustomerId(Long entityId) {
        if (useCustomerQueueItConfig(entityId)) {
            return customerQueueItData.get(entityId).getCustomerId();
        }
        return queueItProperties.getCustomerId();
    }

    private String getQueueItHost(Long entityId) {
        if (useCustomerQueueItConfig(entityId)) {
            return customerQueueItData.get(entityId).getHost();
        }
        return queueItProperties.getHost();
    }

    private String getApiKey(Long entityId) {
        if (useCustomerQueueItConfig(entityId)) {
            return customerQueueItData.get(entityId).getApiKey();
        }
        return queueItProperties.getApiKey();
    }

    private boolean useCustomerQueueItConfig(Long entityId) {
        if (entityId != null && customerQueueItData.containsKey(entityId)) {
            //Call this function after checking if there is a configuration present on sys-config to minimize the calls to ms-entity
            Entity entity = entitiesRepository.getEntity(entityId);
            return entity != null && EntityQueueProvider.QUEUE_IT.equals(entity.getQueueProvider());
        }
        return false;
    }
}
