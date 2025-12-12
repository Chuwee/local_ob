package es.onebox.atm.users.repository;

import es.onebox.atm.users.ATMPromotionsDatasource;
import es.onebox.atm.users.dto.ATMOauthResponse;
import es.onebox.atm.users.dto.ATMUserPromotion;
import es.onebox.atm.users.dto.ChangePromotionStatusResponse;
import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ATMPromotionsRepository {

    private final ATMPromotionsDatasource atmPromotionsDatasource;

    @Autowired
    public ATMPromotionsRepository(ATMPromotionsDatasource atmPromotionsDatasource) {
        this.atmPromotionsDatasource = atmPromotionsDatasource;
    }

    @Cached(key = "ATM_promotions_API_login", expires = 5 * 60)
    public ATMOauthResponse login(@CachedArg String loginUrl, @CachedArg String clientId, @CachedArg String secret) {
        return this.atmPromotionsDatasource.login(loginUrl, clientId, secret);
    }

    public List<ATMUserPromotion> getUserPromotions(String promotionsUrl, String userSalesforceId, String token, String pathParams) {
        return this.atmPromotionsDatasource.getUserPromotions(promotionsUrl, userSalesforceId, token, pathParams);
    }

    public ChangePromotionStatusResponse changeUserPromotionStatus(String promotionsUrl, String userSalesforceId, String salesforcePromotionCode, String status, String token) {
        return this.atmPromotionsDatasource.changeUserPromotionStatus(promotionsUrl, userSalesforceId, salesforcePromotionCode, status, token);
    }

}
