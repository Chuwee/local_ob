import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EventPipesModule } from '@admin-clients/cpanel-promoters-events-utils';
import { DateTimeModule, EmptyStateComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketLocalityManagementRoutingModule } from './season-ticket-locality-management-routing.module';
import { SeasonTicketLocalityManagementComponent } from './season-ticket-locality-management.component';

@NgModule({
    imports: [
        SeasonTicketLocalityManagementRoutingModule,
        DateTimeModule,
        DragDropModule,
        EventPipesModule,
        LocalCurrencyPipe,
        TabsMenuComponent,
        TabDirective,
        EmptyStateComponent,
        MaterialModule,
        LastPathGuardListenerDirective,
        CommonModule,
        FlexLayoutModule,
        TranslatePipe
    ],
    declarations: [
        SeasonTicketLocalityManagementComponent
    ]
})
export class SeasonTicketLocalityManagementModule { }
