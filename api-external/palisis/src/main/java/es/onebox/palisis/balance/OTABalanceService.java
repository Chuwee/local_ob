package es.onebox.palisis.balance;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.palisis.balance.dao.EntityOTABalanceCouchDao;
import es.onebox.palisis.balance.domain.OTABalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OTABalanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTABalanceService.class);

    private final EntityOTABalanceCouchDao entityOTABalanceCouchDao;

    public OTABalanceService(EntityOTABalanceCouchDao entityOTABalanceCouchDao) {
        this.entityOTABalanceCouchDao = entityOTABalanceCouchDao;
    }

    public Map<String, Double> getOTABalance(Long entityId) {
        OTABalance otaBalance = entityOTABalanceCouchDao.get(entityId.toString());
        if (otaBalance == null) {
            LOGGER.warn("[PALISIS] OTA Balance not found for entityId: {}", entityId);
            throw new OneboxRestException(ApiExternalErrorCode.NOT_FOUND, "entity not found", null);
        }
        return otaBalance;
    }
}
