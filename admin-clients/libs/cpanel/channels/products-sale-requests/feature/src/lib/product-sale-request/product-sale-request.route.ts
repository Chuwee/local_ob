import { Routes } from '@angular/router';
import { ProductSaleRequestChannelContentComponent } from './communication/channel-content/product-sale-request-channel-content.component';
import { ProductSaleRequestCommunicationComponent } from './communication/product-sale-request-communication.component';
import { ProductSaleRequestTicketContentComponent } from './communication/ticket-content/product-sale-request-ticket-content.component';
import { ProductSaleRequestDetailsComponent } from './details/product-sale-request-details.component';
import { productSaleRequestDetailsResolver } from './details/product-sale-request-details.resolver';
import { ProductSaleRequestPrincipalInfoComponent } from './general-data/principal-info/product-sale-request-principal-info.component';
import { ProductSaleRequestGeneralDataComponent } from './general-data/product-sale-request-general-data.component';
import { ProductSaleRequestPromotionsComponent } from './general-data/promotions/product-sale-request-promotions.component';

export const PRODUCT_SALE_REQUEST_ROUTES: Routes = [{
    path: '',
    component: ProductSaleRequestDetailsComponent,
    resolve: {
        saleRequest: productSaleRequestDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: ProductSaleRequestGeneralDataComponent,
            data: {
                breadcrumb: 'PRODUCTS_SALE_REQUESTS.TITLES.GENERAL_DATA'
            },
            children: [
                {
                    path: '',
                    redirectTo: 'principal-info',
                    pathMatch: 'full'
                },
                {
                    path: 'principal-info',
                    component: ProductSaleRequestPrincipalInfoComponent,
                    data: {
                        breadcrumb: 'PRODUCTS_SALE_REQUESTS.TITLES.PRINCIPAL_INFO'
                    }
                },
                {
                    path: 'promotions',
                    component: ProductSaleRequestPromotionsComponent,
                    data: {
                        breadcrumb: 'PRODUCTS_SALE_REQUESTS.TITLES.PROMOTIONS'
                    }
                }
            ]
        },
        {
            path: 'communication',
            component: ProductSaleRequestCommunicationComponent,
            data: {
                breadcrumb: 'PRODUCT.COMMUNICATION.TITLE'
            },
            children: [
                {
                    path: '',
                    redirectTo: 'channel-content',
                    pathMatch: 'full'
                },
                {
                    path: 'channel-content',
                    component: ProductSaleRequestChannelContentComponent,
                    data: {
                        breadcrumb: 'PRODUCT.CHANNEL_CONTENTS.TITLE'
                    }
                },
                {
                    path: 'ticket-content',
                    component: ProductSaleRequestTicketContentComponent,
                    data: {
                        breadcrumb: 'PRODUCT.TICKET_CONTENTS.TITLE'
                    }
                }
            ]
        }

    ]
}];
