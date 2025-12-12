package es.onebox.fifaqatar.adapter;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.response.CustomersResponse;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.SearchOrderItemsResponse;
import es.onebox.common.datasources.orderitems.dto.request.OrderItemsRequestParameters;
import es.onebox.common.datasources.orderitems.enums.OrderItemState;
import es.onebox.common.datasources.orderitems.repository.OrderItemsRepository;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.core.utils.common.EnvironmentUtils;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.context.AppRequestContext;
import es.onebox.fifaqatar.error.InvalidAuthException;
import es.onebox.fifaqatar.error.InvalidSignatureException;
import es.onebox.fifaqatar.error.TicketDetailNotFoundException;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import es.onebox.fifaqatar.adapter.dto.response.TicketsResponse;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderDetailResponse;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketDetailResponse;
import es.onebox.fifaqatar.adapter.gif.GifRepository;
import es.onebox.fifaqatar.adapter.mapper.FifaQatarResponseMapper;
import es.onebox.fifaqatar.adapter.mapper.MapperContext;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FifaQatarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarService.class);

    private final TokenRepository tokenRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final OrdersRepository ordersRepository;
    private final FifaQatarResponseMapper ticketsResponse;
    private final FifaQatarMappingHelper fifaQatarMappingHelper;
    private final FifaQatarCustomerHelper customerHelper;
    private final ConfigurableEnvironment env;
    private final GifRepository gifRepository;

    @Autowired
    public FifaQatarService(TokenRepository tokenRepository,
                            OrderItemsRepository orderItemsRepository,
                            OrdersRepository ordersRepository,
                            FifaQatarResponseMapper ticketsResponse,
                            FifaQatarMappingHelper fifaQatarMappingHelper,
                            FifaQatarCustomerHelper customerHelper,
                            ConfigurableEnvironment env,
                            GifRepository gifRepository) {
        this.tokenRepository = tokenRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.ordersRepository = ordersRepository;
        this.ticketsResponse = ticketsResponse;
        this.fifaQatarMappingHelper = fifaQatarMappingHelper;
        this.customerHelper = customerHelper;
        this.env = env;
        this.gifRepository = gifRepository;
    }

    public TicketsResponse tickets(Integer page) {
        MapperContext mapperContext = new MapperContext();
        mapperContext.setDictionary(AppRequestContext.getDictionary());
        mapperContext.setCurrentLang(AppRequestContext.getCurrentLang());
        FifaQatarConfigDocument config = AppRequestContext.getMainConfig();
        MeResponseDTO me = AppRequestContext.getCurrentUser();
        Customer customer = customerHelper.assignCustomerId(me, config.getEntityId());
        mapperContext.setCurrentCustomer(customer);
        String accessToken = this.tokenRepository.getOneboxClientToken(config.getApiKey());
        OrderItemsRequestParameters parameters = new OrderItemsRequestParameters();
        parameters.setCustomerId(List.of(customer.getUserId()));
        parameters.setLimit(500);
        parameters.setState(List.of(OrderItemState.PURCHASE));

//        SearchOrderItemsResponse orderItems = this.orderItemsRepository.getOrderItems(accessToken, parameters);
//        if (orderItems.getData().isEmpty()) {
//            //Dirty hack to prevent no results after sign up
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            orderItems = this.orderItemsRepository.getOrderItems(accessToken, parameters);
//        }
        var items = getItems(accessToken, parameters);
        return ticketsResponse.map(filterItems(items, config), mapperContext);
    }

    private List<OrderItem> getItems(String accessToken, OrderItemsRequestParameters parameters) {
        Long offset = 0L;
        parameters.setOffset(offset.intValue());
        var items = new ArrayList<OrderItem>();
        while (offset != null) {
            parameters.setOffset(offset.intValue());
            SearchOrderItemsResponse orderItems = this.orderItemsRepository.getOrderItems(accessToken, parameters);
            items.addAll(orderItems.getData());
            offset = orderItems.getMetadata().nextOffset();
        }
        return items;
    }


    private List<OrderItem> filterItems(SearchOrderItemsResponse orderItems, FifaQatarConfigDocument configDocument) {
        return filterItems(orderItems.getData(), configDocument);
    }

    private List<OrderItem> filterItems(List<OrderItem> items, FifaQatarConfigDocument configDocument) {
        return items.stream()
                .filter(item -> item.getPack() == null
                        && !configDocument.getBlacklistedEventIds().contains(item.getTicket().getAllocation().getEvent().getId())
                        && !configDocument.getBlacklistedSessionIds().contains(item.getTicket().getAllocation().getSession().getId())
                ).collect(Collectors.toList());
    }

    public TicketDetailResponse ticketDetail(Integer ticketId) {
        FifaQatarConfigDocument config = AppRequestContext.getMainConfig();
        MeResponseDTO me = AppRequestContext.getCurrentUser();
        CustomersResponse customersResponse = customerHelper.getCustomerByOriginId(String.valueOf(me.getId()), config.getEntityId());
        if (customersResponse.getData().size() != 1) {
            throw new InvalidAuthException();
        }
        Customer customer = customerHelper.getCustomer(customersResponse.getData().get(0).getId());
        MapperContext mapperContext = new MapperContext();
        String host = AppRequestContext.getHost();
        String protocol = AppRequestContext.getProtocol();
        if (!EnvironmentUtils.isLocal(env)) {
            host = config.getBarcodeUrlHost();
        }
        mapperContext.setBarcodeUrl(protocol + "://" + host + "/fifa-qatar/api/qr-code");

        mapperContext.setDictionary(AppRequestContext.getDictionary());
        mapperContext.setCurrentLang(AppRequestContext.getCurrentLang());
        mapperContext.setCurrentCustomer(customer);
        mapperContext.setMainConfig(config);
        mapperContext.setAccountProfileUrl(config.getAccountProfileUrl());
        mapperContext.setAccountTicketsUrl(config.getAccountTicketsUrl());
        mapperContext.setAccountTicketsTransferUrl(config.getAccountTicketsTransferUrl());
        mapperContext.setAccountSecMktUrl(config.getAccountSecMktUrl());
        mapperContext.setBarcodeSigningKey(config.getBarcodeSigningKey());
        TicketDetailMapping byTicketId = fifaQatarMappingHelper.getByTicketId(ticketId);
        if (byTicketId == null) {
            throw new TicketDetailNotFoundException();
        }
        String customerToken = tokenRepository.getCustomerToken(customer.getApiKey(), config.getEntityId().longValue());
        mapperContext.setCustomerAccessToken(customerToken);

        String accessToken = this.tokenRepository.getOneboxClientToken(config.getApiKey());
        OrderItemsRequestParameters parameters = new OrderItemsRequestParameters();
        parameters.setCustomerId(List.of(customer.getUserId()));
        parameters.setLimit(500);
        parameters.setState(List.of(OrderItemState.PURCHASE));
        parameters.setSessionId(List.of(byTicketId.getSessionId().intValue()));
        parameters.setOrderCode(List.of(byTicketId.getOrderCode()));

        SearchOrderItemsResponse orderItems = this.orderItemsRepository.getOrderItems(accessToken, parameters);

        return ticketsResponse.mapTicketDetail(filterItems(orderItems, config), mapperContext);
    }

    public OrderDetailResponse orderDetail(Integer orderId) {
        MapperContext mapperContext = new MapperContext();
        mapperContext.setDictionary(AppRequestContext.getDictionary());
        mapperContext.setCurrentLang(AppRequestContext.getCurrentLang());
        FifaQatarConfigDocument config = AppRequestContext.getMainConfig();
        MeResponseDTO me = AppRequestContext.getCurrentUser();
        CustomersResponse customersResponse = customerHelper.getCustomerByOriginId(String.valueOf(me.getId()), config.getEntityId());
        if (customersResponse.getData().size() != 1) {
            return null;
        }

        String orderCode = fifaQatarMappingHelper.getByOrderId(orderId);
        if (orderCode == null) {
            return null;
        }
        String accessToken = this.tokenRepository.getOneboxClientToken(config.getApiKey());
        OrderDetail order = ordersRepository.getById(orderCode, accessToken);

        return ticketsResponse.mapOrderDetail(orderId, order, mapperContext);
    }

    public ResponseEntity<byte[]> getQrImage(String code, String signature) throws IOException {
        FifaQatarConfigDocument config = AppRequestContext.getMainConfig();
        var generatedSignature = GeneratorUtils.getHashSHA256(code + config.getBarcodeSigningKey());
        if (!generatedSignature.equals(signature)) {
            throw new InvalidSignatureException("Invalid Signature");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr.gif")
                .contentType(MediaType.IMAGE_GIF)
                .body(gifRepository.generateGif(code));
    }

}
