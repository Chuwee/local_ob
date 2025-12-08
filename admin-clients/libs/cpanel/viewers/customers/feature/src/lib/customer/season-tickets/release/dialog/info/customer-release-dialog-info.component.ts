import {
    VmCustomerSeasonTicketProduct,
    VmCustomerSession
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-customer-release-dialog-info',
    imports: [
        MatIconModule,
        FlexLayoutModule,
        TranslatePipe,
        DateTimePipe
    ],
    styleUrls: ['./customer-release-dialog-info.component.scss'],
    templateUrl: './customer-release-dialog-info.component.html'
})
export class CustomerReleaseDialogInfoComponent {
    readonly dateTimeFormats = DateTimeFormats;
    @Input() seat: VmCustomerSeasonTicketProduct;
    @Input() session: VmCustomerSession;
}
