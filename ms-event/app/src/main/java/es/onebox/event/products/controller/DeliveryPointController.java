package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateDeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointsDTO;
import es.onebox.event.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.event.products.dto.UpdateDeliveryPointDTO;
import es.onebox.event.products.service.DeliveryPointService;
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
@RequestMapping(value = DeliveryPointController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryPointController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products-delivery-points";

    private final DeliveryPointService deliveryPointService;

    @Autowired
    public DeliveryPointController(DeliveryPointService deliveryPointService) {
        this.deliveryPointService = deliveryPointService;
    }


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDTO> createDeliveryPoint(@Valid @RequestBody CreateDeliveryPointDTO createDeliveryPointDTO) {
        Long deliveryPointId = deliveryPointService.createDeliveryPoint(createDeliveryPointDTO);
        return new ResponseEntity<>(new IdDTO(deliveryPointId), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{deliveryPointId}")
    public ResponseEntity<DeliveryPointDTO> getDeliveryPoint(@Min(value = 1, message = "deliveryPointId must be above 0") @PathVariable Long deliveryPointId) {
        DeliveryPointDTO productDeliveryPointDTO = deliveryPointService.getDeliveryPoint(deliveryPointId);
        return new ResponseEntity<>(productDeliveryPointDTO, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<DeliveryPointsDTO> searchDeliveryPoint(@Valid SearchDeliveryPointFilterDTO searchDeliveryPointFilterDTO) {
        DeliveryPointsDTO productDeliveryPointsDTO = deliveryPointService.searchDeliveryPoint(searchDeliveryPointFilterDTO);
        return new ResponseEntity<>(productDeliveryPointsDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{deliveryPointId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeliveryPoint(@Min(value = 1, message = "deliveryPointId must be above 0") @PathVariable Long deliveryPointId) {
        deliveryPointService.deleteDeliveryPoint(deliveryPointId);
    }

    @PutMapping(value = "/{deliveryPointId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DeliveryPointDTO> updateDeliveryPoint(@Min(value = 1, message = "deliveryPointId must be above 0") @PathVariable Long deliveryPointId,
                                                                @Valid @RequestBody UpdateDeliveryPointDTO updateDeliveryPointDTO) {
        DeliveryPointDTO productDeliveryPointDTO = deliveryPointService.updateDeliveryPoint(deliveryPointId, updateDeliveryPointDTO);
        return new ResponseEntity<>(productDeliveryPointDTO, HttpStatus.OK);
    }

}
