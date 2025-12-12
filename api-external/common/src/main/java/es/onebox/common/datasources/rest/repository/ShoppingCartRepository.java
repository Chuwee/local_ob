package es.onebox.common.datasources.rest.repository;

import es.onebox.common.datasources.rest.AppRestDatasource;
import es.onebox.common.datasources.rest.dto.ns.data_query.session.SessionInfo;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.PromotionalCodeApplicableGroup;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.ShoppingCart;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.UserApplicableGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartRepository {

    @Autowired
    private AppRestDatasource appRestDatasource;

    public SessionInfo getSessionInfo(Long sessionId) {
        return appRestDatasource.getSessionInfo(sessionId);
    }

    public void validateCart(String cartToken) {
        appRestDatasource.validateCart(cartToken);
    }

    public UserApplicableGroup getUserGroupValidation(String cartToken, Long idGroup, String username, String password, Long sessionId) {
        return appRestDatasource.usernameGroupValidation(cartToken, idGroup, username, password, sessionId);
    }

    public ShoppingCart releaseAllItems(String cartToken){
        return appRestDatasource.releaseAllSeats(cartToken);
    }

    public ShoppingCart addIndividualActivitySeats(Long idSession,
                                                   Long idActivityTicketType,
                                                   Integer numSeats,
                                                   String token) {
        return appRestDatasource.addActivitySeats(token, idSession, numSeats, idActivityTicketType);
    }

    public PromotionalCodeApplicableGroup getPromotionalCodeGroupWithGroupId(String token, String promotionalCode, Long idGroup) {
        return appRestDatasource.promotionalCodeGroup(token, idGroup, promotionalCode);
    }
}
