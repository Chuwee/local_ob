import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ion-calendar-week',
    styleUrls: ['./calendar-week.component.scss'],
    templateUrl: './calendar-week.component.html',
    imports: [CommonModule, IonicModule, TranslatePipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CalendarWeekComponent {
    readonly weekArray: string[] = [
        'DAY-MO',
        'DAY-TU',
        'DAY-WE',
        'DAY-TH',
        'DAY-FR',
        'DAY-SA',
        'DAY-SU'
    ];
}
