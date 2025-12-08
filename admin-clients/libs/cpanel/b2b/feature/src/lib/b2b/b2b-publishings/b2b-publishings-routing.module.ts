import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { b2bGuard } from '../b2b.guard';
import { B2bPublishingsListComponent } from './list/b2b-publishings-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [b2bGuard],
        children: [
            {
                path: '',
                component: B2bPublishingsListComponent
            },
            {
                path: ':seatId',
                loadChildren: () => import('./b2b-publishing/b2b-publishing-details.module').then(m => m.OrderDetailsModule),
                data: {
                    breadcrumb: 'TITLES.ORDER_DETAILS'
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class B2bPublishingsRoutingModule { }
