package es.onebox.mgmt.events.ratesgroup;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroupType;
import es.onebox.mgmt.events.dto.CreateEventRatesGroupRequestDTO;
import es.onebox.mgmt.events.dto.RateGroupDTO;
import es.onebox.mgmt.events.dto.UpdateRateGroupDTO;
import es.onebox.mgmt.events.dto.UpdateRateGroupListDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(EventRatesGroupController.BASE_URI)
public class EventRatesGroupController {

    private static final String AUDIT_COLLECTION = "EVENT_RATES_GROUP";

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/rates-group";

    @Autowired
    private EventRatesGroupService eventRatesGroupService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RateGroupDTO> getEventRatesGroup(@PathVariable Long eventId, @RequestParam(required = false) RateGroupType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventRatesGroupService.getRatesGroup(eventId, type);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createEventRatesGroup(@PathVariable Long eventId,
                                 @RequestBody @Valid CreateEventRatesGroupRequestDTO reqDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        if (reqDTO.getName() == null || reqDTO.getName().isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("Name is mandatory")
                    .build();
        }
        return eventRatesGroupService.createRateGroups(eventId, reqDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> updateEventRatesGroup(@PathVariable Long eventId,
                                                        @RequestBody @Valid UpdateRateGroupListDTO rates) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        eventRatesGroupService.updateRatesGroup(eventId, rates.getRatesGroup());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{rateGroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> updateEventRatesGroup(@PathVariable Long eventId, @PathVariable Long rateGroupId,
                                                        @RequestBody @Valid UpdateRateGroupDTO rate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        eventRatesGroupService.updateRateGroup(eventId, rateGroupId, rate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{rateGroupId}")
    public ResponseEntity<Serializable> deleteEventRatesGroup(@PathVariable Long eventId, @PathVariable Long rateGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        eventRatesGroupService.deleteRateGroup(eventId, rateGroupId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
