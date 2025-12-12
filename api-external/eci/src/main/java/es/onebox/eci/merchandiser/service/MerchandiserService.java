package es.onebox.eci.merchandiser.service;

import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.repository.OrderItemsRepository;
import es.onebox.eci.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MerchandiserService {
    private final OrderItemsRepository orderItemsRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public MerchandiserService(OrderItemsRepository orderItemsRepository, TokenRepository tokenRepository) {
        this.orderItemsRepository = orderItemsRepository;
        this.tokenRepository = tokenRepository;
    }

    public List<OrderItem> getOrderItems(ZonedDateTime from, ZonedDateTime to) {
        UserAuthentication userAuthentication = AuthenticationUtils.getUserAuthentication();

        String token = tokenRepository.getApiOrdersToken(userAuthentication.getUser(), userAuthentication.getPassword());

        return orderItemsRepository.getOrderItems(token, from, to);
    }
}
