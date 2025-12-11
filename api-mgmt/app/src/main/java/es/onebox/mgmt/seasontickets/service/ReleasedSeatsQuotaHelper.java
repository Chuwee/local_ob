package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSession;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketAssignationStatus;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.dto.CreateVenueTagConfigRequestDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReleasedSeatsQuotaHelper {

    public static final String RELEASED_SEAT_QUOTA_CODE = "ST-RS";
    private static final String RELEASED_SEAT_QUOTA_NAME = "Released seats";
    private static final String RELEASED_SEAT_QUOTA_COLOR = "ff8708";
    private final SeasonTicketRepository seasonTicketRepository;
    private final SessionsRepository sessionsRepository;
    private final ValidationService validationService;
    private final VenuesRepository venuesRepository;

    @Autowired
    public ReleasedSeatsQuotaHelper(SeasonTicketRepository seasonTicketRepository,
                                    SessionsRepository sessionsRepository,
                                    ValidationService validationService,
                                    VenuesRepository venuesRepository) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.sessionsRepository = sessionsRepository;
        this.validationService = validationService;
        this.venuesRepository = venuesRepository;
    }

    public void initReleasedSeatsQuota(Set<Long> sessionIds) {
        if (sessionIds == null) {
            return;
        }
        Set<Long> venueTemplateIds = sessionIds.stream()
                .map(sessionsRepository::getSession)
                .filter(Objects::nonNull)
                .map(Session::getVenueConfigId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        venueTemplateIds.forEach(this::getOrCreateReleasedSeatsQuota);
    }

    public void initReleasedSeatsQuota(Long seasonTicketId) {
        SeasonTicketSessionsSearchFilter filter = new SeasonTicketSessionsSearchFilter();
        filter.setAssignationStatus(SeasonTicketAssignationStatus.ASSIGNED);
        SeasonTicketSessions assignedSessions = seasonTicketRepository.getSeasonTicketCandidateSessions(filter, seasonTicketId);
        if (assignedSessions == null || CollectionUtils.isEmpty(assignedSessions.getData())) {
            return;
        }
        Set<Long> sessionIds = assignedSessions.getData().stream().map(SeasonTicketSession::getSessionId).filter(Objects::nonNull).collect(Collectors.toSet());
        initReleasedSeatsQuota(sessionIds);
    }

    public Long getOrCreateReleasedSeatsQuota(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        CreateVenueTagConfigRequestDTO releaseSeatQuota = new CreateVenueTagConfigRequestDTO();
        releaseSeatQuota.setCode(RELEASED_SEAT_QUOTA_CODE);
        releaseSeatQuota.setName(RELEASED_SEAT_QUOTA_NAME);
        releaseSeatQuota.setColor(RELEASED_SEAT_QUOTA_COLOR);

        return getReleasedSeatQuotaId(venueTemplateId)
                .orElseGet(() -> venuesRepository.createQuota(venueTemplateId, releaseSeatQuota));
    }

    private Optional<Long> getReleasedSeatQuotaId(Long venueTemplateId) {
        return venuesRepository.getQuotas(venueTemplateId).stream()
                .filter(quota -> quota.getCode() != null && quota.getCode().equals(RELEASED_SEAT_QUOTA_CODE))
                .map(Quota::getId)
                .findFirst();
    }

    public boolean hasReleasedSeatQuota(List<Long> quotaIds, Long venueTemplateId) {
        if (CollectionUtils.isNotEmpty(quotaIds)) {
            List<Quota> quotas = venuesRepository.getQuotas(venueTemplateId);
            if (CollectionUtils.isNotEmpty(quotaIds)) {
                for (Long quotaId : quotaIds) {
                    if (isReleasedSeatQuota(quotaId, quotas)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isReleasedSeatQuota(Long quotaId, Long venueTemplateId) {
        List<Quota> quotas = venuesRepository.getQuotas(venueTemplateId);
        return isReleasedSeatQuota(quotaId, quotas);
    }

    private boolean isReleasedSeatQuota(Long quotaId, List<Quota> quotas) {
        Optional<Quota> optionalQuota = quotas.stream()
                .filter(quota -> quota.getId().equals(quotaId))
                .findFirst();
        if (optionalQuota.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_QUOTAS);
        }
        Quota quota = optionalQuota.get();
        if (quota.getCode().equals(RELEASED_SEAT_QUOTA_CODE)) {
            return true;
        }
        return false;
    }
}
