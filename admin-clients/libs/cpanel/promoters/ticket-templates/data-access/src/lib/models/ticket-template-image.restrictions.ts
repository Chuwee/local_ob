import { ImageRestrictions } from '@admin-clients/shared/data-access/models';
import { TicketTemplatePdfImageType, TicketTemplatePrinterImageType } from './ticket-template-image-type.enum';

export type TicketTemplatePdfImageRestrictions = {
    [type in TicketTemplatePdfImageType]: ImageRestrictions
};

export type TicketTemplatePrinterImageRestrictions = {
    [type in TicketTemplatePrinterImageType]: ImageRestrictions
};

export type TicketTemplateImageRestrictions = {
    [type in (TicketTemplatePrinterImageType | TicketTemplatePdfImageType)]?: ImageRestrictions
};

export const ticketTemplatePrinterImageRestrictions: TicketTemplatePrinterImageRestrictions = {
    [TicketTemplatePrinterImageType.main]: {
        width: 624, height: 696, size: 150000
    },
    [TicketTemplatePrinterImageType.bannerMain]: {
        width: 456, height: 112, size: 150000
    }
};

export const ticketTemplatePdfImageRestrictions: TicketTemplatePdfImageRestrictions = {
    [TicketTemplatePdfImageType.header]: {
        width: 1076, height: 56, size: 25000
    },
    [TicketTemplatePdfImageType.eventLogo]: {
        width: 636, height: 430, size: 37500
    },
    [TicketTemplatePdfImageType.body]: {
        width: 360, height: 430, size: 50000
    },
    [TicketTemplatePdfImageType.bannerMain]: {
        width: 520, height: 856, size: 85000
    },
    [TicketTemplatePdfImageType.bannerSecondary]: {
        width: 520, height: 420, size: 60000
    },
    [TicketTemplatePdfImageType.bannerChannelLogo]: {
        width: 520, height: 420, size: 60000
    }
};

