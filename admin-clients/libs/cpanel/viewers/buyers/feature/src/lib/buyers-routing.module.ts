import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuyersListComponent } from './list/buyers-list.component';

const routes: Routes = [
    {
        path: '',
        component: BuyersListComponent,
        canDeactivate: [unsavedChangesGuard()],
        data: {
            breadcrumb: 'BUYERS.LIST'
        }
    },
    {
        path: ':buyerId',
        loadChildren: () => import('./buyer/buyer.module').then(m => m.BuyerModule),
        data: {
            breadcrumb: 'BUYERS.DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class BuyersRoutingModule {
}
