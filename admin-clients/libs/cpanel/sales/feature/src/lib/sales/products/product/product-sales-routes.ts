import { TicketsApi, TicketsService, TicketsState } from '@admin-clients/cpanel-sales-data-access';
import { Routes } from '@angular/router';
import { productSalesDetailsResolver } from './details/product-sale-details-resolver';
import { ProductSalesDetailsComponent } from './details/product-sale-details.component';
import { ProductSalesGeneralDataComponent } from './general-data/product-sales-general-data.component';

export const PRODUCT_SALE_ROUTES: Routes = [{
    path: '',
    component: ProductSalesDetailsComponent,
    resolve: {
        ticket: productSalesDetailsResolver
    },
    providers: [
        TicketsService,
        TicketsApi,
        TicketsState
    ],
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: ProductSalesGeneralDataComponent
        }
    ]
}];
