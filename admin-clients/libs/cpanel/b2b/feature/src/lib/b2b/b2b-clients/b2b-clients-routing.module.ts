import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { b2bGuard } from '../b2b.guard';
import { B2bClientsListComponent } from './list/b2b-clients-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [b2bGuard],
        children: [
            {
                path: '',
                component: B2bClientsListComponent
            },
            {
                path: ':b2bClientId',
                loadChildren: () => import('./b2b-client/b2b-client.module').then(m => m.B2bClientModule),
                data: {
                    breadcrumb: 'TITLES.B2B_CLIENT_DETAILS'
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class B2bClientsRoutingModule { }
