import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import {
    OperatorsService, PutOperator, PutOperatorCurrencies, WalletAssociation
} from '@admin-clients/cpanel-configurations-operators-data-access';
import {
    CurrenciesService, LanguagesService, TimezonesService, GatewaysService, Gateway
} from '@admin-clients/shared/common/data-access';
import {
    Chip, DialogSize, EphemeralMessageService, MessageDialogService, SelectSearchComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, OnDestroy, QueryList, ViewChildren, inject, OnInit, DestroyRef, signal
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import {
    MatError, MatFormField, MatLabel
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import {
    first, Observable, throwError, firstValueFrom, switchMap, filter, BehaviorSubject, tap, forkJoin, of, map, startWith,
    combineLatest, skip, take
} from 'rxjs';
import { catchError } from 'rxjs/operators';
import { OperatorMultiCurrencyComponent } from './multicurrency/operator-multi-currency.component';

@Component({
    selector: 'app-operator-general-data',
    templateUrl: './operator-general-data.component.html',
    styleUrls: ['./operator-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, FormContainerComponent, TranslatePipe, SelectSearchComponent,
        ReactiveFormsModule, FormControlErrorsComponent, OperatorMultiCurrencyComponent, MatExpansionModule,
        MatInput, MatLabel, MatError, MatSelect, MatOption, MatDivider, MatIconButton, MatCheckbox, MatProgressSpinner,
        MatFormField, ObFormFieldLabelDirective, MatIcon, AsyncPipe, UpperCasePipe
    ]
})
export class OperatorGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #languagesSrv = inject(LanguagesService);
    readonly #timezonesSrv = inject(TimezonesService);
    readonly #currenciesSrv = inject(CurrenciesService);
    readonly #gatewaysSrv = inject(GatewaysService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #env = inject(ENVIRONMENT_TOKEN);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.group({
        generalData: this.#fb.group({
            name: this.#fb.control('', Validators.required),
            language: '',
            timezone: '',
            currency: ''
        }),
        configuration: this.#fb.group({
            gateways: new FormControl([]) as FormControl<string[]>,
            wallets: this.#fb.array<
                FormGroup<{ wallet: FormControl<string>; gateways: FormControl<string[]> }>
            >([], this.walletsValidator())
        }),
        allowFeverZone: false,
        allowGatewayBenefits: false
    });

    readonly gatewaysForm = this.form.controls.configuration.controls.gateways;

    readonly putCurrenciesBS = new BehaviorSubject<PutOperatorCurrencies>(null);

    readonly #availableGatewaysForWallets = new BehaviorSubject<Gateway[]>([]);
    readonly availableGatewaysForWallets$ = this.#availableGatewaysForWallets.asObservable();

    readonly operator$ = this.#operatorsSrv.operator.get$().pipe(filter(Boolean));
    readonly isMultiCurrency$ = this.#operatorsSrv.operator.isMultiCurrency$();
    readonly languages$ = this.#languagesSrv.getLanguages$().pipe(first(Boolean));
    readonly timezones$ = this.#timezonesSrv.timezones.get$().pipe(first(Boolean));
    readonly currencies$ = this.#currenciesSrv.currencies.get$().pipe(first(Boolean));
    readonly gateways$ = this.#gatewaysSrv.gatewaysList.get$().pipe(first(Boolean), map(gateways => gateways.filter(g => !g.wallet)));
    readonly walletGateways$ = this.#gatewaysSrv.gatewaysList.get$().pipe(first(Boolean), map(gateways => gateways.filter(g => g.wallet)));
    readonly reqInProgress$ = booleanOrMerge([
        this.#operatorsSrv.operator.loading$(),
        this.#languagesSrv.isLanguagesInProgress$(),
        this.#timezonesSrv.timezones.loading$(),
        this.#currenciesSrv.currencies.loading$(),
        this.#gatewaysSrv.gatewaysList.loading$()
    ]);

    readonly $walletsConfigured = toSignal(
        this.form.controls.configuration.controls.wallets.valueChanges.pipe(
            startWith(this.form.controls.configuration.controls.wallets.value),
            map(walletGroups =>
                walletGroups.map((value: { wallet?: string; gateways?: string[] }) => value.wallet)
            )
        )
    );

    readonly $walletsChanged = signal<boolean>(false);
    readonly $gatewaysUsedInWallets = signal<Set<string>>(null);

    #initialFormValue = this.form.getRawValue();

    ngOnInit(): void {
        this.#languagesSrv.loadLanguages(true);
        this.#timezonesSrv.timezones.load();
        this.#currenciesSrv.currencies.load();
        this.#gatewaysSrv.gatewaysList.load();

        this.form.controls.configuration.controls.wallets.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.controls.configuration.controls.wallets.controls.forEach(walletGroup => {
                if (walletGroup.controls.gateways.disabled && walletGroup.value.wallet) {
                    walletGroup.controls.gateways.enable();
                }
            });
        });

        combineLatest([
            this.gateways$,
            this.form.controls.configuration.controls.gateways.valueChanges
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([gateways, selectedGateways]) => {
                this.#availableGatewaysForWallets.next(gateways?.filter(g => selectedGateways.includes(g.sid)));
            });

        this.#operatorsSrv.operator.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(operator => {
                this.#initialFormValue = {
                    generalData: {
                        name: operator.name,
                        language: operator.language.code,
                        timezone: operator.timezone.code,
                        currency: operator.currency.code
                    },
                    configuration: {
                        gateways: operator.gateways,
                        wallets: []
                    },
                    allowFeverZone: operator.allow_fever_zone,
                    allowGatewayBenefits: operator.allow_gateway_benefits
                };

                this.form.reset({
                    generalData: this.#initialFormValue.generalData,
                    configuration: {
                        gateways: this.#initialFormValue.configuration.gateways
                    },
                    allowFeverZone: this.#initialFormValue.allowFeverZone,
                    allowGatewayBenefits: this.#initialFormValue.allowGatewayBenefits
                });

                const walletsArray = this.form.controls.configuration.controls.wallets;
                walletsArray.clear();
                operator?.wallets?.forEach((walletAssociation: WalletAssociation) => {
                    this.#initialFormValue.configuration.wallets.push({
                        wallet: walletAssociation.wallet,
                        gateways: walletAssociation.gateways
                    });
                    walletsArray.push(
                        this.#fb.group({
                            wallet: [walletAssociation.wallet, Validators.required],
                            gateways: [walletAssociation.gateways, Validators.required]
                        })
                    );
                });

                this.$gatewaysUsedInWallets.set(
                    new Set(this.form.controls.configuration.controls.wallets.value.flatMap(wallet => wallet.gateways))
                );

                // Deshabilitar currency si es necesario
                if (operator.currencies) {
                    this.form.controls.generalData.controls.currency.disable({ emitEvent: false });
                }
            });

        this.form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(form => {
            this.$walletsChanged.set(JSON.stringify(form.configuration.wallets.map(w => w.gateways?.sort()))
                !== JSON.stringify(this.#initialFormValue.configuration.wallets.map(w => w.gateways?.sort())));
            this.$gatewaysUsedInWallets.set(
                new Set(form.configuration.wallets.flatMap(wallet => wallet.gateways))
            );
        });
    }

    canDeactivate(): Observable<boolean> {
        return of(this.form.dirty || this.$walletsChanged())
            .pipe(
                switchMap(isDirty => {
                    if (!isDirty) {
                        return of(true);
                    } else {
                        return this.#messageDialogSrv.openRichUnsavedChangesWarn().pipe(
                            switchMap(res => {
                                if (res === UnsavedChangesDialogResult.cancel) {
                                    return of(false);
                                } else if (res === UnsavedChangesDialogResult.continue) {
                                    return of(true);
                                } else {
                                    return this.save$().pipe(
                                        switchMap(() => of(true)),
                                        catchError(() => of(false))
                                    );
                                }
                            }));
                    }
                }),
                take(1)
            );
    }

    ngOnDestroy(): void {
        this.#languagesSrv.clearLanguages();
    }

    async cancel(): Promise<void> {
        const operator = await firstValueFrom(this.operator$);
        this.#operatorsSrv.operator.load(operator.id);
    }

    save(): void {
        this.save$().subscribe();
    }

    removeGateway(chip: Chip): void {
        const gateway = chip.key;
        this.gatewaysForm.setValue(this.gatewaysForm.value.filter(elem => elem !== gateway));
        this.gatewaysForm.markAsTouched();
        this.gatewaysForm.markAsDirty();
    }

    removeWallet(walletGroup: FormGroup<{ wallet: FormControl<string>; gateways: FormControl<string[]> }>): void {
        const walletsArray = this.form.controls.configuration.controls.wallets;
        const index = walletsArray.controls.indexOf(walletGroup);
        if (index !== -1) {
            walletsArray.removeAt(index);
        }
    }

    addNewWalletAssociation(): void {
        const walletsArray = this.form.controls.configuration.controls.wallets;
        const newWalletGroup = this.#fb.group({
            wallet: this.#fb.control('', Validators.required),
            gateways: this.#fb.control([], Validators.required)
        });
        newWalletGroup.controls.gateways.disable();
        walletsArray.push(newWalletGroup);
    }

    onGatewaySelectionChange(gatewaySid: string): void {
        const deselected = !this.form.value.configuration.gateways.includes(gatewaySid);

        if (deselected && this.$gatewaysUsedInWallets().has(gatewaySid)) {
            this.#messageDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'OPERATION.FORMS.TITLES.DISABLE_GATEWAY',
                message: 'OPERATION.FORMS.INFOS.DISABLE_GATEWAY',
                actionLabel: 'OPERATOR.FORMS.ACTIONS.DISABLE_GATEWAY',
                showCancelButton: true
            })
                .subscribe(confirmed => {
                    if (confirmed) {
                        this.form.controls.configuration.controls.wallets.controls.forEach(walletGroup => {
                            const updatedGateways = walletGroup.controls.gateways.value.filter(
                                gateway => gateway !== gatewaySid
                            );
                            walletGroup.controls.gateways.setValue(updatedGateways);
                            walletGroup.controls.gateways.markAsTouched();
                        });
                    } else {
                        const currentSelection = this.form.controls.configuration.controls.gateways.value;
                        this.form.controls.configuration.controls.gateways.setValue([...currentSelection, gatewaySid]);
                    }
                });
        }
    }

    save$(): Observable<unknown[]> {
        if (this.form.valid) {
            const requests: Observable<unknown>[] = [];
            if (this.putCurrenciesBS.value) {
                requests.push(this.#operatorsSrv.operator.get$()
                    .pipe(
                        first(),
                        switchMap(operator => this.#operatorsSrv.operatorCurrencies.update(operator.id, this.putCurrenciesBS.value))
                    ));
            }

            const putOperator: PutOperator = {};
            const { name, language, timezone, currency } = this.form.controls.generalData.controls;
            const { gateways, wallets } = this.form.controls.configuration.controls;
            const allowFeverZone = this.form.controls.allowFeverZone;
            const allowGatewayBenefits = this.form.controls.allowGatewayBenefits;

            if (name.dirty) {
                putOperator.name = name.value;
            }

            //TODO: when all operators are multiCurrency, delete currency control
            if (currency.dirty) {
                putOperator.currency_code = currency.value;
            }

            if (timezone.dirty) {
                putOperator.olson_id = timezone.value;
            }

            if (language.dirty) {
                putOperator.language_code = language.value;
            }

            if (gateways.dirty) {
                putOperator.gateways = gateways.value;
            }

            if (allowFeverZone.dirty) {
                putOperator.allow_fever_zone = allowFeverZone.value;
            }

            if (this.$walletsChanged()) {
                putOperator.wallets = wallets.value as WalletAssociation[];
            }

            if (allowGatewayBenefits.dirty) {
                putOperator.allow_gateway_benefits = allowGatewayBenefits.value;
            }

            requests.push(
                this.#operatorsSrv.operator.get$()
                    .pipe(
                        first(),
                        switchMap(operator =>
                            this.#operatorsSrv.operator.update(operator.id, putOperator)
                                .pipe(switchMap(() => {
                                    this.#operatorsSrv.operator.load(operator.id);
                                    return this.#operatorsSrv.operator.get$()
                                        .pipe(skip(1), take(1));
                                }))
                        )
                    )
            );

            if (this.putCurrenciesBS.value) {
                return this.#msgDialogSrv.showWarn({
                    size: DialogSize.MEDIUM,
                    title: 'OPERATOR.MULTICURENCY.ADD_WARN_TITLE',
                    message: 'OPERATOR.MULTICURENCY.ADD_WARN_MESSAGE',
                    actionLabel: 'FORMS.OPTIONS.ENABLE',
                    showCancelButton: true
                })
                    .pipe(
                        switchMap(success => {
                            if (success) {
                                return forkJoin(requests).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
                            } else {
                                return of(null);
                            }
                        }));
            } else {
                return forkJoin(requests).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
            }
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    walletsValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const walletsArray = control as FormArray<FormGroup<{ wallet: FormControl<string>; gateways: FormControl<string[]> }>>;
            return (walletsArray.length > 0 && walletsArray.controls.some(group => (
                group.controls.wallet.invalid || group.controls.gateways.invalid
            ))) ? { invalidWalletGroup: true } : null;
        };
    }
}
