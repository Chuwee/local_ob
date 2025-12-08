import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrdersListComponent } from './list/orders-list.component';

const routes: Routes = [
    {
        path: '',
        component: OrdersListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':orderCode',
        loadChildren: () => import('./order/order-details.module').then(m => m.OrderDetailsModule),
        data: {
            breadcrumb: 'TITLES.ORDER_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class OrdersRoutingModule { }
