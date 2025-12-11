package es.onebox.mgmt.passbook;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.StringMapDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.passbook.dto.AvailablePassbookFieldDTO;
import es.onebox.mgmt.passbook.dto.CodeDTO;
import es.onebox.mgmt.passbook.dto.CreatePassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookRequestFilterDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateListDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateType;
import es.onebox.mgmt.passbook.dto.UpdatePassbookTemplateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/passbook-templates")
public class PassbookTemplateController {

    private static final String AUDIT_COLLECTION = "PASSBOOK_TEMPLATES";
    private static final String AUDIT_LITERALS_COLLECTION = "PASSBOOK_TEMPLATES_LITERALS";
    private static final String AUDIT_FIELDS_COLLECTION = "PASSBOOK_TEMPLATES_FIELDS";
    private static final String AUDIT_COLLECTION_PREVIEW = "PASSBOOK_PREVIEW";

    @Autowired
    private PassbookTemplateService passbookTemplateService;

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public PassbookTemplateListDTO searchPassbookTemplates(@BindUsingJackson @Valid PassbookRequestFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return passbookTemplateService.searchPassbookTemplates(filter);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public CodeDTO createPassbookTemplates(@RequestBody @Valid CreatePassbookTemplateDTO createPassbookTemplate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        if (createPassbookTemplate == null) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_DATA_MANDATORY);
        }
        return passbookTemplateService.createPassbookTemplate(createPassbookTemplate);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{code}")
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassbookTemplate(@PathVariable String code, @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        passbookTemplateService.deletePassbookTemplate(code, entityId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{code}")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public PassbookTemplateDTO getPassbookTemplate(@PathVariable String code, @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return passbookTemplateService.getPassbookTemplate(code, entityId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{code}")
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassbookTemplate(@PathVariable String code,
                                                               @RequestParam(value = "entity_id", required = false) Long entityId,
                                                               @RequestBody UpdatePassbookTemplateDTO updatePassbookTemplate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        if (updatePassbookTemplate == null) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_DATA_MANDATORY);
        }
        passbookTemplateService.updatePassbookTemplate(code, entityId, updatePassbookTemplate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{passbookCode}/literals/{langCode}")
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    public Map<String, String> getPassbookLiterals(@RequestParam(value = "entity_id", required = false) Long entityId,
                                                   @PathVariable String passbookCode, @PathVariable String langCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_LITERALS_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return passbookTemplateService.getPassbookLiterals(entityId, passbookCode, langCode);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{passbookCode}/literals/{langCode}")
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassbookLiterals(@RequestParam(value = "entity_id", required = false) Long entityId,
                                                               @PathVariable String passbookCode, @PathVariable String langCode,
                                                               @RequestBody StringMapDTO updatePassbookLiterals) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_LITERALS_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        if (updatePassbookLiterals == null || updatePassbookLiterals.isEmpty()) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_DATA_MANDATORY);
        }
        passbookTemplateService.updatePassbookLiterals(entityId, passbookCode, langCode, updatePassbookLiterals);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/available-fields")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<AvailablePassbookFieldDTO> getPassbookAvailableFields(@RequestParam(required = false) PassbookTemplateType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_FIELDS_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return passbookTemplateService.availablePassbookFields(type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/available-data-placeholders")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<String> getPassbookAvailableDataPlaceholders(@RequestParam(required = false) PassbookTemplateType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_FIELDS_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return passbookTemplateService.availableDataPlaceholders(type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/available-literals")
    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<String> getPassbookAvailableLiteralKeys() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_FIELDS_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return passbookTemplateService.availableLiteralKeys();
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{code}/preview")
    public TicketPrintResultDTO getPassbookPreview(@PathVariable String code,
                                                   @RequestParam(value = "entity_id", required = false) Long entityId,
                                                   @RequestParam(required = false) String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PREVIEW, AuditTag.AUDIT_ACTION_GET);
        return this.passbookTemplateService.getPassbookPreview(code, entityId, language);
    }

}
