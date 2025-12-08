import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { EventCardComponent } from '../../core/components/event-card/event-card.component';
import { SkeletonCardComponent } from '../../core/components/skeleton-card/skeleton-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { TicketCardComponent } from '../../core/components/ticket-card/ticket-card.component';
import { TransactionCardComponent } from '../../core/components/transaction-card/transaction-card.component';
import { GobalSearchRoutingModule } from './global-search-routing.module';
import { GlobalSearchComponent } from './global-search.component';

@NgModule({
    declarations: [GlobalSearchComponent],
    imports: [
        CommonModule,
        IonicModule,
        GobalSearchRoutingModule,
        BackButtonComponent,
        FormsModule,
        TranslatePipe,
        StaticViewComponent,
        TicketCardComponent,
        DateTimePipe,
        SkeletonCardComponent,
        EventCardComponent,
        TransactionCardComponent
    ]
})
export class GlobalSearchModule { }
