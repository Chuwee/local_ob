import {
    EventChannelContentImageType, EventChannelContentTextType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ValidatorFn } from '@angular/forms';

export interface PackContentTextFields {
    type: EventChannelContentTextType;
    validators?: ValidatorFn[];
}

export interface PackContentImageFields {
    type: EventChannelContentImageType;
    validators?: ValidatorFn[];
    position?: number;
}
