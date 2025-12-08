import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { SubscriptionListsContainerComponent } from './container/subscription-lists-container.component';
import { SubscriptionListGeneralDataComponent } from './subscription-list/general-data/subscription-list-general-data.component';

const routes: Routes = [{
    path: '',
    component: SubscriptionListsContainerComponent,
    children: [
        {
            path: '',
            component: null,
            pathMatch: 'full',
            children: []
        },
        {
            path: ':subscriptionListId',
            component: SubscriptionListGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'SUBSCRIPTION_LIST.DETAILS'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SubscriptionListsRoutingModule {
}
