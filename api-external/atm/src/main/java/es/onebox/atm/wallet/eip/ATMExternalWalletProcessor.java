package es.onebox.atm.wallet.eip;

import es.onebox.atm.wallet.ATMExternalWalletService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMExternalWalletProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMExternalWalletProcessor.class);

    @Autowired
    private ATMExternalWalletService atmExternalWalletService;

    @Override
    public void execute(Exchange exchange) {
        ATMExternalWalletMessage body = exchange.getIn().getBody(ATMExternalWalletMessage.class);
        String orderCode = body.getOrderCode();
        Long itemId = body.getItemId();
        Long eventId = body.getEventId();
        Long sessionId = body.getSessionId();

        LOGGER.info("[ATM EXTERNAL WALLET] [{}] processing external wallet for item: {}, event: {}, session: {}",
                orderCode, itemId, eventId, sessionId);

        atmExternalWalletService.forceAvetWalletGeneration(eventId, sessionId, orderCode, itemId);
    }
}
