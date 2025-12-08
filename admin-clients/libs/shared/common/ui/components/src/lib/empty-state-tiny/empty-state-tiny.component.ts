import { booleanAttribute, ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';

@Component({
    imports: [
        FlexLayoutModule
    ],
    selector: 'app-empty-state-tiny',
    templateUrl: './empty-state-tiny.component.html',
    styleUrls: ['./empty-state-tiny.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmptyStateTinyComponent {
    readonly $title = input<string>(null, { alias: 'title' });
    readonly $description = input<string>(null, { alias: 'description' });
    readonly $emptyBorder = input(false, { alias: 'emptyBorder', transform: booleanAttribute });
}
