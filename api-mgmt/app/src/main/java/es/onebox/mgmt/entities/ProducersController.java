package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CreateProducerRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerDTO;
import es.onebox.mgmt.entities.dto.ProducerSearchFilter;
import es.onebox.mgmt.entities.dto.SearchProducersResponse;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(
        value = ProducersController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProducersController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/producers";

    private static final String AUDIT_COLLECTION = "PRODUCERS";

    @Autowired
    private ProducersService producersService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{producerId}")
    public ProducerDTO getProducer(@PathVariable Long producerId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        ConverterUtils.checkField(producerId, "producerId");
        return producersService.getProducer(producerId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public SearchProducersResponse getProducers(@BindUsingJackson @Valid ProducerSearchFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return producersService.getProducers(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createProducer(@RequestBody @Valid CreateProducerRequestDTO producer) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        if (CommonUtils.isBlank(producer.getName())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "name is mandatory", null);
        }
        if (CommonUtils.isBlank(producer.getSocialReason())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "social_reason is mandatory", null);
        }
        if (CommonUtils.isBlank(producer.getNif())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "nif is mandatory", null);
        }

        Long producerId = producersService.create(producer);

        return new ResponseEntity<>(new IdDTO(producerId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{producerId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> updateProducer(@PathVariable Long producerId, @RequestBody @Valid ProducerDTO producerData) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (producerData.getId() != null && !producerData.getId().equals(producerId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "producer_id is different between pathVariable and requestBody", null);
        }
        producersService.update(producerId, producerData);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{producerId}")
    public ResponseEntity<Serializable> deleteProducer(@PathVariable Long producerId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        producersService.delete(producerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
