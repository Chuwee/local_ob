import {
    FormControlErrorsComponent,
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { FormsRules, FormsRulesInfo } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import {
    FormGroup,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormGroup,
    ValidationErrors,
    ValidatorFn,
    Validators
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';
import { RulesEditorConfigRegex } from '../models/rules-editor-config-regex.enum';
import { FormsFieldRules } from '../models/vm-forms-field-rules.model';

@Component({
    selector: 'app-rules-editor-dialog',
    templateUrl: './rules-editor-dialog.component.html',
    styleUrls: ['./rules-editor-dialog.component.scss'],
    providers: [PrefixPipe.provider('CHANNEL_FORM_CONFIG.')],
    imports: [
        FlexModule,
        MatIconModule,
        ReactiveFormsModule,
        MatButtonModule,
        PrefixPipe,
        TranslatePipe,
        NgIf,
        FormControlErrorsComponent,
        NgClass,
        NgForOf,
        MatDialogModule,
        MaterialModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class RulesEditorDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _auth = inject(AuthenticationService);
    private readonly _data = inject<FormsFieldRules>(MAT_DIALOG_DATA);
    private readonly _dialogRef = inject(MatDialogRef<RulesEditorDialogComponent, FormsRules[]>);

    form: FormGroup = this._fb.group({});
    availableRules: FormsRulesInfo[] = this._data.availableRules || [];
    appliedRules: FormsRules[] = this._data.appliedRules || [];
    keyField: string = this._data.key;

    regexOptions = RulesEditorConfigRegex;
    regexOptionsArray: { key: string; value: string }[] = Object.entries(this.regexOptions).map(([key, value]) => ({ key, value }));

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;

        this.availableRules.forEach(rule => {
            const ruleGroup = this._fb.group({ active: false });
            if (rule.value_required && rule.value_type !== 'REGEX') {
                ruleGroup.addControl('value', this._fb.control(
                    { value: null, disabled: true },
                    rule.value_type === 'INTEGER' ? [Validators.required, Validators.pattern('^[0-9]*$')] : Validators.required)
                );
                ruleGroup.get('active').valueChanges
                    .pipe(takeUntil(this._onDestroy))
                    .subscribe(active => {
                        if (active) {
                            ruleGroup.get('value').enable();
                        } else {
                            ruleGroup.get('value').disable();
                        }
                    });
            }

            if (rule.value_type === 'REGEX') {
                const regexControl = this._fb.group({
                    [RulesEditorConfigRegex.letters]: this._fb.group({ active: false }),
                    [RulesEditorConfigRegex.numbers]: this._fb.group({ active: false }),
                    [RulesEditorConfigRegex.limiter]: this._fb.group({
                        active: false,
                        max: [{ value: null, disabled: true }, Validators.required],
                        min: [{ value: null, disabled: true }, Validators.required]
                    }),
                    [RulesEditorConfigRegex.custom]: this._fb.group({
                        active: false,
                        value: [{ value: null, disabled: true }, Validators.required]
                    })
                }, { validators: this.atLeastOneOptionSelected() });

                regexControl.controls[RulesEditorConfigRegex.limiter].get('active').valueChanges
                    .pipe(takeUntil(this._onDestroy))
                    .subscribe(active => {
                        if (active) {
                            regexControl.controls[RulesEditorConfigRegex.limiter].get('max').enable();
                            regexControl.controls[RulesEditorConfigRegex.limiter].get('min').enable();
                        } else {
                            regexControl.controls[RulesEditorConfigRegex.limiter].get('max').disable();
                            regexControl.controls[RulesEditorConfigRegex.limiter].get('min').disable();
                        }
                    });

                this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
                    .pipe(first())
                    .subscribe((isOperator: boolean) => {
                        regexControl.controls[RulesEditorConfigRegex.custom].get('active').valueChanges
                            .pipe(takeUntil(this._onDestroy))
                            .subscribe(active => {
                                if (active && isOperator) {
                                    regexControl.controls[RulesEditorConfigRegex.custom].get('value').enable();
                                } else {
                                    regexControl.controls[RulesEditorConfigRegex.custom].get('value').disable();
                                }
                            });
                    });
                ruleGroup.addControl('value', regexControl);
            }
            this.form.addControl(rule.rule, ruleGroup);
        });

        this.appliedRules.forEach(rule => {
            this.form.patchValue({
                [rule.rule]: { active: true }
            });
            if (rule.rule !== 'REGEX') {
                this.form.patchValue({
                    [rule.rule]: { value: rule.value }
                });
            } else {
                let originalRegex = rule.value;

                const regexLetterNumbers = /([\[A\-z0\-9\]\*])+/g;
                const lettersNumbersMarch = originalRegex.match(regexLetterNumbers);

                if (lettersNumbersMarch && lettersNumbersMarch.length === 1) {
                    this.form.patchValue({
                        [rule.rule]: {
                            value: {
                                [RulesEditorConfigRegex.letters]: { active: originalRegex.includes('A-z') },
                                [RulesEditorConfigRegex.numbers]: { active: originalRegex.includes('0-9') }
                            }
                        }
                    });
                    originalRegex = originalRegex.replace(lettersNumbersMarch[0], '');
                }

                const regexLimiter = /(\.?)\{(\d+),(\d+)\}/g;
                const limiterMatch = originalRegex.match(regexLimiter);
                if (limiterMatch && limiterMatch.length === 1) {
                    const value = limiterMatch[0].match(/\{(\d+),(\d+)\}/);
                    const min = Number(value[1]);
                    const max = Number(value[2]);

                    this.form.patchValue({
                        [rule.rule]: { value: { [RulesEditorConfigRegex.limiter]: { active: true, min, max } } }
                    });
                    originalRegex = originalRegex.replace(limiterMatch[0], '');
                }
                if (originalRegex.length) {
                    this.form.patchValue({
                        [rule.rule]: {
                            value: {
                                [RulesEditorConfigRegex.custom]: { active: true, value: originalRegex }
                            }
                        }
                    });
                }
            }
        });

        this.form.get('REGEX').get('active').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(active => {
                const regexControl = this.form.get('REGEX').get('value');
                if (active) {
                    regexControl.setValidators(this.atLeastOneOptionSelected() as ValidatorFn);
                } else {
                    regexControl.clearValidators();
                }
                regexControl.updateValueAndValidity();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(newAppliedRules: { rule: string; value: string }[] = null): void {
        this._dialogRef.close(newAppliedRules);
    }

    submit(): void {
        if (this.form.valid) {
            const values = this.form.value;
            const rules: { rule: string; value: string }[] = [];
            Object.keys(values).forEach(rule => {
                if (values[rule].active) {
                    rules.push({ rule, value: rule === 'REGEX' ? this.parseRegex(values[rule].value) : values[rule].value });
                }
            });
            this.close(rules);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    private parseRegex(regexValues: Record<RulesEditorConfigRegex, { active: boolean; min?: number; max?: number; value?: string }>): string {
        let finalRegex = '';
        let limiter = '*';

        if (regexValues[RulesEditorConfigRegex.limiter]?.active) {
            limiter = `{${regexValues[RulesEditorConfigRegex.limiter].min},${regexValues[RulesEditorConfigRegex.limiter].max}}`;
        }

        if (regexValues[RulesEditorConfigRegex.custom]?.active) {
            finalRegex = regexValues[RulesEditorConfigRegex.custom]?.value;
        }

        if (regexValues[RulesEditorConfigRegex.letters]?.active && regexValues[RulesEditorConfigRegex.numbers]?.active) {
            finalRegex += `[A-z0-9]${limiter}`;
        } else if (regexValues[RulesEditorConfigRegex.letters]?.active) {
            finalRegex += `[A-z]${limiter}`;
        } else if (regexValues[RulesEditorConfigRegex.numbers]?.active) {
            finalRegex += `[0-9]${limiter}`;
        } else {
            finalRegex += `.${limiter}`;
        }

        return finalRegex;
    }

    private atLeastOneOptionSelected(): ValidationErrors | null {
        return (group: UntypedFormGroup): ValidationErrors | null => {
            if (this.form.get('REGEX')?.get('active').value) {
                const options = Object.values(RulesEditorConfigRegex);
                const selectedOptions = options.filter(option => group.get(option).get('active').value);
                return selectedOptions.length === 0 ? { required: true } : null;
            }
            return null;
        };

    }
}
