package es.onebox.event.products.controller;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.AddProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsFilterDTO;
import es.onebox.event.products.dto.UpdateProductEventDTO;
import es.onebox.event.products.service.ProductEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = ProductEventController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductEventController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events";

    private final ProductEventService productEventService;

    @Autowired
    public ProductEventController(ProductEventService productEventService) {
        this.productEventService = productEventService;
    }

    @GetMapping()
    public ResponseEntity<ProductEventsDTO> getProductEvents(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                             @Valid @BindUsingJackson ProductEventsFilterDTO filter) {
        ProductEventsDTO productEvents = productEventService.getProductEvents(productId, filter);
        return new ResponseEntity<>(productEvents, HttpStatus.OK);
    }

    @PostMapping()
    public ProductEventsDTO addProductEvents(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                             @Valid @RequestBody AddProductEventsDTO addProductEventsDTO) {

        return productEventService.addProductEvent(productId, addProductEventsDTO);
    }

    @DeleteMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductEvent(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                   @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {

        productEventService.deleteProductEvent(productId, eventId);
    }


    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductEvent(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                   @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateProductEventDTO updateProductEventDTO) {
        productEventService.updateProductEvent(productId, eventId, updateProductEventDTO);
    }
}
