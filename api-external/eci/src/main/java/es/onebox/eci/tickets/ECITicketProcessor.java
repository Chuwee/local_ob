package es.onebox.eci.tickets;

import es.onebox.eci.tickets.service.GenerateECITicketService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ECITicketProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ECITicketProcessor.class);

    @Autowired
    private GenerateECITicketService generateECITicketService;

    @Override
    public void execute(Exchange exchange) {
        ECITicketMessage body = exchange.getIn().getBody(ECITicketMessage.class);
        String orderCode = body.getCode();
        Long userId = body.getUserId();
        LOGGER.info("[ECI GENERATION TICKET] Read message: {}", orderCode);
        generateECITicketService.generateAndJoinTicketsPDF(orderCode, userId);
    }
}
