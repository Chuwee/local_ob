package es.onebox.event.products.lock;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.client.HazelcastClientOfflineException;
import es.onebox.hazelcast.core.service.HazelcastLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class HazelcastLockRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastLockRepository.class);

    public static final String UPDATE_PRODUCT_COMMUNICATION_ELEMENT_KEY = "update.productsCommunicationElements.";
    public static final String UPDATE_PRODUCT_STOCK_KEY = "update.productsStock.";
    public static final String UPDATE_PRODUCT_CONTENT_KEY = "update.productsContent.";
    public static final String UPDATE_PRODUCT_TICKET_CONTENT_KEY = "update.productsTicketContent.";
    public static final int LOCK_TIME_MS = 500;

    @Autowired
    private HazelcastLockService hazelcastLockService;

    public <T> T lockedExecutionProductCommunicationElements(Callable<T> callable, Long productId) throws Exception {
        String key = buildProductCommunicationElementLockedKey(productId);
        try {
            return hazelcastLockService.lockedExecution(callable, key, LOCK_TIME_MS, true);
        } catch (HazelcastClientNotActiveException | HazelcastClientOfflineException e) {
            LOGGER.error("Error on locked execution: ", e);
            return callable.call();
        }
    }

    public <T> T lockedExecutionProductStock(Callable<T> callable, Long productId, Long variantId) throws Exception {
        String key = buildProductStockLockedKey(productId, variantId);
        try {
            return hazelcastLockService.lockedExecution(callable, key, LOCK_TIME_MS, true);
        } catch (HazelcastClientNotActiveException | HazelcastClientOfflineException e) {
            LOGGER.error("Error on locked execution: ", e);
            return callable.call();
        }
    }

    public <T> T lockedExecutionProductContents(Callable<T> callable, Long productId, Long attributeId) throws Exception {
        String key = buildProductContentLockedKey(productId, attributeId);
        try {
            return hazelcastLockService.lockedExecution(callable, key, LOCK_TIME_MS, true);
        } catch (HazelcastClientNotActiveException | HazelcastClientOfflineException e) {
            LOGGER.error("Error on locked execution: ", e);
            return callable.call();
        }
    }

    public <T> void lockedExecutionProductTicketContents(Callable<T> callable, Long productId) throws Exception {
        String key = buildProductTicketContentLockedKey(productId);
        try {
            hazelcastLockService.lockedExecution(callable, key, 1000, true);
        } catch (HazelcastClientNotActiveException | HazelcastClientOfflineException e) {
            LOGGER.error("Error on locked execution: ", e);
            callable.call();
        }
    }

    private String buildProductCommunicationElementLockedKey(Long productId) {
        return UPDATE_PRODUCT_COMMUNICATION_ELEMENT_KEY + productId;
    }

    private String buildProductStockLockedKey(Long productId, Long variantId) {
        return UPDATE_PRODUCT_STOCK_KEY + productId + "." + variantId;
    }

    private String buildProductContentLockedKey(Long productId, Long attributeId) {
        return UPDATE_PRODUCT_CONTENT_KEY + productId + "." + attributeId;
    }

    private String buildProductTicketContentLockedKey(Long productId) {
        return UPDATE_PRODUCT_TICKET_CONTENT_KEY + productId;
    }
}
