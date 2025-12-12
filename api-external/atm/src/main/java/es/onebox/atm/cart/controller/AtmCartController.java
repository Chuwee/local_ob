package es.onebox.atm.cart.controller;


import es.onebox.atm.cart.dto.ATMFriendCodeCartRequest;
import es.onebox.atm.cart.dto.ATMMemberCartRequest;
import es.onebox.atm.cart.service.AtmCartService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/cart")
public class AtmCartController {

    private final AtmCartService atmCartService;
    private static final String CART_TOKEN_HEADER = "ob-order-id";

    @Autowired
    public AtmCartController(AtmCartService atmCartService){
        this.atmCartService = atmCartService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PutMapping(value = "/register-football-member-discount")
    public void registerFootballMemberDiscount(@RequestHeader(name = CART_TOKEN_HEADER) String cartToken,
                                               @RequestBody @Valid @NotNull ATMMemberCartRequest atmCartRequest) {
        atmCartService.registerFootballMemberDiscount(cartToken, atmCartRequest);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PutMapping(value = "/add-tour-member-ticket-and-promo")
    public void addTourMemberTicket(@RequestHeader(name = CART_TOKEN_HEADER) String cartToken,
                                    @RequestBody @Valid @NotNull ATMMemberCartRequest atmCartRequest) {
        atmCartService.addTourMemberTicket(cartToken, atmCartRequest);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PutMapping(value = "/add-football-friend-code-ticket-and-promo")
    public void registerFootballFriendDiscount(@RequestHeader(name = CART_TOKEN_HEADER) String cartToken,
                                                              @RequestBody @Valid @NotNull ATMFriendCodeCartRequest atmCartRequest) {
        atmCartService.registerFootballFriendDiscount(cartToken, atmCartRequest);
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @PutMapping(value = "/add-tour-friend-code-ticket-and-promo")
    public void addTourFriendTicket(@RequestHeader(name = CART_TOKEN_HEADER) String cartToken,
                                    @RequestBody @Valid @NotNull ATMFriendCodeCartRequest atmCartRequest) {
        atmCartService.addTourFriendTicket(cartToken, atmCartRequest);
    }

}

