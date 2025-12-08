import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    EphemeralMessageService, ObMatDialogConfig, SearchTableChangeEvent, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';
import { LoyaltyPointsEditDialogComponent } from './loyalty-points-edit-dialog/loyalty-points-edit-dialog.component';

const PAGE_SIZE = 6;

@Component({
    selector: 'app-customer-loyalty-points',
    templateUrl: './customer-loyalty-points.component.html',
    styleUrls: ['./customer-loyalty-points.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatIconModule, MatButtonModule, SearchTableComponent, MaterialModule, DateTimePipe, NgClass, FormContainerComponent
    ]
})
export class CustomerLoyaltyPointsComponent {
    readonly #customerSrv = inject(CustomersService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly columns = ['date', 'type', 'description', 'points'];
    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly $customer = toSignal(this.#customerSrv.customer.get$().pipe(filter(Boolean)));
    readonly customerLoyaltyPoints$ = this.#customerSrv.customerLoyaltyPoints.getData$();
    readonly customerLoyaltyPointsMetadata$ = this.#customerSrv.customerLoyaltyPoints.getMetaData$();
    readonly $customerTotalPoints = toSignal(this.#customerSrv.customerLoyaltyPoints.getTotalPoints$());
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#customerSrv.customer.loading$(),
        this.#customerSrv.customerLoyaltyPoints.loading$()
    ]));

    #filters: PageableFilter = { offset: 0, limit: PAGE_SIZE };

    loadLoyaltyPointsHistory(event: SearchTableChangeEvent = null): void {
        this.#filters = { ...this.#filters, offset: event?.offset, q: event?.q };
        this.#customerSrv.customerLoyaltyPoints.load(this.$customer().id, this.$customer().entity?.id?.toString(), this.#filters);
    }

    openEditPointsDialog(): void {
        const data = new ObMatDialogConfig({
            actualPoints: this.$customerTotalPoints(),
            customerId: this.$customer().id,
            entityId: this.$customer().entity?.id
        });

        this.#matDialog.open(LoyaltyPointsEditDialogComponent, data)
            .beforeClosed()
            .subscribe(actionPerformed => {
                if (actionPerformed) {
                    this.#customerSrv.customerLoyaltyPoints.load(this.$customer().id,
                        this.$customer().entity?.id?.toString(), this.#filters);

                    this.#ephemeralSrv.showSuccess({ msgKey: 'CUSTOMER.LOYALTY_POINTS_HISTORY.EDIT_SUCCESSFULL' });
                }
            });
    }
}
