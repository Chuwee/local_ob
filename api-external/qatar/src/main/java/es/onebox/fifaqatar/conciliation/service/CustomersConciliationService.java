package es.onebox.fifaqatar.conciliation.service;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerResponse;
import es.onebox.common.datasources.ms.client.dto.request.SearchCustomersRequest;
import es.onebox.common.datasources.ms.client.dto.response.CustomerResponse;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orders.dto.Order;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.core.file.exporter.generator.export.FileFormat;
import es.onebox.fifaqatar.conciliation.dto.UpdateCustomerRequestDTO;
import es.onebox.fifaqatar.conciliation.utils.ConciliationUtils;
import es.onebox.fifaqatar.conciliation.utils.S3Utils;
import es.onebox.fifaqatar.config.config.FifaQatarConfigCouchDao;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.config.FifaQatarConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Service
public class CustomersConciliationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersConciliationService.class);
    private final FifaQatarConfigCouchDao configCouchDao;
    private final CustomerRepository customerRepository;
    private final TokenRepository tokenRepository;
    private final FifaQatarConfigRepository configRepository;
    private final ConciliationUtils conciliationUtils;
    private final S3Utils s3Utils;

    @Autowired
    private OrdersRepository ordersRepository;

    public CustomersConciliationService(FifaQatarConfigCouchDao configCouchDao, CustomerRepository customerRepository, TokenRepository tokenRepository, FifaQatarConfigRepository configRepository, ConciliationUtils conciliationUtils, S3Utils s3Utils) {
        this.configCouchDao = configCouchDao;
        this.configRepository = configRepository;
        this.customerRepository = customerRepository;
        this.tokenRepository = tokenRepository;
        this.conciliationUtils = conciliationUtils;
        this.s3Utils = s3Utils;
    }

    public Map<String, Object> updateCustomers(List<Long> channelIds) throws IOException {
        final Instant start = Instant.now();
        FifaQatarConfigDocument config = configRepository.getMainConfig();
        ZonedDateTime lte = ZonedDateTime.now();
        ZonedDateTime gte = lte.minusMonths(4);

        SearchCustomersRequest req = new SearchCustomersRequest();
        req.setEntityId(config.getEntityId());
        List<CustomerResponse> allCustomers = customerRepository.getAllCustomers(req);

        String accessToken = tokenRepository.getOneboxClientToken(config.getApiKey());
        List<Order> orders = ordersRepository.getOrders(accessToken, channelIds, gte, lte, true);

        Map<String, List<Order>> customerIdToOrders = orders.stream()
                .filter(o -> o.getBuyerData() != null && o.getBuyerData().get("user_id") != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        o -> o.getBuyerData().get("user_id").toString()
                ));


        final ConcurrentHashMap<String, Object> updatedCustomers = new ConcurrentHashMap<>(allCustomers.size() * 2);

        conciliationUtils.checkProcessNotExistsForEntity(config.getEntityId().longValue());

        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = getThreadPoolExecutor(cores);

        final int batchSize = 200;
        final int chunkSize = 10;
        final int total = allCustomers.size();

        final LongAdder updated = new LongAdder();
        final LongAdder already = new LongAdder();
        final LongAdder skippedNoOrders = new LongAdder();
        final LongAdder skippedNoBuyer = new LongAdder();

        try {
            for (int i = 0; i < allCustomers.size(); i += batchSize) {
                conciliationUtils.checkProcessShouldPause(config.getEntityId().longValue());
                int end = Math.min(i + batchSize, allCustomers.size());
                List<CustomerResponse> batch = allCustomers.subList(i, end);

                List<Future<?>> futures = new ArrayList<>();
                for (int offset = 0; offset < batch.size(); offset += chunkSize) {
                    List<CustomerResponse> chunk = batch.subList(offset, Math.min(offset + chunkSize, batch.size()));
                    Future<?> f = pool.submit(() -> {
                        try {
                            conciliationUtils.checkProcessShouldPause(config.getEntityId().longValue());
                            for (CustomerResponse c : chunk) {
                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                try {
                                    List<Order> ordersForCustomer = customerIdToOrders.get(String.valueOf(c.getId()));
                                    if (ordersForCustomer == null || ordersForCustomer.isEmpty()) {
                                        skippedNoOrders.increment();
                                        continue;
                                    }

                                    Map<String, Object> buyerData = ordersForCustomer.stream()
                                            .map(Order::getBuyerData)
                                            .filter(Objects::nonNull)
                                            .findFirst()
                                            .orElse(null);

                                    if (buyerData == null) {
                                        skippedNoBuyer.increment();
                                        continue;
                                    }
                                    Customer detail = customerRepository.getCustomer(c.getId());
                                    UpdateCustomerRequestDTO updateReq = ConciliationUtils.updateCustomerRequest(buyerData);
                                    if (ConciliationUtils.customerAlreadyUpdated(detail, updateReq)) {
                                        already.increment();
                                    } else {
                                        customerRepository.updateCustomer(c.getId(), config.getEntityId(), updateReq);
                                        updatedCustomers.put(c.getId(), true);
                                        updated.increment();
                                    }
                                    continue;

                                } catch (Exception ex) {
                                    LOGGER.info("Failed to update customer: {}", c.getId(), ex);
                                    updatedCustomers.put(c.getId(), false);
                                }

                                try {
                                    TimeUnit.MILLISECONDS.sleep(25L);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    futures.add(f);
                }

                for (Future<?> f : futures) {
                    try {
                        f.get();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        LOGGER.error("Error executing update batch chunk", e);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("Customer update process failed", e);
        } finally {
            pool.shutdownNow();
        }

        conciliationUtils.cleanProcessLock(config.getEntityId().longValue());
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        LOGGER.info("Total customers: {}", total);
        LOGGER.info("Customers Updated: {}", updated.sum());
        LOGGER.info("Customers Already up-to-date: {}", already.sum());
        LOGGER.info("Customers Skipped - no orders: {}", skippedNoOrders.sum());
        LOGGER.info("Customers Skipped - no buyerData: {}", skippedNoBuyer.sum());
        LOGGER.info("Finished customers conciliation. Updated: {}, Elapsed {} ms", updated.sum(), elapsedMs);
        File csvFile = conciliationUtils.buildUserUpdatesCsv(updatedCustomers);
        try {
            UUID uuid = UUID.randomUUID();
            FileFormat fileFormat = FileFormat.CSV;
            String reportTypeName = "CUSTOMERS_CONCILIATION_QATAR";
            String s3url=  s3Utils.toS3(csvFile, String.valueOf(uuid), fileFormat, reportTypeName);
            LOGGER.info("Customers created successfully. Report available at {}", s3url);
        } finally {
            try { Files.deleteIfExists(csvFile.toPath()); } catch (IOException ignore) {}
        }
        return updatedCustomers;
    }

    public Map<String, Object> createCustomersFromCsv(MultipartFile file, List<Long> channelIds) throws IOException {
        final Instant start = Instant.now();
        if (file == null || file.isEmpty()) {
            return Map.of();
        }

        Map<String, String> parsedCsv = ConciliationUtils.readCsv(file);
        FifaQatarConfigDocument config = configRepository.getMainConfig();

        String accessToken = tokenRepository.getOneboxClientToken(config.getApiKey());
        conciliationUtils.checkProcessNotExistsForEntity(config.getEntityId().longValue());

        List<CreateCustomerRequest> requests = conciliationUtils.createCustomerRequests(parsedCsv, accessToken, channelIds, config);

        final ConcurrentHashMap<String, Object> result = new ConcurrentHashMap<>(requests.size() * 2);
        final LongAdder created = new LongAdder();

        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = getThreadPoolExecutor(cores);

        final int batchSize = 200;
        final int chunkSize = 10;
        final int total = requests.size();

        try {
            for (int i = 0; i < requests.size(); i += batchSize) {
                conciliationUtils.checkProcessShouldPause(config.getEntityId().longValue());
                int end = Math.min(i + batchSize, requests.size());
                List<CreateCustomerRequest> batch = requests.subList(i, end);

                List<Future<?>> futures = new ArrayList<>();
                for (int offset = 0; offset < batch.size(); offset += chunkSize) {
                    List<CreateCustomerRequest> chunk = batch.subList(offset, Math.min(offset + chunkSize, batch.size()));
                    Future<?> f = pool.submit(() -> {
                        try {
                            conciliationUtils.checkProcessShouldPause(config.getEntityId().longValue());
                            for (CreateCustomerRequest req : chunk) {
                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                                try {
                                    CreateCustomerResponse resp = customerRepository.createCustomer(req);
                                    result.put(req.getEmail(), resp.getId());
                                    created.increment();
                                } catch (Exception ex) {
                                    result.put(req.getEmail(), Map.of("status", "NOT_CREATED", "error", ex.getMessage()));
                                }
                                try {
                                    TimeUnit.MILLISECONDS.sleep(50L);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    futures.add(f);
                }

                for (Future<?> f : futures) {
                    try {
                        f.get();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        LOGGER.error("Error executing batch chunk", e);
                    }
                }

                int processed = result.size();
                int remaining = total - processed;
                LOGGER.info("[QATAR ]Customer creation process. Processed {} / {}. Remaining {}", processed, total, remaining);
            }
        } catch (Exception e) {
            LOGGER.error("Customer creation failed", e);
        } finally {
            pool.shutdownNow();
        }

        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        LOGGER.info("Finished CSV processing. Customers created: {}, Elapsed {} ms", created.sum(), elapsedMs);
        conciliationUtils.cleanProcessLock(config.getEntityId().longValue());
        File csvFile = conciliationUtils.buildCsv(result);
        try {
            UUID uuid = UUID.randomUUID();
            FileFormat fileFormat = FileFormat.CSV;
            String reportTypeName = "CUSTOMERS_CREATION_QATAR";
            String s3url=  s3Utils.toS3(csvFile, String.valueOf(uuid), fileFormat, reportTypeName);
            LOGGER.info("Customers created successfully. Report available at {}", s3url);
        } finally {
            try { Files.deleteIfExists(csvFile.toPath()); } catch (IOException ignore) {}
        }
        return result;
    }

    private static ThreadPoolExecutor getThreadPoolExecutor(int cores) {
        int reserve = Math.min(2, Math.max(0, cores / 8));
        int target = Math.max(4, cores - reserve);
        int maxThreads = Math.min(12, target);
        int queueCapacity = 200;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                r -> {
                    Thread t = new Thread(r, "customer-create-" + System.nanoTime());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return pool;
    }

}

