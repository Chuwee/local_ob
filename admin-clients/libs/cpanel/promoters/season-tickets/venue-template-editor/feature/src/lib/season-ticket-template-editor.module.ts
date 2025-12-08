import { EventPipesModule } from '@admin-clients/cpanel-promoters-events-utils';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { StandardVenueTplEditorComponent } from '@admin-clients/shared/venues/feature/standard-venue-tpl-editor';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketTemplateEditorContainerComponent } from './season-ticket-template-editor-container.component';
import { SeasonTicketTemplateEditorRoutingModule } from './season-ticket-template-editor-routing.module';

@NgModule({
    declarations: [
        SeasonTicketTemplateEditorContainerComponent
    ],
    imports: [
        SeasonTicketTemplateEditorRoutingModule,
        StandardVenueTplEditorComponent,
        EventPipesModule,
        CommonModule,
        MaterialModule,
        TranslatePipe,
        FlexLayoutModule
    ]
})
export class SeasonTicketTemplateEditorModule {
}
