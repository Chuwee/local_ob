import { Directive, HostBinding, HostListener, Input, TemplateRef, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[appTableDetailRow]',
    standalone: false
})
export class TableDetailRowDirective {
    private _row: unknown;
    private _tRef: TemplateRef<unknown>;
    private _opened: boolean;

    @HostBinding('class.expanded')
    get expended(): boolean {
        return this._opened;
    }

    @Input()
    set appTableDetailRow(value: unknown) {
        if (value !== this._row) {
            this._row = value;
            // this.render();
        }
    }

    @Input()
    set appTableDetailRowTpl(value: TemplateRef<unknown>) {
        if (value !== this._tRef) {
            this._tRef = value;
            // this.render();
        }
    }

    @Input() set change(value: boolean) {
        this._opened = value;
        this.toggle();
    }

    @Input() allToggleableRow = true;

    constructor(public vcRef: ViewContainerRef) { }

    @HostListener('click')
    onClick(): void {
        if (!this.allToggleableRow) {
            return;
        }
        this.toggle();
    }

    toggle(): void {
        if (this._opened) {
            this.vcRef.clear();
        } else {
            this.render();
        }
        this._opened = this.vcRef.length > 0;
    }

    private render(): void {
        this.vcRef.clear();
        if (this._tRef && this._row) {
            this.vcRef.createEmbeddedView(this._tRef, { $implicit: this._row });
        }
    }
}
