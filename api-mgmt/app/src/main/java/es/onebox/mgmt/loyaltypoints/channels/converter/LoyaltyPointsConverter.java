package es.onebox.mgmt.loyaltypoints.channels.converter;

import es.onebox.mgmt.loyaltypoints.channels.dto.UpdateChannelLoyaltyPointsDTO;
import es.onebox.mgmt.loyaltypoints.channels.dto.ChannelLoyaltyPointsDTO;
import es.onebox.mgmt.loyaltypoints.channels.dto.LoyaltyPointsPercentagePerPurchaseDTO;
import es.onebox.mgmt.loyaltypoints.channels.dto.MaxLoyaltyPointsPerPurchaseDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.*;

public class LoyaltyPointsConverter {

    private LoyaltyPointsConverter() {
    }

    public static ChannelLoyaltyPointsDTO toDTO(ChannelLoyaltyPoints from){
        if (from == null) {
            return null;
        }
        ChannelLoyaltyPointsDTO target = new ChannelLoyaltyPointsDTO();
        target.setAllowLoyaltyPoints(from.getAllowLoyaltyPoints());

        if (from.getMaxLoyaltyPointsPerPurchase() != null) {
            MaxLoyaltyPointsPerPurchaseDTO maxPointsDTO = new MaxLoyaltyPointsPerPurchaseDTO();
            maxPointsDTO.setEnabled(from.getMaxLoyaltyPointsPerPurchase().getEnabled());
            maxPointsDTO.setAmount(from.getMaxLoyaltyPointsPerPurchase().getAmount());
            target.setMaxLoyaltyPointsPerPurchase(maxPointsDTO);
        }
        if (from.getLoyaltyPointsPercentagePerPurchase() != null) {
            LoyaltyPointsPercentagePerPurchaseDTO percentageDTO = new LoyaltyPointsPercentagePerPurchaseDTO();
            percentageDTO.setEnabled(from.getLoyaltyPointsPercentagePerPurchase().getEnabled());
            percentageDTO.setPercentage(from.getLoyaltyPointsPercentagePerPurchase().getPercentage());
            target.setLoyaltyPointsPercentagePerPurchase(percentageDTO);
        }
        return target;
    }

    public static ChannelLoyaltyPoints fromDTO(UpdateChannelLoyaltyPointsDTO from) {
        ChannelLoyaltyPoints target = new ChannelLoyaltyPoints();
        target.setAllowLoyaltyPoints(from.getAllowLoyaltyPoints());

        if (from.getMaxLoyaltyPointsPerPurchase() != null) {
            MaxLoyaltyPointsPerPurchase maxPoints = new MaxLoyaltyPointsPerPurchase();
            maxPoints.setEnabled(from.getMaxLoyaltyPointsPerPurchase().getEnabled());
            maxPoints.setAmount(from.getMaxLoyaltyPointsPerPurchase().getAmount());
            target.setMaxLoyaltyPointsPerPurchase(maxPoints);
        }

        if (from.getLoyaltyPointsPercentagePerPurchase() != null) {
            LoyaltyPointsPercentagePerPurchase percentagePoints = new LoyaltyPointsPercentagePerPurchase();
            percentagePoints.setEnabled(from.getLoyaltyPointsPercentagePerPurchase().getEnabled());
            percentagePoints.setPercentage(from.getLoyaltyPointsPercentagePerPurchase().getPercentage());
            target.setLoyaltyPointsPercentagePerPurchase(percentagePoints);
        }
        return target;
    }
}