import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const saleRequestTicketImageRestrictions: Record<string, ImageRestrictions> = {
    pdfHeader: { width: 1076, height: 56, size: 25600 },
    pdfBanner: { width: 520, height: 420, size: 61440 },
    printerBanner: { width: 456, height: 112, size: 150000 }
};
