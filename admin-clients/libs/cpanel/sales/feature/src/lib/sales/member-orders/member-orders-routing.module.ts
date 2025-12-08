import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MemberOrderListComponent } from './list/member-orders-list.component';

const routes: Routes = [
    {
        path: '',
        component: MemberOrderListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':code',
        loadChildren: () => import('./member-order/member-order-details.module').then(m => m.MemberOrderDetailsModule),
        data: {
            breadcrumb: 'TITLES.VOUCHER_ORDER_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MemberOrdersRoutingModule { }
