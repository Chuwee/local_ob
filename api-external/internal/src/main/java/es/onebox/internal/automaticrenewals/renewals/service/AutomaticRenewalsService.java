package es.onebox.internal.automaticrenewals.renewals.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.internal.automaticrenewals.eip.process.AutomaticRenewalsMessage;
import es.onebox.internal.automaticrenewals.renewals.dto.ExecuteAutomaticRenewalsDTO;
import es.onebox.internal.automaticrenewals.renewals.dto.UpdateAutomaticRenewalsExecutionDTO;
import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsProviderType;
import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsStatus;
import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProvider;
import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProviderFactory;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unchecked")
public class AutomaticRenewalsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticRenewalsService.class);

    private final ObjectMapper jacksonMapper;
    private final DefaultProducer automaticRenewalsProducer;
    private final AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final AutomaticRenewalsProviderFactory automaticRenewalsProviderFactory;

    public AutomaticRenewalsService(ObjectMapper jacksonMapper,
                                    @Qualifier("automaticRenewalsProducer") DefaultProducer automaticRenewalsProducer,
                                    AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService,
                                    SeasonTicketRepository seasonTicketRepository,
                                    AutomaticRenewalsProviderFactory automaticRenewalsProviderFactory) {
        this.jacksonMapper = jacksonMapper;
        this.automaticRenewalsProducer = automaticRenewalsProducer;
        this.automaticRenewalsHazelcastService = automaticRenewalsHazelcastService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.automaticRenewalsProviderFactory = automaticRenewalsProviderFactory;
    }

    public void execute(Long seasonTicketId, ExecuteAutomaticRenewalsDTO request) {
        SeasonTicketDTO seasonTicket = validateSeasonTicket(seasonTicketId);
        SeasonTicketRenewalConfigDTO renewalConfig = seasonTicketRepository.getSeasonTicketRenewalConfig(seasonTicketId);
        AutomaticRenewalsProviderType providerType = AutomaticRenewalsProviderType.valueOf(renewalConfig.getRenewalType());
        AutomaticRenewalsProvider<Object, Object> provider = (AutomaticRenewalsProvider<Object, Object>) automaticRenewalsProviderFactory.get(providerType);
        Object data = provider.prepare(seasonTicket, jacksonMapper.convertValue(request.data(), providerType.getPreparationDataClass()));

        AutomaticRenewalsMessage message = new AutomaticRenewalsMessage.Builder()
                .seasonTicketId(seasonTicketId)
                .channelId(request.channelId())
                .providerType(providerType)
                .data(data)
                .build();
        try {
            automaticRenewalsProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.warn("[AUTOMATIC RENEWALS] - AutomaticRenewalMessage could not be send", e);
        }
    }

    public SeasonTicketDTO validateSeasonTicket(Long seasonTicketId) {
        SeasonTicketDTO seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (BooleanUtils.isNotTrue(seasonTicket.getAllowRenewal())) {
            throw new OneboxRestException(ApiExternalErrorCode.RENEWAL_NOT_ENABLED);
        }
        if (BooleanUtils.isNotTrue(seasonTicket.getRenewal().getAutoRenewal())) {
            throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_RENEWAL_NOT_ENABLED);
        }
        return seasonTicket;
    }

    public void updateExecution(Long seasonTicketId, UpdateAutomaticRenewalsExecutionDTO request) {
        AutomaticRenewalsStatus status = automaticRenewalsHazelcastService.getStatus(seasonTicketId);
        if (AutomaticRenewalsStatus.IN_PROGRESS.equals(status)) {
            automaticRenewalsHazelcastService.setStatus(seasonTicketId, request.status());
        }
    }
}
