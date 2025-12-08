import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { NgModule } from '@angular/core';
import { InvoicingRoutingModule } from './invoicing-routing.module';

@NgModule({
    providers: [...entitiesProviders],
    imports: [
        InvoicingRoutingModule
    ]
})
export class InvoicingModule { }
