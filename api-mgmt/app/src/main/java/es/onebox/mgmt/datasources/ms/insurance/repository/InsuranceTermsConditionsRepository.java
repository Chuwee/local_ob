package es.onebox.mgmt.datasources.ms.insurance.repository;

import es.onebox.mgmt.datasources.ms.insurance.MsInsuranceDatasource;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceTermsConditionsList;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditions;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditionsFileContent;
import org.springframework.stereotype.Repository;

@Repository
public class InsuranceTermsConditionsRepository {

    private final MsInsuranceDatasource msInsuranceDatasource;

    public InsuranceTermsConditionsRepository(MsInsuranceDatasource msInsuranceDatasource) {
        this.msInsuranceDatasource = msInsuranceDatasource;
    }

    public InsuranceTermsConditionsList getTermsConditionsListByPolicyId(Integer insurerId, Integer policyId, String lang) {
        return msInsuranceDatasource.getTermsConditionsByPolicyId(insurerId, policyId, lang);
    }

    public void updateTermsConditions(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditions updateInsuranceTermsConditions) {
        msInsuranceDatasource.updateTermsConditionsById(insurerId, policyId, termsId, updateInsuranceTermsConditions);
    }

    public String getTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId) {
        return msInsuranceDatasource.getTermsConditionsFileContent(insurerId, policyId, termsId);
    }

    public void updateTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditionsFileContent newFileContent) {
        msInsuranceDatasource.updateTermsConditionsFileContent(insurerId, policyId, termsId, newFileContent);
    }
}
