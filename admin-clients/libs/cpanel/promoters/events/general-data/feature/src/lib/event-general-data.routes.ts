import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { ATM_FEATURES_ROLES } from '@admin-clients/cpanel/external/data-access';
import { EventRestrictionsComponent } from '@admin-clients/cpanel/promoters/events/restrictions/feature';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventAdditionalOptionsComponent } from './additional-options/event-additional-options.component';
import { EventAttendantsComponent } from './attendants/attendants.component';
import { EventAttributesComponent } from './attributes/attributes.component';
import { EventCodeConfigurationComponent } from './event-code-configuration/event-code-configuration.component';
import { EventGeneralDataComponent } from './event-general-data.component';
import { PrincipalInfoComponent } from './principal-info/event-principal-info.component';
import { EventChangeSeatConfComponent } from './relocations/event-change-seat-conf.component';
import { EventTransferSeatsComponent } from './transfer-seats/event-transfer-seats.component';
import { VendorFeaturesComponent } from './vendor-features/vendor-features.component';

export const EVENT_GENERAL_DATA_ROUTES: Routes = [{
    path: '',
    component: EventGeneralDataComponent,
    data: {
        breadcrumb: 'EVENTS.GENERAL_DATA.TITLE'
    },
    children: [
        {
            path: '',
            redirectTo: 'principal-info',
            pathMatch: 'full'
        },
        {
            path: 'principal-info',
            component: PrincipalInfoComponent,
            data: {
                breadcrumb: 'EVENTS.PRINCIPAL_INFO'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'attendants',
            component: EventAttendantsComponent,
            data: {
                breadcrumb: 'EVENTS.ATTENDANTS_CONFIG'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'additional-options',
            component: EventAdditionalOptionsComponent,
            data: {
                breadcrumb: 'EVENTS.ADDITIONAL_OPTIONS'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'transfer-tickets',
            component: EventTransferSeatsComponent,
            data: {
                breadcrumb: 'EVENTS.TRANSFER_SEATS.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'restrictions',
            component: EventRestrictionsComponent,
            data: {
                breadcrumb: 'EVENTS.TITLES.RESTRICTIONS'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'attributes',
            component: EventAttributesComponent,
            data: {
                breadcrumb: 'EVENTS.ATTRIBUTES'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'code-configuration',
            component: EventCodeConfigurationComponent,
            data: {
                breadcrumb: 'EVENTS.CONF_CODES'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'vendor-features',
            component: VendorFeaturesComponent,
            canActivate: [roleGuard],
            data: {
                roles: [...ATM_FEATURES_ROLES],
                breadcrumb: 'VENDOR_FEATURES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'change-seat-conf',
            component: EventChangeSeatConfComponent,
            data: {
                breadcrumb: 'EVENTS.TITLES.RELOCATIONS_CONFIGURATION'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];
