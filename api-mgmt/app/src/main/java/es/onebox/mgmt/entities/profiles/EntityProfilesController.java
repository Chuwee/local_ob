package es.onebox.mgmt.entities.profiles;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.profiles.dto.CreateProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfileDTO;
import es.onebox.mgmt.entities.profiles.dto.ProfilesDTO;
import es.onebox.mgmt.entities.profiles.dto.UpdateProfileDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = EntityProfilesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntityProfilesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/profiles";

    private static final String AUDIT_COLLECTION = "ENTITY_PROFILES";

    private final EntityProfilesService service;

    @Autowired
    public EntityProfilesController(EntityProfilesService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ProfilesDTO getProfiles(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getProfiles(entityId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{profileId}")
    public ProfileDTO get(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                          @PathVariable @Min(value = 1, message = "profileId must be above 0") Long profileId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getProfile(entityId, profileId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                        @Valid @RequestBody CreateProfileDTO createChannel) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return service.createProfile(entityId, createChannel);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                       @PathVariable @Min(value = 1, message = "profileId must be above 0") Long profileId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteProfile(entityId, profileId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "entityId must be above 0") Long entityId,
                       @PathVariable @Min(value = 1, message = "profileId must be above 0") Long profileId,
                       @Valid @RequestBody UpdateProfileDTO updateChannelRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateProfile(entityId, profileId, updateChannelRequestDTO);
    }
}
