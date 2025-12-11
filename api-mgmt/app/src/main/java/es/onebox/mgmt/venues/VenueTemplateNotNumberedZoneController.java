package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.BaseNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.CloneNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneBulkDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.NotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZonesBulkDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.venues.service.NotNumberedZoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(VenueTemplateNotNumberedZoneController.BASE_URI)
public class VenueTemplateNotNumberedZoneController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/not-numbered-zones";

    private static final String AUDIT_COLLECTION = "VENUETEMPLATES_NN_ZONES";
    private static final String AUDIT_SUBCOLLECTION_TAGS = "VENUETEMPLATES_TAG";
    private static final String AUDIT_SUBCOLLECTION_ASSIGN_TAGS = "VENUETEMPLATES_ASSIGN_TAG";

    private final NotNumberedZoneService notNumberedZoneService;

    @Autowired
    public VenueTemplateNotNumberedZoneController(NotNumberedZoneService notNumberedZoneService) {
        this.notNumberedZoneService = notNumberedZoneService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR})
    @RequestMapping(method = PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotNumberedZoneTags(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @RequestBody VenueTagNotNumberedZoneRequestDTO[] notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_ASSIGN_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        notNumberedZoneService.updateNotNumberedZoneTags(venueTemplateId, notNumberedZone);

    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET)
    public List<BaseNotNumberedZoneDTO> getVenueTemplateNotNumberedZones(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return notNumberedZoneService.getNotNumberedZones(venueTemplateId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.GET, value = "/{zoneId}")
    public NotNumberedZoneDTO getVenueTemplateNotNumberedZone(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @PathVariable(value = "zoneId") Long zoneId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return notNumberedZoneService.getNotNumberedZone(venueTemplateId, zoneId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createNotNumberedZone(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @RequestBody @Valid CreateNotNumberedZoneDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        return notNumberedZoneService.createNotNumberedZone(venueTemplateId, body);

    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST, value = "/{zoneId}/clone")
    public ResponseEntity<IdDTO> cloneNotNumberedZone(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @PathVariable(value = "zoneId") Long zoneId, @RequestBody @Valid CloneNotNumberedZoneDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        Long id = notNumberedZoneService.cloneNotNumberedZone(venueTemplateId, zoneId, requestDTO);

        return new ResponseEntity<>(new IdDTO(id), HttpStatus.CREATED);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/{notNumberedZoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotNumberedZone(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @PathVariable(value = "notNumberedZoneId") Long notNumberedZoneId, @RequestBody UpdateNotNumberedZoneDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        notNumberedZoneService.updateNotNumberedZone(venueTemplateId, notNumberedZoneId, body);

    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.PUT, value = "/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotNumberedZones(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @RequestBody UpdateNotNumberedZonesBulkDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);
        notNumberedZoneService.updateNotNumberedZones(venueTemplateId, body);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<IdDTO> createNotNumberedZones(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @RequestBody @Valid CreateNotNumberedZoneBulkDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);
        return notNumberedZoneService.createNotNumberedZones(venueTemplateId, body);

    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{notNumberedZoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotNumberedZone(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
            @PathVariable(value = "notNumberedZoneId") Long notNumberedZoneId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        notNumberedZoneService.deleteNotNumberedZone(venueTemplateId, notNumberedZoneId);
    }

}
