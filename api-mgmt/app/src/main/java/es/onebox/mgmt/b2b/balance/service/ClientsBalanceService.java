package es.onebox.mgmt.b2b.balance.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.b2b.balance.converter.ClientsBalanceConverter;
import es.onebox.mgmt.b2b.balance.dto.BalanceDTO;
import es.onebox.mgmt.b2b.balance.dto.OperationRequestDTO;
import es.onebox.mgmt.b2b.balance.dto.SearchTransactionsFilterDTO;
import es.onebox.mgmt.b2b.balance.dto.TransactionsDTO;
import es.onebox.mgmt.b2b.balance.enums.OperationType;
import es.onebox.mgmt.b2b.balance.operations.Operation;
import es.onebox.mgmt.b2b.balance.operations.OperationsFactory;
import es.onebox.mgmt.b2b.clients.service.ClientsService;
import es.onebox.mgmt.b2b.utils.B2BUtilsService;
import es.onebox.mgmt.datasources.api.accounting.dto.ProviderClient;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudit;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudits;
import es.onebox.mgmt.datasources.api.accounting.repository.BalanceRepository;
import es.onebox.mgmt.datasources.ms.channel.dto.Channel;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientsBalanceService {

    private static final Integer ALLOWED_DATE_RANGE = 6;

    private final ClientsService clientsService;
    private final B2BUtilsService b2bUtilsService;
    private final BalanceRepository balanceRepository;
    private final ChannelsRepository channelsRepository;
    private final OperationsFactory operationsFactory;

    @Autowired
    public ClientsBalanceService(ClientsService clientsService, B2BUtilsService b2bUtilsService,
                                 BalanceRepository balanceRepository, ChannelsRepository channelsRepository,
                                 OperationsFactory operationsFactory) {
        this.clientsService = clientsService;
        this.b2bUtilsService = b2bUtilsService;
        this.balanceRepository = balanceRepository;
        this.channelsRepository = channelsRepository;
        this.operationsFactory = operationsFactory;
    }

    public void createProviderClientAssociation(Long clientId, Long entityId) {
        entityId = b2bUtilsService.validateEntity(entityId);
        clientsService.validateClient(clientId, entityId);
        balanceRepository.createProviderClientAssociation(entityId, clientId);
    }

    public void deactivateProviderClientAssociation(Long clientId, Long entityId) {
        entityId = b2bUtilsService.validateEntity(entityId);
        clientsService.validateClient(clientId, entityId);
        balanceRepository.deactivateProviderClientAssociation(entityId, clientId);
    }


    public BalanceDTO getClientBalance(Long clientId, Long entityId, String currencyCode) {
        entityId = b2bUtilsService.validateEntity(entityId);
        clientsService.validateClient(clientId, entityId);
        ProviderClient response = balanceRepository.getClientBalance(entityId, clientId);
        return ClientsBalanceConverter.toDTO(response, currencyCode);
    }

    public TransactionsDTO searchClientTransactions(Long clientId, SearchTransactionsFilterDTO filter) {
        validateRequest(clientId, filter);
        TransactionAudits result = balanceRepository.searchTransactions(ClientsBalanceConverter.toMs(clientId, filter));
        return ClientsBalanceConverter.toDTO(result, filter, getChannelNames(result));
    }

    public void performOperation(Long clientId, OperationType operationType,
                                 OperationRequestDTO operationRequest) {
        validateOperation(clientId, operationType, operationRequest);
        Operation operation = operationsFactory.get(operationType);
        operation.execute(clientId, operationRequest);
    }

    private Map<Long, String> getChannelNames(TransactionAudits transactionAudits) {
        ChannelFilter filter = new ChannelFilter();
        filter.setChannelIds(transactionAudits.getTransactionAudits().stream()
                .map(TransactionAudit::getChannelId)
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .toList()
        );
        ChannelsResponse channels = channelsRepository.getChannels(SecurityUtils.getUserOperatorId(), filter);
        return channels.getData().stream()
                .collect(Collectors.toMap(Channel::getId, Channel::getName));
    }

    private void validateRequest(Long clientId, SearchTransactionsFilterDTO filter) {
        b2bUtilsService.validateEntity(filter);
        clientsService.validateClient(clientId, filter.getEntityId());
        validateDateRange(filter.getFrom(), filter.getTo());
    }

    public static void validateDateRange(ZonedDateTime from, ZonedDateTime to) {
        if (from.plusMonths(ALLOWED_DATE_RANGE).isBefore(to)) {
            throw new OneboxRestException(ApiMgmtErrorCode.DATE_RANGE_EXCEEDS_THE_ALLOWED_DATE_RANGE);
        }
    }

    private void validateOperation(Long clientId, OperationType operationType, OperationRequestDTO operationRequest) {
        operationRequest.setEntityId(b2bUtilsService.validateEntity(operationRequest.getEntityId()));
        clientsService.validateClient(clientId, operationRequest.getEntityId());

        if (OperationType.CREDIT_LIMIT.equals(operationType) && operationRequest.getAmount() < 0.0) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                    "amount must be greater or equal 0 for EDIT_CREDIT_LIMIT operation", null);
        }

        if (OperationType.CASH_ADJUSTMENT.equals(operationType)) {
            if (operationRequest.getAmount() * 100.0 == 0) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "amount can not be 0 for CASH_ADJUSTMENT operation", null);
            }
            BalanceDTO balance = getClientBalance(clientId, operationRequest.getEntityId(), operationRequest.getCurrencyCode());
            if (operationRequest.getAmount() < 0
                    && (balance.getDebt() + Math.abs(operationRequest.getAmount()) > (balance.getCreditLimit() + balance.getBalance()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "debt can no be greater than credit limit", null);
            }
        }

        if (OperationType.DEPOSIT.equals(operationType)) {
            if (operationRequest.getAdditionalInfo() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "additional_info can not be null for deposit operation", null);
            }
            if (operationRequest.getAdditionalInfo().getDepositType() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "deposit_type can not be null for deposit operation", null);
            }
            if (operationRequest.getAmount() <= 0.0) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "amount must be greater than 0 for DEPOSIT operation", null);
            }
        }
    }
}
