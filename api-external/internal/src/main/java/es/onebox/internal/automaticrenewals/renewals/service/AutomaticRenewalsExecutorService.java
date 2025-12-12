package es.onebox.internal.automaticrenewals.renewals.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodDTO;
import es.onebox.common.datasources.ms.channel.enums.DeliveryMethod;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.internal.automaticrenewals.eip.process.AutomaticRenewalsMessage;
import es.onebox.internal.automaticrenewals.eip.progress.AutomaticRenewalsProgressMessage;
import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsProviderType;
import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsStatus;
import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProvider;
import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProviderFactory;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalItem;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalSession;
import es.onebox.internal.utils.progress.ProgressService;
import es.onebox.internal.utils.progress.enums.ConsumerType;
import es.onebox.internal.utils.progress.enums.EventMessageType;
import es.onebox.internal.utils.progress.enums.StatusMessage;
import es.onebox.internal.utils.progress.model.ProgressMessage;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.RenewalSeat;
import es.onebox.common.datasources.distribution.dto.RenewalSeats;
import es.onebox.common.datasources.distribution.dto.deliverymethods.DeliveryMethodsRequestDTO;
import es.onebox.common.datasources.distribution.dto.deliverymethods.OrderDeliveryMethod;
import es.onebox.common.datasources.distribution.dto.order.ConfirmRequest;
import es.onebox.common.datasources.distribution.dto.order.PaymentRequest;
import es.onebox.common.datasources.distribution.repository.DistributionRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalDTO;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unchecked")
public class AutomaticRenewalsExecutorService {

    private static final String BUYER_USER_ID = "user_id";
    private static final String BUYER_DATA_EMAIL = "email";
    private static final String BUYER_DATA_NAME = "name";
    private static final String BUYER_DATA_SURNAME = "lastname";

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticRenewalsExecutorService.class);

    private final AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService;
    private final AutomaticRenewalsService automaticRenewalsService;
    private final AutomaticRenewalsProviderFactory providerFactory;
    private final ObjectMapper jacksonMapper;
    private final SeasonTicketRepository seasonTicketRepository;
    private final DistributionRepository distributionRepository;
    private final TokenRepository tokenRepository;
    private final ChannelConfigRepository channelConfigRepository;
    private final ProgressService progressService;
    private final ChannelRepository channelRepository;

    public AutomaticRenewalsExecutorService(AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService,
                                            AutomaticRenewalsService automaticRenewalsService,
                                            ObjectMapper jacksonMapper,
                                            AutomaticRenewalsProviderFactory providerFactory,
                                            SeasonTicketRepository seasonTicketRepository,
                                            DistributionRepository distributionRepository,
                                            TokenRepository tokenRepository,
                                            ChannelConfigRepository channelConfigRepository,
                                            ProgressService progressService, ChannelRepository channelRepository) {
        this.automaticRenewalsHazelcastService = automaticRenewalsHazelcastService;
        this.automaticRenewalsService = automaticRenewalsService;
        this.jacksonMapper = jacksonMapper;
        this.providerFactory = providerFactory;
        this.seasonTicketRepository = seasonTicketRepository;
        this.distributionRepository = distributionRepository;
        this.tokenRepository = tokenRepository;
        this.channelConfigRepository = channelConfigRepository;
        this.progressService = progressService;
        this.channelRepository = channelRepository;
    }

    public void execute(AutomaticRenewalsMessage message) {
        Long seasonTicketId = message.getSeasonTicketId();
        SeasonTicketDTO seasonTicket = automaticRenewalsService.validateSeasonTicket(seasonTicketId);
        LOGGER.info("[AUTOMATIC RENEWALS] Staring process for season ticket: {}", seasonTicketId);
        automaticRenewalsHazelcastService.setStatus(seasonTicketId, AutomaticRenewalsStatus.IN_PROGRESS);
        AutomaticRenewalsProviderType providerType = message.getProviderType();
        AutomaticRenewalsProvider<Object, Object> provider = (AutomaticRenewalsProvider<Object, Object>) providerFactory.get(providerType);

        ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(message.getChannelId());

        RenewalSession<?> renewalSession = provider.createSession(message.getSeasonTicketId(), jacksonMapper.convertValue(message.getData(), providerType.getExecutionDataClass()));

        sendProgress(seasonTicket.getEntityId(), seasonTicketId, 0, StatusMessage.IN_PROGRESS);
        ChannelDeliveryMethodDTO deliveryMethod = null;
        try {
            deliveryMethod = channelRepository.getChannelDeliveryMethods(channelConfig.getId())
                    .getDeliveryMethods().stream().filter(it -> BooleanUtils.isTrue(it.getDefaultMethod())).findFirst().orElse(null);
        } catch (Exception e) {
            LOGGER.info("[AUTOMATIC RENEWALS] Error retrieving delivery methods for channel: {} in season ticket: {}", channelConfig.getId(), seasonTicketId);
        }

        while(renewalSession.hasMore() && !AutomaticRenewalsStatus.BLOCKED.equals(automaticRenewalsHazelcastService.getStatus(seasonTicketId))) {
            var renewals = renewalSession.nextBatch(seasonTicketRepository::getSeasonTicketRenewals);
            String token = tokenRepository.getSellerChannelToken(channelConfig.getId(), channelConfig.getApiKey());
            for (RenewalItem<?> renewalItem : renewals) {
                try {
                    createRenewal(renewalItem, token, seasonTicket.getSessionId(), deliveryMethod, provider::createPayment);
                } catch (Exception e) {
                    String ids = renewalItem.renewalSeats().stream().map(SeasonTicketRenewalDTO::getId).collect(Collectors.joining(", "));
                    LOGGER.info("[AUTOMATIC RENEWALS] Error on renewal creation for season ticket: {} and ids: {}", seasonTicketId, ids, e);
                }
            }
            sendProgress(seasonTicket.getEntityId(), seasonTicketId, renewalSession.getProgress(), StatusMessage.IN_PROGRESS);
        }

        sendProgress(seasonTicket.getEntityId(), seasonTicketId, 100, StatusMessage.DONE);
        automaticRenewalsHazelcastService.setStatus(seasonTicketId, AutomaticRenewalsStatus.DONE);
        LOGGER.info("[AUTOMATIC RENEWALS] Finished renewal generation for season ticket {}", seasonTicketId);
    }

    private void sendProgress(Long entityId, Long seasonTicketId, Integer progress, StatusMessage status) {
        try {
            ProgressMessage progressMessage = buildProgressMessage(entityId, seasonTicketId);
            progressService.sendNotificationProgress(progressMessage, progress, status, ConsumerType.SEASON_TICKET);
        } catch (Exception e) {
            LOGGER.info("[AUTOMATIC RENEWALS] Error sending notification for id: {}", seasonTicketId);
        }
    }

    private AutomaticRenewalsProgressMessage buildProgressMessage(Long entityId, Long seasonTicketId) {
        AutomaticRenewalsProgressMessage progressMessage = new AutomaticRenewalsProgressMessage("NOTIFICATION");
        progressMessage.setId(seasonTicketId);
        progressMessage.setEntityId(entityId);
        progressMessage.setSeasonTicketId(seasonTicketId);
        progressMessage.setType(EventMessageType.AUTOMATIC_RENEWALS);
        return progressMessage;
    }

    private void createRenewal(RenewalItem<?> renewalItem, String token, Long sessionId, ChannelDeliveryMethodDTO defaultDeliveryMethod,
                               BiFunction<Object, Double, PaymentRequest> paymentGetter) {

        List<SeasonTicketRenewalDTO> renewals = renewalItem.renewalSeats();
        SeasonTicketRenewalDTO renewalBase = renewals.get(0);
        OrderResponse orderResponse = distributionRepository.createOrder(token, renewalBase.getLanguage());

        RenewalSeats renewalSeats = new RenewalSeats();
        renewalSeats.setRenewalSeats(renewals.stream().map(renewal -> {
            RenewalSeat renewalSeat = new RenewalSeat();
            renewalSeat.setId(renewal.getActualSeat().getSeatId());
            renewalSeat.setSessionId(sessionId);
            renewalSeat.setRenewalId(renewal.getId());
            renewalSeat.setUserId(renewal.getUserId());
            renewalSeat.setRateId(renewal.getActualRateId());
            return renewalSeat;
        }).toList());

        distributionRepository.addRenewalSeats(token, orderResponse.getId(), renewalSeats);


        DeliveryMethodsRequestDTO deliveryMethod = new DeliveryMethodsRequestDTO();
        if (defaultDeliveryMethod != null) {
            deliveryMethod.setType(fromDeliveryMethod(defaultDeliveryMethod.getType()));
            deliveryMethod.setCost(defaultDeliveryMethod.getCost());
        } else {
            deliveryMethod.setType(OrderDeliveryMethod.EMAIL);
        }
        orderResponse = distributionRepository.setDeliveryMethods(token, orderResponse.getId(), deliveryMethod);

        orderResponse = distributionRepository.addBuyerData(token, orderResponse.getId(), getClientData(renewalBase));

        orderResponse = distributionRepository.preConfirm(token, orderResponse.getId(), null);

        List<PaymentRequest> payments = List.of(paymentGetter.apply(renewalItem.additionalData(), orderResponse.getPrice().getFinalPrice() ));
        ConfirmRequest confirmRequest = new ConfirmRequest(payments, null, null);
        distributionRepository.confirm(token, orderResponse.getId(), confirmRequest);
    }

    private Map<String, Object> getClientData(SeasonTicketRenewalDTO renewal) {
        return Map.of(
                BUYER_USER_ID, renewal.getUserId(),
                BUYER_DATA_EMAIL, renewal.getEmail(),
                BUYER_DATA_NAME, renewal.getName(),
                BUYER_DATA_SURNAME, renewal.getSurname()
        );
    }

    private OrderDeliveryMethod fromDeliveryMethod(DeliveryMethod deliveryMethod) {
        if (deliveryMethod == null) {
            return null;
        }
        return switch (deliveryMethod.getId()) {
            case 1 -> OrderDeliveryMethod.EMAIL;
            case 2 -> OrderDeliveryMethod.VENUE_PICKUP;
            case 3 -> OrderDeliveryMethod.PRINT_EXPRESS;
            case 4 -> OrderDeliveryMethod.PHONE;
            case 5 -> OrderDeliveryMethod.EXTERNAL;
            case 6 -> OrderDeliveryMethod.NATIONAL_POST;
            case 7 -> OrderDeliveryMethod.INTERNATIONAL_POST;
            case 8 -> OrderDeliveryMethod.WHATSAPP;
            default -> null;
        };
    }
}