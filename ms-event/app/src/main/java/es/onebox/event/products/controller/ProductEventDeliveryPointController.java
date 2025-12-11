package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductEventDeliveryPointsDTO;
import es.onebox.event.products.dto.UpdateProductEventDeliveryPointsDTO;
import es.onebox.event.products.service.ProductEventDeliveryPointService;
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


@RestController
@RequestMapping(value = ProductEventDeliveryPointController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductEventDeliveryPointController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/events/{eventId}/delivery-points";

    private final ProductEventDeliveryPointService productEventDeliveryPointService;

    @Autowired
    public ProductEventDeliveryPointController(ProductEventDeliveryPointService productEventDeliveryPointService) {
        this.productEventDeliveryPointService = productEventDeliveryPointService;
    }


    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductEventDeliveryPointsDTO> updateProductEventDeliveryPoint(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId,
                                                @Valid @RequestBody UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO) {
        ProductEventDeliveryPointsDTO productEventDeliveryPoints = productEventDeliveryPointService.updateProductEventDeliveryPoint(productId, eventId, updateProductEventDeliveryPointsDTO);
        return new ResponseEntity<>(productEventDeliveryPoints, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ProductEventDeliveryPointsDTO> getProductEventDeliveryPoint(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                      @Min(value = 1, message = "eventId must be above 0") @PathVariable Long eventId) {
        ProductEventDeliveryPointsDTO productEventDeliveryPoints = productEventDeliveryPointService.getProductEventDeliveryPoints(productId, eventId);
        return new ResponseEntity<>(productEventDeliveryPoints, HttpStatus.OK);
    }

}
