package es.onebox.mgmt.insurance.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.insurance.dto.InsurancePoliciesDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyDTO;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTO;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTOList;
import es.onebox.mgmt.insurance.dto.InsuranceTermsConditionsListDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsurancePolicyDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsDTO;
import es.onebox.mgmt.insurance.dto.UpdateInsuranceTermsConditionsFileContentDTO;
import es.onebox.mgmt.insurance.service.InsurancePolicyService;
import es.onebox.mgmt.insurance.service.InsuranceRangeService;
import es.onebox.mgmt.insurance.service.InsuranceTermsConditionsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = InsurancePolicyController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class InsurancePolicyController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/insurers/{insurerId}/policies";

    private static final String AUDIT_COLLECTION = "INSURANCE_POLICIES";
    private static final String AUDIT_COLLECTION_TERMS_CONDITIONS = "INSURANCE_TERMS_CONDITIONS";
    private static final String AUDIT_COLLECTION_RANGES = "INSURANCE_RANGES";

    private final InsurancePolicyService insurancePolicyService;
    private final InsuranceTermsConditionsService insuranceTermsConditionsService;
    private final InsuranceRangeService insuranceRangeService;

    public InsurancePolicyController(InsurancePolicyService insurancePolicyService, InsuranceTermsConditionsService insuranceTermsConditionsService, InsuranceRangeService insuranceRangeService) {
        this.insurancePolicyService = insurancePolicyService;
        this.insuranceTermsConditionsService = insuranceTermsConditionsService;
        this.insuranceRangeService = insuranceRangeService;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping()
    public InsurancePoliciesDTO getPoliciesByInsurerId(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurancePolicyService.getPoliciesByInsurerId(insurerId);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping("/{policyId}")
    public InsurancePolicyDTO getPolicyDetails(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                               @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurancePolicyService.getPolicyDetails(insurerId, policyId);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public InsurancePolicyDTO createPolicyDetails(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                                  @RequestBody final InsurancePolicyCreateDTO insurancePolicyCreateDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return insurancePolicyService.createPolicy(insurerId, insurancePolicyCreateDTO);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{policyId}")
    public void updatePolicy(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                             @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
                             @Valid @RequestBody final UpdateInsurancePolicyDTO insurancePolicyDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_UPDATE);
        insurancePolicyService.updatePolicy(insurerId, policyId, insurancePolicyDTO);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{policyId}")
    public void deleteProduct(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                              @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_UPDATE);
        insurancePolicyService.deletePolicy(insurerId, policyId);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping("/{policyId}/ranges")
    public List<InsuranceRangeDTO> getRangesByPolicyId(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                                       @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RANGES, AuditTag.AUDIT_ACTION_GET);
        return insuranceRangeService.getRangesByPolicyId(insurerId, policyId);
    }


    @Secured({ROLE_SYS_MGR})
    @PostMapping("/{policyId}/ranges")
    public List<InsuranceRangeDTO> updateRangesByPolicyId(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                                          @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
                                                          @RequestBody InsuranceRangeDTOList rangeDTOList) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RANGES, AuditTag.AUDIT_ACTION_GET);
        return insuranceRangeService.updateRangesByPolicyId(insurerId, policyId, rangeDTOList);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping("/{policyId}/terms-conditions")
    public InsuranceTermsConditionsListDTO getTermsConditionsByPolicyId(
            @PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
            @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
            @RequestParam(required = false) final String lang) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_GET);
        return insuranceTermsConditionsService.getTermsConditionsList(insurerId, policyId, lang);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{policyId}/terms-conditions/{termsId}")
    public void updateTermsConditions(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                      @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
                                      @PathVariable @Min(value = 1, message = "termsId must be above 0") final Integer termsId,
                                      @Valid @RequestBody final UpdateInsuranceTermsConditionsDTO termsConditionsDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_UPDATE);
        insuranceTermsConditionsService.updateTermsConditions(insurerId, policyId, termsId, termsConditionsDTO);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping("/{policyId}/terms-conditions/{termsId}/file-content")
    public String getTermsConditionsFileContentById(
            @PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
            @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
            @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer termsId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_GET);
        return insuranceTermsConditionsService.getTermsConditionsFileContent(insurerId, policyId, termsId);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{policyId}/terms-conditions/{termsId}/file-content")
    public void updateTermsConditionsFileContent(@PathVariable @Min(value = 1, message = "insurerId must be above 0") final Integer insurerId,
                                                 @PathVariable @Min(value = 1, message = "policyId must be above 0") final Integer policyId,
                                                 @PathVariable @Min(value = 1, message = "termsId must be above 0") final Integer termsId,
                                                 @Valid @RequestBody final UpdateInsuranceTermsConditionsFileContentDTO fileContentDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_TERMS_CONDITIONS, AuditTag.AUDIT_ACTION_UPDATE);
        insuranceTermsConditionsService.updateTermsConditionsFileContent(insurerId, policyId, termsId, fileContentDTO);
    }



}
