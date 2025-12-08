import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { StaticViewComponent } from '../../core/components/static-view/static-view.component';
import { Slide1Component } from './components/slide1/slide1.component';
import { Slide2Component } from './components/slide2/slide2.component';
import { Slide3Component } from './components/slide3/slide3.component';
import { Slide4Component } from './components/slide4/slide4.component';
import { OnboardingRoutingModule } from './onboarding-routing.module';
import { OnboardingPage } from './onboarding.page';

@NgModule({
    declarations: [OnboardingPage],
    imports: [
        CommonModule,
        IonicModule,
        OnboardingRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        StaticViewComponent,
        Slide1Component,
        Slide2Component,
        Slide3Component,
        Slide4Component
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OnboardingModule { }
