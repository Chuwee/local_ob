package es.onebox.fifaqatar.tickets.service;

import es.onebox.common.datasources.accesscontrol.dto.ACTicketDTO;
import es.onebox.common.datasources.accesscontrol.dto.ACTicketResponse;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeInfo;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListDTO;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeListFilter;
import es.onebox.common.datasources.accesscontrol.dto.TicketFilter;
import es.onebox.common.datasources.accesscontrol.repository.AccessControlRepository;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.SeasonProductTransferStatus;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.utils.common.EncryptionUtils;
import es.onebox.fifaqatar.tickets.converter.TicketConverter;
import es.onebox.fifaqatar.tickets.dto.HayyaTicket;
import es.onebox.fifaqatar.tickets.dto.HayyaTicketResponse;
import es.onebox.fifaqatar.tickets.dto.ZucchettiTicket;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private AccessControlRepository accessControlRepository;
    @Autowired
    private MsOrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EncryptionUtils encryptionUtils;
    @Autowired
    private MsEventRepository eventRepository;

    private static final Long LIMIT = 2000L;

    private static final int THREADS = 10;

    public List<ZucchettiTicket> getWhitelist(Long sessionId) {
        List<ZucchettiTicket> tickets = new ArrayList<>();
        String token = AuthenticationUtils.getCurrentJwtToken();
        SessionDTO session = eventRepository.getSession(sessionId);

        // First call to know the total
        BarcodeListFilter firstFilter = new BarcodeListFilter();
        firstFilter.setLimit(LIMIT);
        firstFilter.setOffset(0L);

        BarcodeListDTO firstPage = accessControlRepository.getWhitelist(token, session.getEventId(), sessionId, firstFilter);

        long total = firstPage.getMetadata().getTotal();

        List<BarcodeInfo> whitelist = new ArrayList<>();
        if (firstPage.getData() != null) {
            whitelist.addAll(firstPage.getData());
        }

        // Calculate how many pages we need
        List<Long> offsets = getOffsets(total);

        // Create a pool of threads
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        try {
            // all the request in parallel
            List<CompletableFuture<List<BarcodeInfo>>> futures = offsets.stream()
                    .map(offset -> CompletableFuture.supplyAsync(() -> {
                        BarcodeListFilter filter = new BarcodeListFilter();
                        filter.setLimit(LIMIT);
                        filter.setOffset(offset);

                        BarcodeListDTO page =
                                accessControlRepository.getWhitelist(token, session.getEventId(), sessionId, filter);

                        return page.getData() != null ? page.getData() : null;
                    }, executor))
                    .toList();

            // wait for all the requests
            for (CompletableFuture<List<BarcodeInfo>> future : futures) {
                whitelist.addAll(future.get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while parallel fetching whitelist", e);
        } finally {
            executor.shutdown();
        }

        tickets.addAll(TicketConverter.convert(whitelist));
        return  tickets;
    }

    public HayyaTicketResponse getTickets(@Valid TicketFilter filter) {
        HayyaTicketResponse response = new HayyaTicketResponse();
        List<HayyaTicket> hayyaTickets = new ArrayList<>();
        String token = AuthenticationUtils.getCurrentJwtToken();
        ACTicketResponse tickets = accessControlRepository.getTickets(filter, token);
        replaceStringsInObject(tickets.getMetadata(), "access-control-api", "hayya-api");
        response.setMetadata(tickets.getMetadata());
        for (ACTicketDTO acTicketDTO : tickets.getData()) {
            HayyaTicket hayyaTicket = new HayyaTicket();
            // Fill access control data
            TicketConverter.fillACTicket(hayyaTicket, acTicketDTO);
            OrderDTO order = orderRepository.getOrderByCodeCached(acTicketDTO.getOrderCode());

            String encryptBarcode = encryptBarcode(acTicketDTO.getBarcode());
            OrderProductDTO product = getOrderProductByBarcode(order, encryptBarcode);
            if (isTransferred(product)) {
                String customerId = null;

                if (product.getTransferData().getData() != null) {
                    customerId = product.getTransferData().getData().get("customerId");
                }

                Customer customer = (customerId != null) ? getCustomer(customerId) : null;

                if (customer != null) {
                    TicketConverter.fillCustomer(hayyaTicket, customer);
                } else {
                    TicketConverter.fillTransferred(hayyaTicket, product);
                }
            } else {
                TicketConverter.fillOwner(hayyaTicket, order);
            }
            hayyaTickets.add(hayyaTicket);
        }
        response.setData(hayyaTickets);

        return response;
    }

    private List<Long> getOffsets(Long total) {
        List<Long> offsets = new ArrayList<>();
        for (long offset = LIMIT; offset < total; offset += LIMIT) {
            offsets.add(offset);
        }
        return offsets;
    }

    private OrderProductDTO getOrderProductByBarcode(OrderDTO order, String barcode) {
        return order.getProducts().stream()
                .filter(orderProductDTO -> barcode.equals(orderProductDTO.getTicketData().getBarcode()))
                .findFirst()
                .orElse(null);
    }

    private boolean isTransferred(OrderProductDTO product) {
        return product != null && product.getTransferData() != null &&
                product.getTransferData().getStatus().equals(SeasonProductTransferStatus.TRANSFERRED);
    }

    private Customer getCustomer(String customerId) {
        return customerRepository.getCustomer(customerId);
    }

    private String encryptBarcode(String barcode) {
        return encryptionUtils.encode(barcode);
    }

    private void replaceStringsInObject(Object obj, String oldText, String newText) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(obj);
                    if (value != null && value.contains(oldText)) {
                        field.set(obj, value.replace(oldText, newText));
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn("[Hayya] Error replacing metadata information", e);
                }
            }
        }
    }
}
