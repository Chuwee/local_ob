import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TicketsPassbookCustomFieldTypes } from '../../../../models/ticket-passbook-custom-field-types.enum';

@Component({
    selector: 'app-custom-content-field',
    templateUrl: './custom-content-field.component.html',
    styleUrls: ['./custom-content-field.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CustomContentFieldComponent implements OnInit, OnDestroy {

    private _onDestroy = new Subject<void>();

    @Input() customTemplateLiterals$: Observable<string[]>;
    @Input() customTemplatePlaceholders$: Observable<string[]>;
    @Input() formGroup: UntypedFormGroup;
    @Input() contentNumber = 1;
    @Output() deleteContent = new EventEmitter<void>();

    get titleList$(): Observable<string[]> {
        if (this.formGroup.get('title.type').value === TicketsPassbookCustomFieldTypes.label) {
            return this.customTemplateLiterals$;
        } else {
            return this.customTemplatePlaceholders$;
        }
    }

    get descriptionList$(): Observable<string[]> {
        if (this.formGroup.get('description.type').value === TicketsPassbookCustomFieldTypes.label) {
            return this.customTemplateLiterals$;
        } else {
            return this.customTemplatePlaceholders$;
        }
    }

    ticketsPassbookCustomFieldTypes = TicketsPassbookCustomFieldTypes;

    ngOnInit(): void {
        this.watchFormChanges();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private watchFormChanges(): void {
        combineLatest([
            this.formGroup.get('title.type').valueChanges,
            this.customTemplateLiterals$
        ])
            .pipe(
                takeUntil(this._onDestroy)
            ).subscribe(([type, literals]: [TicketsPassbookCustomFieldTypes, string[]]) => {
                if (!type || type === this.ticketsPassbookCustomFieldTypes.empty) {
                    const blankLiteral = literals.find(literal => literal === 'BLANK_SPACE') || null;
                    this.formGroup.get('title.value').setValue(blankLiteral);
                    this.formGroup.get('title.value').disable();
                } else {
                    this.formGroup.get('title.value').enable();
                }
                this.formGroup.updateValueAndValidity();
            });

        combineLatest([
            this.formGroup.get('description.type').valueChanges,
            this.customTemplateLiterals$
        ])
            .pipe(
                takeUntil(this._onDestroy)
            ).subscribe(([type, literals]: [TicketsPassbookCustomFieldTypes, string[]]) => {
                if (!type || type === this.ticketsPassbookCustomFieldTypes.empty) {
                    const blankLiteral = literals.find(literal => literal === 'BLANK_SPACE') || null;
                    this.formGroup.get('description.value').disable();
                    this.formGroup.get('description.value').setValue(blankLiteral);
                } else {

                    this.formGroup.get('description.value').enable();
                }
                this.formGroup.updateValueAndValidity();

            });

    }

}
