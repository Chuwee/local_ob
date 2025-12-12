package es.onebox.eci.digitalservices.service;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.eci.digitalservices.converter.DigitalServicesConverter;
import es.onebox.eci.digitalservices.dto.Order;
import es.onebox.eci.service.ChannelsHelper;
import es.onebox.eci.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DigitalServicesService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private ChannelsHelper channelsHelper;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private MasterDataRepository masterDataRepository;

    public List<Order> getOrdersByChannel(ZonedDateTime orderGTE, ZonedDateTime orderLTE, String channelIdentifier, Long limit, Long offset) {
        List<Order> result = new ArrayList<>();
        UserAuthentication userAuthentication = AuthenticationUtils.getUserAuthentication();

        String token = tokenRepository.getApiOrdersToken(userAuthentication.getUser(), userAuthentication.getPassword());
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        List<Long> channelIds = channelDetails.stream().map(ChannelDTO::getId).collect(Collectors.toList());

        List<es.onebox.common.datasources.orders.dto.Order> orders = ordersRepository.getOrders(token, channelIds, orderGTE, orderLTE, true);
        orders = orders.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        List<OrderDetail> orderDetails = orders
                .stream()
                .map(order -> ordersRepository.getById(order.getCode(), token))
                .collect(Collectors.toList());

        List<CountryDTO> countries = masterDataRepository.countries();

        for (OrderDetail orderDetail : orderDetails) {
            EntityDTO entityDTO = entitiesRepository.getByIdCached(getEventEntityId(orderDetail));
            result.add(DigitalServicesConverter.convert(orderDetail, entityDTO, countries));
        }

        return result;
    }

    // We read the first event because the purchase is mono-event always
    private Long getEventEntityId(OrderDetail orderDetail) {
        if (orderDetail.getItems() != null && orderDetail.getItems().get(0) != null) {
            return orderDetail.getItems().get(0).getTicket().getAllocation().getEvent().getEntity().getId();
        }
        return null;
    }
}
