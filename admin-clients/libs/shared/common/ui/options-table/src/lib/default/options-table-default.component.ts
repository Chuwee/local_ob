import {
    ChangeDetectionStrategy, ChangeDetectorRef,
    Component, DestroyRef, EventEmitter, inject,
    Input, OnInit, Output
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormControl } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatIcon, MatIconButton, FlexLayoutModule
    ],
    selector: 'app-options-table-default',
    templateUrl: './options-table-default.component.html'
})
export class OptionsTableDefaultComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _ref = inject(ChangeDetectorRef);

    @Input() index: number;
    @Input() form: FormArray;
    @Input() formCtrl: FormControl;
    @Output() setDefault = new EventEmitter<number>();

    ngOnInit(): void {
        this.formCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(() => {
                this._ref.markForCheck();
            });

        this.form.statusChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(() => {
                this._ref.markForCheck();
            });
    }
}
