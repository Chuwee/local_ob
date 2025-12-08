import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ImageUploaderComponent, RichTextAreaComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SessionChannelContentComponent } from './session-channel-content/session-channel-content.component';
import { SessionTicketContentComponent } from './session-ticket-content/session-ticket-content.component';
import { SessionTicketPassbookComponent } from './session-ticket-passbook/session-ticket-passbook.component';

@NgModule({
    declarations: [
        SessionChannelContentComponent,
        SessionTicketContentComponent,
        SessionTicketPassbookComponent
    ],
    exports: [
        SessionChannelContentComponent,
        SessionTicketPassbookComponent,
        SessionTicketContentComponent
    ],
    imports: [
        CommonModule, FlexLayoutModule, MaterialModule, ReactiveFormsModule,
        TranslatePipe, FormControlErrorsComponent, RichTextAreaComponent,
        ImageUploaderComponent, ContextNotificationComponent
    ]
})
export class SessionCommunicationContentModule { }
