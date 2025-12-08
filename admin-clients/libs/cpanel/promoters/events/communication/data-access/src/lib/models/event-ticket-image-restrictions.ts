import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const eventTicketImageRestrictions: Record<string, ImageRestrictions> = {
    pdfBody: { width: 360, height: 430, size: 51200 },
    pdfBannerMain: { width: 520, height: 856, size: 87040 },
    pdfBannerSecondary: { width: 520, height: 420, size: 61440 },
    printerBody: { width: 624, height: 696, size: 153600 },
    printerBannerMain: { width: 456, height: 112, size: 153600 },
    passbookBannerMain: { width: 640, height: 168, size: 153600 },
    passbookThumbnailMain: { width: 320, height: 322, size: 92160 }
};
