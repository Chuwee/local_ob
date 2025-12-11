package es.onebox.mgmt.datasources.ms.insurance.repository;

import es.onebox.mgmt.datasources.ms.insurance.MsInsuranceDatasource;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurers;
import es.onebox.mgmt.datasources.ms.insurance.dto.SearchInsurerFilter;
import org.springframework.stereotype.Repository;

@Repository
public class InsurerRepository {

    private final MsInsuranceDatasource msInsuranceDatasource;

    public InsurerRepository(MsInsuranceDatasource msInsuranceDatasource) {
        this.msInsuranceDatasource = msInsuranceDatasource;
    }

    public Insurer getInsurer(Integer insurerId) {
        return msInsuranceDatasource.getInsurer(insurerId);
    }

    public Insurers searchInsurers(SearchInsurerFilter filter) {
        return msInsuranceDatasource.searchInsurers(filter);
    }

    public Insurer createInsurer(Insurer insurer) {
        return msInsuranceDatasource.createInsurer(insurer);
    }

    public Insurer updateInsurer(Insurer insurer) {
        return msInsuranceDatasource.updateInsurer(insurer);
    }
}
