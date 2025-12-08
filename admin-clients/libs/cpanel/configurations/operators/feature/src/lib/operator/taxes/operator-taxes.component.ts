import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EphemeralMessageService, ObDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    OnDestroy,
    inject, OnInit
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { startWith, Subject, switchMap, take, takeUntil, withLatestFrom } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { NewOperatorTaxDialogComponent } from './create/new-operator-tax-dialog.component';

@Component({
    selector: 'app-operator-taxes',
    templateUrl: './operator-taxes.component.html',
    styleUrls: ['./operator-taxes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        FormContainerComponent,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ]
})
export class OperatorTaxesComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _ephemeralMsgSrv = inject(EphemeralMessageService);
    private readonly _operatorsSrv = inject(OperatorsService);

    readonly form = this._fb.group({
        taxes: this._fb.array<FormGroup<{
            id: FormControl<number>;
            default: FormControl<boolean>;
            name: FormControl<string>;
            value: FormControl<number>;
        }>>([], { updateOn: 'blur' })
    });

    readonly taxGroups$ = this.form.valueChanges.pipe(
        startWith(null),
        map(() => this.form.controls.taxes?.controls?.length ? this.form.controls.taxes?.controls : null)
    );

    readonly reqInProgress$ = booleanOrMerge([
        this._operatorsSrv.operatorTaxes.loading$(), this._operatorsSrv.operatorTaxes.saving$()
    ]);

    ngOnInit(): void {
        this._operatorsSrv.operator.get$().pipe(take(1))
            .subscribe(operatorEnt => this._operatorsSrv.operatorTaxes.load(operatorEnt.id));
        this._operatorsSrv.operatorTaxes.get$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(taxes => {
                this.form.markAsPristine();
                if (taxes) {
                    taxes.forEach((tax, index) => {
                        const formTaxGroup = this.form.controls.taxes.controls[index];
                        if (formTaxGroup) {
                            formTaxGroup.setValue({
                                id: tax.id,
                                default: tax.default,
                                name: tax.name,
                                value: tax.value
                            });
                        } else {
                            this.form.controls.taxes.push(this._fb.group({
                                id: tax.id,
                                default: tax.default,
                                name: [tax.name, [Validators.required]],
                                value: tax.value
                            }));
                        }
                    });
                    while (this.form.controls.taxes.controls.length > taxes.length) {
                        this.form.controls.taxes.removeAt(this.form.controls.taxes.controls.length - 1);
                    }
                } else {
                    this.form.controls.taxes.clear();
                }
                this.form.updateValueAndValidity();
            });
        this.form.valueChanges.pipe(takeUntil(this._onDestroy)).subscribe(() => this.updateTaxes());
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._operatorsSrv.operatorTaxes.clear();
    }

    changeDefaultTax(taxId: number): void {
        this.form.controls.taxes.controls.forEach(taxGroup => taxGroup.controls.default.setValue(false, { emitEvent: false }));
        const newDefault = this.form.controls.taxes.controls.find(tax => tax.value.id === taxId);
        newDefault?.controls.default.setValue(true, { emitEvent: false });
        newDefault.markAsDirty();
        this.updateTaxes();
    }

    updateTaxes(): void {
        if (this.form.controls.taxes.length && this.form.valid && this.form.dirty) {
            this._operatorsSrv.operator.get$()
                .pipe(
                    take(1),
                    switchMap(operator => this._operatorsSrv.operatorTaxes.update(operator.id, this.form.value.taxes.map(taxValue => ({
                        id: taxValue.id,
                        default: taxValue.default,
                        name: taxValue.name
                    })))
                        .pipe(map(() => operator)))
                )
                .subscribe(operator => {
                    this._ephemeralMsgSrv.showSaveSuccess();
                    this._operatorsSrv.operatorTaxes.load(operator.id);
                });
        }
    }

    openNewTaxDialog(): void {
        this._operatorsSrv.operator.get$()
            .pipe(
                take(1),
                switchMap(operator => this._dialogSrv.open(NewOperatorTaxDialogComponent, { operatorId: operator.id }).beforeClosed()),
                filter(Boolean),
                withLatestFrom(this._operatorsSrv.operator.get$())
            )
            .subscribe(([_, operator]) => this._operatorsSrv.operatorTaxes.load(operator.id));
    }
}
