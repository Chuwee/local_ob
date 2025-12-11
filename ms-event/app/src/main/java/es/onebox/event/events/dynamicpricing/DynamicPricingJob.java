package es.onebox.event.events.dynamicpricing;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicPricingJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicPricingJob.class);

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Autowired
    private DynamicPricingService dynamicPricingService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("[DYNAMIC PRICING] Start dynamic pricing cron");

        int attempt = 0;
        while (true) {
            try {
                dynamicPricingService.updatePrices();
                return;
            } catch (Exception e) {
                attempt++;
                LOGGER.warn("[DYNAMIC PRICING] Error processing dynamic pricing (attempt {}/{}). Retrying in {} ms",
                        attempt, MAX_RETRIES, RETRY_DELAY_MS, e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new JobExecutionException("[DYNAMIC PRICING] Job interrupted during retry sleep", ie);
                    }
                } else {
                    throw new JobExecutionException("[DYNAMIC PRICING] Max retries reached", e);
                }
            }
        }
    }

}
