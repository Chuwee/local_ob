package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider.AvailableCampaigns;
import es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider.Campaign;
import es.onebox.mgmt.entities.dto.AvailableCampaignsDTO;
import es.onebox.mgmt.entities.dto.CampaignDTO;

public class AvailableCampaignsConverter {

    private AvailableCampaignsConverter() {
    }

    public static AvailableCampaignsDTO buildAvailableCampaignsDTO (AvailableCampaigns availableCampaigns){
        AvailableCampaignsDTO dto = new AvailableCampaignsDTO();

        availableCampaigns.stream()
                .map(AvailableCampaignsConverter::createCampaignDTO)
                .forEach(dto::add);
        return dto;
    }

    private static CampaignDTO createCampaignDTO(Campaign campaign) {
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(campaign.getId());
        campaignDTO.setName(campaign.getName());
        campaignDTO.setCurrencyCode(campaign.getCurrencyCode());
        campaignDTO.setFoundation(campaign.getFoundation());

        return campaignDTO;
    }
}
