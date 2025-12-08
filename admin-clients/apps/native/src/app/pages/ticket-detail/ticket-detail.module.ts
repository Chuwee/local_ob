import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { TicketDetailRoutingModule } from './ticket-detail-routing.module';
import { TicketDetailPage } from './ticket-detail.page';

@NgModule({
    declarations: [TicketDetailPage],
    imports: [
        CommonModule,
        IonicModule,
        TicketDetailRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        StaticViewComponent,
        LocalCurrencyPipe
    ]
})
export class TicketDetailModule {
}
