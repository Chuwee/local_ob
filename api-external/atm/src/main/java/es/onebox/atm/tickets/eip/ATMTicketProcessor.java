package es.onebox.atm.tickets.eip;

import es.onebox.atm.tickets.ATMGenerateTicketService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMTicketProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMTicketProcessor.class);

    @Autowired
    private ATMGenerateTicketService atmGenerateTicketService;

    @Override
    public void execute(Exchange exchange) {
        ATMTicketMessage body = exchange.getIn().getBody(ATMTicketMessage.class);
        String orderCode = body.getCode();
        LOGGER.info("[ATM GENERATION TICKET] Read message: {}", orderCode);
        atmGenerateTicketService.generateAndJoinTicketsPDF(orderCode, null);
    }
}
