import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { memberOrderDetailsResolver } from './details/member-order-details-resolver';
import { MemberOrderDetailsComponent } from './details/member-order-details.component';
import { MemberOrderGeneralDataComponent } from './general-data/member-order-general-data.component';

const routes: Routes = [
    {
        path: '',
        component: MemberOrderDetailsComponent,
        resolve: {
            order: memberOrderDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: MemberOrderGeneralDataComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MemberOrderDetailsRoutingModule { }
