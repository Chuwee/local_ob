package es.onebox.atm.tickets;


import com.oneboxtds.datasource.s3.enums.ContentDisposition;
import es.onebox.atm.config.ATMEntityConfiguration;
import es.onebox.atm.tickets.dto.TicketURLContentDTO;
import es.onebox.atm.tickets.eip.ATMTicketMessage;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.enums.OrderType;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.tickets.TicketGenerationSupport;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ATMTicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMTicketService.class);

    @Value("${atm.tickets.download-endpoint}")
    private String ticketDownloadUrl;
    @Value("${atm.tickets.download-single-ticket-endpoint}")
    private String singleTicketDownloadUrl;


    private final MsOrderRepository msOrderRepository;
    private final DefaultProducer atmTicketProducer;
    private final TicketGenerationSupport ticketGenerationSupport;
    private final ATMEntityConfiguration atmEntityConfiguration;

    @Autowired
    public ATMTicketService(MsOrderRepository msOrderRepository,
                            @Qualifier("atmTicketProducer") DefaultProducer atmTicketProducer,
                            TicketGenerationSupport ticketGenerationSupport,
                            ATMEntityConfiguration atmEntityConfiguration) {
        this.msOrderRepository = msOrderRepository;
        this.atmTicketProducer = atmTicketProducer;
        this.ticketGenerationSupport = ticketGenerationSupport;
        this.atmEntityConfiguration = atmEntityConfiguration;
    }

    public TicketURLContentDTO getTicketsURLContent(String orderCode) {
        String channelOauthToken = AuthContextUtils.getToken();
        OrderDTO orderDTO = msOrderRepository.getOrderByCode(orderCode);
        if (orderDTO == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        if (!atmEntityConfiguration.getAllowedEntities().contains(orderDTO.getOrderData().getChannelEntityId().longValue())) {
            throw new OneboxRestException(ApiExternalErrorCode.FORBIDDEN_RESOURCE);
        }
        if (!qualifiesForAdditionalTickets(orderDTO)) {
            return new TicketURLContentDTO(false);
        }

        TicketURLContentDTO result;

        String integrityHash = HashUtils.encodeHashIds(orderCode);
        String url = String.format(ticketDownloadUrl, orderCode, integrityHash);
        List<String> individualTicketLinks = orderDTO.getProducts().stream()
                .filter(pr -> !pr.getType().equals(ProductType.PRODUCT))
                .map(p -> String.format(singleTicketDownloadUrl, orderCode, p.getId(), integrityHash))
                .collect(Collectors.toList());
        result = new TicketURLContentDTO(true, url, individualTicketLinks);
        String mergedTicketsURL = ticketGenerationSupport.getMergedTickets(orderDTO, null);
        if (mergedTicketsURL == null) {
            ATMTicketMessage message = new ATMTicketMessage();
            message.setCode(orderCode);
            message.setAuthToken(channelOauthToken);
            try {
                atmTicketProducer.sendMessage(message);
                LOGGER.info("[ATM TICKET][{}] Generating mobile tickets", orderCode);
            } catch (Exception e) {
                LOGGER.error("[ATM TICKET][{}] Error enqueueing ticket generation", orderCode, e);
            }
        }
        return result;
    }


    public RedirectView getTickets(String orderCode, String verificationHash) {
        if (orderCode == null || verificationHash == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        String integrityHash = HashUtils.encodeHashIds(orderCode);
        if (!integrityHash.equals(verificationHash)) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        OrderDTO orderDTO = msOrderRepository.getOrderByCode(orderCode);
        if (orderDTO == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        String url = ticketGenerationSupport.getMergedTickets(orderDTO, ContentDisposition.INLINE);
        LOGGER.info("[ATM TICKET][{}] Downloading PDF mobile", orderCode);
        return new RedirectView(url);
    }

    public RedirectView getTicket(String orderCode, Long itemId, String verificationHash) {
        if (orderCode == null || verificationHash == null || itemId == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        String integrityHash = HashUtils.encodeHashIds(orderCode);
        if (!integrityHash.equals(verificationHash)) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        OrderDTO orderDTO = msOrderRepository.getOrderByCode(orderCode);
        if (orderDTO == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        OrderProductDTO item = orderDTO.getProducts().stream()
                .filter(p -> !ProductType.PRODUCT.equals(p.getType()))
                .filter(p -> itemId.equals(p.getId()))
                .findAny().orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND));

        String url = ticketGenerationSupport.getSingleTickets(orderDTO.getCode(), item, ContentDisposition.INLINE);
        LOGGER.info("[ATM TICKET][{}] Downloading PDF mobile for item {}", orderCode, item.getId());
        return new RedirectView(url);
    }


    private boolean qualifiesForAdditionalTickets(OrderDTO orderDTO) {
        return OrderType.PURCHASE.equals(orderDTO.getStatus().getType());
    }

}
