import { Rate } from '@admin-clients/cpanel/promoters/shared/data-access';
import { UntypedFormControl } from '@angular/forms';

export interface VmEventRate extends Rate {
    nameCtrl: UntypedFormControl;
}
