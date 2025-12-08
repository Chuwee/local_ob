import { CustomersService, CustomerProductsService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { CustomerExternalProductsListComponent } from '../external-products-list/customer-external-products-list.component';
import { CustomerProductsListComponent } from '../products-list/customer-products-list.component';

@Component({
    selector: 'app-customer-products-container',
    imports: [FormContainerComponent, TranslatePipe, MatExpansionModule, AsyncPipe, MatSpinner,
        CustomerProductsListComponent, CustomerExternalProductsListComponent
    ],
    templateUrl: './customer-products-container.component.html',
    styleUrls: ['./customer-products-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        CustomerProductsService
    ]
})
export class CustomerProductsContainerComponent implements OnInit {
    isLoading$: Observable<boolean>;

    constructor(
        private _customerProductsSrv: CustomerProductsService,
        private _customersSrv: CustomersService
    ) {
    }

    ngOnInit(): void {
        this.model();
    }

    private model(): void {
        this.isLoading$ = booleanOrMerge([
            this._customerProductsSrv.isProductsListInProgress$(),
            this._customerProductsSrv.isExternalProductsListInProgress$(),
            this._customersSrv.customer.loading$()
        ]);
    }
}
