import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { LoginConfigComponent } from '@admin-clients/cpanel/shared/feature/login-config';
import { ChannelGiftCardComponent } from '@admin-clients/cpanel-channels-operative-feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ChannelB2BPublishingComponent } from './b2b-publishing/channel-b2b-publishing.component';
import { ChannelBlacklistsComponent } from './blacklists/channel-blacklists.component';
import { ChannelBookingComponent } from './booking/channel-booking.component';
import { ChannelBookingsSharingComponent } from './bookings-sharing/channel-bookings-sharing.component';
import { ChannelOperativeComponent } from './channel-operative.component';
import { ChannelCommissionsComponent } from './commissions/channel-commissions.component';
import { ChannelCrossRestrictionsComponent } from './cross-restrictions/channel-cross-restrictions.component';
import { ChannelCrossSellingComponent } from './cross-selling/channel-cross-selling.component';
import { ChannelDonationsComponent } from './donations/channel-donations.component';
import { ChannelLegacyDeliveryMethodsComponent } from './legacy-delivery-methods/channel-legacy-delivery-methods.component';
import { ChannelSurchargesComponent } from './surcharges/channel-surcharges.component';

export const routes: Routes = [{
    path: '',
    component: ChannelOperativeComponent,
    children: [
        {
            path: '',
            redirectTo: 'surcharges',
            pathMatch: 'full'
        },
        {
            path: 'surcharges',
            component: ChannelSurchargesComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.SURCHARGES.TITLE'
            }
        },
        {
            path: 'member-surcharges',
            loadComponent: () => import('@admin-clients/cpanel-channels-member-external-feature').then(m => m.MembersSurchargesComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.SURCHARGES.TITLE'
            }
        },
        {
            path: 'commissions',
            component: ChannelCommissionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.COMMISSIONS.TITLE'
            }
        },
        {
            path: 'gift-card',
            component: ChannelGiftCardComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.GIFT_CARD.TITLE'
            }
        },
        {
            path: 'delivery-methods',
            loadComponent: () => import('./delivery-methods/channel-delivery-methods.component')
                .then(m => m.ChannelDeliveryMethodsComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.DELIVERY_METHODS.TITLE'
            }
        },
        {
            path: 'legacy-delivery-methods',
            component: ChannelLegacyDeliveryMethodsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.DELIVERY_METHODS.TITLE'
            }
        },
        {
            path: 'members-delivery-methods',
            loadComponent: () => import('./members-delivery-methods/channel-members-delivery-methods.component')
                .then(m => m.ChannelMembersDeliveryMethodsComponent),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.DELIVERY_METHODS.TITLE'
            }
        },
        {
            path: 'options',
            loadChildren: () => import('./options/channel-options.module'),
            data: {
                breadcrumb: 'CHANNELS.OPTIONS.TITLE'
            }
        },
        {
            path: 'blacklists',
            component: ChannelBlacklistsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.BLACKLISTS.TITLE'
            }
        },
        {
            path: 'cross-selling',
            component: ChannelCrossSellingComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.CROSS_SELLING.TITLE'
            }
        },
        {
            path: 'cross-restrictions',
            component: ChannelCrossRestrictionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.CROSS_RESTRICTIONS.TITLE'
            }
        },
        {
            path: 'booking',
            component: ChannelBookingComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.BOOKING.TITLE'
            }
        },
        {
            path: 'sharing',
            component: ChannelBookingsSharingComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.TITLES.BOOKINGS_SHARING'
            }
        },
        {
            path: 'donations',
            component: ChannelDonationsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.DONATIONS.TITLE'
            }
        },
        {
            path: 'b2b-publishing',
            component: ChannelB2BPublishingComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'CHANNELS.B2B_PUBLISHING.TITLE'
            }
        },
        {
            path: 'login-config',
            component: LoginConfigComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            data: {
                breadcrumb: 'CHANNELS.CUSTOMER_LOGIN.TITLE',
                isChannel: true,
                roles: [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CNL_MGR]
            }
        }
    ]
}];
