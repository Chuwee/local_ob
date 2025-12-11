package es.onebox.mgmt.events.tags.controller;


import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.tags.dto.SessionTagRequestDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagResponseDTO;
import es.onebox.mgmt.events.tags.dto.SessionTagsResponseDTO;
import es.onebox.mgmt.events.tags.service.SessionTagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
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

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(SessionTagController.BASE_URI)
public class SessionTagController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/tags";
    private static final String AUDIT_COLLECTION = "EVENT_TAGS";
    private final SessionTagService sessionTagService;

    public SessionTagController(SessionTagService sessionTagService) {
        this.sessionTagService = sessionTagService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping
    public SessionTagsResponseDTO getSessionTags(@PathVariable(value = "eventId")
                                                 @Min(value = 1, message = "event id must be above 0") Long eventId,
                                                 @PathVariable(value = "sessionId")
                                                 @Min(value = 1, message = "session id must be above 0") Long sessionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionTagService.getSessionTags(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionTagResponseDTO createSessionTag(@PathVariable(value = "eventId")
                                                  @Min(value = 1, message = "event id must be above 0") Long eventId,
                                                  @PathVariable(value = "sessionId")
                                                  @Min(value = 1, message = "session id must be above 0") Long sessionId,
                                                  @RequestBody @Valid SessionTagRequestDTO sessionTagRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_ADD);
        return sessionTagService.createSessionTag(eventId, sessionId, sessionTagRequestDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionTag(@PathVariable(value = "eventId")
                                 @Min(value = 1, message = "event id must be above 0") Long eventId,
                                 @PathVariable(value = "sessionId")
                                 @Min(value = 1, message = "session id must be above 0") Long sessionId,
                                 @PathVariable(value = "positionId")
                                 @Min(value = 0, message = "position id must be equal or above 0") Long positionId,
                                 @RequestBody @Valid SessionTagRequestDTO sessionTagRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionTagService.updateSessionTag(eventId, sessionId, positionId, sessionTagRequestDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSessionTag(@PathVariable(value = "eventId")
                                 @Min(value = 1, message = "event id must be above 0") Long eventId,
                                 @PathVariable(value = "sessionId")
                                 @Min(value = 1, message = "session id must be above 0") Long sessionId,
                                 @PathVariable(value = "positionId")
                                 @Min(value = 0, message = "position id must be equal or above 0") Long positionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        sessionTagService.deleteSessionTag(eventId, sessionId, positionId);
    }
}
