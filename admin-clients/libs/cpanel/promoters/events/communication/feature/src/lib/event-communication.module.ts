import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { channelsProviders } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import {
    EmptyStateComponent, ImageUploaderComponent,
    LanguageBarComponent, NavTabsMenuComponent, RichTextAreaComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { EventChannelContentComponent } from './channel-content/event-channel-content.component';
import { EventCommunicationRoutingModule } from './event-communication-routing.module';
import { EventCommunicationComponent } from './event-communication.component';
import { EventInvitationContentComponent } from './invitation-content/event-invitation-content.component';
import { EventTicketContentComponent } from './ticket-content/event-ticket-content/event-ticket-content.component';
import {
    EventTicketContentPassbookComponent
} from './ticket-content/event-ticket-content/ticket-content-passbook/event-ticket-content-passbook.component';
import {
    EventTicketContentPdfComponent
} from './ticket-content/event-ticket-content/ticket-content-pdf/event-ticket-content-pdf.component';
import {
    EventTicketContentPrinterComponent
} from './ticket-content/event-ticket-content/ticket-content-printer/event-ticket-content-printer.component';
import { EventTicketTemplatesComponent } from './ticket-content/event-ticket-content/ticket-templates/event-ticket-templates.component';
import { PriceTypeTicketContentComponent } from './ticket-content/price-type-ticket-content/price-type-ticket-content.component';
import {
    PriceTypeTicketContentPassbookComponent
} from './ticket-content/price-type-ticket-content/ticket-content-passbook/price-type-ticket-content-passbook.component';
import {
    PriceTypeTicketContentPdfComponent
} from './ticket-content/price-type-ticket-content/ticket-content-pdf/price-type-ticket-content-pdf.component';
import {
    PriceTypeTicketContentPrinterComponent
} from './ticket-content/price-type-ticket-content/ticket-content-printer/price-type-ticket-content-printer.component';
import { TicketContentComponent } from './ticket-content/ticket-content.component';

@NgModule({
    declarations: [
        EventCommunicationComponent,
        EventChannelContentComponent,
        EventInvitationContentComponent,
        EventTicketTemplatesComponent,
        EventTicketContentComponent,
        EventTicketContentPdfComponent,
        EventTicketContentPrinterComponent,
        EventTicketContentPassbookComponent,
        PriceTypeTicketContentPdfComponent,
        PriceTypeTicketContentPrinterComponent,
        PriceTypeTicketContentPassbookComponent,
        PriceTypeTicketContentComponent,
        TicketContentComponent
    ],
    imports: [
        CommonModule,
        LastPathGuardListenerDirective,
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        ImageUploaderComponent,
        LanguageBarComponent,
        FormContainerComponent,
        RichTextAreaComponent,
        FlexLayoutModule,
        EventCommunicationRoutingModule,
        ArchivedEventMgrComponent,
        EmptyStateComponent,
        NavTabsMenuComponent,
        EllipsifyDirective,
        RouterModule,
        ContextNotificationComponent
    ],
    providers: [...channelsProviders]
})
export class EventCommunicationModule { }
