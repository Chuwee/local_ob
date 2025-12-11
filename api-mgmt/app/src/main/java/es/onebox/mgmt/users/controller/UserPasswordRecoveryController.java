package es.onebox.mgmt.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.users.dto.ChangePwdRequestDTO;
import es.onebox.mgmt.users.dto.ForgotPasswordPropertiesDTO;
import es.onebox.mgmt.users.dto.ForgotPwdRequestDTO;
import es.onebox.mgmt.users.dto.ForgotPwdResponseDTO;
import es.onebox.mgmt.users.dto.RecoverForgotPasswordRequestDTO;
import es.onebox.mgmt.users.service.UsersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = UserPasswordRecoveryController.BASE_URI)
public class UserPasswordRecoveryController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/users";

    private static final String AUDIT_COLLECTION = "USERS";
    private static final String AUDIT_SUBCOLLECTION_PASS = "PASSWORD";

    private final UsersService usersService;
    
    @Autowired
    public UserPasswordRecoveryController(UsersService usersService) {
        this.usersService = usersService;
    }
    
    // PASSWORD
    @PostMapping(value = "/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPassword(@PathVariable Long userId, @RequestBody @Valid ChangePwdRequestDTO req) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_UPDATE);
        usersService.setUserPassword(userId, req.password(), req.token());
    }

    @PostMapping(value = "/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ForgotPwdResponseDTO forgotPassword(@RequestBody @Valid ForgotPwdRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_FORGOT);
        return usersService.forgotPassword(request);
    }

    @GetMapping(value = "/forgot-password")
    public ForgotPasswordPropertiesDTO validateToken(@RequestParam @NotNull String token) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_FORGOT);
        return usersService.validateToken(token);
    }

    @PostMapping(value = "/forgot-password/recover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recoverForgotPassword(@RequestBody @Valid RecoverForgotPasswordRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASS, AuditTag.AUDIT_ACTION_FORGOT);
        usersService.recoverForgotPassword(request);
    }
}
