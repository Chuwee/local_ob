package es.onebox.mgmt.datasources.ms.insurance.repository;

import es.onebox.mgmt.datasources.ms.insurance.MsInsuranceDatasource;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServices;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServicesUpdate;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicies;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsurancePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InsurancePoliciesRepository {

    private final MsInsuranceDatasource msInsuranceDatasource;

    @Autowired
    public InsurancePoliciesRepository(MsInsuranceDatasource msInsuranceDatasource) {
        this.msInsuranceDatasource = msInsuranceDatasource;
    }

    public void updateChannelCancellationServices(Long channelId, ChannelCancellationServicesUpdate cancellationServices) {
        msInsuranceDatasource.updateChannelCancellationServices(channelId, cancellationServices);
    }

    public ChannelCancellationServices getChannelCancellationServices(Long channelId, Long userOperatorId) {
        return msInsuranceDatasource.getChannelCancellationServices(channelId, userOperatorId);
    }

    public InsurancePolicies getPoliciesByInsurerId(Integer insurerId) {
        return msInsuranceDatasource.getPoliciesByInsurerId(insurerId);
    }

    public InsurancePolicyV1 getPolicyDetails(Integer insurerId, Integer policyId) {
        return msInsuranceDatasource.getPolicyDetails(insurerId, policyId);
    }

    public void updatePolicy(Integer insurerId, Integer policyId, UpdateInsurancePolicy updateInsurancePolicy) {
        msInsuranceDatasource.updatePolicy(insurerId, policyId, updateInsurancePolicy);
    }

    public InsurancePolicyV1 createPolicy(Integer insurerId, InsurancePolicyV1 insurancePolicy) {
        return msInsuranceDatasource.createPolicy(insurerId, insurancePolicy);
    }

    public void deletePolicy(Integer insurerId, Integer policyId) {
        msInsuranceDatasource.deletePolicy(insurerId, policyId);
    }
}
