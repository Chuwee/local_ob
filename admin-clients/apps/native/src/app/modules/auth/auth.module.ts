import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { PickerComponent } from '../../core/components/picker/picker.component';
import { DeviceStorage } from '../../core/services/deviceStorage';
import { AuthRoutingModule } from './auth-routing.module';
import { AuthComponent } from './auth.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { AutofillDirective } from './directives/autofill.directive';

@NgModule({
    declarations: [
        AuthComponent,
        ForgotPasswordComponent,
        AutofillDirective
    ],
    imports: [
        CommonModule,
        IonicModule.forRoot({
            mode: 'md'
        }),
        AuthRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        ModalComponent,
        PickerComponent,
        BackButtonComponent,
        TranslatePipe,
        ContextNotificationComponent
    ],
    providers: [
        DeviceStorage,
        provideHttpClient(withInterceptorsFromDi())
    ]
})
export class AuthModule { }
