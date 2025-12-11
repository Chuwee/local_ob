package es.onebox.mgmt.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.TransferPolicy;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferUpdateDTO;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketTransferSeat;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferDTO;
import es.onebox.mgmt.seasontickets.enums.TransferPolicyDTO;

public class SeasonTicketTransferSeatConverter {

    public static SeasonTicketTransferDTO fromMs(SeasonTicketTransferSeat config) {
        if (config == null) {
            return null;
        }
        
        SeasonTicketTransferDTO configDTO = new SeasonTicketTransferDTO();
        if (config.getTransferPolicy() != null) {
            configDTO.setTransferPolicy(TransferPolicyDTO.valueOf(config.getTransferPolicy().name()));
        }
        
        boolean hasTransferDelay = config.getTransferTicketMaxDelayTime() != null || config.getTransferTicketMinDelayTime() != null;
        configDTO.setEnableTransferDelay(hasTransferDelay);
        configDTO.setTransferTicketMaxDelayTime(config.getTransferTicketMaxDelayTime());
        configDTO.setTransferTicketMinDelayTime(config.getTransferTicketMinDelayTime());
        
        boolean hasRecoveryDelay = config.getRecoveryTicketMaxDelayTime() != null;
        configDTO.setEnableRecoveryDelay(hasRecoveryDelay);
        configDTO.setRecoveryTicketMaxDelayTime(config.getRecoveryTicketMaxDelayTime());
        
        configDTO.setEnableMaxTicketTransfers(config.getEnableMaxTicketTransfers());
        configDTO.setMaxTicketTransfers(config.getMaxTicketTransfers());
        configDTO.setEnableBulk(config.getEnableBulk());
        configDTO.setBulkCustomerTypes(config.getBulkCustomerTypes());
        configDTO.setExcludedSessions(config.getExcludedSessions());

        return configDTO;
    }

    public static UpdateSeasonTicketTransferSeat toMs(SeasonTicketTransferUpdateDTO request) {
        if (request == null) {
            return null;
        }

        UpdateSeasonTicketTransferSeat config = new UpdateSeasonTicketTransferSeat();
        if (request.getTransferPolicy() != null) {
            config.setTransferPolicy(TransferPolicy.valueOf(request.getTransferPolicy().name()));
        }
        
        if (Boolean.FALSE.equals(request.getEnableTransferDelay())) {
            config.setTransferTicketMaxDelayTime(null);
            config.setTransferTicketMinDelayTime(null);
        } else {
            config.setTransferTicketMaxDelayTime(request.getTransferTicketMaxDelayTime());
            config.setTransferTicketMinDelayTime(request.getTransferTicketMinDelayTime());
        }
        
        if (Boolean.FALSE.equals(request.getEnableRecoveryDelay())) {
            config.setRecoveryTicketMaxDelayTime(null);
        } else {
            config.setRecoveryTicketMaxDelayTime(request.getRecoveryTicketMaxDelayTime());
        }
        
        config.setEnableMaxTicketTransfers(request.getEnableMaxTicketTransfers());
        config.setMaxTicketTransfers(request.getMaxTicketTransfers());
        config.setEnableBulk(request.getEnableBulk());
        config.setBulkCustomerTypes(request.getBulkCustomerTypes());
        config.setExcludedSessions(request.getExcludedSessions());
        
        return config;
    }
}
