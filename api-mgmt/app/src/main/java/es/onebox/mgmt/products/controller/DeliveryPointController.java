package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.CreateDeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointsDTO;
import es.onebox.mgmt.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.mgmt.products.dto.UpdateDeliveryPointDTO;
import es.onebox.mgmt.products.service.DeliveryPointService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = DeliveryPointController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryPointController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products-delivery-points";

    private static final String AUDIT_COLLECTION = "PRODUCTS-DELIVERY-POINTS";

    private final DeliveryPointService deliveryPointService;

    @Autowired
    public DeliveryPointController(DeliveryPointService deliveryPointService) {
        this.deliveryPointService = deliveryPointService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createDeliveryPoint(@Valid @RequestBody CreateDeliveryPointDTO createProductDeliveryPointDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        Long productDeliveryPointId = deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
        return new ResponseEntity<>(new IdDTO(productDeliveryPointId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{deliveryPointId}")
    public DeliveryPointDTO getDeliveryPoint(@PathVariable @Min(value = 1, message = "deliveryPointId must be above 0") Long deliveryPointId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return deliveryPointService.getDeliveryPoint(deliveryPointId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public DeliveryPointsDTO searchDeliveryPoint(@BindUsingJackson @Valid SearchDeliveryPointFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return deliveryPointService.searchDeliveryPoint(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{deliveryPointId}")
    public void deleteDeliveryPoint(@PathVariable @Min(value = 1, message = "deliveryPointId must be above 0") Long deliveryPointId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        deliveryPointService.deleteDeliveryPoint(deliveryPointId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{deliveryPointId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeliveryPointDTO> changeDeliveryPoint(@PathVariable @Min(value = 1, message = "deliveryPointId must be above 0") Long deliveryPointId, @Valid @RequestBody UpdateDeliveryPointDTO updateProductDeliveryPointDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        DeliveryPointDTO productDeliveryPointDTO = deliveryPointService.updateDeliveryPoint(deliveryPointId, updateProductDeliveryPointDTO);
        return new ResponseEntity<>(productDeliveryPointDTO, HttpStatus.OK);
    }

}
