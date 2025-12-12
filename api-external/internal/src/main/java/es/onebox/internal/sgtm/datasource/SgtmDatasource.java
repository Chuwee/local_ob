package es.onebox.internal.sgtm.datasource;

import java.util.HashMap;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.internal.sgtm.dto.SgtmMessageDTO;

@Component
public class SgtmDatasource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SgtmDatasource.class);
    private static final long CONNECTION_TIMEOUT = 10000L;
    private static final long READ_TIMEOUT = 30000L;
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long[] RETRY_DELAYS = {1000, 2000, 5000, 10000};

    private final OrdersRepository ordersRepository;
    private final HttpClient sgtmHttpClient;

    @Autowired
    public SgtmDatasource(OrdersRepository ordersRepository,
                          @Value("${tag_manager_server.url}") String sgtmUrl,
                          ObjectMapper jacksonMapper) {
        
        this.ordersRepository = ordersRepository;

        // HTTP client for SGTM endpoint
        this.sgtmHttpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .baseUrl(sgtmUrl)
                .jacksonMapper(jacksonMapper)
                .build();
    }

    /**
     * Generic retry method that executes a supplier function with retry logic
     * @param supplier the function to execute
     * @param operationName descriptive name for logging
     * @param <T> the return type
     * @return the result of the supplier function
     * @throws RuntimeException if all retry attempts fail
     */
    private <T> T executeWithRetry(Supplier<T> supplier, String operationName) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                LOGGER.debug("[SGTM] Attempting {} (attempt {}/{})", operationName, attempt, MAX_RETRY_ATTEMPTS);
                
                T result = supplier.get();
                
                if (attempt > 1) {
                    LOGGER.info("[SGTM] Successfully executed {} on attempt {}", operationName, attempt);
                }
                
                return result;
                
            } catch (Exception e) {
                lastException = e;
                
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    LOGGER.error("[SGTM] Failed to execute {} after {} attempts. Last error: {}", 
                        operationName, MAX_RETRY_ATTEMPTS, e.getMessage(), e);
                    break;
                }
                
                long delay = RETRY_DELAYS[attempt - 1];
                LOGGER.warn("[SGTM] Failed to execute {} on attempt {}/{}. Retrying in {}ms. Error: {}", 
                    operationName, attempt, MAX_RETRY_ATTEMPTS, delay, e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("[SGTM] Retry interrupted for {}", operationName);
                    throw new RuntimeException("Retry interrupted for " + operationName, ie);
                }
            }
        }
        
        throw new RuntimeException("Failed to execute " + operationName + " after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }

    public HashMap getOrderDetail(String orderCode, String token) {
        return executeWithRetry(
            () -> ordersRepository.getRawOrder(orderCode, token),
            "Get order details for orderCode: " + orderCode
        );
    }

    /**
     * Send message to SGTM endpoint
     * @param message the message to send
     * @param xGtmServerPreview the x-gtm-server-preview header (optional)
     */
    public void sendToSgtm(SgtmMessageDTO message, String xGtmServerPreview) {
        sgtmHttpClient.buildRequest(HttpMethod.POST, "/wbhk")
            .body(new ClientRequestBody(message))
            .headers(getHeaders(message, xGtmServerPreview))
            .execute();
    }

    private RequestHeaders getHeaders(SgtmMessageDTO message, String xGtmServerPreview) {
        RequestHeaders.Builder builder = new RequestHeaders.Builder();
        if (xGtmServerPreview != null) builder.addHeader("x-gtm-server-preview", xGtmServerPreview);
        if (message.getAction() != null) builder.addHeader("ob-action", message.getAction());
        if (message.getEvent() != null) builder.addHeader("ob-event", message.getEvent());
        if (message.getDeliveryId() != null) builder.addHeader("ob-delivery-id", message.getDeliveryId());
        if (message.getHookId() != null) builder.addHeader("ob-hook-id", message.getHookId());
        if (message.getSignature() != null) builder.addHeader("ob-gtm-server-signature", message.getSignature());
        return builder.build();
    }
}