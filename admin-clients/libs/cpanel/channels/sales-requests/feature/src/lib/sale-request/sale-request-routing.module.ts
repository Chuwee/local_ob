import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SaleRequestChannelContentComponent } from './communication/channel-content/sale-request-channel-content.component';
import { SaleRequestEmailContentsComponent } from './communication/email-contents/sale-request-email-contents.component';
import { SaleRequestPaymentMethodsBenefitsContentsComponent } from './communication/payment-methods-benefits-contents/sale-request-benefits-contents.component';
import { SaleRequestCommunicationComponent } from './communication/sale-request-communication.component';
import { SaleRequestTicketContentsComponent } from './communication/ticket-contents/sale-request-ticket-contents.component';
import { saleRequestDetailsResolver } from './details/sale-request-details-resolver';
import { SaleRequestDetailsComponent } from './details/sale-request-details.component';
import { SaleRequestPrincipalInfoComponent } from './general-data/principal-info/sale-request-principal-info.component';
import { SaleRequestPromotionsComponent } from './general-data/promotions/sale-request-promotions.component';
import { SaleRequestGeneralDataComponent } from './general-data/sale-request-general-data.component';
import { SaleRequestAdditionalConditionsComponent } from './operative/additional-conditions/sale-request-additional-conditions.component';
import { SaleRequestCommissionsComponent } from './operative/commissions/sale-request-commissions.component';
import { SaleRequestConfigurationComponent } from './operative/configuration/sale-request-configuration.component';
import { SaleRequestDeliveryConditionsComponent } from './operative/delivery-conditions/sale-request-delivery-conditions.component';
import { SaleRequestPaymentMethodsComponent } from './operative/payment-methods/sale-request-payment-methods.component';
import { SaleRequestOperativeComponent } from './operative/sale-request-operative.component';
import { SaleRequestSurchargesComponent } from './operative/surcharges/sale-request-surcharges.component';

const routes: Routes = [{
    path: '',
    component: SaleRequestDetailsComponent,
    resolve: {
        saleRequest: saleRequestDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'operative',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: SaleRequestGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'SALE_REQUEST.GENERAL_DATA.TITLE'
            },
            children: [
                {
                    path: '',
                    redirectTo: 'principal-info',
                    pathMatch: 'full'
                },
                {
                    path: 'principal-info',
                    component: SaleRequestPrincipalInfoComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.PRINCIPAL_INFO.TITLE'
                    }
                },
                {
                    path: 'promotions',
                    component: SaleRequestPromotionsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.PROMOTIONS.TITLE'
                    }
                }
            ]
        },
        {
            path: 'operative',
            component: SaleRequestOperativeComponent,
            data: {
                breadcrumb: 'SALE_REQUEST.OPERATIVE.TITLE'
            },
            children: [
                {
                    path: '',
                    redirectTo: 'surcharges',
                    pathMatch: 'full'
                },
                {
                    path: 'surcharges',
                    component: SaleRequestSurchargesComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.SURCHARGES.TITLE'
                    }
                },
                {
                    path: 'commissions',
                    component: SaleRequestCommissionsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMISSIONS.TITLE'
                    }
                },
                {
                    path: 'delivery-conditions',
                    component: SaleRequestDeliveryConditionsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.DELIVERY_CONDITIONS.TITLE'
                    }
                },
                {
                    path: 'additional-conditions',
                    component: SaleRequestAdditionalConditionsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.ADDITIONAL_CONDITIONS.TITLE'
                    }
                },
                {
                    path: 'payment-methods',
                    component: SaleRequestPaymentMethodsComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.PAYMENT_METHODS.TITLE'
                    }
                },
                {
                    path: 'configuration',
                    component: SaleRequestConfigurationComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'SALE_REQUEST.CONFIGURATION.TITLE'
                    }
                }
            ]
        },
        {
            path: 'communication',
            component: SaleRequestCommunicationComponent,
            data: {
                breadcrumb: 'SALE_REQUEST.COMMUNICATION.TITLE'
            },
            children: [
                {
                    path: '',
                    redirectTo: 'channel-contents',
                    pathMatch: 'full'
                },
                {
                    path: 'channel-contents',
                    component: SaleRequestChannelContentComponent,
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMUNICATION.CHANNEL.TITLE'
                    },
                    canDeactivate: [unsavedChangesGuard()]
                },
                {
                    path: 'ticket-contents',
                    component: SaleRequestTicketContentsComponent,
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMUNICATION.TICKET.TITLE'
                    },
                    canDeactivate: [unsavedChangesGuard()]
                },
                {
                    path: 'email-contents',
                    component: SaleRequestEmailContentsComponent,
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMUNICATION.EMAIL.TITLE'
                    },
                    canDeactivate: [unsavedChangesGuard()]
                },
                {
                    path: 'payment-methods-benefits-contents',
                    component: SaleRequestPaymentMethodsBenefitsContentsComponent,
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMUNICATION.PAYMENT_METHODS_BENEFITS.TITLE'
                    },
                    canDeactivate: [unsavedChangesGuard()]
                }
                /*
                {
                    path: 'invitation-contents',
                    component: SaleRequestInvitationContentComponent,
                    data: {
                        breadcrumb: 'SALE_REQUEST.COMMUNICATION.INVITATION.TITLE'
                    },
                    canDeactivate: [unsavedChangesGuard()]
                }*/
            ]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SaleRequestRoutingModule { }
