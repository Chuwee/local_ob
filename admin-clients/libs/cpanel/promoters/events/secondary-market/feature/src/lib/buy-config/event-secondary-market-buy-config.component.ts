import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SecondaryMarketConfig, SecondaryMarketService } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'ob-event-secondary-market-buy-config',
    imports: [TranslatePipe, FormContainerComponent, MatExpansionModule, ReactiveFormsModule, MatSpinner,
        MatFormFieldModule, MatCheckboxModule, MatInputModule, MatSelectModule, FormControlErrorsComponent
    ],
    templateUrl: './event-secondary-market-buy-config.component.html',
    styleUrl: './event-secondary-market-buy-config.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSecondaryMarketBuyConfigComponent implements OnDestroy {

    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #eventSrv = inject(EventsService);
    readonly #secondaryMarketSrv = inject(SecondaryMarketService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    #eventId: number;
    #prevSecondaryMarketConfig: SecondaryMarketConfig;

    readonly $customerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());
    readonly $loading = toSignal(booleanOrMerge([
        this.#secondaryMarketSrv.eventConfiguration.loading$(),
        this.#entitiesSrv.entityCustomerTypes.inProgress$()
    ]));

    readonly form = this.#fb.group({
        customer_limits_enabled: [false],
        customer_limits: this.#fb.group({
            limit: [null as number, [Validators.required, Validators.min(0)]],
            excluded_customer_types: [[] as string[]]
        })
    });

    constructor() {
        this.#handleCustomerTypeLimitEnabledChange();
        this.#handleSecondaryMarketConfigLoaded();
        this.#loadSecondaryMarketEventConfig();
        this.#loadCustomerTypes();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
    }

    cancel(): void {
        this.#loadSecondaryMarketEventConfig();
    }

    save(): void {
        this.save$().pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.markAsPristine();
            this.#loadSecondaryMarketEventConfig();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#secondaryMarketSrv.eventConfiguration.save(this.#eventId, {
                ...this.#prevSecondaryMarketConfig,
                ...this.form.value
            } as SecondaryMarketConfig).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'Form is invalid');
        }
    }

    goToSurcharges(): void {
        this.#router.navigate(['../../prices/surcharges'], { relativeTo: this.#route, queryParams: { from: 'secondary-market' } });
    }

    #loadSecondaryMarketEventConfig(): void {
        this.#eventSrv.event.get$().pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef)).subscribe(event => {
            this.#eventId = event.id;
            this.#secondaryMarketSrv.eventConfiguration.load(event.id);
        });
    }

    #loadCustomerTypes(): void {
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef)).subscribe(entity => {
            this.#entitiesSrv.entityCustomerTypes.load(entity.id);
        });
    }

    #handleCustomerTypeLimitEnabledChange(): void {
        this.form.controls.customer_limits_enabled.valueChanges.subscribe(enabled => {
            if (enabled) {
                this.form.get('customer_limits.limit').enable();
                this.form.get('customer_limits.excluded_customer_types').enable();
            } else {
                this.form.get('customer_limits.limit').disable();
                this.form.get('customer_limits.excluded_customer_types').disable();
            }
        });
    }

    #handleSecondaryMarketConfigLoaded(): void {
        this.#secondaryMarketSrv.eventConfiguration.get$().pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef)).subscribe(config => {
            this.#prevSecondaryMarketConfig = config;
            this.form.patchValue({ ...config });
        });
    }
}
