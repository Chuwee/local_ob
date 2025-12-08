import { Validators } from '@angular/forms';
import { TicketContentFieldsRestrictions as Restrictions } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImageType, TicketContentTextType } from '@admin-clients/cpanel/promoters/events/data-access';

export const PRINTER_TEXT_FIELDS = [
    {
        type: TicketContentTextType.title,
        validators: [Validators.maxLength(Restrictions.titlePrinterLength)]
    },
    {
        type: TicketContentTextType.subtitle,
        validators: [Validators.maxLength(Restrictions.subtitlePrinterLength)]
    },
    {
        type: TicketContentTextType.additionalData,
        validators: [
            Validators.maxLength(Restrictions.additionalDataPrinterLength)
        ]
    }
];

export const PRINTER_IMAGE_FIELDS = [
    { type: TicketContentImageType.body },
    { type: TicketContentImageType.bannerMain },
    { type: TicketContentImageType.bannerSecondary }
];
