import { OrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { BuyerData } from '@admin-clients/shared/common/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { OrderProfileAttributesDialogComponent } from '../order-profile-attributes/order-profile-attributes-dialog.component';

@Component({
    selector: 'app-order-details-buyer-data',
    templateUrl: './buyer-data.component.html',
    styleUrls: ['./buyer-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, MatTooltipModule, MatIconModule, MatButtonModule, EllipsifyDirective, RouterLink]
})
export class OrderDetailsBuyerDataComponent {
    readonly #dialog = inject(MatDialog);

    readonly $order = input.required<OrderDetail>({ alias: 'order' });

    openProfileAttributesDialog(attributes: BuyerData['profile_data']['attributes']): void {
        this.#dialog.open(OrderProfileAttributesDialogComponent, new ObMatDialogConfig(attributes));
    }
}
