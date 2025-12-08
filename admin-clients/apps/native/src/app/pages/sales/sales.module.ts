import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { CalendarSelectorComponent } from '../../core/components/calendar-selector/calendar-selector.component';
import { CheckboxListTypeaheadComponent } from '../../core/components/checkbox-list-typeahead/checkbox-list-typeahead.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { PickerComponent } from '../../core/components/picker/picker.component';
import { SkeletonCardComponent } from '../../core/components/skeleton-card/skeleton-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { TicketCardComponent } from '../../core/components/ticket-card/ticket-card.component';
import { TransactionCardComponent } from '../../core/components/transaction-card/transaction-card.component';
import { EventsPageRoutingModule } from './sales-routing.module';
import { SalesPage } from './sales.page';

@NgModule({
    imports: [
        IonicModule,
        CommonModule,
        FormsModule,
        EventsPageRoutingModule,
        StaticViewComponent,
        TranslatePipe,
        TicketCardComponent,
        TransactionCardComponent,
        SkeletonCardComponent,
        CheckboxListTypeaheadComponent,
        ModalComponent,
        PickerComponent,
        CalendarSelectorComponent,
        LocalCurrencyPipe
    ],
    declarations: [SalesPage]
})
export class SalesPageModule {
}
