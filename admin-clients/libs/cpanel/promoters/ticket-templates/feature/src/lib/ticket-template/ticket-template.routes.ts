import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ticketTemplateDetailsResolver } from './details/ticket-template-details-resolver';
import { TicketTemplateDetailsComponent } from './details/ticket-template-details.component';
import { TicketTemplateGeneralDataComponent } from './general-data/ticket-template-general-data.component';
import { TicketTemplateImagesComponent } from './images/ticket-template-images.component';
import { TicketTemplateLiteralsComponent } from './literals/ticket-template-literals.component';

export const routes: Routes = [{
    path: '',
    component: TicketTemplateDetailsComponent,
    resolve: {
        event: ticketTemplateDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: TicketTemplateGeneralDataComponent,
            data: {
                breadcrumb: 'TICKET_TEMPLATE.GENERAL_DATA'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'images',
            component: TicketTemplateImagesComponent,
            data: {
                breadcrumb: 'TICKET_TEMPLATE.IMAGES.TITLE'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'literals',
            component: TicketTemplateLiteralsComponent,
            data: {
                breadcrumb: 'TICKET_TEMPLATE.LITERALS'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];
