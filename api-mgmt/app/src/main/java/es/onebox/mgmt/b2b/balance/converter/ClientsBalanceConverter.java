package es.onebox.mgmt.b2b.balance.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.serializer.param.ZonedDateTimeParam;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.mgmt.b2b.balance.dto.BalanceDTO;
import es.onebox.mgmt.b2b.balance.dto.BaseSearchTransactionsFilterDTO;
import es.onebox.mgmt.b2b.balance.dto.ClientTransactionsExportFileFieldDTO;
import es.onebox.mgmt.b2b.balance.dto.ClientTransactionsExportRequestDTO;
import es.onebox.mgmt.b2b.balance.dto.CurrencyBalanceDTO;
import es.onebox.mgmt.b2b.balance.dto.SearchTransactionsFilterDTO;
import es.onebox.mgmt.b2b.balance.dto.TransactionDTO;
import es.onebox.mgmt.b2b.balance.dto.TransactionsDTO;
import es.onebox.mgmt.b2b.balance.enums.DepositType;
import es.onebox.mgmt.b2b.balance.enums.TransactionType;
import es.onebox.mgmt.datasources.api.accounting.dto.ClientTransactionsExportFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.CurrencyBalance;
import es.onebox.mgmt.datasources.api.accounting.dto.MovementType;
import es.onebox.mgmt.datasources.api.accounting.dto.ProviderClient;
import es.onebox.mgmt.datasources.api.accounting.dto.SearchTransactionsFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudit;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudits;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionSupportType;
import es.onebox.mgmt.datasources.api.accounting.enums.ClientTransactionField;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.export.dto.ExportFilter;
import org.apache.commons.collections.CollectionUtils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientsBalanceConverter {

    private static final String CPANEL_OPERATION_CHANNEL = "CPanel";

    private ClientsBalanceConverter() {
    }

    public static BalanceDTO toDTO(ProviderClient source, String currencyCode) {
        BalanceDTO target = new BalanceDTO();
        target.setBalance(source.getBalance().doubleValue() / 100);
        target.setCreditLimit(source.getMaxCredit().doubleValue() / 100);
        target.setDebt(source.getUsedCredit().doubleValue() / 100);
        target.setTotalAvailable(target.getBalance() + target.getCreditLimit() - target.getDebt());
        target.setCurrencyCode(source.getCurrencyCode());
        if (currencyCode != null && CollectionUtils.isNotEmpty(source.getCurrenciesBalance())) {
            CurrencyBalance currencyProvider = source.getCurrenciesBalance().stream()
                    .filter(c -> currencyCode.equals(c.getCurrencyCode()))
                    .findFirst().orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.B2B_BALANCE_CURRENCY_NOT_FOUND));
            target.setBalance(currencyProvider.getBalance().doubleValue() / 100);
            target.setCreditLimit(currencyProvider.getMaxCredit().doubleValue() / 100);
            target.setDebt(currencyProvider.getUsedCredit().doubleValue() / 100);
            target.setTotalAvailable(target.getBalance() + target.getCreditLimit() - target.getDebt());
            target.setCurrencyCode(currencyProvider.getCurrencyCode());
        } else {
            if (CollectionUtils.isNotEmpty(source.getCurrenciesBalance())) {
                toDTOByCurrencies(target, source.getCurrenciesBalance());
            }
        }
        return target;
    }

    private static void toDTOByCurrencies(BalanceDTO target, List<CurrencyBalance> currencyBalances) {
        List<CurrencyBalanceDTO> currencyBalanceList = new ArrayList<>();
        currencyBalances.forEach(c -> {
            CurrencyBalanceDTO currencyBalance = new CurrencyBalanceDTO();
            currencyBalance.setCurrencyCode(c.getCurrencyCode());
            currencyBalance.setCreditLimit(c.getMaxCredit().doubleValue() / 100);
            currencyBalance.setDebt(c.getUsedCredit().doubleValue() / 100);
            currencyBalance.setBalance(c.getBalance().doubleValue() / 100);
            currencyBalance.setTotalAvailable(currencyBalance.getBalance() + currencyBalance.getCreditLimit() - currencyBalance.getDebt());
            currencyBalanceList.add(currencyBalance);
        });
        target.setCurrenciesBalance(currencyBalanceList);
    }

    public static SearchTransactionsFilter toMs(Long clientId, SearchTransactionsFilterDTO source) {
        SearchTransactionsFilter target = new SearchTransactionsFilter();
        target.setClientId(clientId);
        target.setProviderId(source.getEntityId());
        target.setDateFrom(new ZonedDateTimeParam(source.getFrom()));
        target.setDateTo(new ZonedDateTimeParam(source.getTo()));
        target.setFreeText(source.getQ());
        target.setNumberOfResults(source.getLimit().intValue());
        target.setFromElement(source.getOffset().intValue());
        target.setMovementType(toMs(source.getType()));
        target.setCurrencyCode(source.getCurrencyCode());
        return target;
    }

    public static SearchTransactionsFilter toMs(Long clientId, BaseSearchTransactionsFilterDTO source) {
        SearchTransactionsFilter target = new SearchTransactionsFilter();
        target.setClientId(clientId);
        if (source != null) {
            target.setDateFrom(new ZonedDateTimeParam(source.getFrom()));
            target.setDateTo(new ZonedDateTimeParam(source.getTo()));
            target.setFreeText(source.getQ());
            target.setMovementType(toMs(source.getType()));
            target.setCurrencyCode(source.getCurrencyCode());
        }
        return target;
    }

    private static MovementType toMs(TransactionType source) {
        if (source == null) {
            return null;
        }
        return switch (source) {
            case REFUND -> MovementType.REFUND;
            case DEPOSIT -> MovementType.ADD_AMOUNT;
            case PURCHASE -> MovementType.PAYMENT;
            case CREDIT_LIMIT -> MovementType.CHANGE_MAX_CREDIT;
            case CASH_ADJUSTMENT -> MovementType.MODIFY_AMOUNT;
        };
    }

    public static TransactionsDTO toDTO(TransactionAudits source, BaseRequestFilter filter, Map<Long, String> channelNames) {
        TransactionsDTO target = new TransactionsDTO();
        Metadata metadata = new Metadata();
        metadata.setTotal(source.getTotalElements());
        metadata.setLimit(filter.getLimit());
        metadata.setOffset(filter.getOffset());
        target.setMetadata(metadata);
        target.setData(source.getTransactionAudits().stream().map(transaction -> toDTO(transaction, channelNames)).toList());
        return target;
    }

    private static TransactionDTO toDTO(TransactionAudit source, Map<Long, String> channelNames) {
        TransactionDTO target = new TransactionDTO();
        target.setId(source.getMovementId());
        target.setTransactionCode(source.getTransactionId());
        target.setOrderCode(source.getLocator());
        target.setCreated(ZonedDateTime.ofInstant(Instant.ofEpochMilli(source.getTimestamp()), DateUtils.getUTC()));
        target.setUser(source.getUsername());
        target.setNotes(source.getComment());
        target.setTransactionType(toDTO(source.getMovementType()));
        target.setDepositType(toDTO(source.getTransactionType()));
        target.setChannel(source.getChannelId() != null ? channelNames.get(source.getChannelId().longValue()) : CPANEL_OPERATION_CHANNEL);
        target.setPreviousBalance(source.getOldBalance().doubleValue() / 100);
        target.setAmount(source.getAmount().doubleValue() / 100);
        target.setCredit(source.getNewMaxCredit().doubleValue() / 100);
        target.setBalance(source.getNewBalance().doubleValue() / 100);
        target.setDebt(source.getNewUsedCredit().doubleValue() / 100);
        return target;
    }

    private static TransactionType toDTO(MovementType source) {
        if (source == null) {
            return null;
        }
        return switch (source) {
            case REFUND -> TransactionType.REFUND;
            case ADD_AMOUNT -> TransactionType.DEPOSIT;
            case PAYMENT -> TransactionType.PURCHASE;
            case CHANGE_MAX_CREDIT -> TransactionType.CREDIT_LIMIT;
            case MODIFY_AMOUNT -> TransactionType.CASH_ADJUSTMENT;
        };
    }

    private static DepositType toDTO(TransactionSupportType source) {
        if (source == null) {
            return null;
        }
        return switch (source) {
            case CASH -> DepositType.CASH;
            case WIRE -> DepositType.TRANSFER;
            case CHECK -> DepositType.CHECK;
        };
    }

    public static ClientTransactionsExportFilter toFilter(Long clientId, ClientTransactionsExportRequestDTO body,
                                                          ExportFilter<ClientTransactionsExportFileFieldDTO> baseFilter) {
        ClientTransactionsExportFilter filter = new ClientTransactionsExportFilter();
        filter.setFilter(toMs(clientId, body.getFilter()));
        filter.getFilter().setProviderId(body.getEntityId());
        filter.setEmail(baseFilter.getEmail());
        filter.setLanguage(baseFilter.getLanguage());
        filter.setUserId(baseFilter.getUserId());
        filter.setTranslations(baseFilter.getTranslations());
        filter.setFormat(baseFilter.getFormat());
        filter.setTimeZone(baseFilter.getTimeZone());
        filter.setFields(body.getFields().stream()
                .map(elem -> new es.onebox.mgmt.datasources.api.accounting.dto.ClientTransactionsExportFileFieldDTO(
                        ClientTransactionField.valueOf(elem.getField().name())))
                .toList());
        return filter;
    }
}
