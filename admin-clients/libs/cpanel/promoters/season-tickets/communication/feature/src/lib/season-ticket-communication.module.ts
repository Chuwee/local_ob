import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    ImageUploaderComponent, LanguageBarComponent, RichTextAreaComponent, ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketChannelContentComponent } from './channel-content/season-ticket-channel-content.component';
import { SeasonTicketCommunicationRoutingModule } from './season-ticket-communication-routing.module';
import { SeasonTicketCommunicationComponent } from './season-ticket-communication.component';
import { SeasonTicketTicketContentComponent } from './ticket-content/season-ticket-ticket-content.component';
import {
    SeasonTicketTicketContentPassbookComponent
} from './ticket-content/ticket-content-passbook/season-ticket-ticket-content-passbook.component';
import { SeasonTicketTicketContentPdfComponent } from './ticket-content/ticket-content-pdf/season-ticket-ticket-content-pdf.component';
import {
    SeasonTicketTicketContentPrinterComponent
} from './ticket-content/ticket-content-printer/season-ticket-ticket-content-printer.component';
import { SeasonTicketTicketTemplatesComponent } from './ticket-content/ticket-templates/season-ticket-ticket-templates.component';

@NgModule({
    declarations: [
        SeasonTicketCommunicationComponent,
        SeasonTicketChannelContentComponent,
        SeasonTicketTicketContentComponent,
        SeasonTicketTicketTemplatesComponent,
        SeasonTicketTicketContentPdfComponent,
        SeasonTicketTicketContentPrinterComponent,
        SeasonTicketTicketContentPassbookComponent
    ],
    imports: [
        CommonModule,
        SeasonTicketCommunicationRoutingModule,
        FormContainerComponent,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        LanguageBarComponent,
        TranslatePipe,
        FormControlErrorsComponent,
        RichTextAreaComponent,
        ImageUploaderComponent,
        LastPathGuardListenerDirective,
        ContextNotificationComponent,
        EllipsifyDirective
    ]
})
export class SeasonTicketCommunicationModule {
}
