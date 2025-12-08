import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { VoucherOrderListComponent } from './list/voucher-orders-list.component';

const routes: Routes = [
    {
        path: '',
        component: VoucherOrderListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':voucherOrderCode',
        loadChildren: () => import('./voucher-order/voucher-order-details.module').then(m => m.VoucherOrderDetailsModule),
        data: {
            breadcrumb: 'TITLES.VOUCHER_ORDER_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VoucherOrdersRoutingModule { }
