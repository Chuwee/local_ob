package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneUnlinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSeatLinkDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketCapacityService;
import es.onebox.mgmt.sessions.dto.SessionVenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagSeatRequestDTO;
import es.onebox.venue.venuetemplates.VenueMapProto;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.io.InputStream;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(SeasonTicketCapacityController.BASE_URI)
public class SeasonTicketCapacityController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/capacity";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_CAPACITY";

    private SeasonTicketCapacityService seasonTicketCapacityService;

    @Autowired
    public SeasonTicketCapacityController(SeasonTicketCapacityService seasonTicketCapacityService) {
        this.seasonTicketCapacityService = seasonTicketCapacityService;
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            produces = {"application/x-protobuf"})
    public byte[] getVenueTemplateMapProtobuf(@PathVariable(value = "seasonTicketId") Long seasonTicketId) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = seasonTicketCapacityService.getCapacityMap(seasonTicketId);
        return IOUtils.toByteArray(is);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_ANS, Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public VenueMapProto.VenueMap getVenueTemplateMapJson(@PathVariable(value = "seasonTicketId") Long seasonTicketId) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        InputStream is = seasonTicketCapacityService.getCapacityMap(seasonTicketId);
        return VenueMapProto.VenueMap.parseFrom(is);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/seats")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateSeatsCapacity(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                    @RequestBody SessionVenueTagSeatRequestDTO[] seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketCapacityService.updateSeatsCapacity(seasonTicketId, seats);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/not-numbered-zones")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateNotNumberedZonesCapacity(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                               @RequestBody SessionVenueTagNotNumberedZoneRequestDTO[] notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketCapacityService.updateNotNumberedZoneCapacity(seasonTicketId, notNumberedZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/seats/link")
    public SeasonTicketLinkResponseDTO linkSeats(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                                 @Valid @RequestBody SeasonTicketSeatLinkDTO seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);
        return seasonTicketCapacityService.linkSeasonTicketSeats(seasonTicketId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/seats/unlink")
    public SeasonTicketLinkResponseDTO unLinkSeats(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                                   @Valid @RequestBody SeasonTicketSeatLinkDTO seats) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UNLINK);
        return seasonTicketCapacityService.unLinkSeasonTicketSeats(seasonTicketId, seats);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/not-numbered-zones/link")
    @ResponseStatus(HttpStatus.OK)
    public SeasonTicketNotNumberedZoneLinkResponseDTO linkNotNumberedZone(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                                                          @Valid @RequestBody SeasonTicketNotNumberedZoneLinkDTO notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);
        return seasonTicketCapacityService.linkSeasonTicketNNZ(seasonTicketId, notNumberedZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/not-numbered-zones/unlink")
    @ResponseStatus(HttpStatus.OK)
    public SeasonTicketNotNumberedZoneUnlinkResponseDTO unlinkNotNumberedZone(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                                                              @Valid @RequestBody SeasonTicketNotNumberedZoneLinkDTO notNumberedZone) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_LINK);
        return seasonTicketCapacityService.unlinkSeasonTicketNNZ(seasonTicketId, notNumberedZone);
    }
}
