package es.onebox.event.products.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.event.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.event.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.event.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.event.products.service.ProductDeliveryPointRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;


@RestController
@RequestMapping(value = ProductDeliveryPointRelationController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductDeliveryPointRelationController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/delivery-points";

    private final ProductDeliveryPointRelationService productDeliveryPointRelationService;

    @Autowired
    public ProductDeliveryPointRelationController(ProductDeliveryPointRelationService productDeliveryPointRelationService) {
        this.productDeliveryPointRelationService = productDeliveryPointRelationService;
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void upsertProductDeliveryPointRelation(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                   @Valid @RequestBody UpsertProductDeliveryPointRelationDTO upsertProductDeliveryPointRelationDTO) {
        productDeliveryPointRelationService.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelationDTO);
    }

    @GetMapping(value = "/{deliveryPointId}")
    public ResponseEntity<ProductDeliveryPointRelationDTO> getProductDeliveryPointRelation(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                           @Min(value = 1, message = "deliveryPointId must be above 0") @PathVariable Long deliveryPointId) {
        ProductDeliveryPointRelationDTO productDeliveryPointRelationDTO = productDeliveryPointRelationService.getProductDeliveryPointRelation(productId, deliveryPointId);
        return new ResponseEntity<>(productDeliveryPointRelationDTO, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ProductDeliveryPointsRelationsDTO> searchProductDeliveryPointRelations(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                                                                 @Valid SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointRelationFilterDTO) {
        ProductDeliveryPointsRelationsDTO productDeliveryPointsRelationsDTO = productDeliveryPointRelationService.searchProductDeliveryPoinRelations(productId, searchProductDeliveryPointRelationFilterDTO);
        return new ResponseEntity<>(productDeliveryPointsRelationsDTO, HttpStatus.OK);
    }

}
