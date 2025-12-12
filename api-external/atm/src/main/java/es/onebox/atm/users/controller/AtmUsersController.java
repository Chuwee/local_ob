package es.onebox.atm.users.controller;

import es.onebox.atm.users.dto.ATMAddPromotionRequest;
import es.onebox.atm.users.dto.ATMUserPromotionDTO;
import es.onebox.atm.users.service.AtmUsersService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/users")
public class AtmUsersController {

    private final AtmUsersService atmUsersService;

    @Autowired
    public AtmUsersController(AtmUsersService atmUsersService) {
        this.atmUsersService = atmUsersService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PostMapping(value = "/{userSalesforceId}/presale/{sessionId}")
    public OrderResponse validateMemberPresale(@PathVariable("userSalesforceId") String userSalesforceId,
                                               @PathVariable("sessionId") Long sessionId,
                                               @RequestHeader(value = "ob-order-id") String orderId) {
        return atmUsersService.validateMemberPresale(userSalesforceId, orderId, sessionId);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @GetMapping(value = "/{tutorId}/related-users")
    public List<AuthVendorUserData> getRelatedUsers(@PathVariable("tutorId") String tutorId) {
        return atmUsersService.getRelatedUsers(tutorId);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @GetMapping(value = "/{userSalesforceId}/promotions")
    public List<ATMUserPromotionDTO> getUserPromotions(@PathVariable("userSalesforceId") String userSalesforceId) {
        return atmUsersService.getUserPromotions(userSalesforceId);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PutMapping(value = "/{userSalesforceId}/add-promotion/{promotionCode}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPromotion(@PathVariable("userSalesforceId") String userSalesforceId,
                             @PathVariable("promotionCode") String promotionCode,
                             @RequestBody @Valid @NotNull ATMAddPromotionRequest atmAddPromotionRequest,
                             @RequestHeader(value = "Session-Preview-Token", required = false) String sessionPreviewToken) {
        atmUsersService.addPromotion(userSalesforceId, promotionCode, atmAddPromotionRequest, sessionPreviewToken);
    }
}

