import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PacksContainerComponent } from './container/packs-container.component';
import { PackCommunicationComponent } from './pack/communication/pack-communication.component';
import { PackDesignComponent } from './pack/design/pack-design.component';
import { PackDetailsResolverService } from './pack/details/pack-details-resolver.service';
import { PackDetailsComponent } from './pack/details/pack-details.component';
import { PackElementsComponent } from './pack/elements/pack-elements.component';
import { PackGeneralDataComponent } from './pack/general-data/pack-general-data.component';
import { PackPreviewComponent } from './pack/preview/pack-preview.component';
import { PackPricesComponent } from './pack/prices/pack-prices.component';
import { PackProgrammingComponent } from './pack/programming/pack-programming.component';
import { PackPromoComponent } from './pack/promotion/pack-promotion.component';
import { PackTicketContentComponent } from './pack/ticket-communication/pack-ticket-content.component';

export const PACKS_ROUTES: Routes = [
    {
        path: '',
        component: PacksContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':packId',
                component: PackDetailsComponent,
                resolve: {
                    pack: PackDetailsResolverService
                },
                data: {
                    breadcrumb: 'PACK_EDITOR'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'elements'
                    },
                    {
                        path: 'elements',
                        pathMatch: 'full',
                        component: PackElementsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.ELEMENTS_TITLE'
                        }
                    },
                    {
                        path: 'general-data',
                        pathMatch: 'full',
                        component: PackGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.GENERAL_DATA_TITLE'
                        }
                    },
                    {
                        path: 'programming',
                        pathMatch: 'full',
                        component: PackProgrammingComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.PROGRAMMING_TITLE'
                        }
                    },
                    {
                        path: 'communication',
                        pathMatch: 'full',
                        component: PackCommunicationComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.COMMUNICATION_TITLE'
                        }
                    },
                    {
                        path: 'promotion',
                        pathMatch: 'full',
                        component: PackPromoComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.PROMOTION_TITLE'
                        }
                    },
                    {
                        path: 'prices',
                        pathMatch: 'full',
                        component: PackPricesComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.PRICES_TITLE'
                        }
                    },
                    {
                        path: 'tickets',
                        pathMatch: 'full',
                        component: PackTicketContentComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.TICKETS_TITLE'
                        }
                    },
                    {
                        path: 'preview',
                        pathMatch: 'full',
                        component: PackPreviewComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.PREVIEW_TITLE'
                        }
                    },
                    {
                        path: 'design',
                        pathMatch: 'full',
                        component: PackDesignComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PACKS.DESIGN_TITLE'
                        }
                    }
                ]
            }
        ]
    }
];
