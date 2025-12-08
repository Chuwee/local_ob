import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [TranslatePipe],
    selector: 'app-badge',
    templateUrl: './badge.component.html',
    styleUrls: ['./badge.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BadgeComponent {
    readonly $badge = input<string>('', { alias: 'badge' });
    readonly $badgeClass = input<string>(null, { alias: 'badgeClass' });
}
