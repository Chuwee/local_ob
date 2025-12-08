import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EntityZoneTemplateEmailComponent } from '../email/entity-zone-template-email.component';
import { EntityZoneTemplateGeneralDataComponent } from '../general-data/entity-zone-template-general-data.component';
import { EntityZoneTemplateThanksConfigComponent } from '../thanks-config/entity-zone-template-thanks-config.component';

export const ENTITY_ZONE_TEMPLATE_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'general-data',
        pathMatch: 'full'
    },
    {
        path: 'general-data',
        component: EntityZoneTemplateGeneralDataComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'ENTITY.ZONE_TEMPLATES.DETAIL.GENERAL_DATA.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'email',
        component: EntityZoneTemplateEmailComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'ENTITY.ZONE_TEMPLATES.DETAIL.EMAIL.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'thanks-config',
        component: EntityZoneTemplateThanksConfigComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'ENTITY.ZONE_TEMPLATES.DETAIL.THANKS_CONFIG.TITLE'
        },
        canDeactivate: [unsavedChangesGuard()]
    }
];
