import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { VoucherListComponent } from './list/voucher-list.component';
import { voucherResolver } from './voucher/voucher-resolver';
import { VoucherComponent } from './voucher/voucher.component';

const routes: Routes = [{
    path: '',
    children: [
        {
            path: '',
            component: VoucherListComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'VOUCHER_GROUP.CODE_LIST'
            }
        },
        {
            path: ':code',
            component: VoucherComponent,
            resolve: {
                voucher: voucherResolver
            },
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'VOUCHER.GENERAL_DATA'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VoucherRoutingModule {
}
