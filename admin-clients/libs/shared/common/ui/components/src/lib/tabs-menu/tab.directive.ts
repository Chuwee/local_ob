import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { Directive, inject, Input, TemplateRef } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Directive({
    standalone: true,
    selector: '[appTab]',
    exportAs: 'appTab'
})
export class TabDirective {
    private _isLazyLoad = false;

    templateRef = inject<TemplateRef<unknown>>(TemplateRef<unknown>);
    isActive = false;
    @Input() label: string;
    @Input() ctrl: AbstractControl<unknown>;
    @Input() key: string;
    @Input()
    set lazyLoad(value: BooleanInput) {this._isLazyLoad = coerceBooleanProperty(value);}

    get isLazyLoad(): boolean { return this._isLazyLoad; }
}
