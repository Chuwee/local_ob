package es.onebox.internal.automaticrenewals.renewals.service;

import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsStatus;
import es.onebox.common.config.HazelcastConfiguration;
import es.onebox.common.datasources.ms.event.dto.UpdateSeasonTicketAutomaticRenewalStatus;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AutomaticRenewalsHazelcastService {

    private final HazelcastMapService hazelcastMapService;
    private final SeasonTicketRepository seasonTicketRepository;

    public AutomaticRenewalsHazelcastService(HazelcastMapService hazelcastMapService, SeasonTicketRepository seasonTicketRepository) {
        this.hazelcastMapService = hazelcastMapService;
        this.seasonTicketRepository = seasonTicketRepository;
    }

    public void setStatus(Long seasonTicketId, AutomaticRenewalsStatus status) {
        hazelcastMapService.putIntoMapWithTTL(HazelcastConfiguration.API_EXTERNAL_AUTOMATIC_RENEWALS_MAP, seasonTicketId.toString(), status, 240, TimeUnit.MINUTES);
        seasonTicketRepository.updateAutomaticRenewalStatus(seasonTicketId, new UpdateSeasonTicketAutomaticRenewalStatus(status.name()));
    }

    public AutomaticRenewalsStatus getStatus(Long seasonTicketId) {
        return hazelcastMapService.getObjectFromMap(HazelcastConfiguration.API_EXTERNAL_AUTOMATIC_RENEWALS_MAP, seasonTicketId.toString());
    }
}
