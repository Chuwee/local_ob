import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';

@Component({
    // It's a svg component, this is the best way found, works as a directive,
    // but it's a component, for this reason has a directive selector
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: '[appEditorPointControl]',
    imports: [CommonModule],
    templateUrl: './point-control.component.html',
    styleUrls: ['./point-control.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PointControlComponent {
    @Output()
    readonly move = new EventEmitter<MouseEvent>();
}
