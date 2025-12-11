package es.onebox.mgmt.venues;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles.Codes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.venues.dto.CloneVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.NotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSectorDTO;
import es.onebox.mgmt.venues.service.NotNumberedZoneService;
import es.onebox.mgmt.venues.service.VenueTemplateSectorsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(VenueTemplateSectorsController.BASE_URI)
public class VenueTemplateSectorsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/sectors";
    private static final String AUDIT_COLLECTION = "VENUE_TEMPLATES_SECTORS";
    private static final String AUDIT_SUBCOLLECTION_TAGS = "VENUE_TEMPLATES_SECTORS_TAG";

    private final VenueTemplateSectorsService venueTemplateSectorsService;
    private final NotNumberedZoneService notNumberedZoneService;

    @Autowired
    public VenueTemplateSectorsController(VenueTemplateSectorsService venueTemplateSectorsService,
                                          NotNumberedZoneService notNumberedZoneService) {
        this.venueTemplateSectorsService = venueTemplateSectorsService;
        this.notNumberedZoneService = notNumberedZoneService;
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @GetMapping()
    public List<VenueTemplateSectorDTO> getVenueTemplateSectors(@PathVariable(value = "venueTemplateId") Long venueTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateSectorsService.getSectors(venueTemplateId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @GetMapping(value = "/{sectorId}")
    public VenueTemplateSectorDTO getVenueTemplateSector(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                         @PathVariable(value = "sectorId") Long sectorId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return venueTemplateSectorsService.getSector(venueTemplateId, sectorId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @PostMapping()
    public ResponseEntity<IdDTO> createSector(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                              @RequestBody @Valid CreateVenueTemplateSectorRequestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CREATE);

        Long id = venueTemplateSectorsService.createSector(venueTemplateId, body);

        return new ResponseEntity<>(new IdDTO(id), HttpStatus.CREATED);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @PostMapping(value = "/{sectorId}/clone")
    public ResponseEntity<IdDTO> cloneSector(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                             @PathVariable(value = "sectorId") Long sectorId,
                                             @RequestBody @Valid CloneVenueTemplateSectorRequestDTO cloneSectorDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_CLONE);

        Long id = venueTemplateSectorsService.cloneSector(venueTemplateId, sectorId, cloneSectorDTO);

        return new ResponseEntity<>(new IdDTO(id), HttpStatus.CREATED);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @PutMapping(value = "/{sectorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSector(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                             @PathVariable(value = "sectorId") Long sectorId,
                             @RequestBody UpdateVenueTemplateSectorRequestDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_UPDATE);

        venueTemplateSectorsService.updateSector(venueTemplateId, sectorId, requestDTO);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_OPR_MGR, Codes.ROLE_REC_EDI})
    @DeleteMapping(value = "/{sectorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSector(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                             @PathVariable(value = "sectorId") Long sectorId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_DELETE);

        venueTemplateSectorsService.deleteSector(venueTemplateId, sectorId);
    }

    @Secured({Codes.ROLE_REC_MGR, Codes.ROLE_EVN_MGR, Codes.ROLE_ENT_ANS, Codes.ROLE_OPR_MGR, Codes.ROLE_OPR_ANS, Codes.ROLE_REC_EDI})
    @GetMapping(value = "/{sectorId}/not-numbered-zones")
    public List<NotNumberedZoneDTO> getVenueTemplateSectorNotNumberedZones(@PathVariable(value = "venueTemplateId") Long venueTemplateId,
                                                                           @PathVariable(value = "sectorId") Long sectorId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAGS, AuditTag.AUDIT_ACTION_GET);

        return notNumberedZoneService.getNotNumberedZones(venueTemplateId, sectorId);
    }

}
