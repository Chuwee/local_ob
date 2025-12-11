package es.onebox.mgmt.sessions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.sessions.converters.CapacityConverter;
import es.onebox.mgmt.sessions.dto.CapacityRelocationRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionCapacityUpdateBulkNnzDTO;
import es.onebox.mgmt.sessions.dto.SessionCapacityUpdateBulkSeatsDTO;
import es.onebox.mgmt.sessions.dto.SessionPackNotNumberedZoneLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionPackSeatLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagSeatRequestDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityListDTO;
import es.onebox.venue.venuetemplates.VenueMapProto;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SessionsCapacityController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionsCapacityController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions";

    private static final String AUDIT_COLLECTION = "SESSION_CAPACITY";

    private final SessionsService sessionsService;

    private final SessionsCapacityService sessionsCapacityService;

    @Autowired
    public SessionsCapacityController(SessionsService sessionService, SessionsCapacityService sessionsCapacityService) {
        this.sessionsService = sessionService;
        this.sessionsCapacityService = sessionsCapacityService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{sessionId}/capacity",
            produces = {"application/x-protobuf"})
    public byte[] getVenueTemplateMapProtobuf(@PathVariable(value = "eventId") Long eventId,
                                              @PathVariable(value = "sessionId") Long sessionId) throws IOException {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = sessionsCapacityService.getCapacityMap(eventId, sessionId);
        return IOUtils.toByteArray(is);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{sessionId}/capacity",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public VenueMapProto.VenueMap getVenueTemplateMapJson(@PathVariable(value = "eventId") Long eventId,
                                                          @PathVariable(value = "sessionId") Long sessionId) throws IOException {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = sessionsCapacityService.getCapacityMap(eventId, sessionId);
        return CapacityConverter.fromMsToProto(is);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{sessionId}/capacity/seats")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateSeatsCapacity(@PathVariable(value = "eventId") Long eventId,
                                    @PathVariable(value = "sessionId") Long sessionId,
                                    @RequestBody SessionVenueTagSeatRequestDTO[] seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionsCapacityService.updateSeatsCapacity(eventId, sessionId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{sessionId}/capacity/not-numbered-zones")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateNotNumberedZonesCapacity(@PathVariable(value = "eventId") Long eventId,
                                               @PathVariable(value = "sessionId") Long sessionId,
                                               @RequestBody @Valid SessionVenueTagNotNumberedZoneRequestDTO[] notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionsCapacityService.updateNotNumberedZoneCapacity(eventId, sessionId, notNumberedZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/capacity/seats/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateSeatsCapacityBulk(@PathVariable(value = "eventId") Long eventId,
                                        @RequestBody SessionCapacityUpdateBulkSeatsDTO seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionsCapacityService.updateSeatsCapacityBulk(eventId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/capacity/not-numbered-zones/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateNotNumberedZonesCapacityBulk(@PathVariable(value = "eventId") Long eventId,
                                                   @RequestBody SessionCapacityUpdateBulkNnzDTO nnzs) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        sessionsCapacityService.updateNotNumberedZonesCapacityBulk(eventId, nnzs);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{sessionId}/capacity/seats/link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void linkSeats(@PathVariable(value = "eventId") Long eventId,
                          @PathVariable(value = "sessionId") Long sessionId,
                          @Valid @RequestBody SessionPackSeatLinkDTO seats) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);
        sessionsCapacityService.linkSessionPackSeats(eventId, sessionId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{sessionId}/capacity/seats/unlink")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unlinkSeats(@PathVariable(value = "eventId") Long eventId,
                            @PathVariable(value = "sessionId") Long sessionId,
                            @Valid @RequestBody SessionPackSeatLinkDTO seats) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UNLINK);
        sessionsCapacityService.unlinkSessionPackSeats(eventId, sessionId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{sessionId}/capacity/not-numbered-zones/link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void linkNotNumberedZone(@PathVariable(value = "eventId") Long eventId,
                                    @PathVariable(value = "sessionId") Long sessionId,
                                    @Valid @RequestBody SessionPackNotNumberedZoneLinkDTO notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);
        sessionsCapacityService.linkSessionPackNNZ(eventId, sessionId, notNumberedZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/{sessionId}/capacity/not-numbered-zones/unlink")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unlinkNotNumberedZone(@PathVariable(value = "eventId") Long eventId,
                                      @PathVariable(value = "sessionId") Long sessionId,
                                      @Valid @RequestBody SessionPackNotNumberedZoneLinkDTO notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UNLINK);
        sessionsCapacityService.unlinkSessionPackNNZ(eventId, sessionId, notNumberedZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{sessionId}/external-inventory")
    public void updateExternalAvailability(@PathVariable(value = "eventId") Long eventId,
                                           @PathVariable(value = "sessionId") Long sessionId) {

        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionsService.updateExternalAvailability(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{sessionId}/capacity/quotas")
    public List<QuotaCapacityDTO> getSessionQuotasCapacity(@PathVariable(value = "eventId") Long eventId,
                                                           @PathVariable(value = "sessionId") Long sessionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return sessionsService.getQuotasCapacity(eventId, sessionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{sessionId}/capacity/quotas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionQuotasCapacity(@PathVariable(value = "eventId") Long eventId,
                                            @PathVariable(value = "sessionId") Long sessionId,
                                            @RequestBody QuotaCapacityListDTO requestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionsService.updateQuotasCapacity(eventId, sessionId, requestDTO);
    }

    @Secured({ROLE_REC_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PostMapping("/{sessionId}/capacity/relocation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void relocateSeats(@PathVariable(value = "eventId") Long eventId,
                              @PathVariable(value = "sessionId") Long sessionId,
                              @RequestBody CapacityRelocationRequestDTO relocationRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        sessionsCapacityService.relocateSeats(eventId, sessionId, relocationRequest);
    }
}
