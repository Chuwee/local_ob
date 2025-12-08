import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const seasonTicketTicketImageRestrictions = {
    pdfBody: { width: 360, height: 430, size: 51200 } as ImageRestrictions,
    pdfBannerMain: { width: 520, height: 856, size: 87040 } as ImageRestrictions,
    pdfBannerSecondary: { width: 520, height: 420, size: 61440 } as ImageRestrictions,
    printerBody: { width: 624, height: 696, size: 153600 } as ImageRestrictions,
    printerBannerMain: { width: 456, height: 112, size: 153600 } as ImageRestrictions,
    passbookBannerMain: { width: 640, height: 168, size: 153600 } as ImageRestrictions
};
