package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CreateSubscriptionRequestDTO;
import es.onebox.mgmt.entities.dto.Subscription;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilterDTO;
import es.onebox.mgmt.entities.dto.UpdateSubscriptionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_CNL_SAC;
import static es.onebox.core.security.Roles.Codes.ROLE_CRM_DLIST;
import static es.onebox.core.security.Roles.Codes.ROLE_CRM_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_CALL;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;


@RestController
@Validated
@RequestMapping(
        value = EntitySubscriptionListsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EntitySubscriptionListsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/subscription-lists";

    private static final String AUDIT_COLLECTION = "SUBSCRIPTIONS";
    private final EntitySubscriptionListsService entitySubscriptionListsService;

    @Autowired
    public EntitySubscriptionListsController(EntitySubscriptionListsService entitySubscriptionListsService) {
        this.entitySubscriptionListsService = entitySubscriptionListsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR, ROLE_CNL_SAC,
            ROLE_OPR_CALL, ROLE_CRM_MGR, ROLE_CRM_DLIST})
    @RequestMapping(method = RequestMethod.GET)
    public List<Subscription> getSubscriptionLists(@BindUsingJackson @Valid SubscriptionRequestFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return entitySubscriptionListsService.getSubscriptionLists(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR, ROLE_CNL_SAC, ROLE_OPR_CALL, ROLE_CRM_MGR, ROLE_CRM_DLIST})
    @GetMapping(value = "/{subscriptionListId}")
    public Subscription getSubscriptionList(@PathVariable Integer subscriptionListId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(subscriptionListId, "subscriptionListId");
        return entitySubscriptionListsService.getSubscriptionList(subscriptionListId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_CRM_MGR, ROLE_CRM_DLIST})
    @PostMapping
    public ResponseEntity<IdDTO> addSubscriptionLists(@Validated @RequestBody CreateSubscriptionRequestDTO subscription) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_ADD);

        return new ResponseEntity<>(entitySubscriptionListsService.addSubscriptionLists(subscription), HttpStatus.CREATED);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_CRM_MGR, ROLE_CRM_DLIST})
    @PutMapping(value = "/{subscriptionListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSubscriptionLists(@PathVariable Long subscriptionListId,
                                        @RequestBody UpdateSubscriptionRequestDTO subscription) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        entitySubscriptionListsService.updateSubscriptionLists(subscriptionListId, subscription);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_EVN_MGR, ROLE_CNL_MGR, ROLE_CRM_MGR, ROLE_CRM_DLIST})
    @DeleteMapping(value = "/{subscriptionListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscriptionLists(@PathVariable Long subscriptionListId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        entitySubscriptionListsService.deleteSubscriptionLists(subscriptionListId);
    }

}
