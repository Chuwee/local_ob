package es.onebox.mgmt.donations;

import es.onebox.mgmt.datasources.ms.entity.dto.DonationProvider;
import es.onebox.mgmt.datasources.ms.entity.dto.DonationProviders;
import es.onebox.mgmt.donations.dto.DonationProviderDTO;
import es.onebox.mgmt.donations.dto.DonationProvidersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DonationProvidersService {

    private final DonationProvidersRepository donationProvidersRepository;

    @Autowired
    public DonationProvidersService(DonationProvidersRepository donationProvidersRepository) {
        this.donationProvidersRepository = donationProvidersRepository;
    }

    public DonationProvidersDTO getDonationProviders() {
        DonationProviders donations = donationProvidersRepository.getDonationProviders();
        DonationProvidersDTO donationProvidersDTO = new DonationProvidersDTO();

        for (DonationProvider provider : donations) {
            DonationProviderDTO providerDTO = new DonationProviderDTO();
            providerDTO.setId(provider.getId());
            providerDTO.setName(provider.getProviderName());
            donationProvidersDTO.add(providerDTO);
        }

        return donationProvidersDTO;
    }
}
