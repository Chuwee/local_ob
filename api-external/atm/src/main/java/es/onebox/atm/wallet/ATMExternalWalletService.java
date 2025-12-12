package es.onebox.atm.wallet;

import es.onebox.atm.config.ATMEntityConfiguration;
import es.onebox.common.datasources.ms.event.enums.DigitalTicketMode;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.ticket.dto.ExternalMode;
import es.onebox.common.datasources.ms.ticket.repository.MsTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ATMExternalWalletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMExternalWalletService.class);

    private final MsTicketRepository msTicketDatasource;
    private final MsEventRepository msEventRepository;
    private final ATMEntityConfiguration atmEntityConfiguration;

    @Autowired
    public ATMExternalWalletService(MsTicketRepository msTicketDatasource, MsEventRepository msEventRepository,
                                    ATMEntityConfiguration atmEntityConfiguration) {
        this.msTicketDatasource = msTicketDatasource;
        this.msEventRepository = msEventRepository;
        this.atmEntityConfiguration = atmEntityConfiguration;
    }


    public void forceAvetWalletGeneration(Long eventId, Long sessionId, String orderCode, Long itemId) {
        try {
            DigitalTicketMode dtm = msEventRepository.getDigitalTicketMode(atmEntityConfiguration.getEntityId(), eventId, sessionId);
            if (DigitalTicketMode.WALLET_NFC.equals(dtm) || DigitalTicketMode.WALLET.equals(dtm)) {
                msTicketDatasource.getItemPassbook(orderCode, itemId, ExternalMode.from(dtm));
                LOGGER.info("[ATM AVET WALLET][{}] pre generated avet wallet for item: {}", orderCode, itemId);
            }
        } catch (Exception e) {
            LOGGER.warn("[ATM AVET WALLET][{}] Error while forcing avet wallet generation for item {}", orderCode, itemId, e);
            throw e;
        }
    }
}
