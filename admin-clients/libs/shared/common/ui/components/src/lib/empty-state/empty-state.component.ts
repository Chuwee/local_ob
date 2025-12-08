import { booleanAttribute, ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
    selector: 'app-empty-state',
    templateUrl: './empty-state.component.html',
    styleUrls: ['./empty-state.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmptyStateComponent {
    readonly $title = input<string>(null, { alias: 'title' });
    readonly $description = input<string>(null, { alias: 'description' });
    readonly $alt = input<string>(null, { alias: 'accessibilityText' });
    readonly $hideImage = input(false, { alias: 'hideImage', transform: booleanAttribute });
}
