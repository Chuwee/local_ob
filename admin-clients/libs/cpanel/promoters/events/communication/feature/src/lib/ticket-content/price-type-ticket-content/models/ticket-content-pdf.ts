import { TicketContentFieldsRestrictions as Restrictions } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImageType, TicketContentTextType } from '@admin-clients/cpanel/promoters/events/data-access';
import { Validators } from '@angular/forms';

export const PDF_TEXT_FIELDS = [
    {
        type: TicketContentTextType.title,
        validators: [Validators.maxLength(Restrictions.titlePdfLength)]
    },
    {
        type: TicketContentTextType.subtitle,
        validators: [Validators.maxLength(Restrictions.subtitlePdfLength)]
    },
    {
        type: TicketContentTextType.additionalData,
        validators: [Validators.maxLength(Restrictions.additionalDataPdfLength)]
    }
];

export const PDF_IMAGE_FIELDS = [
    { type: TicketContentImageType.body },
    { type: TicketContentImageType.bannerMain },
    { type: TicketContentImageType.bannerSecondary }
];
