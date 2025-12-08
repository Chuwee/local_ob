import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { ProductSalesGeneralDataComponent } from '../general-data/product-sales-general-data.component';
import { productSalesDetailsResolver } from './product-sale-details-resolver';
import { ProductSalesDetailsComponent } from './product-sale-details.component';

export const PRODUCT_SALE_DETAILS_ROUTES: Routes = [
    {
        path: '',
        component: ProductSalesDetailsComponent,
        canActivate: [authCanActivateGuard],
        resolve: {
            ticket: productSalesDetailsResolver
        },
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
    }
];
