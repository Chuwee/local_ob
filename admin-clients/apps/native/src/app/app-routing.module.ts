import { Routes } from '@angular/router';
import { authCanActivateGuard, authCanMatchGuard } from './modules/auth/services/auth.guard';
import { roleEvent, roleSales, roleTransaction } from './modules/auth/services/role-constant';
import { roleGuard } from './modules/auth/services/role.guard';

export const routes: Routes = [
    {
        path: 'onboarding',
        loadChildren: () =>
            import('./pages/onboarding/onboarding.module').then(m => m.OnboardingModule)
    },
    {
        path: 'auth',
        loadChildren: () =>
            import('./modules/auth/auth.module').then(m => m.AuthModule)
    },
    {
        path: 'profile',
        canActivate: [authCanActivateGuard],
        loadChildren: () =>
            import('./modules/profile/profile.module').then(
                m => m.ProfileModule
            )
    },
    {
        path: 'tabs',
        canActivate: [authCanActivateGuard],
        loadChildren: () =>
            import('./modules/tabs/tabs.module').then(m => m.TabsPageModule)
    },
    {
        path: 'global-search',
        canActivate: [authCanActivateGuard],
        loadChildren: () =>
            import('./modules/global-search/global-search.module').then(
                m => m.GlobalSearchModule
            )
    },
    {
        path: 'filters',
        canActivate: [authCanActivateGuard],
        loadChildren: () =>
            import('./modules/filters/filters.module').then(
                m => m.FiltersModule
            )
    },
    {
        path: 'ticket-detail',
        loadChildren: () =>
            import('./pages/ticket-detail/ticket-detail.module').then(
                m => m.TicketDetailModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleSales
        }
    },
    {
        path: 'transaction-detail',
        loadChildren: () =>
            import('./pages/transaction-detail/transaction-detail.module').then(
                m => m.TransactionDetailModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleTransaction
        }
    },
    {
        path: 'event-detail',
        loadChildren: () =>
            import('./pages/events-detail/events-detail.module').then(
                m => m.EventsDetailPageModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleEvent
        }
    },
    {
        path: 'session-detail',
        loadChildren: () =>
            import('./pages/session-detail/session-detail.module').then(
                m => m.SessionDetailPageModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleEvent
        }
    },
    {
        path: 'promotion-detail',
        loadChildren: () =>
            import('./pages/promotion-detail/promotion-detail.module').then(
                m => m.PromotionDetailModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleEvent
        }
    },
    {
        path: 'channel-detail',
        loadChildren: () =>
            import('./pages/channel-detail/channel-detail.module').then(
                m => m.ChannelDetailPageModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleEvent
        }

    },
    {
        path: 'weekly-sales-detail',
        loadChildren: () =>
            import('./pages/weekly-sales-detail/weekly-sales-detail.module').then(
                m => m.WeeklySalesDetailModule
            ),
        canMatch: [authCanMatchGuard],
        canActivate: [roleGuard],
        data: {
            roles: roleSales
        }
    }
];
