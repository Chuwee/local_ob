import { BuyersService } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { ListFiltersService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BuyerOrderItemsListComponent } from '../order-items-list/buyer-order-items-list.component';

@Component({
    selector: 'app-order-items-tickets',
    templateUrl: './buyer-order-items.component.html',
    styleUrls: ['./buyer-order-items.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    imports: [FormContainerComponent, TranslatePipe, MatExpansionModule,
        BuyerOrderItemsListComponent, MatProgressSpinner
    ]
})
export class BuyerOrderItemsComponent {
    readonly #buyersSrv = inject(BuyersService);

    $isLoading = toSignal(booleanOrMerge([
        this.#buyersSrv.orderItems.loadingSeat$(),
        this.#buyersSrv.orderItems.loadingProduct$()
    ]));
}
