package es.onebox.internal.xmlsepa.converter;

import es.onebox.common.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import es.onebox.common.datasources.ms.event.dto.SeatRenewalStatus;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequest;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequestItem;
import es.onebox.common.datasources.ms.event.dto.XMLSEPAConfigData;
import es.onebox.internal.xmlsepa.eip.sepa.SEPADirectDebitMessage;
import es.onebox.internal.xmlsepa.format.SEPAFormatDate;
import es.onebox.internal.xmlsepa.sepa.SEPABankAccount;
import es.onebox.internal.xmlsepa.sepa.SEPATransaction;
import es.onebox.internal.xmlsepa.sepa.enums.Currency;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SEPAConverter {

    private static final String EMPTY_FIELD = "N/A";
    private static final Integer MAX_STREET_NAME_LENGTH = 70;
    private static final Integer MAX_POSTAL_CODE_LENGTH = 16;
    private static final Integer MAX_CITY_LENGTH = 35;
    private static final String DEFAULT_COUNTRY_CODE = "ZZ";
    private static final String SEPA_SUBSTATUS = "REMITTANCE";

    private SEPAConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static SEPATransaction createTransaction(SeasonTicketRenewalDTO renewalItem, String receiverName, int renewalCount, Date date, BigDecimal price) {
        String userId = renewalItem.getMemberId() != null ? renewalItem.getMemberId() : renewalItem.getUserId();
        String subject = receiverName + " - " + userId + " - " + renewalItem.getSeasonTicketName() + " - " +
                SEPAFormatDate.formatDate(date) + "-" + userId + "-1-1";

        SEPATransaction transaction = new SEPATransaction();
        transaction.setBankAccount(createSenderBankAccount(renewalItem));
        transaction.setValue(price);
        transaction.setSubject(subject);
        transaction.setDate(date);
        transaction.setEndToEndId(renewalCount);
        transaction.setMandatReference(renewalItem.getActualSeat().getSeatId().toString());
        transaction.setMandatReferenceDate(date);
        transaction.setCurrency(Currency.EUR);

        return transaction;
    }

    public static SEPABankAccount createReceiverBankAccount(XMLSEPAConfigData config) {
        return new SEPABankAccount(
                config.getCreditorId(),
                config.getIban(),
                config.getBic(),
                config.getName()
        );
    }

    public static SeasonTicketRenewalsFilter toFilter(Long limit, Long offset, SeatRenewalStatus status) {
        SeasonTicketRenewalsFilter filter = new SeasonTicketRenewalsFilter();
        filter.setAutoRenewal(Boolean.TRUE);
        filter.setLimit(limit);
        filter.setOffset(offset);
        filter.setRenewalStatus(status);
        return filter;
    }

    public static UpdateRenewalRequestItem toUpdate(SeasonTicketRenewalDTO renewalItem) {
        UpdateRenewalRequestItem out = new UpdateRenewalRequestItem();
        out.setUserId(renewalItem.getUserId());
        out.setId(renewalItem.getId());
        out.setRenewalSubstatus(SEPA_SUBSTATUS);
        return out;
    }

    public static UpdateRenewalRequest toUpdate(List<UpdateRenewalRequestItem> updateItems) {
        UpdateRenewalRequest out = new UpdateRenewalRequest();
        out.setItems(updateItems);
        return out;
    }

    public static SEPADirectDebitMessage toMessage(Long seasonTicketId, Long userId) {
        SEPADirectDebitMessage message = new SEPADirectDebitMessage();
        message.setSeasonTicketId(seasonTicketId);
        message.setUserId(userId);
        return message;
    }

    public static XMLSEPAConfigData toXMLSEPAConfigData(EntityBankAccount bankAccount) {
        XMLSEPAConfigData config = new XMLSEPAConfigData();
        config.setIban(bankAccount.getIban());
        config.setBic(bankAccount.getBic());
        config.setName(bankAccount.getName());
        config.setCreditorId(bankAccount.getCc());
        return config;
    }

    private static SEPABankAccount createSenderBankAccount(SeasonTicketRenewalDTO renewalItem) {
        String address = truncateOrEmpty(renewalItem.getAddress(), MAX_STREET_NAME_LENGTH);
        String postalCode = truncateOrEmpty(renewalItem.getPostalCode(), MAX_POSTAL_CODE_LENGTH);
        String city = truncateOrEmpty(renewalItem.getCity(), MAX_CITY_LENGTH);
        String country = renewalItem.getCountry() != null ? renewalItem.getCountry() : DEFAULT_COUNTRY_CODE;

        return new SEPABankAccount(
                renewalItem.getIban(),
                renewalItem.getBic(),
                renewalItem.getName(),
                EMPTY_FIELD,
                address,
                postalCode,
                city,
                country
        );
    }

    private static String truncateOrEmpty(String value, int maxLength) {
        if (value == null) return EMPTY_FIELD;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
