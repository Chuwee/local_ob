package es.onebox.mgmt.sessions;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.sessions.dto.SessionWhitelistDTO;
import es.onebox.mgmt.sessions.dto.WhitelistFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(SessionWhitelistController.BASE_URI)
public class SessionWhitelistController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/whitelist";

    private final SessionWhitelistService sessionWhitelistService;

    @Autowired
    public SessionWhitelistController(SessionWhitelistService sessionWhitelistService) {
        this.sessionWhitelistService = sessionWhitelistService;
    }

    @GetMapping
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SessionWhitelistDTO getWhitelist(@PathVariable Long eventId,
                                            @PathVariable Long sessionId, @BindUsingJackson WhitelistFilterDTO filter) {
        return sessionWhitelistService.getWhitelist(eventId, sessionId, filter);
    }
}
