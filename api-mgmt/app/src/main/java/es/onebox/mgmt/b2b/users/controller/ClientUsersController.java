package es.onebox.mgmt.b2b.users.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.b2b.clients.controller.ClientsController;
import es.onebox.mgmt.b2b.users.dto.ClientSecretDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUserResponseDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUsersDTO;
import es.onebox.mgmt.b2b.users.dto.CreateClientUserRequestDTO;
import es.onebox.mgmt.b2b.users.dto.SearchClientUsersFilterDTO;
import es.onebox.mgmt.b2b.users.dto.UpdateClientUserRequestDTO;
import es.onebox.mgmt.b2b.users.service.ClientUsersService;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientSecret;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ClientUsersController.BASE_URI)
public class ClientUsersController {

    public static final String BASE_URI = ClientsController.BASE_URI + "/{clientId}/users";

    private static final String AUDIT_COLLECTION = "B2B_USERS";

    private final ClientUsersService clientUsersService;

    @Autowired
    public ClientUsersController(ClientUsersService clientUsersService) {
        this.clientUsersService = clientUsersService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    public ClientUsersDTO getClientUsers(@PathVariable Long clientId,
                                         @BindUsingJackson SearchClientUsersFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return clientUsersService.getClientUsers(clientId, filter);
    }

    @RequestMapping(method = RequestMethod.GET,
            path = "/{clientUserId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    public ClientUserResponseDTO getClientUser(
            @PathVariable Long clientId,
            @PathVariable Long clientUserId,
            @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return clientUsersService.getClientUser(clientId, clientUserId, entityId);
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createClientUser(@PathVariable Long clientId,
                                  @Valid @RequestBody CreateClientUserRequestDTO clientUser) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return clientUsersService.createClientUser(clientUser, clientId);
    }

    @RequestMapping(method = RequestMethod.PUT,
            path = "/{clientUserId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClient(
            @PathVariable Long clientId,
            @PathVariable Long clientUserId,
            @Valid @RequestBody UpdateClientUserRequestDTO clientUser) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        clientUsersService.updateClientUser(clientUser, clientId, clientUserId);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{clientUserId}")
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long clientId,
                             @PathVariable Long clientUserId,
                             @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        clientUsersService.deleteClientUser(clientId, clientUserId, entityId);
    }

    @RequestMapping(method = RequestMethod.POST,
            path = "/{clientUserId}/reset-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(
            @PathVariable Long clientId,
            @PathVariable Long clientUserId,
            @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_FORGOT, AuditTag.AUDIT_ACTION_UPDATE);
        clientUsersService.resetPassword(clientId, clientUserId, entityId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR, ROLE_EVN_MGR})
    @PostMapping(value = "/{clientUserId}/apikey/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ClientSecretDTO refreshApiKey(@PathVariable Long clientId,
                                         @PathVariable final Long clientUserId,
                                         @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_REFRESH);
        return clientUsersService.refreshApiKey(clientId, clientUserId, entityId);
    }
}
