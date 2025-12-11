package es.onebox.mgmt.datasources.ms.insurance.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.mgmt.datasources.ms.insurance.ExternalDonationProvidersDatasource;
import es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider.AvailableCampaigns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExternalDonationProvidersRepository {

    private final ExternalDonationProvidersDatasource externalDonationProvidersDatasource;

    @Autowired
    public ExternalDonationProvidersRepository(ExternalDonationProvidersDatasource externalDonationProvidersDatasource) {
        this.externalDonationProvidersDatasource = externalDonationProvidersDatasource;
    }

    @Cached(key = "donations-available-ngos", expires = 60 * 30)
    public AvailableCampaigns getDonationCampaigns(@CachedArg Long entityId, @CachedArg Long providerId) {
        return externalDonationProvidersDatasource.getDonationCampaigns(entityId, providerId);
    }

}
