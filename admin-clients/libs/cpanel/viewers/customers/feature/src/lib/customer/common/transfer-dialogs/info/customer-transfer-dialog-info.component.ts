import {
    VmCustomerSeasonTicketProduct,
    VmCustomerSession
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import {
    CustomerTransferDialogUserInfoComponent
} from '../user-transferred-info/customer-transfer-dialog-user-info.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-customer-transfer-dialog-info',
    imports: [
        MatIconModule, TranslatePipe, DateTimePipe, CustomerTransferDialogUserInfoComponent
    ],
    styleUrls: ['./customer-transfer-dialog-info.component.scss'],
    templateUrl: './customer-transfer-dialog-info.component.html'
})
export class CustomerTransferDialogInfoComponent {
    readonly dateTimeFormats = DateTimeFormats;
    readonly $seat = input.required<VmCustomerSeasonTicketProduct>({ alias: 'seat' });
    readonly $session = input.required<VmCustomerSession>({ alias: 'session' });
}
