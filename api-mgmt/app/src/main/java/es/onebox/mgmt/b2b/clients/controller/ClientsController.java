package es.onebox.mgmt.b2b.clients.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.b2b.clients.dto.ClientDTO;
import es.onebox.mgmt.b2b.clients.dto.ClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateClientDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateDirectoryClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.DirectoryClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.SearchClientsFilterDTO;
import es.onebox.mgmt.b2b.clients.dto.UpdateClientDTO;
import es.onebox.mgmt.b2b.clients.service.ClientsService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientSecret;
import es.onebox.mgmt.users.dto.UserSecretDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ClientsController.BASE_URI)
public class ClientsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/b2b/clients";

    private static final String AUDIT_COLLECTION = "B2B_CLIENTS";
    private static final String AUDIT_COLLECTION_DIRECTORY = "B2B_CLIENTS_DIRECTORY";

    private final ClientsService clientsService;

    @Autowired
    public ClientsController(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ClientsDTO getClients(@BindUsingJackson SearchClientsFilterDTO filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return clientsService.searchClients(filter);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{clientId}")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ClientDTO getClient(@PathVariable Long clientId, @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return clientsService.getClient(clientId, entityId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createClient(@Valid @RequestBody CreateClientDTO createClient) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return clientsService.createClient(createClient);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{clientId}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClient(@PathVariable Long clientId,
                             @Valid @RequestBody UpdateClientDTO updateClientDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        clientsService.updateClient(clientId, updateClientDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{clientId}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long clientId, @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        clientsService.deleteClient(clientId, entityId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/directory")
    @Secured({ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public DirectoryClientsDTO getClientsDirectory(@BindUsingJackson SearchClientsFilterDTO filter){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_DIRECTORY, AuditTag.AUDIT_ACTION_SEARCH);
        return clientsService.searchClientsDirectory(filter);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/directory")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public void createDirectoryClients(@Valid @RequestBody CreateDirectoryClientsDTO createDirectoryClients) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_DIRECTORY, AuditTag.AUDIT_ACTION_CREATE);
        clientsService.createDirectoryClients(createDirectoryClients);
    }
}
