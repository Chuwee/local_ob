package es.onebox.bepass.common;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BepassEntityService {

    private static final String TENANT = "bepassTenantId";
    private static final String LOCATION = "bepassLocationId";
    private static final String COMPANY_ID = "bepassCompanyId";

    private final EntitiesRepository entitiesRepository;

    public BepassEntityService(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public BepassEntityConfiguration getConfig(Long entityId) {
        EntityDTO entity = this.entitiesRepository.getByIdCached(entityId);
        Map<String, Object> externalData = entity.getExternalData();
        if (MapUtils.isEmpty(externalData)) {
            throw new OneboxRestException();
        }
        String tenant = (String) externalData.get(TENANT);
        String company = (String) externalData.get(COMPANY_ID);
        List<String> locations = null;
        if (externalData.get(LOCATION) != null && externalData.get(LOCATION) instanceof List) {
            locations = (List<String>) externalData.get(LOCATION);
        }
        if (StringUtils.isAnyEmpty(tenant, company)) {
            throw new OneboxRestException(BepassErrorCode.MISSING_ENTITY_CONFIGURATION);
        }

        return new BepassEntityConfiguration(entityId, tenant, company, locations);
    }

    public void initContext(OrderDTO order) {
        Long entityId = order.getOrderData().getChannelEntityId().longValue();
        BepassEntityConfiguration config = this.getConfig(entityId);
        BepassAuthContext.add(config);
    }
}
