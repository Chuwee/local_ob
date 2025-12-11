package es.onebox.event.products.controller;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointsDTO;
import es.onebox.event.products.service.ProductSessionDeliveryPointService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

@RestController
@RequestMapping(value = ProductSessionDeliveryPointController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSessionDeliveryPointController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}/session-delivery-points";

    private final ProductSessionDeliveryPointService productSessionDeliveryPointService;

    @Autowired
    public ProductSessionDeliveryPointController(ProductSessionDeliveryPointService productSessionDeliveryPointService) {
        this.productSessionDeliveryPointService = productSessionDeliveryPointService;
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductSessionDeliveryPointsDTO> updateProductSessionDeliveryPoint(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                             @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
                                                                                             @Valid @RequestBody UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointDTOS) {
        ProductSessionDeliveryPointsDTO productSessionDeliveryPoints = productSessionDeliveryPointService.updateProductSessionDeliveryPoint(productId, eventId, updateProductSessionDeliveryPointDTOS);
        return new ResponseEntity<>(productSessionDeliveryPoints, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ProductSessionDeliveryPointsDTO> getProductSessionDeliveryPoint(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                          @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
                                                                                          @BindUsingJackson @Valid ProductSessionDeliveryPointsFilterDTO filter) {
        ProductSessionDeliveryPointsDTO productSessionDeliveryPoints = productSessionDeliveryPointService.getProductSessionDeliveryPoints(productId, eventId, filter);
        return new ResponseEntity<>(productSessionDeliveryPoints, HttpStatus.OK);
    }

}
