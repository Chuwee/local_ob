import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChannelPromotionDiscountTypeComponent } from '@admin-clients/cpanel-channels-promotions-feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { ChannelPromotionsContainerComponent } from './container/channel-promotions-container.component';
import { ChannelPromotionConditionsComponent } from './promotion/conditions/channel-promotion-conditions.component';
import { ChannelPromotionDetailsResolverService } from './promotion/details/channel-promotion-details-resolver.service';
import { ChannelPromotionDetailsComponent } from './promotion/details/channel-promotion-details.component';
import { ChannelPromotionGeneralDataComponent } from './promotion/general-data/channel-promotion-general-data.component';
import { ChannelPromotionZonesComponent } from './promotion/zones/channel-promotion-zones.component';

const routes: Routes = [
    {
        path: '',
        component: ChannelPromotionsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':promotionId',
                component: ChannelPromotionDetailsComponent,
                resolve: {
                    promotion: ChannelPromotionDetailsResolverService
                },
                data: {
                    breadcrumb: 'PROMOTION_EDITOR'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'general-data'
                    },
                    {
                        path: 'general-data',
                        component: ChannelPromotionGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'CHANNELS.PROMOTIONS.GENERAL_DATA'
                        }
                    },
                    {
                        path: 'conditions',
                        component: ChannelPromotionConditionsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'CHANNELS.PROMOTIONS.LIMITS_AND_CONDITIONS'
                        }
                    },
                    {
                        path: 'type',
                        component: ChannelPromotionDiscountTypeComponent,
                        pathMatch: 'full',
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'CHANNELS.PROMOTIONS.DISCOUNT_TYPE'
                        }
                    },
                    {
                        path: 'zones',
                        component: ChannelPromotionZonesComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'CHANNELS.PROMOTIONS.ZONES'
                        }
                    }
                ]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ChannelPromotionsRoutingModule { }
