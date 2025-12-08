import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-product-sale-details',
    templateUrl: './product-sale-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        RouterModule,
        NgIf, GoBackComponent,
        AsyncPipe,
        TranslatePipe,
        ObfuscateStringPipe
    ]
})
export class ProductSalesDetailsComponent implements OnDestroy {
    readonly #ticketsSrv = inject(TicketsService);
    ticketDetail$ = this.#ticketsSrv.ticketDetail.get$();

    ngOnDestroy(): void {
        this.#ticketsSrv.ticketDetail.clear();
    }

}
