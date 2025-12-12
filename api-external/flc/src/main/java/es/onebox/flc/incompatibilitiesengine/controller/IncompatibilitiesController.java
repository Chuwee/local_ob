package es.onebox.flc.incompatibilitiesengine.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.flc.incompatibilitiesengine.dto.LoginData;
import es.onebox.flc.incompatibilitiesengine.service.IncompatibilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/incompatibilities-engine")
public class IncompatibilitiesController {

    private final IncompatibilitiesService incompatibilitiesService;

    @Autowired
    public IncompatibilitiesController(IncompatibilitiesService incompatibilitiesService){
        this.incompatibilitiesService = incompatibilitiesService;
    }

    @Secured({Role.ENTITY_ANALYST, Role.ENTITY_MANAGER, Role.EVENT_MANAGER})
    @GetMapping(value = "/login-data")
    public LoginData getLoginData() {
        return incompatibilitiesService.getLoginData();
    }

    @PutMapping(value = "/publish")
    public void publish(@RequestParam(value = "session_ids") List<Long> sessionIds,
                        @RequestParam(value = "sequence_number") Long sequenceNumber) {

        incompatibilitiesService.changePublishState(sessionIds, sequenceNumber, null,  true);
    }

    @PutMapping(value = "/unpublish")
    public void unpublish(@RequestParam(value = "session_ids") List<Long> sessionIds,
                          @RequestParam(value = "reason", required = false) String reason,
                          @RequestParam(value = "sequence_number") Long sequenceNumber) {

        incompatibilitiesService.changePublishState(sessionIds, sequenceNumber, reason,  false);
    }
}
