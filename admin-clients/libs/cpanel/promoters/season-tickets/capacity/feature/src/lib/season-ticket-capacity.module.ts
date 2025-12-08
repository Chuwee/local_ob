import { StdVenueTplMgrComponent } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketCapacityRoutingModule } from './season-ticket-capacity-routing.module';
import { SeasonTicketCapacityComponent } from './season-ticket-capacity.component';

@NgModule({
    declarations: [
        SeasonTicketCapacityComponent
    ],
    imports: [
        StdVenueTplMgrComponent,
        SeasonTicketCapacityRoutingModule,
        ContextNotificationComponent,
        FlexLayoutModule,
        CommonModule,
        MaterialModule,
        TranslatePipe
    ]
})
export class SeasonTicketCapacityModule {
}
