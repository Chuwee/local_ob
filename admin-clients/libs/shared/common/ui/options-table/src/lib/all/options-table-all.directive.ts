import { Directive, ElementRef, inject, Renderer2 } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatCheckbox } from '@angular/material/checkbox';
import { startWith } from 'rxjs';
import { OptionsTableDirective } from '../options-table.directive';

@Directive({
    standalone: true,
    selector: '[appOptionsTableAll]'
})
export class OptionsTableAllDirective {
    private readonly _optionsTableDirective = inject(OptionsTableDirective);
    private readonly _elementRef = inject<ElementRef<HTMLInputElement>>(ElementRef);
    private readonly _matCheckBox = inject<MatCheckbox>(MatCheckbox);
    private readonly _renderer = inject(Renderer2);

    constructor() {
        this._optionsTableDirective.allHidden$
            .pipe(takeUntilDestroyed())
            .subscribe(allHidden => {
                if (allHidden) {
                    this._renderer.setStyle(this._elementRef.nativeElement, 'visibility', 'hidden' );
                } else {
                    this._renderer.setStyle(this._elementRef.nativeElement, 'visibility', 'visible' );
                }
            });

        this._optionsTableDirective.allIndeterminate$
            .pipe(takeUntilDestroyed())
            .subscribe(allIndeterminate => {
                this._matCheckBox.indeterminate = allIndeterminate;
            });

        this._optionsTableDirective.allCheck$
            .pipe(takeUntilDestroyed())
            .subscribe(allCheck => {
                this._matCheckBox.checked = allCheck;
            });

        this._optionsTableDirective.form.statusChanges
            .pipe(startWith(this._optionsTableDirective.form.status), takeUntilDestroyed())
            .subscribe(status => {
                this._matCheckBox.disabled = status === 'DISABLED';
            });

        this._matCheckBox.change
            .pipe(takeUntilDestroyed())
            .subscribe(() => {
                this._optionsTableDirective.toggleAllActive();
            });
    }
}
