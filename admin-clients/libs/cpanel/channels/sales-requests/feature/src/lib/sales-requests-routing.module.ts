import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SalesRequestsListComponent } from './list/sales-requests-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        component: SalesRequestsListComponent
    },
    {
        path: ':saleRequestId',
        loadChildren: () => import('./sale-request/sale-request.module').then(m => m.SaleRequestModule),
        data: {
            breadcrumb: 'TITLES.SALE_REQUEST_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SalesRequestsRoutingModule {}
