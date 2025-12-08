import { DateTimePipe, LocalCurrencyPipe, LocalCurrencySymbolPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { NativeChartComponent } from '../../core/components/chart/chart.component';
import { DatePickerComponent } from '../../core/components/date-picker/date-picker.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { WeeklySalesDetailRoutingModule } from './weekly-sales-detail-routing.module';
import { WeeklySalesDetailPage } from './weekly-sales-detail.page';

@NgModule({
    declarations: [WeeklySalesDetailPage],
    imports: [
        CommonModule,
        IonicModule,
        WeeklySalesDetailRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        StaticViewComponent,
        NativeChartComponent,
        DateTimePipe,
        DatePickerComponent,
        LocalCurrencyPipe,
        LocalCurrencySymbolPipe
    ]
})
export class WeeklySalesDetailModule { }
