package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSurchargeDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSurchargeListDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChannelSurchargesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(
        value = SeasonTicketChannelSurchargesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketChannelSurchargesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_CHANNELS";

    @Autowired
    private SeasonTicketChannelSurchargesService seasonTicketChannelSurchargesService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/surcharges")
    public List<SeasonTicketChannelSurchargeDTO> getSeasonTicketChannelSurcharges(@PathVariable Long seasonTicketId, @PathVariable Long channelId,
                                                                                  @Valid @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelSurchargesService.getSeasonTicketChannelSurcharges(seasonTicketId, channelId, types);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/surcharges")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSeasonTicketChannelSurcharges(@PathVariable Long seasonTicketId, @PathVariable Long channelId,
                                                    @Valid @RequestBody SeasonTicketChannelSurchargeListDTO surcharges) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        seasonTicketChannelSurchargesService.createSeasonTicketChannelSurcharges(seasonTicketId, channelId, surcharges);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/channel-surcharges")
    public List<SaleRequestSurchargeDTO> getChannelSurcharges(@PathVariable Long seasonTicketId, @PathVariable Long channelId,
                                                              @Valid @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelSurchargesService.getChannelSurcharges(seasonTicketId, channelId, types);
    }
}
