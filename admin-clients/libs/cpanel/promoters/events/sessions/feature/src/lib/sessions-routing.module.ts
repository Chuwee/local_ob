import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SessionsContainerComponent } from './container/sessions-container.component';
import { sessionDetailsResolver } from './details/session-details-resolver';

const routes: Routes = [
    {
        path: '',
        component: SessionsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: 'multi',
                loadChildren: () => import('./multi/details/multi-session-details.module')
                    .then(m => m.MultiSessionDetailsModule),
                data: {
                    breadcrumb: 'EVENTS.SESSION.MULTI_EDITION'
                }
            },
            {
                path: ':sessionId',
                loadChildren: () => import('./session.module').then(m => m.SessionModule),
                resolve: {
                    session: sessionDetailsResolver
                },
                data: {
                    breadcrumb: 'SESSION_NAME'
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SessionsRoutingModule {
}
