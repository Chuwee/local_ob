import { TicketContentFieldsRestrictions as Restrictions } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImageType, TicketContentTextType } from '@admin-clients/cpanel/promoters/events/data-access';
import { Validators } from '@angular/forms';

export const PASSBOOK_TEXT_FIELDS = [
    {
        type: TicketContentTextType.title,
        validators: [Validators.maxLength(Restrictions.titlePassbookLength)]
    },
    {
        type: TicketContentTextType.additionalData1,
        validators: [
            Validators.maxLength(Restrictions.additionalDataPassbookLength)
        ]
    },
    {
        type: TicketContentTextType.additionalData2,
        validators: [
            Validators.maxLength(Restrictions.additionalDataPassbookLength)
        ]
    },
    {
        type: TicketContentTextType.additionalData3,
        validators: [
            Validators.maxLength(Restrictions.additionalDataPassbookLength)
        ]
    }
];

export const PASSBOOK_IMAGE_FIELDS = [{ type: TicketContentImageType.strip }];
