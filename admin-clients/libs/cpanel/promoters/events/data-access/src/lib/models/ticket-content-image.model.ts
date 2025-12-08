import { BaseContentImage } from '@admin-clients/shared/data-access/models';
import { ValidatorFn } from '@angular/forms';
import { TicketContentImageType as TicketContentImageType } from './ticket-content-image-type.enum';

export interface TicketContentImageRequest extends BaseContentImage<TicketContentImageType> { }
export interface TicketContentImage extends TicketContentImageRequest {
    image_url?: string;
}

export interface TicketContentImageFields {
    type: TicketContentImageType;
    validators?: ValidatorFn[];
}

