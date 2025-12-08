import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ChannelDesignComponent } from './container/channel-design-container.component';

export const DESIGN_ROUTES: Routes = [
    {
        path: '',
        component: ChannelDesignComponent,
        children: [
            {
                path: '',
                redirectTo: 'components',
                pathMatch: 'full'
            },
            {
                path: 'components',
                loadComponent: () => import('./components/channel-components.component').then(c => c.ChannelComponentsComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.COMPONENTS.TITLE'
                }
            },
            {
                path: 'catalog-config',
                loadComponent: () => import('./catalog-config/channel-catalog-config.component').then(c => c.ChannelCatalogConfigComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CATALOG_CONFIG.TITLE'
                }
            },
            {
                path: 'venue-config',
                loadComponent: () => import('./venue-config/venue-config.component').then(c => c.VenueMapConfigComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.VENUE_MAP_CONFIG.TITLES.VENUE_MAP'
                }
            },
            {
                path: 'thanks-config',
                loadComponent: () => import('./thanks-config/thanks-config.component').then(c => c.ThanksConfigComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.VENUE_MAP_CONFIG.TITLES.THANK_YOU_PAGE'
                }
            },
            {
                path: 'event-promotions-config',
                loadComponent: () =>
                    import('./event-promotions-config/event-promotions-config.component').then(c => c.EventPromotionsConfigComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EVENT_PROMOTIONS_CONFIG.TITLES.EVENT_PROMOTIONS'
                }
            },
            {
                path: 'code-editor',
                loadComponent: () =>
                    import('./channel-code-editor/channel-code-editor.component').then(c => c.ChannelCodeEditorComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CODE_EDITOR.TITLE'
                }
            },
            {
                path: 'payment-config',
                loadComponent: () =>
                    import('./payment-config/payment-config.component').then(c => c.ChannelPaymentConfigComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.PAYMENT_CONFIG.TITLES.PAYMENT_CONFIG'
                }
            },
            {
                path: 'review-config',
                loadComponent: () =>
                    import('./reviews/reviews.component').then(c => c.ChannelReviewsComponent),
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.REVIEWS.TITLE'
                }
            }
        ]
    }
];
