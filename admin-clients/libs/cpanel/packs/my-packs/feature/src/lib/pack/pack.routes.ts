import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { packDetailsResolver } from './details/pack-details-resolver';
import { PackDetailsComponent } from './details/pack-details.component';

export const routes: Routes = [{
    path: '',
    component: PackDetailsComponent,
    resolve: {
        event: packDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            loadComponent: () => import('./general-data/pack-general-data.component').then(c => c.PackGeneralDataComponent),
            data: {
                breadcrumb: 'PACK.GENERAL_DATA_TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'elements',
            pathMatch: 'full',
            loadComponent: () => import('./elements/pack-elements.component').then(c => c.PackElementsComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'PACK.ELEMENTS_TITLE'
            }
        },
        {
            path: 'programming',
            pathMatch: 'full',
            loadComponent: () => import('./programming/pack-programming.component').then(c => c.PackProgrammingComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'PACK.PROGRAMMING_TITLE'
            }
        },
        {
            path: 'prices',
            loadChildren: () => import('./prices/pack-prices.routes').then(m => m.PACK_PRICES_ROUTES),
            data: {
                breadcrumb: 'PACK.PRICES_TITLE'
            }
        },
        {
            path: 'promotion',
            pathMatch: 'full',
            loadComponent: () => import('./promotion/pack-promotion.component').then(c => c.PackPromoComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'PACK.PROMOTION_TITLE'
            }
        },
        {
            path: 'design',
            pathMatch: 'full',
            loadComponent: () => import('./design/pack-design.component').then(c => c.PackDesignComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.PACKS.DESIGN_TITLE'
            }
        },
        {
            path: 'communication',
            loadChildren: () =>
                import('./communication/pack-communication.routes').then(m => m.PACK_COMMUNICATION_ROUTES),
            data: {
                breadcrumb: 'PACK.COMMUNICATION_TITLE'
            }
        },
        {
            path: 'channels',
            loadChildren: () =>
                import('./channels/pack-channels.routes').then(m => m.PACK_CHANNELS_ROUTES),
            data: {
                breadcrumb: 'PACK.CHANNELS_TITLE'
            }
        },
        {
            path: 'promotions',
            loadComponent: () =>
                import('./promotion/pack-promotion.component').then(c => c.PackPromoComponent),
            data: {
                breadcrumb: 'PACK.PROMOS_TITLE'
            }
        }
    ]
}];
