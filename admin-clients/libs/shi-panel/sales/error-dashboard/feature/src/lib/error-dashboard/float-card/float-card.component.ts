
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [MatIconModule, TranslatePipe],
    selector: 'app-float-card',
    templateUrl: './float-card.component.html',
    styleUrls: ['./float-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FloatCardComponent {
    $title = input<string>(null, { alias: 'cardTitle' });
    $icon = input<string>(null, { alias: 'cardIcon' });
}
