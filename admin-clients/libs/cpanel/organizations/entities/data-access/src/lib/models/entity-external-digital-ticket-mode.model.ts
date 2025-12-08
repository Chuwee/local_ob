export const digitalTicketModes = ['DISABLED', 'PASSBOOK_FILE', 'WALLET_NFC', 'WALLET'] as const;
export type DigitalTicketModes = typeof digitalTicketModes[number];
