package es.onebox.common.datasources.ms.ticket.dto;

import es.onebox.common.datasources.ms.event.enums.DigitalTicketMode;

public enum ExternalMode {

    PASSBOOK_FILE,
    WALLET,
    WALLET_NFC;

    public static ExternalMode from(DigitalTicketMode dtm) {
        if (dtm == null) {
            return null;
        }
        return switch (dtm) {
            case DISABLED -> null;
            case WALLET -> WALLET;
            case WALLET_NFC -> WALLET_NFC;
            case PASSBOOK_FILE -> PASSBOOK_FILE;
        };

    }

}
