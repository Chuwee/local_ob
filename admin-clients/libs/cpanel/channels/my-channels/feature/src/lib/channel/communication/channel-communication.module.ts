import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsPipesModule, IsV3$Pipe, IsWebV4$Pipe } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    ColorPickerComponent, EmptyStateTinyComponent, HelpButtonComponent, ImageUploaderComponent, LanguageBarComponent,
    RichTextAreaComponent, SearchTableComponent, SelectServerSearchComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BoxofficeLiteralsComponent } from './boxoffice-literals/boxoffice-literals.component';
import { ChannelCommunicationRoutingModule } from './channel-communication-routing.module';
import { ChannelCommunicationContainerComponent } from './container/channel-communication-container.component';
import { ChannelImportComComponentsDialogComponent } from './container/import-com-contents/channel-import-com-contents-dialog.component';
import { ChannelCommunicationContentsComponent } from './contents/channel-communication-contents.component';
import { ChannelCommunicationBookingConfirmComponent } from './emails/booking-confirm/channel-communication-booking-confirm.component';
import { ChannelCommunicationGiftCardConfirmComponent } from './emails/gift-card-confirm/channel-communication-gift-card-confirm.component';
import { ChannelCommunicationPurchaseConfirmComponent } from './emails/purchase-confirm/channel-communication-purchase-confirm.component';
import { ChannelCommunicationReallocationConfirmComponent } from './emails/reallocation-confirm/channel-communication-reallocation-confirm.component';
import { ChannelCommunicationTicketsEmailComponent } from './emails/tickets/channel-communication-tickets-email.component';
import { ChannelCommunicationVoucherRefundComponent } from './emails/voucher-refund-email/channel-communication-voucher-refund.component';
import { ChannelCommunicationReviewsEmailComponent } from './emails/reviews-email/channel-communication-reviews-email.component';
import { ChannelFaqsComponent } from './faqs/channel-faqs.component';
import { ChannelFaqDialogComponent } from './faqs/faq-dialog/channel-faq-dialog.component';
import { ChannelPurchaseContentsComponent } from './purchase-contents/channel-purchase-contents.component';
import { ChannelTicketContentsComponent } from './ticket-contents/channel-ticket-contents.component';
import { ChannelTicketContentPassbookComponent } from './ticket-contents/ticket-content-passbook/channel-ticket-content-passbook.component';
import { ChannelTicketContentPdfComponent } from './ticket-contents/ticket-content-pdf/channel-ticket-content-pdf.component';
import { ChannelTicketContentPrinterComponent } from './ticket-contents/ticket-content-printer/channel-ticket-content-printer.component';

@NgModule({
    declarations: [
        ChannelCommunicationContainerComponent,
        ChannelImportComComponentsDialogComponent,
        BoxofficeLiteralsComponent,
        ChannelCommunicationContentsComponent,
        ChannelCommunicationPurchaseConfirmComponent,
        ChannelCommunicationBookingConfirmComponent,
        ChannelCommunicationGiftCardConfirmComponent,
        ChannelCommunicationReallocationConfirmComponent,
        ChannelCommunicationTicketsEmailComponent,
        ChannelCommunicationVoucherRefundComponent,
        ChannelTicketContentsComponent,
        ChannelTicketContentPdfComponent,
        ChannelTicketContentPassbookComponent,
        ChannelTicketContentPrinterComponent,
        ChannelPurchaseContentsComponent,
        ChannelCommunicationReviewsEmailComponent
    ],
    imports: [
        MaterialModule,
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        ImageUploaderComponent,
        LanguageBarComponent,
        FormContainerComponent,
        FormControlErrorsComponent,
        RichTextAreaComponent,
        SelectServerSearchComponent,
        LastPathGuardListenerDirective,
        ChannelCommunicationRoutingModule,
        ChannelsPipesModule,
        SearchTableComponent,
        DateTimePipe,
        TabsMenuComponent,
        TabDirective,
        ColorPickerComponent,
        EmptyStateTinyComponent,
        DragDropModule,
        HelpButtonComponent,
        ChannelFaqsComponent,
        ChannelFaqDialogComponent,
        IsV3$Pipe,
        IsWebV4$Pipe
    ]
})
export class ChannelCommunicationModule { }
