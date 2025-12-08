import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import {
    ErrorMessage$Pipe, LocalCurrencyPipe, LocalNumberPipe
} from '@admin-clients/shared/utility/pipes';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { RangeElement, RangeTableElement } from '@admin-clients/shared-utility-models';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest } from 'rxjs';
import { filter, first } from 'rxjs/operators';
import { CurrencyInputComponent } from '../currency-input/currency-input.component';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { MessageDialogService } from '../message-dialog/message-dialog.service';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';
import { PercentageInputComponent } from '../percentage-input/percentage-input.component';
import { NewRangeDialogComponent, NewRangeDialogInput } from './create/new-range-dialog.component';
import { DeleteRangeDialogComponent, ResizeRangeResult } from './delete/delete-range-dialog.component';
import { RangeColumn } from './range-column.model';
import { atLeastOneRequired, minMaxRangeValidator } from './range-table-validators';

export * from './range-table-utils';
export * from './range-table-validators';

@Component({
    selector: 'app-range-table',
    templateUrl: './range-table.component.html',
    styleUrls: ['./range-table.component.scss'],
    providers: [LocalCurrencyPipe],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, NgClass, ReactiveFormsModule, FlexLayoutModule,
        PercentageInputComponent, TranslatePipe, ErrorIconDirective, ErrorMessage$Pipe,
        LocalCurrencyPipe, LocalNumberPipe, CurrencyInputComponent, MatButton,
        MatIconButton, MatIcon, MatTableModule, MatFormField, MatPrefix
    ]
})
export class RangeTableComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #currencyPipe = inject(LocalCurrencyPipe);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #authSrv = inject(AUTHENTICATION_SERVICE);

    readonly rangeColumn = RangeColumn;
    columns: string[] = Object.values(RangeColumn);
    readonly inputColumns = this.columns.filter(column =>
        column !== RangeColumn.actions && column !== RangeColumn.name
    );

    readonly disabler$ = new BehaviorSubject(false);
    readonly ranges$ = new BehaviorSubject<RangeElement[]>(null);
    ranges: RangeElement[];
    rangesArray: UntypedFormArray;
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();

    @Input() currency: string;
    @Input() currencyFormat: 'wide' | 'narrow';
    @Input() type: 'generic' | 'promotion' | 'invitation' | 'change-seat' = 'generic';
    @Input() onlyFixed = false;
    @Input() form: UntypedFormGroup;
    @Input()
    set data(ranges: RangeElement[]) {
        if (ranges) {
            if (!ranges.length) {
                ranges = [{ from: 0, values: { fixed: 0, percentage: 0 } }];
            } else if (ranges.length === 1) {
                if (ranges[0].values.fixed === undefined) {
                    ranges[0].values.fixed = 0;
                }
                if (ranges[0].values.percentage === undefined) {
                    ranges[0].values.percentage = 0;
                }
            }
            this.ranges$.next(ranges);
        }
    }

    @Input()
    set disabled(disabled: BooleanInput) {
        this.disabler$.next(coerceBooleanProperty(disabled));
    }

    ngOnInit(): void {
        this.rangesArray = this.#fb.array([]);
        // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
        if (!this.currency) {
            this.#authSrv.getLoggedUser$()
                .pipe(first(user => user !== null))
                .subscribe(user => this.currency = user.currency);
        }
        combineLatest([
            this.ranges$.pipe(filter(r => r !== null)),
            this.disabler$
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([ranges, disabled]) => {
                this.processRanges(ranges);
                this.toggleRanges(disabled || false);
            });
        // add our rangeForm to the input form
        if (this.form) {
            this.form.setControl('ranges', this.rangesArray);
        }

        if (this.onlyFixed) {
            this.columns = [RangeColumn.name, RangeColumn.fixed, RangeColumn.actions];
        }
    }

    ngOnDestroy(): void {
        if (this.form) {
            this.form.removeControl('ranges');
        }
    }

    newRange(): void {
        this.#matDialog.open<
            NewRangeDialogComponent,
            NewRangeDialogInput>(
                NewRangeDialogComponent,
                new ObMatDialogConfig({ currency: this.currency, currencyFormat: this.currencyFormat })
            ).beforeClosed()
            .subscribe(from => {
                if (!from) return;
                if (this.findRange(from)) {
                    return this.showWarning('RANGES.RANGE_ALREADY_EXISTS'); // this range already exists
                }
                this.updateData(this.ranges);
                const newRange: RangeElement = { from, values: {} };
                this.ranges.push(newRange);
                this.ranges$.next(this.ranges);
                this.rangesArray.markAsDirty();
                this.rangesArray.markAllAsTouched();
            });
    }

    deleteRange(range: RangeTableElement): void {
        let nextRange: string;
        let previousRange: string;
        if (!this.isLastRange(range) && !this.isFirstRange(range)) {
            nextRange = this.formatRangeName(this.nextRange(range));
            previousRange = this.formatRangeName(this.prevRange(range));
        }
        this.#matDialog.open(DeleteRangeDialogComponent, new ObMatDialogConfig({
            range: this.formatRangeName(range),
            previousRange,
            nextRange
        }))
            .beforeClosed()
            .subscribe(result => {
                if (result) {
                    if (result === ResizeRangeResult.resizeNext || this.isFirstRange(range)) {
                        this.nextRange(range).from = range.from;
                    }
                    this.ranges.splice(this.ranges.indexOf(range), 1);
                    this.updateData(this.ranges);
                    this.ranges$.next(this.ranges);
                    this.rangesArray.markAsDirty();
                    this.rangesArray.markAllAsTouched();
                }
            });
    }

    formatRangeName(range: RangeTableElement): string {
        let fromName = range.from === 0 ? '0' : this.#currencyPipe.transform(range.from, this.currency, this.currencyFormat);
        const to = this.nextRange(range) ? this.nextRange(range).from : 0;
        const toName = to === 0 ? '' : this.#currencyPipe.transform(to - 0.01, this.currency, this.currencyFormat);
        fromName = toName === '' ? this.#currencyPipe.transform(range.from, this.currency, this.currencyFormat) : fromName;
        return fromName + ' - ' + toName;
    }

    isLastRange(range: RangeTableElement): boolean {
        return this.ranges.indexOf(range) === this.ranges.length - 1;
    }

    private isFirstRange(range: RangeTableElement): boolean {
        return this.ranges.indexOf(range) === 0;
    }

    private findRange(from: number): RangeTableElement {
        return this.ranges.find(elem => elem.from === from);
    }

    private nextRange(range: RangeTableElement): RangeElement {
        return this.ranges[this.ranges.indexOf(range) + 1];
    }

    private prevRange(range: RangeTableElement): RangeElement {
        return this.ranges[this.ranges.indexOf(range) - 1];
    }

    private toggleRanges(disabled: boolean): void {
        if (disabled && !this.rangesArray.disabled) {
            this.rangesArray.disable();
            if (this.columns.includes('actions')) {
                this.columns.pop();
            }
        } else if (!disabled && this.rangesArray.disabled) {
            this.rangesArray.enable();
            this.columns = Object.values(RangeColumn);
        }
    }

    private processRanges(ranges: RangeTableElement[]): void {
        this.rangesArray.clear(); // deletes all elements of inputs array
        this.rangesArray.markAsPristine();
        ranges.sort((a, b) => a.from - b.from);
        for (const range of ranges) {
            // create group of controls per range
            const rangeControl = this.createRangeControl(range);
            // push this control to the form array
            this.rangesArray.push(rangeControl);
            range.ctrl = rangeControl.get('values') as UntypedFormGroup;
        }
        this.ranges = ranges;
        this.#ref.markForCheck();
    }

    private createRangeControl(range: RangeElement): UntypedFormGroup {
        const minCtrl = this.#fb.control(range.values.min);
        const maxCtrl = this.#fb.control(range.values.max);
        const percentageCtrl = this.#fb.control(range.values.percentage);
        const fixedCtrl = this.#fb.control(range.values.fixed);
        const controlsRow = this.#fb.group({
            from: [range.from, [Validators.required, Validators.min(0)]],
            values: this.#fb.group({
                min: minCtrl,
                max: maxCtrl,
                percentage: percentageCtrl,
                fixed: fixedCtrl
            })
        });

        minCtrl.setValidators([Validators.min(0), minMaxRangeValidator(minCtrl, maxCtrl)]);
        minCtrl.updateValueAndValidity();
        maxCtrl.setValidators([Validators.min(0), minMaxRangeValidator(minCtrl, maxCtrl)]);
        maxCtrl.updateValueAndValidity();
        percentageCtrl.setValidators([Validators.min(0), atLeastOneRequired([percentageCtrl, fixedCtrl])]);
        percentageCtrl.updateValueAndValidity();
        fixedCtrl.setValidators([Validators.min(0), atLeastOneRequired([percentageCtrl, fixedCtrl])]);
        fixedCtrl.updateValueAndValidity();
        controlsRow.get('values').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                minCtrl.updateValueAndValidity({ emitEvent: false });
                maxCtrl.updateValueAndValidity({ emitEvent: false });
                percentageCtrl.updateValueAndValidity({ emitEvent: false });
                fixedCtrl.updateValueAndValidity({ emitEvent: false });
            });

        return controlsRow;
    }

    private updateData(ranges: RangeTableElement[]): void {
        ranges.forEach(range => {
            if (Object.keys(range.values).length === 0) {
                range.values = range.ctrl.value;
            }
        });
    }

    private showWarning(message: string): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL, title: 'TITLES.WARNING', message
        });
    }

}
