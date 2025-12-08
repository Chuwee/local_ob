import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { GroupContainerComponent } from '../container/group-container.component';
import { GroupPrincipalInfoComponent } from '../principal-info/group-principal-info.component';
import { GroupRedeemComponent } from '../redeem/group-redeem.component';

const routes: Routes = [{
    path: 'configuration',
    component: GroupContainerComponent,
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'principal-info'
        },
        {
            path: 'principal-info',
            component: GroupPrincipalInfoComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'VOUCHER_GROUP.PRINCIPAL_INFO'
            }
        },
        {
            path: 'redeem',
            component: GroupRedeemComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'GIFT_CARD.REDEEM'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VoucherGeneralDataRoutingModule {
}
