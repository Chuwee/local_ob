import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { channelTypesResolver } from '../channel-type-resolver';
import { BoxofficeLiteralsComponent } from './boxoffice-literals/boxoffice-literals.component';
import { ChannelCommunicationContainerComponent } from './container/channel-communication-container.component';
import { ChannelCommunicationContentsComponent } from './contents/channel-communication-contents.component';
import { ChannelCommunicationBookingConfirmComponent } from './emails/booking-confirm/channel-communication-booking-confirm.component';
import { ChannelCommunicationGiftCardConfirmComponent } from './emails/gift-card-confirm/channel-communication-gift-card-confirm.component';
import { ChannelCommunicationPublishedTicketComponent } from './emails/published-ticket/channel-communication-published-ticket.component';
import { ChannelCommunicationPurchaseConfirmComponent } from './emails/purchase-confirm/channel-communication-purchase-confirm.component';
import {ChannelCommunicationReallocationConfirmComponent} from './emails/reallocation-confirm/channel-communication-reallocation-confirm.component';
import { ChannelCommunicationSoldTicketComponent } from './emails/sold-ticket/channel-communication-sold-ticket.component';
import { ChannelCommunicationTicketsEmailComponent } from './emails/tickets/channel-communication-tickets-email.component';
import { ChannelCommunicationVoucherRefundComponent } from './emails/voucher-refund-email/channel-communication-voucher-refund.component';
import { ChannelFaqsComponent } from './faqs/channel-faqs.component';
import { ChannelLiteralsComponent } from './literals/channel-literals.component';
import { ChannelPurchaseContentsComponent } from './purchase-contents/channel-purchase-contents.component';
import { ChannelTicketContentsComponent } from './ticket-contents/channel-ticket-contents.component';
import { ChannelWhatsappContentsComponent } from './whatsapp/channel-communication-whatsapp.component';
import { ChannelCommunicationReviewsEmailComponent } from './emails/reviews-email/channel-communication-reviews-email.component';

const routes: Routes = [
    {
        path: '',
        component: ChannelCommunicationContainerComponent,
        children: [
            {
                path: '',
                redirectTo: 'literals',
                pathMatch: 'full'
            },
            {
                path: 'portal-literals',
                component: ChannelLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.LITERALS.TITLE'
                }
            },
            {
                path: 'literals',
                component: ChannelLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.NEW_LITERALS.TITLE'
                }
            },
            {
                path: 'literals/email-receipt',
                component: BoxofficeLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.LITERALS.RECEIPT_EMAIL'
                }
            },
            {
                path: 'literals/receipt',
                component: BoxofficeLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.LITERALS.RECEIPT'
                }
            },
            {
                path: 'literals/booking',
                component: BoxofficeLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.LITERALS.BOOKING'
                }
            },
            {
                path: 'literals/cashbox',
                component: BoxofficeLiteralsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.LITERALS.CASHBOX'
                }
            },
            {
                path: 'channel-contents/general',
                component: ChannelCommunicationContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CHANNEL_COMMUNICATION.GENERAL'
                }
            },
            {
                path: 'channel-contents/legal-texts',
                component: ChannelCommunicationContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CHANNEL_COMMUNICATION.LEGAL_TEXTS'
                }
            },
            {
                path: 'channel-contents/error-texts',
                component: ChannelCommunicationContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CHANNEL_COMMUNICATION.ERROR_TEXTS'
                }
            },
            {
                path: 'channel-contents/delivery-methods',
                component: ChannelCommunicationContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CHANNEL_COMMUNICATION.DELIVERY_METHODS'
                }
            },
            {
                path: 'channel-contents/purchase-process',
                component: ChannelCommunicationContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.CHANNEL_COMMUNICATION.PURCHASE_PROCESS'
                }
            },
            {
                path: 'email-contents/images',
                component: ChannelPurchaseContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.IMAGES.TITLE'
                }
            },
            {
                path: 'email-contents/purchase-confirm',
                component: ChannelCommunicationPurchaseConfirmComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.PURCHASE_CONFIRM.TITLE'
                }
            },
            {
                path: 'email-contents/published-ticket',
                component: ChannelCommunicationPublishedTicketComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.PUBLISHED_TICKET.TITLE'
                }
            },
            {
                path: 'email-contents/sold-ticket',
                component: ChannelCommunicationSoldTicketComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.SOLD_TICKET.TITLE'
                }
            },
            {
                path: 'email-contents/booking-confirm',
                component: ChannelCommunicationBookingConfirmComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.BOOKING_CONFIRM.TITLE'
                }
            },
            {
                path: 'email-contents/gift-card-confirm',
                component: ChannelCommunicationGiftCardConfirmComponent,
                resolve: {
                    channel: channelTypesResolver
                },
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.GIFT_CARD_CONFIRM.TITLE',
                    allowedChannelTypes: [ChannelType.web, ChannelType.webBoxOffice]
                }
            },
            {
                path: 'email-contents/tickets-email',
                component: ChannelCommunicationTicketsEmailComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.TICKETS_EMAIL.TITLE'
                }
            },
            {
                path: 'email-contents/reviews-email',
                component: ChannelCommunicationReviewsEmailComponent,
                resolve: {
                    channel: channelTypesResolver
                },
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.REVIEWS_EMAIL.TITLE',
                    allowedChannelTypes: [ChannelType.web]
                }
            },
            {
                path: 'email-contents/voucher-refund-email',
                component: ChannelCommunicationVoucherRefundComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.VOUCHER_REFUND_EMAIL.TITLE'
                }
            },
            {
                path: 'ticket-contents',
                component: ChannelTicketContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.TICKETS_COMMUNICATION.TITLE'
                }
            },
            {
                path: 'whatsapp-contents',
                component: ChannelWhatsappContentsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.WHATSAPP_COMMUNICATION.TITLE'
                }
            },
            {
                path: 'faqs',
                component: ChannelFaqsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.FAQS.TITLE'
                }
            },
            {
                path: 'portal-contents/faqs',
                component: ChannelFaqsComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.PORTAL.FAQS.TITLE'
                }
            },
            {
                path: 'portal-contents/reallocation-confirm',
                component: ChannelCommunicationReallocationConfirmComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'CHANNELS.EMAILS_COMMUNICATION.REALLOCATION_CONFIRM.TITLE'
                }
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ChannelCommunicationRoutingModule { }
