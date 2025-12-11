package es.onebox.mgmt.datasources.ms.insurance.repository;

import es.onebox.mgmt.datasources.ms.insurance.MsInsuranceDatasource;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InsuranceRangesRepository {

    private final MsInsuranceDatasource msInsuranceDatasource;

    @Autowired
    public InsuranceRangesRepository(MsInsuranceDatasource msInsuranceDatasource) {
        this.msInsuranceDatasource = msInsuranceDatasource;
    }

    public List<InsuranceRange> getRangesByPolicyId(Integer insurerId, Integer policyId) {
        return msInsuranceDatasource.getRangesByPolicyId(insurerId, policyId);
    }

    public List<InsuranceRange> updateRangesByPolicyId(
            Integer insurerId, Integer policyId, List<InsuranceRange> insuranceRangeList) {
        return msInsuranceDatasource.updateRangesByPolicyId(insurerId, policyId, insuranceRangeList);
    }
}
