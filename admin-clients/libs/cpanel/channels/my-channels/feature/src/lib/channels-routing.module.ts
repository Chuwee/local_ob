import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChannelsListComponent } from './list/channels-list.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        component: ChannelsListComponent
    },
    {
        path: ':channelId',
        loadChildren: () => import('./channel/routes').then(m => m.routes),
        data: {
            breadcrumb: 'TITLES.CHANNEL_DETAILS'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ChannelsRoutingModule { }
