package es.onebox.atm.email.config;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PdfTicketGenerationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfTicketGenerationService.class);

    private final DefaultProducer pdfTicketGenerationProducer;

    @Autowired
    public PdfTicketGenerationService(@Qualifier("pdfTicketGenerationProducer") DefaultProducer pdfTicketGenerationProducer) {
        this.pdfTicketGenerationProducer = pdfTicketGenerationProducer;
    }

    public void sendTicketAndReceiptEmail(String orderCode, String language, boolean regenerateTickets) {
        sendPdfTicketGenerationMessage(orderCode, language,true,true, regenerateTickets);
    }

    public void sendTicketEmail(String orderCode, String language, boolean regenerateTickets) {
        sendPdfTicketGenerationMessage(orderCode, language,false,false,false);
    }

    public void sendReceiptEmail(String orderCode, String language) {
        sendPdfTicketGenerationMessage(orderCode,language,false,true,false);
    }

    private void sendPdfTicketGenerationMessage(String orderCode, String language, boolean sendTicket, boolean sendReceipt,
                                        boolean regenerateTickets){

        PdfTicketGenerationMessage pdfTicketGenerationMessage = new PdfTicketGenerationMessage();
        pdfTicketGenerationMessage.setOrderCode(orderCode);
        pdfTicketGenerationMessage.setLanguage(language);
        pdfTicketGenerationMessage.setEmailFromOrder(true);
        pdfTicketGenerationMessage.setSourceApp(PdfTicketGenerationMessage.SourceApp.PORTAL);
        pdfTicketGenerationMessage.setSendTicketEmail(sendTicket);
        pdfTicketGenerationMessage.setSendReceiptEmail(sendReceipt);
        pdfTicketGenerationMessage.setForceRegeneration(regenerateTickets);
        pdfTicketGenerationMessage.setExternal(true);

        try {
            pdfTicketGenerationProducer.sendMessage(pdfTicketGenerationMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] pdfTicketGeneration Message could not be send for orderCode:" + orderCode, e);
        }
    }
}
