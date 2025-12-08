import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { ChannelDetailPageRoutingModule } from './channel-detail-routing.module';
import { ChannelDetailPage } from './channel-detail.page';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        ChannelDetailPageRoutingModule,
        BackButtonComponent,
        DateTimePipe,
        StaticViewComponent,
        TranslatePipe
    ],
    declarations: [ChannelDetailPage]
})
export class ChannelDetailPageModule { }
