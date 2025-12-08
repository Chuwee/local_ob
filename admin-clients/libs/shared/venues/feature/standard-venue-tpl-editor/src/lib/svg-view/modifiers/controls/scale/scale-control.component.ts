import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, HostListener, Output } from '@angular/core';

@Component({
    // It's a svg component, this is the best way found, works as a directive,
    // but it's a component, for this reason has a directive selector
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: '[appEditorScaleControl]',
    imports: [CommonModule],
    templateUrl: './scale-control.component.html',
    styleUrls: ['./scale-control.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ScaleControlComponent {
    @Output()
    readonly scale = new EventEmitter<MouseEvent>();

    dragging = false;

    mouseDown(event: MouseEvent): void {
        this.dragging = true;
        this.scale.emit(event);
    }

    @HostListener('window:mouseup')
    mouseUpHandler(): void {
        this.dragging = false;
    }
}
