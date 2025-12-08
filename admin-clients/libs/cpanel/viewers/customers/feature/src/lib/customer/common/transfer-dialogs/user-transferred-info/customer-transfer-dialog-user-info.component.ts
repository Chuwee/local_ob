import { VmCustomerSession } from '@admin-clients/cpanel-viewers-customers-data-access';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIconModule],
    selector: 'app-customer-transfer-dialog-user-info',
    styleUrls: ['./customer-transfer-dialog-user-info.component.scss'],
    templateUrl: './customer-transfer-dialog-user-info.component.html'
})
export class CustomerTransferDialogUserInfoComponent {
    @Input() session: VmCustomerSession;
}
