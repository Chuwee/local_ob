import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { DeviceStorage } from 'apps/native/src/app/core/services/deviceStorage';

@Component({
    selector: 'slide4',
    imports: [
        CommonModule,
        IonicModule,
        TranslatePipe
    ],
    templateUrl: './slide4.component.html',
    styleUrls: ['./slide4.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class Slide4Component {
    private _deviceStorage = inject(DeviceStorage);
    private readonly _router = inject(Router);

    onNext(): void {
        this._deviceStorage.setItem('onboarding-executed', true);
        this._router.navigate(['/login']);
    }
}
