package es.onebox.event.products.amqp;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.common.amqp.refreshdata.ProductMigrationMessage;
import es.onebox.event.products.amqp.productupdater.ProductCatalogUpdater;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ProductMigrationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductMigrationProcessor.class);

    private static final int MIGRATION_LOCK_TTL = 1;
    private static final String PRODUCT_MIGRATION_EXECUTION_KEY = "productMigrationExecutionKey";

    private final ProductCatalogUpdater productCatalogUpdater;
    protected final CacheRepository cacheRepository;

    @Autowired
    public ProductMigrationProcessor(ProductCatalogUpdater productCatalogUpdater, CacheRepository cacheRepository) {
        this.productCatalogUpdater = productCatalogUpdater;
        this.cacheRepository = cacheRepository;
    }


    @Override
    public void execute(Exchange exchange) {

        ProductMigrationMessage message = exchange.getIn().getBody(ProductMigrationMessage.class);
        Long productId = message.getProductId();

        try {
            Boolean inExecution = cacheRepository.get(PRODUCT_MIGRATION_EXECUTION_KEY, Boolean.class, new Object[]{productId});

            if (inExecution == null) {
                long initTime = System.currentTimeMillis();

                cacheRepository.set(PRODUCT_MIGRATION_EXECUTION_KEY, Boolean.TRUE,
                        MIGRATION_LOCK_TTL, TimeUnit.SECONDS, new Object[]{productId});

                productCatalogUpdater.updateCatalog(productId);

                LOGGER.info("[PRODUCT CATALOG UPDATER] Finished updating productId: {} - with total time: {}",
                        productId, (System.currentTimeMillis() - initTime));
            }
        } catch (Exception e) {
            LOGGER.error("[PRODUCT CATALOG UPDATER] productId: {} - Error saving product", productId, e);
        } finally {
            cacheRepository.remove(PRODUCT_MIGRATION_EXECUTION_KEY, new Object[]{productId});
        }
    }

}
