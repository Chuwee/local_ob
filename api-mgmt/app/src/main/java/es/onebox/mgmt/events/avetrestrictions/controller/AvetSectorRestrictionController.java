package es.onebox.mgmt.events.avetrestrictions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionUpdateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.mgmt.events.avetrestrictions.service.AvetSectorRestrictionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping(value = AvetSectorRestrictionController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class AvetSectorRestrictionController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/avet-sector-restrictions";

    private static final String AUDIT_COLLECTION = "RESTRICTIONS";

    private final AvetSectorRestrictionService avetSectorRestrictionService;

    @Autowired
    public AvetSectorRestrictionController(AvetSectorRestrictionService avetSectorRestrictionService) {
        this.avetSectorRestrictionService = avetSectorRestrictionService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public AvetSectorRestrictionDetailDTO get(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                              @PathVariable(value = "sid") @NotNull String restrictionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return avetSectorRestrictionService.getAvetSectorRestriction(eventId, restrictionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public AvetSectorRestrictionsDTO getAvetSectorRestrictions
            (@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return avetSectorRestrictionService.getAvetSectorRestrictions(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CodeDTO> create(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                          @RequestBody @Valid AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        String restrictionId = avetSectorRestrictionService.createAvetSectorRestriction(eventId, avetSectorRestrictionCreateDTO);

        return new ResponseEntity<>(new CodeDTO(restrictionId), HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                       @PathVariable(value = "sid") @NotNull String restrictionId,
                       @Valid @RequestBody AvetSectorRestrictionUpdateDTO avetSectorRestrictionUpdateDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        avetSectorRestrictionService.updateAvetSectorRestriction(eventId, restrictionId, avetSectorRestrictionUpdateDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                       @PathVariable(value = "sid") @NotNull String restrictionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        avetSectorRestrictionService.deleteAvetSectorRestriction(eventId, restrictionId);
    }
}
