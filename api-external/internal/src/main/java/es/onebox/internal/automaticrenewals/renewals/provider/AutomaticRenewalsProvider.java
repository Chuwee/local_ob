package es.onebox.internal.automaticrenewals.renewals.provider;

import es.onebox.common.datasources.distribution.dto.order.PaymentRequest;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;

public interface AutomaticRenewalsProvider<T, Y> {
    Y prepare(SeasonTicketDTO seasonTicket, T data);
    RenewalSession<?> createSession(Long seasonTicketId, Y data);
    PaymentRequest createPayment(Object additionalData, Double price);
}
