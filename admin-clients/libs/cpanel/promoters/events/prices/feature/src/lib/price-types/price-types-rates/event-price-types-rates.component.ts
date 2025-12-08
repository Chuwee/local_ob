import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService, Event, EventGroupPricePolicy } from '@admin-clients/cpanel/promoters/events/data-access';
import { VenueTemplatePriceTypesService } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { priceLimitsForWarning } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom, forkJoin, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import { EventPriceTypesMatrixComponent } from './price-types-matrix/event-price-types-matrix.component';

@Component({
    selector: 'app-event-price-types-rates',
    templateUrl: './event-price-types-rates.component.html',
    styleUrls: ['./event-price-types-rates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormContainerComponent, AsyncPipe, MatProgressSpinner, TranslatePipe,
        EventPriceTypesMatrixComponent, MatFormFieldModule, MatRadioButton, MatRadioGroup, MatCheckbox
    ]
})
export class EventPriceTypesRatesComponent {
    readonly #eventsSrv = inject(EventsService);
    readonly #venueTplSrv = inject(VenueTemplatesService);
    readonly #venueTplPriceTypesSrv = inject(VenueTemplatePriceTypesService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #localCurrencyPipe = inject(LocalCurrencyPipe);

    readonly #destroyRef = inject(DestroyRef);
    readonly #saveBtnClickedSbj = new Subject<void>();

    private readonly _pricesComponent = viewChild<EventPriceTypesMatrixComponent>('individualPrices');
    private readonly _groupPricesComponent = viewChild<EventPriceTypesMatrixComponent>('groupPrices');

    readonly event$ = this.#eventsSrv.event.get$();
    readonly isActivity$ = this.#eventsSrv.event.get$().pipe(
        filter(event => event !== null),
        map(event => event.type === EventType.activity || event.type === EventType.themePark)
    );

    readonly eventGroupPricePolicy = EventGroupPricePolicy;
    readonly saveBtnClicked$ = this.#saveBtnClickedSbj.asObservable();
    readonly groupPricesDetailsForm = this.getGroupPricesDetailsForm();
    readonly formPriceTypesRates = this.#fb.group({
        groupPricesDetailsForm: this.groupPricesDetailsForm
    });

    readonly isInProgress$ = booleanOrMerge([
        this.#eventsSrv.event.inProgress$(),
        this.#eventsSrv.eventPrices.inProgress$(),
        this.#venueTplSrv.isVenueTemplatesListLoading$(),
        this.#venueTplSrv.isVenueTemplatePriceTypesLoading$(),
        this.#venueTplSrv.isVenueTemplatePriceTypeSaving$(),
        this.#venueTplPriceTypesSrv.isVenueTemplatePriceTypeChannelContentLoading$(),
        this.#venueTplPriceTypesSrv.isPriceTypeChannelContentSaving$()
    ]);

    readonly #$currencyCode = toSignal(this.event$.pipe(first(Boolean), map(event => event.currency_code)));
    async cancel(): Promise<void> {
        const event = await firstValueFrom(this.event$);
        this._pricesComponent()?.cancel();
        this.#eventsSrv.event.load(String(event.id));
    }

    save$(): Observable<unknown[]> {
        this.#saveBtnClickedSbj.next();
        if (this.formPriceTypesRates.valid && this.formPriceTypesRates.dirty) {
            const obs$ = [];
            if (this._pricesComponent()) {
                obs$.push(...this._pricesComponent().savePrices());
            }
            if (this._groupPricesComponent()) {
                obs$.push(...this._groupPricesComponent().savePrices());
            }
            if (this.groupPricesDetailsForm?.dirty) {
                obs$.push(this.saveGroupPricesDetails().pipe(
                    tap(() => this.#eventsSrv.event.get$()
                        .pipe(first())
                        .subscribe(event => this.#eventsSrv.event.load(event.id.toString()))
                    )
                )
                );
            }
            return forkJoin(obs$)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    savePrices(): void {
        const pricesMatrix = this.formPriceTypesRates.value.priceTypesRatesMatrix.pricesMatrix;
        const numbersArray = Object.values(pricesMatrix).flat() as number[];
        const minimumPriceForThisCurrency = priceLimitsForWarning.find(price => price.currency === this.#$currencyCode())?.limit;
        const shouldShowWarning = numbersArray.some(price => price < minimumPriceForThisCurrency);
        const priceWithCurrencyCode = this.#localCurrencyPipe.transform(minimumPriceForThisCurrency, this.#$currencyCode());
        if (shouldShowWarning) {
            this.#messageDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'EVENTS.SOME_RATE_BELOW_LIMIT_WARNING_TITLE',
                message: 'EVENTS.SOME_RATE_BELOW_LIMIT_WARNING_DESCRIPTION',
                messageParams: { price: priceWithCurrencyCode },
                actionLabel: 'FORMS.ACTIONS.CONTINUE',
                showCancelButton: true
            })
                .pipe(filter(Boolean), switchMap(() => this.save$()))
                .subscribe(() => this.#ref.markForCheck());
        } else {
            this.save$().subscribe(() => this.#ref.markForCheck());
        }
    }

    private saveGroupPricesDetails(): Observable<boolean> {
        return this.#eventsSrv.event.get$()
            .pipe(
                first(),
                switchMap(event => {
                    const value = this.groupPricesDetailsForm.value;
                    return this.#eventsSrv.event.update(event.id, {
                        settings: {
                            groups: {
                                allowed: value.allowed,
                                price_policy: value.pricePolicy,
                                companions_payment: value.companionsPayment
                            }
                        }
                    });
                }),
                tap(() => this.groupPricesDetailsForm.markAsPristine()),
                map(() => true)
            );
    }

    private getGroupPricesDetailsForm(): UntypedFormGroup {
        const formGroup = this.#fb.group({});
        this.#eventsSrv.event.get$()
            .pipe(first())
            .subscribe(event => {
                if (event.type === EventType.activity || event.type === EventType.themePark) {
                    // form definition
                    formGroup.addControl('pricePolicy', new UntypedFormControl(null), { emitEvent: false });
                    formGroup.addControl('companionsPayment', new UntypedFormControl(null), { emitEvent: false });
                    // nested enabled fields behavior
                    formGroup.get('pricePolicy').valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(pricePolicy => this.updateCompanionsPaymentFieldEnabled(pricePolicy, formGroup));
                    // server data to form
                    this.#eventsSrv.event.get$()
                        .pipe(filter(value => !!value), takeUntilDestroyed(this.#destroyRef))
                        .subscribe(e => this.setGroupPricesDetailsFormData(e, formGroup));
                }
            });
        return formGroup;
    }

    private updateCompanionsPaymentFieldEnabled(pricePolicy: EventGroupPricePolicy, groupPricesDetailsForm: FormGroup): void {
        if (pricePolicy === EventGroupPricePolicy.fixed) {
            groupPricesDetailsForm.get('companionsPayment').disable();
        } else {
            groupPricesDetailsForm.get('companionsPayment').enable();
        }
    }

    private setGroupPricesDetailsFormData(event: Event, groupPricesDetailsForm: FormGroup): void {
        groupPricesDetailsForm.reset();
        groupPricesDetailsForm.setValue({
            pricePolicy: event.settings.groups?.price_policy || null,
            companionsPayment: event.settings.groups?.companions_payment || false
        });
    }

}
