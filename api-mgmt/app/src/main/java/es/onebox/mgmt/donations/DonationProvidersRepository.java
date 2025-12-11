package es.onebox.mgmt.donations;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.DonationProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DonationProvidersRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public DonationProvidersRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public DonationProviders getDonationProviders(){
        return msEntityDatasource.getDonationProviders();
    }
}
