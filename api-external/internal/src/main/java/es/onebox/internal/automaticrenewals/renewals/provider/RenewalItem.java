package es.onebox.internal.automaticrenewals.renewals.provider;

import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalDTO;

import java.util.List;

public record RenewalItem<T> (
        List<SeasonTicketRenewalDTO> renewalSeats,
        T additionalData
) { }