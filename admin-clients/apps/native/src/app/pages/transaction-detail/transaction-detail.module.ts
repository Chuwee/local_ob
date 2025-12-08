import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule, UpperCasePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { TicketDetailCardComponent } from '../../core/components/ticket-detail-card/ticket-detail-card.component';
import { TransactionDetailRoutingModule } from './transaction-detail-routing.module';
import { TransactionDetailPage } from './transaction-detail.page';

@NgModule({
    declarations: [TransactionDetailPage],
    imports: [
        CommonModule,
        IonicModule,
        TransactionDetailRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        StaticViewComponent,
        FormsModule,
        ReactiveFormsModule,
        TicketDetailCardComponent,
        LocalCurrencyPipe,
        UpperCasePipe
    ]
})
export class TransactionDetailModule {
}
