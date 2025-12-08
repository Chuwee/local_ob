import { TransferDataSessionDeliveryMethod } from '@admin-clients/shared/common/data-access';
import { FormControl } from '@angular/forms';

export interface CustomerTransferTransferDialogForm {
    name: FormControl<string>;
    surname: FormControl<string>;
    send_type: FormControl<TransferDataSessionDeliveryMethod>;
    email: FormControl<string>;
}
