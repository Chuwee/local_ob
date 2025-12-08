import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const channelTicketImageRestrictions: Record<string, ImageRestrictions> = {
    pdfHeader: { width: 1076, height: 56, size: 25600 },
    pdfBanner: { width: 520, height: 420, size: 61440 },
    passbookChannelImage: { width: 350, height: 60, size: 10240 },
    printerBanner: { width: 456, height: 112, size: 150000 }
};
