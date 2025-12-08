import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { orderDetailsResolver } from './details/order-details-resolver';
import { OrderDetailsComponent } from './details/order-details.component';
import { OrderGeneralDataComponent } from './general-data/order-general-data.component';

const routes: Routes = [
    {
        path: '',
        component: OrderDetailsComponent,
        resolve: {
            order: orderDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: OrderGeneralDataComponent
            },
            {
                path: 'notes',
                loadChildren: () => import('./notes/order-notes.module').then(m => m.OrderNotesModule),
                data: {
                    breadcrumb: 'ORDER.NOTES.TITLE'
                }
            }

        ]
    },
    {
        path: 'tickets/:ticketId',
        loadChildren: () => import('../../tickets/ticket/ticket-details.module').then(m => m.TicketDetailsModule),
        data: {
            previousBreadcrumb: 'TITLES.ORDER_DETAILS',
            breadcrumb: 'TITLES.TICKET_DETAILS'
        }
    },
    {
        path: 'products/:ticketId',
        loadChildren: () => import('../../products/product/product-sales-routes').then(m => m.PRODUCT_SALE_ROUTES),
        data: {
            previousBreadcrumb: 'TITLES.ORDER_DETAILS',
            breadcrumb: 'TITLES.PRODUCT_DETAILS'
        }
    }

];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class OrderDetailsRoutingModule { }
