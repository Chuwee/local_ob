import { LocalCurrencyPipe, LocalCurrencySymbolPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { NativeChartComponent } from '../../core/components/chart/chart.component';
import { EventCardComponent } from '../../core/components/event-card/event-card.component';
import { SelectComponent } from '../../core/components/select/select.component';
import { SkeletonCardComponent } from '../../core/components/skeleton-card/skeleton-card.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';

@NgModule({
    declarations: [HomeComponent],
    imports: [
        CommonModule,
        IonicModule,
        TranslatePipe,
        HomeRoutingModule,
        NativeChartComponent,
        EventCardComponent,
        SkeletonCardComponent,
        StaticViewComponent,
        LocalCurrencyPipe,
        LocalCurrencySymbolPipe,
        SelectComponent
    ]
})
export class HomeModule {
}
