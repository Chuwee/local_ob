import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { VoucherGroupsListComponent } from './list/voucher-groups-list.component';

const routes: Routes = [
    {
        path: '',
        component: VoucherGroupsListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':voucherGroupId',
        loadChildren: () => import('./voucher-group/configuration/details/voucher-group.module').then(m => m.VoucherGroupModule),
        data: {
            breadcrumb: 'TITLES.VOUCHER_GROUP_DETAIL'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VouchersRoutingModule { }
