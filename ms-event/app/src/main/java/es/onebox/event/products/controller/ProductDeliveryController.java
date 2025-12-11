package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductDeliveryDTO;
import es.onebox.event.products.service.ProductDeliveryService;
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
@RequestMapping(value = ProductDeliveryController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductDeliveryController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/delivery";

    private final ProductDeliveryService productDeliveryService;

    @Autowired
    public ProductDeliveryController(ProductDeliveryService productDeliveryService) {
        this.productDeliveryService = productDeliveryService;
    }


    @GetMapping()
    public ResponseEntity<ProductDeliveryDTO> getProductDelivery(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductDeliveryDTO productDeliveryDTO = productDeliveryService.getProductDelivery(productId);
        return new ResponseEntity<>(productDeliveryDTO, HttpStatus.OK);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductDeliveryDTO> updateProductDelivery(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                    @Valid @RequestBody ProductDeliveryDTO productDeliveryDTO) {
        ProductDeliveryDTO result = productDeliveryService.updateProductDelivery(productId, productDeliveryDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
