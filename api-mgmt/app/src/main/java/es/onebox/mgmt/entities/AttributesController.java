package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.StringMapDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.entities.dto.CreateAttributeRequestDTO;
import jakarta.validation.Valid;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

@RestController
@RequestMapping(
        value = AttributesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AttributesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/attributes";

    private static final String AUDIT_COLLECTION = "ATTRIBUTES";
    private static final String AUDIT_SUBCOLLECTION_VALUES = "VALUES";

    @Autowired
    private AttributesService attributesService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{attributeId}")
    public AttributeDTO getAttribute(@PathVariable long entityId, @PathVariable long attributeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return attributesService.getAttribute(entityId, attributeId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<AttributeDTO> getAttributes(@PathVariable long entityId,
                                            @BindUsingJackson @Valid AttributeSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return attributesService.getAttributes(entityId, filter);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createAttribute(@PathVariable long entityId, @RequestBody CreateAttributeRequestDTO attribute) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        ConverterUtils.checkField(attribute.getName(), "name");

        Long attributeId = attributesService.createAttribute(entityId, attribute);

        return new ResponseEntity<>(new IdDTO(attributeId), HttpStatus.CREATED);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{attributeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> updateAttribute(@PathVariable long entityId, @PathVariable Long attributeId, @RequestBody @Valid AttributeDTO attribute) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (attribute.getId() != null && !attribute.getId().equals(attributeId)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "attributeId is different between pathVariable and requestBody", null);
        }

        attributesService.updateAttribute(entityId, attributeId, attribute);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{attributeId}")
    public ResponseEntity<Serializable> deleteAttribute(@PathVariable long entityId, @PathVariable Long attributeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        attributesService.deleteAttribute(entityId, attributeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{attributeId}/values")
    public ResponseEntity<Serializable> addValue(@PathVariable long entityId, @PathVariable Long attributeId, @RequestBody StringMapDTO value) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_VALUES, AuditTag.AUDIT_ACTION_ADD);

        if (MapUtils.isEmpty(value)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "value is mandatory", null);
        }

        attributesService.addValue(entityId, attributeId, value);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{attributeId}/values/{valueId}")
    public ResponseEntity<Serializable> updateValue(@PathVariable long entityId, @PathVariable Long attributeId,
                                                    @PathVariable Long valueId, @RequestBody StringMapDTO value) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_VALUES, AuditTag.AUDIT_ACTION_UPDATE);

        if (MapUtils.isEmpty(value)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "value is mandatory", null);
        }

        attributesService.updateValue(entityId, attributeId, valueId, value);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{attributeId}/values/{valueId}")
    public ResponseEntity<Serializable> deleteValue(@PathVariable long entityId, @PathVariable Long attributeId, @PathVariable Long valueId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_VALUES, AuditTag.AUDIT_ACTION_DELETE);

        attributesService.deleteValue(entityId, attributeId, valueId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
