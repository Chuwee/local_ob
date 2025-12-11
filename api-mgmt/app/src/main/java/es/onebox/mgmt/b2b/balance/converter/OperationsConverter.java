package es.onebox.mgmt.b2b.balance.converter;

import es.onebox.mgmt.b2b.balance.dto.OperationRequestDTO;
import es.onebox.mgmt.datasources.api.accounting.dto.BalanceRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.DepositRequest;
import es.onebox.mgmt.security.SecurityUtils;

public class OperationsConverter {

    private OperationsConverter() {}

    public static BalanceRequest toMs(OperationRequestDTO source, Long clientId) {
        BalanceRequest request = new BalanceRequest();
        fill(source, clientId, request);
        return request;
    }

    public static DepositRequest toMsDeposit(OperationRequestDTO source, Long clientId) {
        DepositRequest request = new DepositRequest();
        fill(source, clientId, request);
        request.setTransactionId(source.getAdditionalInfo().getDepositTransactionId());
        request.setTransactionType(source.getAdditionalInfo().getDepositType().getTransactionSupportType());
        return request;
    }

    public static void fill(OperationRequestDTO source, Long clientId, BalanceRequest request) {
        request.setClientId(clientId.intValue());
        request.setUsername(SecurityUtils.getUsername());
        request.setAmount((int) (source.getAmount() * 100L));
        request.setComment(source.getNotes());
        request.setProviderId(source.getEntityId().intValue());
        request.setCurrencyCode(source.getCurrencyCode());
        request.setEffectiveDate(source.getEffectiveDate());
    }
}