import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    DestroyRef,
    Directive,
    inject,
    Input,
    OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormArray } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { BehaviorSubject, startWith } from 'rxjs';
import { OptionsTableColumnOption as Col } from './models/options-table.model';

@Directive({
    standalone: true,
    selector: '[appOptionsTable]',
    exportAs: 'appOptionsTable'
})
export class OptionsTableDirective implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);
    private _disabled = true;
    private readonly _allCheckBS = new BehaviorSubject<boolean>(false);
    private readonly _allIndeterminateBS = new BehaviorSubject<boolean>(false);
    private readonly _allHiddenBS = new BehaviorSubject<boolean>(false);

    readonly allCheck$ = this._allCheckBS.asObservable();
    readonly allIndeterminate$ = this._allIndeterminateBS.asObservable();
    readonly allHidden$ = this._allHiddenBS.asObservable();

    @Input() activeRequired = true;
    @Input() form: UntypedFormArray;
    @Input() columns: string[];

    @Input() set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.form?.disable() : this.form?.enable();
    }

    defaultEnabled = true;
    activeEnabled = true;

    ngOnInit(): void {
        if (this._disabled) { this.form.disable(); }
        this.defaultEnabled = this.columns.some(column => column === Col.default);
        this.activeEnabled = this.columns.some(column => column === Col.active);

        this.form.valueChanges
            .pipe(startWith(this.form.value), takeUntilDestroyed(this._destroyRef))
            .subscribe(values => {
                const allActive = values.every(value => value.active);
                const someActive = values.some(value => value.active);
                const hidden = values.length <= 1;
                this._allCheckBS.next(allActive);
                this._allIndeterminateBS.next(!allActive && someActive);
                this._allHiddenBS.next(hidden);
            });
    }

    setDefault(index: number): void {
        if (this.activeEnabled && !this.getValue(index, Col.active)) {
            this.getControl(index, Col.active).setValue(true);
        }
        this.markAllAs({ default: false });
        this.getControl(index, Col.default).setValue(true);
        this.form.markAsDirty();
    }

    toggleActive(index: number, event: MatCheckboxChange): void {
        // if activeRequired option is enable avoids unchecking last checkbox
        if ((this.activeRequired && !this.form.value.some(dm => dm.active) && !event.checked) || this.form.disabled) {
            this.getControl(index, Col.active).setValue(true);
        }
        // if default is checked uncheck-it
        if (this.defaultEnabled && this.getValue(index, Col.default)) {
            this.getControl(index, Col.default).setValue(false);
        }
        // if there is something active, use it as default
        if (this.defaultEnabled && this.firstControl(Col.active) && !this.firstControl(Col.default)) {
            this.firstControl(Col.active).get(Col.default).setValue(true);
        }
        this.form.markAsDirty();
    }

    toggleAllActive(): void {
        if (this._allCheckBS.value) {
            if (this.activeRequired) {
                this.markAllAs({ active: false });
                const index = this.defaultEnabled ? this.form.controls.indexOf(this.firstControl(Col.default)) : 0;
                this.getControl(index, Col.active).setValue(true);
            } else {
                this.defaultEnabled ?
                    this.markAllAs({ active: false, default: false }) :
                    this.markAllAs({ active: false });
            }
        } else {
            this.markAllAs({ active: true });
            if (this.defaultEnabled && !this.firstControl(Col.default)) {
                this.firstControl(Col.active).get(Col.default).setValue(true);
            }
        }
        this.form.markAsDirty();
    }

    // get a row/column control
    private getControl(index: number, column: string): AbstractControl {
        return this.form.controls[index].get([column]);
    }

    // get a row/column value
    private getValue(index: number, column: string): unknown {
        return this.getControl(index, column).value;
    }

    /**
     * Sets the value in data parameter for all the forms in the array.
     * This way it only emits one change event
     * @example markAllAs({active: true}); // sets everything as active
     * @param data some data to be setted
     */
    private markAllAs(data: unknown): void {
        this.form.patchValue(Array(this.form.length).fill(data));
    }

    // returns first control with the specified name with a true value
    private firstControl(name: string): AbstractControl {
        return this.form.controls.find(control => control.get([name]).value);
    }
}
