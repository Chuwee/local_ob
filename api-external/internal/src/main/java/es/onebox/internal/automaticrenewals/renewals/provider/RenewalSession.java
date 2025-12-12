package es.onebox.internal.automaticrenewals.renewals.provider;

import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;

import java.util.List;
import java.util.function.BiFunction;

public interface RenewalSession<T> {
    List<RenewalItem<T>> nextBatch(BiFunction<Long, SeasonTicketRenewalsFilter, SeasonTicketRenewalsDTO> renewalsGetter);
    boolean hasMore();
    Integer getProgress();
}
