import { NgForTrackByPropertyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { SelectComponent } from '../../core/components/select/select.component';
import { SessionCardComponent } from '../../core/components/session-card/session-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import {
    TicketAccessControlCardComponent
} from '../../core/components/ticket-access-control-card/ticket-access-control-card.component';
import { TicketDetailCardComponent } from '../../core/components/ticket-detail-card/ticket-detail-card.component';
import { EventsDetailPageModule } from '../events-detail/events-detail.module';
import { PriceZoneComponent } from './components/price-zone/price-zone.component';
import { SessionDetailPageRoutingModule } from './session-detail-routing.module';
import { SessionDetailPage } from './session-detail.page';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        SessionDetailPageRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        StaticViewComponent,
        TicketDetailCardComponent,
        DateTimePipe,
        SelectComponent,
        SessionCardComponent,
        TicketAccessControlCardComponent,
        EventsDetailPageModule,
        NgForTrackByPropertyDirective
    ],
    declarations: [SessionDetailPage, PriceZoneComponent]
})
export class SessionDetailPageModule {
}
