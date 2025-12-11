package es.onebox.event.products.controller;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.products.dto.ProductSessionsDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingFilterDTO;
import es.onebox.event.products.dto.UpdateProductSessionDTO;
import es.onebox.event.products.dto.UpdateProductSessionsDTO;
import es.onebox.event.products.service.ProductSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = ProductSessionController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSessionController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}";

    private final ProductSessionService productSessionService;

    @Autowired
    public ProductSessionController(ProductSessionService productSessionService) {
        this.productSessionService = productSessionService;
    }

    @GetMapping("/publishing-sessions")
    public ResponseEntity<ProductSessionsPublishingDTO> getPublishingSessions(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
            @Valid @BindUsingJackson ProductSessionsPublishingFilterDTO filter) {
        ProductSessionsPublishingDTO productSessions = productSessionService.getPublishingSessions(productId, eventId, filter);
        return new ResponseEntity<>(productSessions, HttpStatus.OK);
    }

    @PutMapping("/publishing-sessions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessions(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
            @RequestBody @Valid @NotNull UpdateProductSessionsDTO request) {
        productSessionService.updatePublishingSessions(productId, eventId, request);
    }

    @GetMapping("/sessions")
    public ResponseEntity<ProductSessionsDTO> getProductSessions(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
            @BindUsingJackson @Valid ProductSessionSearchFilter filter) {
        ProductSessionsDTO productSessions = productSessionService.getProductSessions(productId, eventId, filter);
        return new ResponseEntity<>(productSessions, HttpStatus.OK);
    }

    @PutMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessions(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
            @Min(value = 1, message = "eventId must be above 0") @PathVariable Long sessionId,
            @RequestBody @Valid @NotNull UpdateProductSessionDTO request) {
        productSessionService.updateProductSession(productId, eventId, sessionId, request);
    }


}
