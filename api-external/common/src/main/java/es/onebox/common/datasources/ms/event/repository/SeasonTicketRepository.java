package es.onebox.common.datasources.ms.event.repository;

import es.onebox.common.datasources.ms.event.MsEventDatasource;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketPrice;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import es.onebox.common.datasources.ms.event.dto.UpdateSeasonTicketAutomaticRenewalStatus;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeasonTicketRepository {

    private final MsEventDatasource msEventDatasource;

    public SeasonTicketRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public SeasonTicketDTO getSeasonTicket(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicket(seasonTicketId);
    }

    public SeasonTicketRenewalsDTO getSeasonTicketRenewals(Long seasonTicketId, SeasonTicketRenewalsFilter filter) {
        return msEventDatasource.getSeasonTicketRenewals(seasonTicketId, filter);
    }

    public SeasonTicketRenewalConfigDTO getSeasonTicketRenewalConfig(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketRenewalConfig(seasonTicketId);
    }

    public void updateAutomaticRenewalStatus(Long seasonTicketId, UpdateSeasonTicketAutomaticRenewalStatus body) {
        msEventDatasource.updateSeasonTicketRenewalStatus(seasonTicketId, body);
    }
  
    public void updateSeasonTicketRenewals(Long seasonTicketId, UpdateRenewalRequest request) {
        msEventDatasource.updateSeasonTicketRenewals(seasonTicketId, request);
    }

    public List<SeasonTicketPrice> getSeasonTicketPrices(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketPrices(seasonTicketId);
    }
}