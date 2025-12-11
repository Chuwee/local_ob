package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CreateEntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.EntityBankAccountDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityBankAccountDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EntityBankAccountController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EntityBankAccountController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/bank-accounts";

    private static final String AUDIT_COLLECTION = "ENTITY_BANK_ACCOUNTS";

    private final EntityBankAccountService entityBankAccountService;

    @Autowired
    public EntityBankAccountController(EntityBankAccountService entityBankAccountService) {
        this.entityBankAccountService = entityBankAccountService;
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping
    public List<EntityBankAccountDTO> getBankAccounts(@PathVariable Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityBankAccountService.getBankAccounts(entityId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping("/{bankAccountId}")
    public EntityBankAccountDTO getBankAccount(@PathVariable Long entityId, @PathVariable Long bankAccountId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityBankAccountService.getBankAccount(entityId, bankAccountId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createBankAccount(@PathVariable Long entityId, @RequestBody @Valid CreateEntityBankAccountDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return entityBankAccountService.createBankAccount(entityId, dto);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PutMapping("/{bankAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBankAccount(@PathVariable Long entityId, @PathVariable Long bankAccountId, @RequestBody @Valid UpdateEntityBankAccountDTO dto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        entityBankAccountService.updateBankAccount(entityId, bankAccountId, dto);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{bankAccountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBankAccount(@PathVariable Long entityId, @PathVariable Long bankAccountId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        entityBankAccountService.deleteBankAccount(entityId, bankAccountId);
    }
}

