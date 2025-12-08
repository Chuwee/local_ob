import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'slide3',
    imports: [
        CommonModule,
        IonicModule,
        TranslatePipe
    ],
    templateUrl: './slide3.component.html',
    styleUrls: ['./slide3.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class Slide3Component { }
