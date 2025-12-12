package es.onebox.fever.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.fever.dto.FvUserAuth;
import es.onebox.fever.service.AuthUserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.FeverApiConfig.BASE_URL + "/user-auth")
public class AuthUserController {

    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Secured(Role.ROLE_FV_REPORTING)
    @GetMapping()
    public FvUserAuth getUserInfo(@RequestParam(value = "entity_id") Long entityId) {
        return this.authUserService.getUserInfo(entityId);
    }
}
