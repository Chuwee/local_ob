import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService, EntityLoyaltyPoints } from '@admin-clients/cpanel/organizations/entities/data-access';
import { PutEntity } from '@admin-clients/shared/common/data-access';
import {
    CurrencyInputComponent, DialogSize, EphemeralMessageService, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatFormField } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { combineLatest, firstValueFrom, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';

const MIN_POINT_EXCHANGE = 0.01;

@Component({
    selector: 'app-entity-register-users-loyalty-program',
    templateUrl: './entity-register-users-loyalty-program.component.html',
    styleUrl: './entity-register-users-loyalty-program.component.scss',
    imports: [
        AsyncPipe, FormContainerComponent, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        ReactiveFormsModule, TranslatePipe, LocalCurrencyPartialTranslationPipe, MatFormField, CurrencyInputComponent,
        MatProgressSpinner, MatButton, MatTooltip, MatIcon
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class EntityLoyaltyProgramComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #authSrv = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    readonly $lastLoyaltyPointsReset = signal(null);
    readonly $loading = toSignal(
        booleanOrMerge([
            this.#entitiesSrv.entityLoyaltyProgram.inProgress$(),
            this.#entitiesSrv.isEntityLoading$(),
            this.#entitiesSrv.entityLoyaltyProgram.resetInProgress$()
        ])
    );

    readonly $entityId = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.id)
    ));

    // TODO(MULTICURRENCY): check when the multicurrency functionality is finished
    readonly currencies$: Observable<Currency[]> = this.#authSrv.getLoggedUser$().pipe(
        first(user => user !== null),
        map(user => {
            const currencies = AuthenticationService.operatorCurrencies(user);
            return currencies || [user.operator.currency];
        }),
        shareReplay(1)
    );

    form = this.#fb.group({
        point_exchange: this.#fb.array([])
        // TODO: uncomment when expiration works in back
        /*expiration: this.#fb.group({
            enabled: [false],
            months: [{ value: null as number, disabled: true }]
        })*/
    });

    get pointExchange(): FormArray {
        return this.form.get('point_exchange') as FormArray;
    }

    $isLoyaltyEnabled = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_loyalty_points)
    ));

    $isLoyaltyButtonEnabled = toSignal(
        combineLatest([this.#entitiesSrv.getEntity$(), this.pointExchange.valueChanges]).pipe(
            filter(val => val.every(Boolean)),
            map(([entity, pointExchange]) => {
                const enableLoyaltyPoints = pointExchange.every(control => control.value !== null && control.value >= MIN_POINT_EXCHANGE);
                return enableLoyaltyPoints && !entity.settings?.allow_loyalty_points;
            })
        ));

    ngOnInit(): void {
        this.#entitiesSrv.entityLoyaltyProgram.load(this.$entityId());

        this.#entitiesSrv.entityLoyaltyProgram.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef),
                tap(loyaltyPoints => {
                    this.initializeForm(loyaltyPoints);
                    if (loyaltyPoints.last_reset) {
                        const lastReset = moment(loyaltyPoints.last_reset).format(DateTimeFormats.shortDateTime);
                        this.$lastLoyaltyPointsReset.set(lastReset);
                    }
                })
            ).subscribe();
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            return this.#entitiesSrv.entityLoyaltyProgram.update(this.$entityId(), this.form.value as EntityLoyaltyPoints)
                .pipe(
                    tap(() => {
                        this.#ephemeralMessageService.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
                        this.#entitiesSrv.entityLoyaltyProgram.load(this.$entityId());
                        this.form.markAsPristine();
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this.#entitiesSrv.entityLoyaltyProgram.load(this.$entityId());
        this.form.markAsPristine();
    }

    resetLoyaltyPoints(): void {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.LOYALTY_POINTS.RESET_MODAL_TITLE',
            message: 'ENTITY.LOYALTY_POINTS.RESET_MODAL_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.RESET_POINTS',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#entitiesSrv.entityLoyaltyProgram.reset(this.$entityId())
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.LOYALTY_POINTS.RESET_SUCCESS' });
                            this.#entitiesSrv.entityLoyaltyProgram.load(this.$entityId());
                        });
                }
            });
    }

    async enableLoyaltyPoints(): Promise<void> {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.LOYALTY_POINTS.ENABLE_DIALOG_TITLE',
            message: 'ENTITY.LOYALTY_POINTS.ENABLE_DIALOG_MESSAGE',
            actionLabel: 'ENTITY.LOYALTY_POINTS.ENABLE_DIALOG_BUTTON',
            showCancelButton: true
        })
            .subscribe(async success => {
                if (success) {
                    const entity = await firstValueFrom(this.#entitiesSrv.getEntity$());
                    const updatedEntity: PutEntity = {
                        ...entity,
                        settings: { allow_loyalty_points: true }
                    };

                    await firstValueFrom(this.#entitiesSrv.entityLoyaltyProgram
                        .update(this.$entityId(), this.form.value as EntityLoyaltyPoints));
                    await firstValueFrom(this.#entitiesSrv.updateEntity(entity.id, updatedEntity));

                    this.#entitiesSrv.loadEntity(entity.id);
                    this.#entitiesSrv.entityLoyaltyProgram.load(this.$entityId());

                    this.#ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.LOYALTY_POINTS.UPDATE_SUCCESS' });
                }
            });
    }

    private initializeForm(loyaltyPoints: EntityLoyaltyPoints): void {
        // TODO: uncomment when expiration works in back
        /*this.form.patchValue({
            expiration: {
                enabled: loyaltyPoints?.expiration?.enabled ?? false,
                months: loyaltyPoints?.expiration?.months ?? null
            }
        });

        this.updateExpirationControlStatus(this.form.value.expiration.enabled);

        this.form.controls.expiration.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.updateExpirationControlStatus.bind(this));

        if (this.$isLoyaltyEnabled()) {
            this.form.controls.expiration.disable({ emitEvent: false });
        } */

        this.configureCurrencies(loyaltyPoints.point_exchange);
    }

    // TODO: uncomment when expiration works in back
    /* private updateExpirationControlStatus(isEnabled: boolean): void {
        const valueCtrl = this.form.controls.expiration.controls.months;

        valueCtrl.setValidators(isEnabled ? [Validators.required, Validators.min(1), maxDecimalLength(0)] : []);
        if (isEnabled) {
            valueCtrl.enable({ emitEvent: false });
        } else {
            valueCtrl.disable({ emitEvent: false });
        }
        valueCtrl.updateValueAndValidity({ emitEvent: false });
    } */

    private configureCurrencies(pointExchange: { code: string; value: number }[]): void {
        const formArray = this.form.controls.point_exchange as FormArray;
        formArray.clear();

        pointExchange?.forEach(conversion => {
            const conversionGroup = this.#fb.group({
                code: [conversion.code],
                value: [conversion.value, [Validators.required, Validators.min(MIN_POINT_EXCHANGE)]]
            });
            formArray.push(conversionGroup);
        });
    }
}
