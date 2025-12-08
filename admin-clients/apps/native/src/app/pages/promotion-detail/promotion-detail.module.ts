import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { PromotionDetailRoutingModule } from './promotion-detail-routing.module';
import { PromotionDetailPage } from './promotion-detail.page';

@NgModule({
    declarations: [PromotionDetailPage],
    imports: [
        CommonModule,
        IonicModule,
        PromotionDetailRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        StaticViewComponent,
        DateTimePipe,
        LocalCurrencyPipe
    ]
})
export class PromotionDetailModule { }
