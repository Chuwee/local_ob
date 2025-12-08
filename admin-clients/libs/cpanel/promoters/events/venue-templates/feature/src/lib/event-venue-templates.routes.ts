import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventVenueTemplatesContainerComponent } from './container/event-venue-templates-container.component';
import { ActivityEventVenueTemplateComponent } from './venue-template/activity/activity-event-venue-template.component';
import { EventActivityTemplateGatesComponent } from './venue-template/activity/gates/event-activity-template-gates.component';
import { ActivityTemplateGeneralDataComponent } from './venue-template/activity/general-data/activity-template-general-data.component';
import { EventActivityTemplateGroupsComponent } from './venue-template/activity/groups/event-activity-template-groups.component';
import { eventVenueTemplateResolver } from './venue-template/event-venue-template-resolver';
import { StandardEventVenueTemplateComponent } from './venue-template/standard/standard-event-venue-template.component';

export const EVENT_VENUE_TEMPLATES_ROUTES: Routes = [{
    path: '',
    component: EventVenueTemplatesContainerComponent,
    providers: [venuesProviders],
    children: [
        {
            path: ':venueTemplateId',
            resolve: { venueTemplate: eventVenueTemplateResolver },
            data: { breadcrumb: 'templateName' },
            children: [
                {
                    path: 'standard',
                    component: StandardEventVenueTemplateComponent,
                    canDeactivate: [unsavedChangesGuard()]
                },
                {
                    path: 'activity',
                    component: ActivityEventVenueTemplateComponent,
                    children: [
                        {
                            path: '',
                            pathMatch: 'full',
                            redirectTo: 'general-data'
                        },
                        {
                            path: 'general-data',
                            component: ActivityTemplateGeneralDataComponent,
                            canDeactivate: [unsavedChangesGuard()]
                        },
                        {
                            path: 'gates',
                            component: EventActivityTemplateGatesComponent,
                            canDeactivate: [unsavedChangesGuard()]
                        },
                        {
                            path: 'groups',
                            component: EventActivityTemplateGroupsComponent,
                            canDeactivate: [unsavedChangesGuard()]
                        }
                    ]
                }
            ]
        }
    ]
}];
