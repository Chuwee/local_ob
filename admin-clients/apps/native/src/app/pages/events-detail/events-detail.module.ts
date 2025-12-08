import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { CalendarSelectorComponent } from '../../core/components/calendar-selector/calendar-selector.component';
import { ChannelEventCardComponent } from '../../core/components/channel-event-card/channel-event-card.component';
import { CheckboxListTypeaheadComponent } from '../../core/components/checkbox-list-typeahead/checkbox-list-typeahead.component';
import { EventCardComponent } from '../../core/components/event-card/event-card.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { PickerComponent } from '../../core/components/picker/picker.component';
import { PromotionCardComponent } from '../../core/components/promotion-card/promotion-card.component';
import { SelectComponent } from '../../core/components/select/select.component';
import { SessionCardComponent } from '../../core/components/session-card/session-card.component';
import { SkeletonCardComponent } from '../../core/components/skeleton-card/skeleton-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { TicketAccessControlCardComponent } from '../../core/components/ticket-access-control-card/ticket-access-control-card.component';
import { ChannelsTabComponent } from './components/channels-tab/channels-tab.component';
import { InfoTabComponent } from './components/info-tab/info-tab.component';
import { PromotionsTabComponent } from './components/promotions-tab/promotions-tab.component';
import { SeasonPackTabComponent } from './components/season-pack-tab/season-pack-tab.component';
import { SessionsTabComponent } from './components/sessions-tab/sessions-tab.component';
import { EventsDetailPageRoutingModule } from './events-detail-routing.module';
import { EventsDetailPage } from './events-detail.page';

@NgModule({
    imports: [
        IonicModule,
        CommonModule,
        FormsModule,
        EventsDetailPageRoutingModule,
        StaticViewComponent,
        TranslatePipe,
        EventCardComponent,
        SkeletonCardComponent,
        BackButtonComponent,
        DateTimePipe,
        CheckboxListTypeaheadComponent,
        ModalComponent,
        PickerComponent,
        SelectComponent,
        SessionCardComponent,
        MatTabsModule,
        CalendarSelectorComponent,
        TicketAccessControlCardComponent,
        PromotionCardComponent,
        CalendarSelectorComponent,
        ChannelEventCardComponent,
        LocalCurrencyPipe
    ],
    declarations: [
        EventsDetailPage,
        InfoTabComponent,
        SessionsTabComponent,
        SeasonPackTabComponent,
        PromotionsTabComponent,
        SeasonPackTabComponent,
        ChannelsTabComponent
    ]
})
export class EventsDetailPageModule {
}
