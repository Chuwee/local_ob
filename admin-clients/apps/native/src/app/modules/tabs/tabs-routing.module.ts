import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard, authCanMatchGuard } from '../auth/services/auth.guard';
import { roleEvent, roleSales } from '../auth/services/role-constant';
import { roleGuard } from '../auth/services/role.guard';
import { TabsPage } from './tabs.page';

const routes: Routes = [
    {
        path: '',
        component: TabsPage,
        children: [
            {
                path: '',
                redirectTo: 'home',
                pathMatch: 'full'
            },
            {
                path: 'home',
                canActivate: [authCanActivateGuard],
                loadChildren: () =>
                    import('../../pages/home/home.module').then(
                        m => m.HomeModule
                    )
            },
            {
                path: 'events',
                loadChildren: () =>
                    import('../../pages/events/events.module').then(
                        m => m.EventsPageModule
                    ),
                canMatch: [authCanMatchGuard],
                canActivate: [roleGuard],
                data: {
                    roles: roleEvent
                }
            },
            {
                path: 'sales',
                loadChildren: () =>
                    import('../../pages/sales/sales.module').then(
                        m => m.SalesPageModule
                    ),
                canMatch: [authCanMatchGuard],
                canActivate: [roleGuard],
                data: {
                    roles: roleSales
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class TabsPageRoutingModule { }
