package es.onebox.internal.automaticrenewals.renewals.provider.xmlsepa;

import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProvider;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalItem;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalSession;
import es.onebox.common.datasources.distribution.dto.order.PaymentRequest;
import es.onebox.common.datasources.distribution.dto.order.PaymentType;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;

@Service
public class XMLSepaProvider implements AutomaticRenewalsProvider<XMLSepaData, String> {

    private static final String RENEWAL_SUBSTATUS = "REMITTANCE";

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLSepaProvider.class);

    public String prepare(SeasonTicketDTO seasonTicket, XMLSepaData data) {
        LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - Preparing data...");
        return RENEWAL_SUBSTATUS;
    }

    @Override
    public RenewalSession<Void> createSession(Long seasonTicketId, String data) {
        return new XMLSeapaRenewalSession(seasonTicketId, data, 15);
    }

    @Override
    public PaymentRequest createPayment(Object additionalData, Double price) {
        return new PaymentRequest(PaymentType.BANK_TRANSFER, price, null, null, null);
    }

    private static class XMLSeapaRenewalSession implements RenewalSession<Void> {

        private final Long seasonTicketId;
        private final String data;
        private final Integer batchSize;
        private Integer total;
        private Integer index;

        public XMLSeapaRenewalSession(Long seasonTicketId, String data, Integer batchSize) {
            this.seasonTicketId = seasonTicketId;
            this.data = data;
            this.batchSize = batchSize;
            total = null;
            index = 0;
        }

        @Override
        public List<RenewalItem<Void>> nextBatch(BiFunction<Long, SeasonTicketRenewalsFilter, SeasonTicketRenewalsDTO> renewalsGetter) {
            if (!hasMore()) return List.of();
            SeasonTicketRenewalsFilter filter = new SeasonTicketRenewalsFilter();
            filter.setLimit(batchSize.longValue());
            filter.setOffset(index.longValue());
            filter.setState(data);
            filter.setAutoRenewal(Boolean.TRUE);
            filter.setRenewalSubstatus(RENEWAL_SUBSTATUS);
            SeasonTicketRenewalsDTO renewals = renewalsGetter.apply(seasonTicketId, filter);
            total = renewals.getMetadata().getTotal().intValue();
            index += batchSize;
            return renewals.getData().stream().map(renewal -> new RenewalItem<Void>(List.of(renewal), null)).toList();
        }

        @Override
        public boolean hasMore() {
            return total == null || index < total;
        }

        @Override
        public Integer getProgress() {
            return total == null ? 0 : index * 100 / total;
        }
    }
}