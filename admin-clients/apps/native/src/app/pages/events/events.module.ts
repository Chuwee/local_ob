import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { CalendarSelectorComponent } from '../../core/components/calendar-selector/calendar-selector.component';
import { CheckboxListTypeaheadComponent } from '../../core/components/checkbox-list-typeahead/checkbox-list-typeahead.component';
import { EventCardComponent } from '../../core/components/event-card/event-card.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { PickerComponent } from '../../core/components/picker/picker.component';
import { SessionCardComponent } from '../../core/components/session-card/session-card.component';
import { SkeletonCardComponent } from '../../core/components/skeleton-card/skeleton-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { EventsPageRoutingModule } from './events-routing.module';
import { EventsPage } from './events.page';

@NgModule({
    imports: [
        IonicModule,
        CommonModule,
        FormsModule,
        EventsPageRoutingModule,
        StaticViewComponent,
        TranslatePipe,
        EventCardComponent,
        SessionCardComponent,
        SkeletonCardComponent,
        CheckboxListTypeaheadComponent,
        ModalComponent,
        PickerComponent,
        CalendarSelectorComponent
    ],
    declarations: [EventsPage]
})
export class EventsPageModule {
}
