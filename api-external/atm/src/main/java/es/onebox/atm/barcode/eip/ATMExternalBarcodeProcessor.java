package es.onebox.atm.barcode.eip;

import es.onebox.atm.barcode.ATMExternalBarcodeService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ATMExternalBarcodeProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMExternalBarcodeProcessor.class);

    @Autowired
    private ATMExternalBarcodeService atmExternalBarcodeService;

    @Override
    public void execute(Exchange exchange) {
        ATMExternalBarcodeMessage body = exchange.getIn().getBody(ATMExternalBarcodeMessage.class);
        String orderCode = body.getOrderCode();
        Long itemId = body.getItemId();
        Long eventId = body.getEventId();
        Long sessionId = body.getSessionId();

        LOGGER.info("[ATM EXTERNAL BARCODE] [{}] processing external barcode for item: {}, event: {}, session: {}",
                orderCode, itemId, eventId, sessionId);

        atmExternalBarcodeService.addExternalBarcode(orderCode, itemId, eventId, sessionId, body.getRow(), body.getSeat(),
                body.getSectorName(), body.getAttendantData());
    }
}
