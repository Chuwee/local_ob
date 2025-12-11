package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTransferSeat;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferUpdateDTO;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketTransferSeatConverter;
import es.onebox.mgmt.seasontickets.dto.transferseat.SeasonTicketTransferDTO;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketTransferSeatService {

    private final SeasonTicketRepository seasonTicketRepository;
    private final SeasonTicketService seasonTicketService;

    public SeasonTicketTransferSeatService(SeasonTicketRepository seasonTicketRepository, SeasonTicketService seasonTicketService) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public SeasonTicketTransferDTO getSeasonTicketTransferSeat(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketTransferSeat transferSeat = seasonTicketRepository.getSeasonTicketTransferSeat(seasonTicketId);
        return SeasonTicketTransferSeatConverter.fromMs(transferSeat);
    }

    public void updateSeasonTicketTransferSeat(Long seasonTicketId, SeasonTicketTransferUpdateDTO request) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        
        SeasonTicketTransferSeat currentConfig = seasonTicketRepository.getSeasonTicketTransferSeat(seasonTicketId);
        SeasonTicketTransferDTO currentDTO = SeasonTicketTransferSeatConverter.fromMs(currentConfig);
        
        SeasonTicketTransferUpdateDTO mergedRequest = mergeWithCurrent(request, currentDTO);
        
        validateTransferTicketMinDelay(mergedRequest, seasonTicket);
        
        UpdateSeasonTicketTransferSeat transferSeat = SeasonTicketTransferSeatConverter.toMs(mergedRequest);
        seasonTicketRepository.updateSeasonTicketTransferSeat(seasonTicketId, transferSeat);
    }
    
    private SeasonTicketTransferUpdateDTO mergeWithCurrent(SeasonTicketTransferUpdateDTO request, SeasonTicketTransferDTO current) {
        SeasonTicketTransferUpdateDTO merged = new SeasonTicketTransferUpdateDTO();
        
        merged.setTransferPolicy(request.getTransferPolicy() != null ? request.getTransferPolicy() : current.getTransferPolicy());
        merged.setEnableMaxTicketTransfers(request.getEnableMaxTicketTransfers() != null ? request.getEnableMaxTicketTransfers() : current.getEnableMaxTicketTransfers());
        merged.setMaxTicketTransfers(request.getMaxTicketTransfers() != null ? request.getMaxTicketTransfers() : current.getMaxTicketTransfers());
        merged.setEnableBulk(request.getEnableBulk() != null ? request.getEnableBulk() : current.getEnableBulk());
        merged.setBulkCustomerTypes(request.getBulkCustomerTypes() != null ? request.getBulkCustomerTypes() : 
                (current.getBulkCustomerTypes() != null ? current.getBulkCustomerTypes().stream().map(ct -> ct.getId()).toList() : null));
        merged.setExcludedSessions(request.getExcludedSessions() != null ? request.getExcludedSessions() : current.getExcludedSessions());
        
        if (request.getEnableTransferDelay() != null) {
            merged.setEnableTransferDelay(request.getEnableTransferDelay());
            if (Boolean.FALSE.equals(request.getEnableTransferDelay())) {
                merged.setTransferTicketMaxDelayTime(null);
                merged.setTransferTicketMinDelayTime(null);
            } else {
                merged.setTransferTicketMaxDelayTime(request.getTransferTicketMaxDelayTime());
                merged.setTransferTicketMinDelayTime(request.getTransferTicketMinDelayTime());
            }
        } else {
            merged.setEnableTransferDelay(current.getEnableTransferDelay());
            merged.setTransferTicketMaxDelayTime(request.getTransferTicketMaxDelayTime() != null ? request.getTransferTicketMaxDelayTime() : current.getTransferTicketMaxDelayTime());
            merged.setTransferTicketMinDelayTime(request.getTransferTicketMinDelayTime() != null ? request.getTransferTicketMinDelayTime() : current.getTransferTicketMinDelayTime());
        }
        
        if (request.getEnableRecoveryDelay() != null) {
            merged.setEnableRecoveryDelay(request.getEnableRecoveryDelay());
            if (Boolean.FALSE.equals(request.getEnableRecoveryDelay())) {
                merged.setRecoveryTicketMaxDelayTime(null);
            } else {
                merged.setRecoveryTicketMaxDelayTime(request.getRecoveryTicketMaxDelayTime());
            }
        } else {
            merged.setEnableRecoveryDelay(current.getEnableRecoveryDelay());
            merged.setRecoveryTicketMaxDelayTime(request.getRecoveryTicketMaxDelayTime() != null ? request.getRecoveryTicketMaxDelayTime() : current.getRecoveryTicketMaxDelayTime());
        }
        
        return merged;
    }
    
    private void validateTransferTicketMinDelay(SeasonTicketTransferUpdateDTO request, SeasonTicket seasonTicket) {
        if (request.getTransferTicketMinDelayTime() == null || request.getTransferTicketMaxDelayTime() == null) {
            return;
        }
        if (request.getTransferTicketMinDelayTime() <= request.getTransferTicketMaxDelayTime()) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_TRANSFER_SEAT_INVALID_MIN_DELAY_TIMES);
        }
        if (seasonTicket.getTransfer() != null && seasonTicket.getTransfer().getTransferTicketMaxDelayTime() != null
                && request.getTransferTicketMinDelayTime() <= seasonTicket.getTransfer().getTransferTicketMaxDelayTime()) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_TRANSFER_SEAT_INVALID_MIN_DELAY_TIMES);
        }
    }
}
