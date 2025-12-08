import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { b2bPublishingDetailsResolver } from './details/b2b-publishing-details-resolver';
import { B2bPublishingDetailsComponent } from './details/b2b-publishing-details.component';
import { B2bPublishingGeneralDataComponent } from './general-data/b2b-publishing-general-data.component';

const routes: Routes = [
    {
        path: '',
        component: B2bPublishingDetailsComponent,
        resolve: {
            order: b2bPublishingDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: B2bPublishingGeneralDataComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class B2bPublishingDetailsRoutingModule { }
