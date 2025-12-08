import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Component({
    // It's a svg component, this is the best way found, works as a directive,
    // but it's a component, for this reason has a directive selector
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: '[appEditorRotateControl]',
    imports: [CommonModule],
    templateUrl: './rotate-control.component.html',
    styleUrls: ['./rotate-control.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RotateControlComponent {

    @Output()
    readonly rotate = new EventEmitter<MouseEvent>();

    @Input()
    hideRects = false;

    dragging = false;

    mouseDown(event: MouseEvent): void {
        this.dragging = true;
        this.rotate.emit(event);
    }

    @HostListener('window:mouseup')
    mouseUpHandler(): void {
        this.dragging = false;
    }
}
