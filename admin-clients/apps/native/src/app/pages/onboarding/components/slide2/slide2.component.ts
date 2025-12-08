import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'slide2',
    imports: [
        CommonModule,
        IonicModule,
        TranslatePipe
    ],
    templateUrl: './slide2.component.html',
    styleUrls: ['./slide2.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class Slide2Component { }
