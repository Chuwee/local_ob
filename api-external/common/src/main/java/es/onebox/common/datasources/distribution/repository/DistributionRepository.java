package es.onebox.common.datasources.distribution.repository;

import es.onebox.common.datasources.distribution.ApiDistributionDatasource;
import es.onebox.common.datasources.distribution.dto.AddSeatsDTO;
import es.onebox.common.datasources.distribution.dto.InvitationRequest;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.PresalesRequest;
import es.onebox.common.datasources.distribution.dto.PromotionRequestType;
import es.onebox.common.datasources.distribution.dto.RenewalSeats;
import es.onebox.common.datasources.distribution.dto.SeatsAutoRequest;
import es.onebox.common.datasources.distribution.dto.attendee.ItemAttendees;
import es.onebox.common.datasources.distribution.dto.deliverymethods.DeliveryMethodsRequestDTO;
import es.onebox.common.datasources.distribution.dto.deliverymethods.PreConfirmRequestDTO;
import es.onebox.common.datasources.distribution.dto.order.ConfirmRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DistributionRepository {

    private final ApiDistributionDatasource apiDistributionDatasource;

    @Autowired
    public DistributionRepository(ApiDistributionDatasource apiDistributionDatasource) {
        this.apiDistributionDatasource = apiDistributionDatasource;
    }

    public void addCartPromotion(String channelOauthToken, String cartToken, Long promotionId, String userSalesforceId,
                                 String promotionCode, String discountType, Double discountValue, String sessionPreviewToken) {
        apiDistributionDatasource.addPromotion(channelOauthToken, cartToken, promotionId, null, PromotionRequestType.ORDER,
                userSalesforceId, promotionCode, discountType, discountValue, sessionPreviewToken);
    }

    public void addItemPromotion(String channelOauthToken, String cartToken, Long promotionId, List<Long> itemIds) {
        apiDistributionDatasource.addPromotion(channelOauthToken, cartToken, promotionId, itemIds, PromotionRequestType.ITEM,
                null, null, null, null, null);
    }

    public void addCollective(String channelOauthToken, String cartToken, Long collectiveId, String code, String pin) {
        apiDistributionDatasource.addCollective(channelOauthToken, cartToken, collectiveId, code, pin);
    }

    public OrderResponse createOrder(String token, String language) {
        return apiDistributionDatasource.createOrder(token, language);
    }

    public OrderResponse addSeats(String token, String orderId, AddSeatsDTO addSeats, String previewToken) {
        return apiDistributionDatasource.addSeats(token, orderId, addSeats, previewToken);
    }

    public OrderResponse releaseSeats(String token, String orderId, Set<Long> itemIds) {
        return apiDistributionDatasource.releaseSeats(token, orderId, itemIds);
    }

    public OrderResponse addSeatsAuto(String token, String orderId, SeatsAutoRequest seatsAutoDTO, String previewToken) {
        return apiDistributionDatasource.addSeatsAuto(token, orderId, seatsAutoDTO, previewToken);
    }

    public OrderResponse setDeliveryMethods(String token, String orderId, DeliveryMethodsRequestDTO deliveryMethod) {
        return apiDistributionDatasource.setDeliveryMethods(token, orderId, deliveryMethod);
    }

    public OrderResponse addBuyerData(String token, String orderId, Map<String, Object> buyerData) {
        return apiDistributionDatasource.addBuyerData(token, orderId, buyerData);
    }

    public OrderResponse preConfirm(String token, String orderId, PreConfirmRequestDTO deliveryMethod) {
        return apiDistributionDatasource.preConfirm(token, orderId, deliveryMethod);
    }

    public OrderResponse confirm(String token, String orderId, ConfirmRequest confirmRequest) {
        return apiDistributionDatasource.confirm(token, orderId, confirmRequest);
    }

    public OrderResponse addItemAttendees(String token, String orderId, ItemAttendees body) {
        return apiDistributionDatasource.addItemAttendees(token, orderId, body);
    }

    public OrderResponse addRenewalSeats(String token, String orderId, RenewalSeats renewalSeats) {
        return apiDistributionDatasource.addRenewalSeats(token, orderId, renewalSeats);
    }

    public OrderResponse addInvitations(String token, String orderId, InvitationRequest invitationRequest) {
        return apiDistributionDatasource.addInvitations(token, orderId, invitationRequest);
    }

    public OrderResponse validatePresales(String token, PresalesRequest presale, String orderId) {
        return apiDistributionDatasource.validatePresales(token, presale, orderId);
    }

}
