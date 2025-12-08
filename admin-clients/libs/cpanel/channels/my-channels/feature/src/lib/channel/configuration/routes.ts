import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Route } from '@angular/router';
import { ChannelConfigurationComponent } from './container/channel-configuration-container.component';
import { ChannelEmailConfComponent } from './email/channel-email-conf.component';
import { ChannelExternalToolsComponent } from './external-tools/channel-external-tools.component';
import { ChannelPaymentMethodsComponent } from './payment-methods/payment-methods.component';
import { ChannelServerTrackingComponent } from './server-tracking/server-tracking.component';

export default [
    {
        path: '',
        component: ChannelConfigurationComponent,
        data: {
            breadcrumb: 'CHANNELS.CONFIGURATION'
        },
        children: [
            {
                path: '',
                redirectTo: 'customer-communication',
                pathMatch: 'full'
            },
            {
                path: 'customer-communication',
                component: ChannelEmailConfComponent,
                canActivate: [roleGuard],
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CUSTOMER_COMMUNICATION.TITLE',
                    roles: [UserRoles.OPR_MGR]
                }
            },
            {
                path: 'payment-methods',
                component: ChannelPaymentMethodsComponent,
                canActivate: [roleGuard],
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.PAYMENT_METHODS.TITLE',
                    roles: [UserRoles.OPR_MGR]
                }
            },
            {
                path: 'external-tools',
                component: ChannelExternalToolsComponent,
                canActivate: [roleGuard],
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EXTERNAL_TOOLS.TITLE',
                    roles: [UserRoles.OPR_MGR]
                }
            },
            {
                path: 'server-tracking',
                component: ChannelServerTrackingComponent,
                canActivate: [roleGuard],
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.SERVER_TRACKING.TITLE',
                    roles: [UserRoles.OPR_MGR]
                }
            }
        ]
    }

] as Route[];