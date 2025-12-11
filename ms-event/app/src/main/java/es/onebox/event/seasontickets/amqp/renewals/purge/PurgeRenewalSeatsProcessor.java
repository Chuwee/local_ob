package es.onebox.event.seasontickets.amqp.renewals.purge;

import es.onebox.event.seasontickets.converter.SeasonTicketRenewalsFilterConverter;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurgeRenewalSeatsProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeRenewalSeatsProcessor.class);

    private final SeasonTicketRenewalsService service;

    @Autowired
    public PurgeRenewalSeatsProcessor(SeasonTicketRenewalsService service) {
        this.service = service;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        LOGGER.debug("Init purge renewals");
        try {
            PurgeRenewalSeatsMessage message = exchange.getIn().getBody(PurgeRenewalSeatsMessage.class);

            Long seasonTicketId = message.getSeasonTicketId();
            RenewalSeatsPurgeFilter purgeFilter = SeasonTicketRenewalsFilterConverter.convertToRenewalSeatsPurgeFilter(message);

            service.purgeRenewalSeats(seasonTicketId, purgeFilter);
        } catch (Exception e) {
            LOGGER.error("Error on purge renewals", e);
        }
        LOGGER.debug("End purge renewals");
    }
}
