import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { voucherGroupDetailsResolver } from './voucher-group-details-resolver';
import { VoucherGroupDetailsComponent } from './voucher-group-details.component';

const routes: Routes = [{
    path: '',
    component: VoucherGroupDetailsComponent,
    resolve: {
        voucherGroup: voucherGroupDetailsResolver
    },
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'group'
        },
        {
            path: 'group',
            loadChildren: () => import('../manual-code/manual-code-group.module').then(m => m.ManualCodeGroupModule),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VOUCHER_GROUP.CONFIGURATION_GROUP.CONFIGURATION'
            }
        },
        {
            path: 'gift-card',
            loadChildren: () => import('../gift-card/voucher-gift-card.module').then(m => m.VoucherGiftCardModule),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VOUCHER_GROUP.CONFIGURATION_GROUP.CONFIGURATION'
            }
        },
        {
            path: 'voucher-codes',
            loadChildren: () => import('../../voucher-codes/voucher.module').then(m => m.VoucherModule),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VOUCHER_GROUP.CODE_LIST'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VoucherGroupRoutingModule {
}
