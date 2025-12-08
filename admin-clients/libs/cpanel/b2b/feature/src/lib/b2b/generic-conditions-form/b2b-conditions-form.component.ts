import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    B2BClientDiscountCurrency, B2bCondition, B2bConditions, BookingExpirationDaysMode, ClientDiscountMode,
    ConditionsToVmConditionsMap, ConditionType, PaymentMethodType, VmB2bConditions
} from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { CurrencyInputComponent, HelpButtonComponent, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import {
    ErrorMessage$Pipe,
    LocalCurrencyPartialTranslationPipe,
    LocalCurrencyPipe,
    LocalNumberPipe
} from '@admin-clients/shared/utility/pipes';
import { greaterThanValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

interface ConditionsForm {
    canBuy: FormControl<boolean>;
    canBook: FormControl<boolean>;
    canPublish: FormControl<boolean>;
    canInvite: FormControl<boolean>;
    paymentMethods: FormControl<PaymentMethodType[]>;
    maxSeats: FormControl<number>;
    bookingExpirationMode: FormControl<BookingExpirationDaysMode>;
    bookingExpirationDays: FormControl<number>;
    clientComission: FormControl<number>;
    clientDiscountMode: FormControl<ClientDiscountMode>;
    clientDiscountFixed: FormGroup<{
        currencies: FormArray<FormGroup<{ value: FormControl<number>; currency_code: FormControl<string> }>>;
    }>;
    clientDiscountPercent: FormControl<number>;
}

@Component({
    selector: 'app-b2b-conditions-form',
    templateUrl: './b2b-conditions-form.component.html',
    styleUrls: ['./b2b-conditions-form.component.scss'],
    imports: [
        CommonModule, ReactiveFormsModule, TranslatePipe, FlexLayoutModule, FormControlErrorsComponent,
        CurrencyInputComponent, PercentageInputComponent, LocalNumberPipe, LocalCurrencyPipe,
        LocalCurrencyPartialTranslationPipe, ErrorIconDirective, ErrorMessage$Pipe, HelpButtonComponent,
        MatFormField, MatSelect, MatOption, MatInput, MatLabel, MatError, MatRadioButton, MatRadioGroup, MatTableModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class B2bConditionsFormComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #$user = toSignal(inject(AuthenticationService).getLoggedUser$());

    readonly paymentMethodTypes = Object.values(PaymentMethodType);
    readonly bookingExpirationDaysModes = BookingExpirationDaysMode;
    readonly clientDiscountModes = ClientDiscountMode;
    readonly showCanPublish$ = this.#entitiesService.getEntity$()
        .pipe(
            map(entity => entity?.settings?.allow_B2B_publishing),
            tap(value => {
                if (value) {
                    this.form?.controls.conditions?.controls?.canPublish?.enable();
                } else {
                    this.form?.controls.conditions?.controls?.canPublish?.disable();
                }
            }));

    readonly showCanInvite$ = this.#entitiesService.getEntity$()
        .pipe(
            map(entity => entity?.settings?.allow_invitations),
            tap(value => {
                if (value) {
                    this.form?.controls.conditions?.controls?.canInvite?.enable();
                } else {
                    this.form?.controls.conditions?.controls?.canInvite?.disable();
                }
            }));

    readonly $dataSource = signal([{}]);

    @Input() form: FormGroup<{ conditions?: FormGroup<ConditionsForm> }>;
    @Input() getConditions$: () => Observable<B2bConditions>;
    @Input() currencies: string[] = [];

    formConditions = this.#fb.group({
        canBuy: [false, Validators.required],
        canBook: [false, Validators.required],
        canPublish: [false, Validators.required],
        canInvite: [false, Validators.required],
        paymentMethods: [[] as PaymentMethodType[], Validators.required],
        maxSeats: [null as number, [Validators.required, Validators.min(0)]],
        bookingExpirationMode: [null as BookingExpirationDaysMode, Validators.required],
        bookingExpirationDays: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(0)]],
        clientComission: [null as number, [Validators.required, Validators.min(0)]],
        clientDiscountMode: [null as ClientDiscountMode, Validators.required],
        clientDiscountFixed: this.#fb.group({
            currencies: this.#fb.array([this.#fb.group({ value: null as number, currency_code: '' })])
        }),
        clientDiscountPercent: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(0)]]
    });

    ngOnInit(): void {
        this.#initForm();
        this.#formChangeHandler();
        this.#refreshFormDataHandler();
    }

    getNormalizedConditions(): B2bCondition[] {
        const formValues = this.formConditions.value;

        const conditionsKeys = Object.keys(ConditionsToVmConditionsMap) as ConditionType[];
        if (formValues.bookingExpirationMode === BookingExpirationDaysMode.fromEvent) {
            formValues.bookingExpirationDays = 0;
        }
        return conditionsKeys
            .filter(cond => this.#filterCondition(cond))
            .map(cond => {
                const formValue = formValues[ConditionsToVmConditionsMap[cond]];

                if (ConditionsToVmConditionsMap[cond] === ConditionsToVmConditionsMap.CLIENT_DISCOUNT) {
                    return {
                        condition_type: cond,
                        value: formValue.currencies[0].value, //TODO(MULTICURRENCY): Delete when all migrated to multicurrency
                        currencies: formValue.currencies
                    } as B2bCondition;
                } else {
                    return {
                        condition_type: cond,
                        value: formValue
                    } as B2bCondition;
                }
            });
    }

    #filterCondition(cond: ConditionType): boolean {
        const isDiscountPercentage = this.formConditions.value.clientDiscountMode === ClientDiscountMode.percent;
        const discountTypeIsCorrect = isDiscountPercentage ? cond !== 'CLIENT_DISCOUNT' : cond !== 'CLIENT_DISCOUNT_PERCENTAGE';
        const filterPublishIfDisabled = cond !== 'CAN_PUBLISH' || this.formConditions.controls.canPublish.enabled;
        const filterInviteIfDIsabled = cond !== 'CAN_INVITE' || this.formConditions.controls.canInvite.enabled;
        return discountTypeIsCorrect && filterPublishIfDisabled && filterInviteIfDIsabled;
    }

    #initForm(): void {
        this.form.setControl('conditions', this.formConditions);
        this.#initConditionCurrencies();
    }

    #initConditionCurrencies(): void {
        this.formConditions.controls.clientDiscountFixed.controls.currencies.clear({ emitEvent: false });
        this.currencies.forEach(currency => {
            const fixedValueCtrl = this.#fb.group({
                value: [null as number, [Validators.required, greaterThanValidator(0)]],
                currency_code: currency
            });
            this.formConditions.controls.clientDiscountFixed.controls.currencies.push(fixedValueCtrl, { emitEvent: false });
        });
    }

    #formChangeHandler(): void {
        this.formConditions.get('bookingExpirationMode').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((mode: BookingExpirationDaysMode) => {
                const bookingExpirationDaysCtrl = this.formConditions.get('bookingExpirationDays');
                if (mode === BookingExpirationDaysMode.fromEvent) {
                    bookingExpirationDaysCtrl.disable();
                } else {
                    bookingExpirationDaysCtrl.enable();
                }
            });

        this.formConditions.get('clientDiscountMode').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((mode: ClientDiscountMode) => {
                const clientDiscountFixedCtrl = this.formConditions.get('clientDiscountFixed');
                const clientDiscountPercentCtrl = this.formConditions.get('clientDiscountPercent');
                if (mode === ClientDiscountMode.percent) {
                    clientDiscountFixedCtrl.disable();
                    clientDiscountPercentCtrl.enable();
                } else {
                    clientDiscountFixedCtrl.enable();
                    clientDiscountPercentCtrl.disable();
                }
            });
    }

    #refreshFormDataHandler(): void {
        this.getConditions$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(b2bCond => {
                if (b2bCond) {
                    let result = b2bCond.conditions.reduce<Partial<VmB2bConditions>>((accum, cond) => {

                        const vmProperty = ConditionsToVmConditionsMap[cond.condition_type];
                        if (cond.condition_type === 'CLIENT_DISCOUNT') {
                            if (this.currencies.length > 1) {
                                let condCurrencies: B2BClientDiscountCurrency[];
                                if (cond.currencies?.length) {
                                    condCurrencies = this.currencies.map(currency => {
                                        const condCurrency = cond.currencies.find(condCurrency => condCurrency.currency_code === currency);

                                        if (condCurrency) {
                                            return {
                                                value: condCurrency.value,
                                                currency_code: condCurrency.currency_code
                                            };
                                        } else {
                                            return {
                                                value: null as number,
                                                currency_code: currency
                                            };
                                        }
                                    });
                                } else {
                                    condCurrencies = this.currencies.map(currency => {
                                        if (
                                            (currency === this.#$user().operator.currencies?.default_currency) ||
                                            (currency === this.#$user().currency)
                                        ) {
                                            return {
                                                value: cond.value,
                                                currency_code: currency
                                            };
                                        } else {
                                            return {
                                                value: null as number,
                                                currency_code: currency
                                            };
                                        }
                                    });
                                }

                                return {
                                    ...accum,
                                    ...(vmProperty ? {
                                        [vmProperty]: {
                                            currencies: condCurrencies
                                        } as VmB2bConditions['clientDiscountFixed']
                                    } : {})
                                };
                            } else {
                                //TODO(MULTICURRENCY): Delete when all migrated to multicurrency
                                const condCurrencies: B2BClientDiscountCurrency[] = cond.currencies ??
                                    [{ value: cond.value, currency_code: this.currencies[0] }];
                                return {
                                    ...accum,
                                    ...(vmProperty ? {
                                        [vmProperty]: {
                                            currencies: condCurrencies
                                        } as VmB2bConditions['clientDiscountFixed']
                                    } : {})
                                };
                            }
                        } else {
                            return {
                                ...accum,
                                ...(vmProperty ? { [vmProperty]: cond.value } : {})
                            };
                        }
                    }, {});

                    result = {
                        ...result,
                        bookingExpirationMode: result.bookingExpirationDays > 0 ?
                            this.bookingExpirationDaysModes.customDays : this.bookingExpirationDaysModes.fromEvent,
                        clientDiscountMode: result.clientDiscountFixed != null ?
                            this.clientDiscountModes.fixed : this.clientDiscountModes.percent
                    };
                    this.formConditions.patchValue(result);
                } else {
                    this.formConditions.reset();
                    this.#initConditionCurrencies();
                }
                this.formConditions.markAsPristine();
            });
    }
}
