import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsPipesModule } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { LinkListComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    CopyTextComponent, EmptyStateComponent, GoBackComponent, ImageUploaderComponent, LanguageBarComponent, NavTabsMenuComponent,
    RichTextAreaComponent, SelectSearchComponent, StatusSelectComponent, ContextNotificationComponent, TabDirective, TabsMenuComponent, EmptyStateTinyComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    DateTimePipe, ErrorMessage$Pipe, LocalCurrencyPipe, LocalNumberPipe
} from '@admin-clients/shared/utility/pipes';
import { OptionsTableComponent } from '@admin-clients/shared-common-ui-options-table';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { IsSaleRequestWebChannelPipe } from '../pipes/sale-request-web-type.pipe';
import {
    SaleRequestAdditionalBannerComponent
} from './communication/channel-content/additional-banner/sale-request-additional-banner.component';
import {
    SaleRequestEventChannelContentsComponent
} from './communication/channel-content/event-channel-contents/sale-request-event-channel-contents.component';
import {
    SaleRequestPurchaseContentsComponent
} from './communication/channel-content/purchase-contents/sale-request-purchase-contents.component';
import { SaleRequestChannelContentComponent } from './communication/channel-content/sale-request-channel-content.component';
import {
    SaleRequestSeasonTicketChannelContentsComponent
} from './communication/channel-content/season-ticket-channel-contents/sale-request-season-ticket-channel-contents.component';
import { SaleRequestSessionsLinkListComponent }
    from './communication/channel-content/sessions-link-list/sale-request-sessions-link-list.component';
import { SaleRequestEmailContentsComponent } from './communication/email-contents/sale-request-email-contents.component';
import { SaleRequestCommunicationComponent } from './communication/sale-request-communication.component';
import { SaleRequestTicketContentsComponent } from './communication/ticket-contents/sale-request-ticket-contents.component';
import {
    SaleRequestTicketContentPdfComponent
} from './communication/ticket-contents/ticket-content-pdf/sale-request-ticket-content-pdf.component';
import {
    SaleRequestTicketContentPrinterComponent
} from './communication/ticket-contents/ticket-content-printer/sale-request-ticket-content-printer.component';
import { SaleRequestDetailsComponent } from './details/sale-request-details.component';
import { SaleRequestPrincipalInfoComponent } from './general-data/principal-info/sale-request-principal-info.component';
import { SaleRequestSessionsListComponent }
    from './general-data/principal-info/sale-request-sessions-list/sale-request-sessions-list.component';
import { SaleRequestPromotionsComponent } from './general-data/promotions/sale-request-promotions.component';
import { SaleRequestGeneralDataComponent } from './general-data/sale-request-general-data.component';
import {
    SaleRequestAdditionalConditionsDialogComponent
} from './operative/additional-conditions/additional-conditions-dialog/sale-request-additional-conditions-dialog.component';
import { SaleRequestAdditionalConditionsComponent } from './operative/additional-conditions/sale-request-additional-conditions.component';
import {
    SaleRequestCommissionsGenericComponent
} from './operative/commissions/generic/sale-request-commissions-generic.component';
import {
    SaleRequestCommissionsPromotionComponent
} from './operative/commissions/promotion/sale-request-commissions-promotion.component';
import { SaleRequestCommissionsComponent } from './operative/commissions/sale-request-commissions.component';
import { SaleRequestConfigurationComponent } from './operative/configuration/sale-request-configuration.component';
import { SaleRequestDeliveryConditionsComponent } from './operative/delivery-conditions/sale-request-delivery-conditions.component';
import { SaleRequestPaymentMethodsComponent } from './operative/payment-methods/sale-request-payment-methods.component';
import { SaleRequestOperativeComponent } from './operative/sale-request-operative.component';
import {
    SaleRequestSurchargesGenericComponent
} from './operative/surcharges/generic/sale-request-surcharges-generic.component';
import {
    SaleRequestSurchargesInvitationComponent
} from './operative/surcharges/invitation/sale-request-surcharges-invitation.component';
import {
    SaleRequestSurchargesPromotionComponent
} from './operative/surcharges/promotion/sale-request-surcharges-promotion.component';
import { SaleRequestPriceSimulationComponent }
    from './operative/surcharges/sale-request-price-simulation/sale-request-price-simulation.component';
import { SaleRequestSurchargesComponent } from './operative/surcharges/sale-request-surcharges.component';
import {
    SaleRequestChannelSurchargesTaxesComponent
} from './operative/surcharges/taxes/sale-request-surcharges-taxes.component';
import { SaleRequestRoutingModule } from './sale-request-routing.module';

@NgModule({
    declarations: [
        SaleRequestDetailsComponent,
        SaleRequestOperativeComponent,
        SaleRequestSurchargesComponent,
        IsSaleRequestWebChannelPipe,
        SaleRequestCommissionsComponent,
        SaleRequestGeneralDataComponent,
        SaleRequestPrincipalInfoComponent,
        SaleRequestSessionsListComponent,
        SaleRequestPurchaseContentsComponent,
        SaleRequestEventChannelContentsComponent,
        SaleRequestPromotionsComponent,
        SaleRequestPriceSimulationComponent,
        SaleRequestConfigurationComponent,
        SaleRequestCommunicationComponent,
        SaleRequestChannelContentComponent,
        SaleRequestTicketContentsComponent,
        SaleRequestTicketContentPdfComponent,
        SaleRequestTicketContentPrinterComponent,
        SaleRequestSessionsLinkListComponent,
        SaleRequestPaymentMethodsComponent,
        SaleRequestDeliveryConditionsComponent,
        SaleRequestAdditionalConditionsComponent,
        SaleRequestAdditionalConditionsDialogComponent,
        SaleRequestEmailContentsComponent,
        SaleRequestAdditionalBannerComponent,
        SaleRequestSeasonTicketChannelContentsComponent
    ],
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule,
        FormContainerComponent,
        FormControlErrorsComponent,
        ContextNotificationComponent,
        SaleRequestRoutingModule,
        StatusSelectComponent,
        ChannelsPipesModule,
        LocalNumberPipe,
        LocalCurrencyPipe,
        DateTimePipe,
        TabsMenuComponent,
        TabDirective,
        EmptyStateComponent,
        NavTabsMenuComponent,
        SaleRequestCommissionsGenericComponent,
        SaleRequestCommissionsPromotionComponent,
        SaleRequestSurchargesGenericComponent,
        SaleRequestSurchargesInvitationComponent,
        SaleRequestSurchargesPromotionComponent,
        OptionsTableComponent,
        CopyTextComponent,
        RichTextAreaComponent,
        ImageUploaderComponent,
        LanguageBarComponent,
        LastPathGuardListenerDirective,
        GoBackComponent,
        SelectSearchComponent,
        EmptyStateTinyComponent,
        EllipsifyDirective,
        LinkListComponent,
        ErrorMessage$Pipe,
        SaleRequestChannelSurchargesTaxesComponent
    ]
})
export class SaleRequestModule { }
