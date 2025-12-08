import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { DeviceStorage } from '../../core/services/deviceStorage';
import { LanguageSelectorComponent } from './components/language-selector/language-selector.component';
import { ProfileConfigComponent } from './components/profile-config/profile-config.component';
import { ProfileRoutingModule } from './profile-routing.module';
import { ProfileComponent } from './profile.component';

@NgModule({
    declarations: [
        ProfileComponent,
        ProfileConfigComponent,
        LanguageSelectorComponent
    ],
    imports: [
        CommonModule,
        IonicModule,
        ProfileRoutingModule,
        TranslatePipe,
        BackButtonComponent,
        StaticViewComponent
    ],
    providers: [DeviceStorage]
})
export class ProfileModule { }
