package es.onebox.event.seasontickets.amqp.renewals.commit;

import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommitRenewalsProcessor extends DefaultProcessor {

    private final SeasonTicketRenewalsService service;

    @Autowired
    public CommitRenewalsProcessor(SeasonTicketRenewalsService service) {
        this.service = service;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        CommitRenewalsMessage message = exchange.getIn().getBody(CommitRenewalsMessage.class);

        if(message.getItems() != null && !message.getItems().isEmpty()) {
            message.getItems().forEach(item -> {
                service.commitRenewal(item.getSeasonTicketId(), item.getUserId(), item.getRenewalId(),
                        item.getOrderCode(), item.getRateId(), item.getPurchaseDate());
            });

            Map<Long, Set<String>> seasonTicketUsers = message.getItems().stream()
                    .collect(Collectors.groupingBy(CommitRenewalsItem::getSeasonTicketId,
                            Collectors.mapping(CommitRenewalsItem::getUserId, Collectors.toSet())));
            seasonTicketUsers.forEach((seasonTicketId, userIds) ->
                    service.migrateCustomerRenewals(userIds.stream().collect(Collectors.toList()), seasonTicketId));
        }
    }
}
