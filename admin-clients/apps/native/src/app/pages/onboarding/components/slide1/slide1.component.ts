import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'slide1',
    imports: [
        CommonModule,
        IonicModule,
        TranslatePipe
    ],
    templateUrl: './slide1.component.html',
    styleUrls: ['./slide1.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class Slide1Component { }
