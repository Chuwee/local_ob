import { ChannelsService, ChannelsApi, ChannelsState } from '@admin-clients/cpanel/channels/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PromotionTplDiscountTypeComponent } from '../discount-type/promotion-tpl-discount-type.component';
import { PromotionTplConditionsComponent } from './conditions/promotion-tpl-conditions.component';
import { promotionTplDetailsResolver } from './details/promotion-tpl-details-resolver';
import { PromotionTplDetailsComponent } from './details/promotion-tpl-details.component';
import { PromotionTplGeneralDataComponent } from './general-data/promotion-tpl-general-data.component';

export const PROMOTION_TEMPLATE_ROUTES: Routes = [{
    path: '',
    component: PromotionTplDetailsComponent,
    resolve: {
        promotion: promotionTplDetailsResolver
    },
    providers: [
        ChannelsService,
        ChannelsApi,
        ChannelsState
    ],
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'general-data'
        },
        {
            path: 'general-data',
            component: PromotionTplGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'EVENT_PROMOTION_TEMPLATE.GENERAL_DATA'
            }
        },
        {
            path: 'conditions',
            component: PromotionTplConditionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'EVENT_PROMOTION_TEMPLATE.LIMITS_AND_CONDITIONS'
            }
        },
        {
            path: 'discount-type',
            pathMatch: 'full',
            component: PromotionTplDiscountTypeComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'EVENT_PROMOTION_TEMPLATE.DISCOUNT_TYPE'
            }
        }
    ]
}];
