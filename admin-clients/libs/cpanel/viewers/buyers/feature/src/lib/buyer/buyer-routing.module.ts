import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuyerBasicInfoComponent } from './basic-info/buyer-basic-info.component';
import { BuyerCommercialInfoComponent } from './commercial-info/buyer-commercial-info.component';
import { BuyerDetailsResolverService } from './details/buyer-details-resolver.service';
import { BuyerDetailsComponent } from './details/buyer-details.component';
import { BuyerOrderItemsComponent } from './order-items/buyer-order-items.component';

const routes: Routes = [{
    path: '',
    component: BuyerDetailsComponent,
    resolve: {
        event: BuyerDetailsResolverService
    },
    canDeactivate: [unsavedChangesGuard()],
    children: [
        {
            path: '',
            redirectTo: 'basic-info',
            pathMatch: 'full'
        },
        {
            path: 'basic-info',
            component: BuyerBasicInfoComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'BUYERS.BASIC_INFO'
            }
        },
        {
            path: 'commercial-info',
            component: BuyerCommercialInfoComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'BUYERS.COMMERCIAL_INFO'
            }
        },
        {
            path: 'tickets',
            component: BuyerOrderItemsComponent,
            data: {
                breadcrumb: 'CUSTOMER.PURCHASES'
            }
        }
    ]

}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class BuyerRoutingModule {
}
