package es.onebox.fifaqatar.notification;

import es.onebox.common.datasources.ms.accesscontrol.dto.Barcode;
import es.onebox.common.datasources.ms.accesscontrol.dto.ImportExternalBarcode;
import es.onebox.common.datasources.ms.accesscontrol.repository.ExternalBarcodeRepository;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;
import es.onebox.common.datasources.ms.order.dto.response.barcodes.BarcodeValidationStatus;
import es.onebox.common.datasources.ms.order.dto.response.barcodes.ProductBarcode;
import es.onebox.common.datasources.ms.order.dto.response.barcodes.ProductBarcodesResponse;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.order.repository.ProductBarcodesRepository;
import es.onebox.common.datasources.ms.order.request.barcodes.ProductBarcodesSearchRequest;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.core.utils.common.EncryptionUtils;
import es.onebox.fifaqatar.conciliation.dto.UpdateCustomerRequestDTO;
import es.onebox.fifaqatar.conciliation.utils.ConciliationUtils;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.config.FifaQatarConfigRepository;
import es.onebox.fifaqatar.notification.dto.request.BarcodeRequestDTO;
import es.onebox.fifaqatar.notification.dto.request.NotificationMessageDTO;
import es.onebox.fifaqatar.notification.mapping.SessionMappingRepository;
import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodeMapping;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.fifaqatar.notification.NotificationAction.CANCEL;
import static es.onebox.fifaqatar.notification.NotificationAction.PURCHASE;
import static es.onebox.fifaqatar.notification.NotificationAction.REFUND;
import static es.onebox.fifaqatar.notification.NotificationAction.TRANSFER;
import static es.onebox.fifaqatar.notification.NotificationEvent.ITEM;
import static es.onebox.fifaqatar.notification.NotificationEvent.ORDER;

@Service
public class FifaQatarNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarNotificationService.class);

    private final String BARCODE_PROVIDER_ONEBOX = "ONEBOX";

    private final ProductBarcodesRepository productBarcodesRepository;
    private final ExternalBarcodeRepository externalBarcodeRepository;
    private final MsEventRepository msEventRepository;
    private final SessionMappingRepository mappingRepository;
    private final MsOrderRepository orderRepository;
    private final EncryptionUtils encryptionUtils;
    private final CustomerRepository customerRepository;
    private final FifaQatarConfigRepository configRepository;
    private final OrdersRepository ordersRepository;
    private final TokenRepository tokenRepository;


    public FifaQatarNotificationService(ProductBarcodesRepository productBarcodesRepository,
                                        ExternalBarcodeRepository externalBarcodeRepository,
                                        MsEventRepository msEventRepository, SessionMappingRepository mappingRepository, MsOrderRepository orderRepository, EncryptionUtils encryptionUtils, CustomerRepository customerRepository, FifaQatarConfigRepository configRepository, OrdersRepository ordersRepository, TokenRepository tokenRepository) {
        this.productBarcodesRepository = productBarcodesRepository;
        this.externalBarcodeRepository = externalBarcodeRepository;
        this.msEventRepository = msEventRepository;
        this.mappingRepository = mappingRepository;
        this.orderRepository = orderRepository;
        this.encryptionUtils = encryptionUtils;
        this.customerRepository = customerRepository;
        this.configRepository = configRepository;
        this.ordersRepository = ordersRepository;
        this.tokenRepository = tokenRepository;
    }

    public List<SessionBarcodeMapping> getSessionsMapping(Long sourceSessionId, Long destinationSessionId) {
        if (sourceSessionId != null) {
            var sessionMapped = mappingRepository.getBySessionId(sourceSessionId);
            return sessionMapped != null ? List.of(sessionMapped) : List.of();
        } else if (destinationSessionId != null) {
            return mappingRepository.getByDestinationSessionId(destinationSessionId);
        } else {
            return mappingRepository.get();
        }
    }

    public void migrateSessionMappedBarcodes(Long sessionId) {
        SessionBarcodeMapping sessionMapping = mappingRepository.getBySessionId(sessionId);
        if (sessionMapping == null) {
            LOGGER.warn("[QATAR] session barcodes: session {} not found in mapping", sessionId);
            return;
        }
        final long LIMIT = 2000L;
        long offset = 0L;
        SessionCatalog sessionCatalog = msEventRepository.getSessionCatalog(sessionMapping.getDestinationSessionId());
        ProductBarcodesSearchRequest barcodesRequest = new ProductBarcodesSearchRequest();
        barcodesRequest.setSessionId(List.of(sessionId.intValue()));
        barcodesRequest.setBarcodeOrderProvider(BARCODE_PROVIDER_ONEBOX);
        barcodesRequest.setLimit(LIMIT);

        ProductBarcodesResponse productBarcodesResponse;
        List<ProductBarcode> barcodes = new ArrayList<>();
        do {
            barcodesRequest.setOffset(offset);
            productBarcodesResponse = productBarcodesRepository.searchBarcodes(barcodesRequest);
            if (CollectionUtils.isNotEmpty(productBarcodesResponse.getData())) {
                barcodes.addAll(productBarcodesResponse.getData());
                offset += productBarcodesResponse.getData().size();
            }
        } while (offset < productBarcodesResponse.getMetadata().getTotal());

        List<Barcode> barcodesToImport = barcodes.stream().map(barcode -> {
            Barcode importedBarcode = new Barcode();
            importedBarcode.setBarcode(barcode.getBarcode());

            importedBarcode.setStatus(BarcodeValidationStatus.INVALID.equals(barcode.getStatus()) ? BarcodeValidationStatus.INVALID.name() : BarcodeValidationStatus.NOT_VALIDATED.name());
            var gate = sessionMapping.getGates().get(0);
            importedBarcode.setAccessId(gate.getDestinationId().longValue());
            if (barcode.getSeat() != null && barcode.getSeat().getSeat() != null) {
                importedBarcode.setSeat(barcode.getSeat().getSeat().getName());
                importedBarcode.setSectorName(gate.getDestinationSectorName());
            } else {
                //We need to set a seat name if we want sector name ¬¬
                importedBarcode.setSeat(gate.getDestinationSectorName());
                importedBarcode.setSectorName(gate.getDestinationSectorName());
            }

            return importedBarcode;
        }).collect(Collectors.toList());
        ImportExternalBarcode externalBarcodes = new ImportExternalBarcode();
        externalBarcodes.setSessionId(sessionCatalog.getSessionId());
        externalBarcodes.setEventId(sessionCatalog.getEventId());

        LOGGER.info("[QATAR] session barcodes: importing {} barcodes from session {} to session {}", barcodesToImport.size(), sessionId, sessionMapping.getDestinationSessionId());
        List<List<Barcode>> chunks = ListUtils.partition(barcodesToImport, 200);
        int progress = 0;
        for (List<Barcode> chunk : chunks) {
            progress += chunk.size();
            LOGGER.info("[QATAR] session barcodes: importing {} barcodes from session {} to session {} . {}/{}", chunk.size(), sessionId, sessionMapping.getDestinationSessionId(), progress, barcodesToImport.size());
            externalBarcodes.setBarcodes(chunk);
            externalBarcodeRepository.importExternalBarcodes(externalBarcodes);
        }


    }

    public void attemptHook(NotificationMessageDTO notificationMessage, String action, String event) {
        LOGGER.info("[QATAR] webhook notification: {} {} {} {}", action, event, notificationMessage.getCode(), notificationMessage.getItemId());

        NotificationAction notificationAction = NotificationAction.fromValue(action);
        NotificationEvent notificationEvent = NotificationEvent.fromValue(event);
        if (TRANSFER.equals(notificationAction) && ITEM.equals(notificationEvent)) {
            String orderCode = notificationMessage.getCode();
            Long itemId = notificationMessage.getItemId();
            OrderDTO orderByCode = orderRepository.getOrderByCode(orderCode);
            List<OrderProductDTO> filteredProducts = orderByCode.getProducts().stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList());
            processOrder(filteredProducts, orderByCode.getCode(), notificationAction, notificationEvent, notificationMessage);
        } else if (REFUND.equals(notificationAction) && ORDER.equals(notificationEvent)) {
            String orderCode = notificationMessage.getPreviousCode();
            OrderDTO orderByCode = orderRepository.getOrderByCode(orderCode);
            processOrder(orderByCode.getProducts(), orderByCode.getCode(), notificationAction, notificationEvent, notificationMessage);
        } else if (CANCEL.equals(notificationAction) && ORDER.equals(notificationEvent)) {
            String orderCode = notificationMessage.getCode();
            OrderDTO orderByCode = orderRepository.getOrderByCode(orderCode);
            processOrder(orderByCode.getProducts(), orderByCode.getCode(), notificationAction, notificationEvent, notificationMessage);
        } else if (PURCHASE.equals(notificationAction) && ORDER.equals(notificationEvent)) {
            String orderCode = notificationMessage.getCode();
            OrderDTO orderByCode = orderRepository.getOrderByCode(orderCode);
            processOrder(orderByCode.getProducts(), orderByCode.getCode(), notificationAction, notificationEvent, notificationMessage);
            updateCustomer(orderByCode);
        }
    }

    private void updateCustomer(OrderDTO orderByCode) {
        FifaQatarConfigDocument config = configRepository.getMainConfig();
        String accessToken = tokenRepository.getOneboxClientToken(config.getApiKey());
        OrderDetail order = ordersRepository.getById(orderByCode.getCode(), accessToken);
        String customerId = orderByCode.getCustomer().getUserId();
        if (StringUtils.isBlank(customerId)) {
            return;
        }
        Customer customer = customerRepository.getCustomer(customerId);
        UpdateCustomerRequestDTO updateCustomerRequestDTO = ConciliationUtils.updateCustomerRequest(customer, order.getBuyerData());
        boolean isAlreadyUpdated = ConciliationUtils.customerAlreadyUpdated(customer, updateCustomerRequestDTO);
        if (!isAlreadyUpdated) {
            LOGGER.info("[QATAR] Updating customer: {} from order: {}", customerId, orderByCode.getCode());
            customerRepository.updateCustomer(customerId, config.getEntityId(), updateCustomerRequestDTO);
        }
    }

    private void buildAndSendBarcodes(OrderTicketDataDTO ticketData, String orderCode, NotificationAction notificationAction, NotificationEvent notificationEvent, NotificationMessageDTO notificationMessage) {
        if (ticketData == null) {
            return;
        }
        ticketData.getBarcodes().forEach(barcode -> {
            var sessionId = barcode.getSessionId().longValue();
            SessionBarcodeMapping sessionMapping = mappingRepository.getBySessionId(sessionId);
            if (sessionMapping != null) {
                SessionCatalog sessionCatalog = msEventRepository.getSessionCatalog(sessionMapping.getDestinationSessionId());
                ImportExternalBarcode externalBarcodes = new ImportExternalBarcode();
                externalBarcodes.setSessionId(sessionMapping.getDestinationSessionId());
                externalBarcodes.setEventId(sessionCatalog.getEventId());

                Barcode importBarcode = new Barcode();
                importBarcode.setLocator(orderCode);
                importBarcode.setBarcode(encryptionUtils.decode(barcode.getBarcode()));
                if (CANCEL.equals(notificationAction)) {
                    importBarcode.setStatus(BarcodeValidationStatus.INVALID.name());
                } else {
                    BarcodeValidationStatus barcodeValidationStatus = "VALID".equals(barcode.getStatus()) ? BarcodeValidationStatus.NOT_VALIDATED : BarcodeValidationStatus.INVALID;
                    importBarcode.setStatus(barcodeValidationStatus.name());
                }
                if (CollectionUtils.isNotEmpty(sessionMapping.getGates())) {
                    var gate = sessionMapping.getGates().get(0); //We assume just qatar case... extend me if we need it
                    importBarcode.setAccessId(gate.getDestinationId().longValue());
                    importBarcode.setSeat(gate.getDestinationSectorName());
                    importBarcode.setSectorName(gate.getDestinationSectorName());
                }
                externalBarcodes.setBarcodes(List.of(importBarcode));
                LOGGER.info("[QATAR] webhook notification: {} {} importing barcode {} with status {} to session {} and locator {}", notificationAction, notificationEvent, importBarcode.getBarcode(), importBarcode.getStatus(), externalBarcodes.getSessionId(), importBarcode.getLocator());

                externalBarcodeRepository.importExternalBarcodes(externalBarcodes);
            } else {
                LOGGER.info("[QATAR] webhook notification: {} {} session {} not mapped for order {} and item {}", notificationAction, notificationEvent, sessionId, notificationMessage.getCode(), notificationMessage.getItemId());
            }
        });
    }

    private void processOrder(List<OrderProductDTO> products, String orderCode, NotificationAction notificationAction, NotificationEvent notificationEvent, NotificationMessageDTO notificationMessage) {
        products.forEach(item -> {
            OrderTicketDataDTO ticketData = item.getTicketData();
            buildAndSendBarcodes(ticketData, orderCode, notificationAction, notificationEvent, notificationMessage);
        });
    }


    /* TODO
     *
     * limit n offset
     * fill more fields
     * eye danger with enums
     * map with qatar sessions - gate mapping
     *
     *
     * */
    public void migrateBarcodes(BarcodeRequestDTO request) {

        SessionCatalog sourceSession = msEventRepository.getSessionCatalog(request.getSourceSessionId().longValue());
        SessionCatalog destinationSession = msEventRepository.getSessionCatalog(request.getDestinationSessionId().longValue());

        ProductBarcodesSearchRequest barcodesRequest = new ProductBarcodesSearchRequest();
        barcodesRequest.setSessionId(List.of(request.getSourceSessionId()));
        barcodesRequest.setBarcodeOrderProvider("ONEBOX");

        ProductBarcodesResponse productBarcodesResponse = productBarcodesRepository.searchBarcodes(barcodesRequest);
        List<Barcode> barcodesToImport = productBarcodesResponse.getData().stream().map(barcode -> {
            Barcode importedBarcode = new Barcode();
            importedBarcode.setBarcode(barcode.getBarcode());
            importedBarcode.setStatus(barcode.getStatus().name());

            return importedBarcode;
        }).collect(Collectors.toList());
        //TODO transform productBarcodesResponse with mapping and import it!
        ImportExternalBarcode externalBarcodes = new ImportExternalBarcode();
        externalBarcodes.setSessionId(destinationSession.getSessionId());
        externalBarcodes.setEventId(destinationSession.getEventId());
        externalBarcodes.setBarcodes(barcodesToImport);

        externalBarcodeRepository.importExternalBarcodes(externalBarcodes);
    }
}
