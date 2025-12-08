import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, GetSessionZoneDynamicPricesResponse
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    DialogSize, EphemeralMessageService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map, Observable, skip, switchMap } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { SessionDynamicPricesRangePipe } from './session-dynamic-prices-range.pipe';
import { SessionZoneDynamicPricesDetailsComponent } from './zone-details/session-zone-dynamic-prices-details.component';

@Component({
    selector: 'app-session-dynamic-prices',
    templateUrl: './session-dynamic-prices.component.html',
    styleUrls: ['./session-dynamic-prices.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, FormContainerComponent, MatProgressSpinner, RouterLink, LocalDateTimePipe,
        MatRadioGroup, MatRadioButton, MatIcon, MatTooltip, MatTableModule, SessionDynamicPricesRangePipe
    ]
})
export class SessionDynamicPricesComponent {
    readonly #dialogSrv = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly dateTimeFormats = DateTimeFormats;
    readonly columns = ['price_zone', 'tier_name', 'condition_type', 'tier_end', 'prices'];
    readonly dataSource$ = toSignal(
        this.#eventSessionsSrv.dynamicPrices.get$()
            .pipe(
                map(response => response?.dynamic_price_zones
                    .map(zone => ({
                        ...zone,
                        dynamic_prices: zone.dynamic_prices.filter(tier => tier.status_dynamic_price === 'ACTIVE')
                    }))
                )
            )
    );

    readonly $currency = toSignal(this.#eventsSrv.event.get$().pipe(map(event => event.currency_code)));
    readonly $session = toSignal(this.#eventSessionsSrv.session.get$());
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#eventSessionsSrv.session.loading$(),
        this.#eventSessionsSrv.dynamicPrices.loading$(),
        this.#eventSessionsSrv.isSessionSaving$()
    ]));

    readonly form = this.#fb.group({
        isEnabled: [false, Validators.required]
    });

    constructor() {
        this.#eventSessionsSrv.session.get$()
            .pipe(takeUntilDestroyed())
            .subscribe(session => {
                this.form.controls.isEnabled.reset(session.settings.use_dynamic_prices ?? false, { emitEvent: false });
                this.#eventSessionsSrv.dynamicPrices.load(session.event.id, session.id);
            });
    }

    cancelChanges(): void {
        this.form.markAsUntouched();
        this.form.markAsPristine();
        this.#eventSessionsSrv.session.load(this.$session().event.id, this.$session().id);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        return this.#eventSessionsSrv.dynamicPrices.update(
            this.$session().event.id,
            this.$session().id,
            {
                status: this.form.controls.isEnabled.value
            }
        ).pipe(
            switchMap(() => {
                this.#eventSessionsSrv.session.load(this.$session().event.id, this.$session().id);
                return this.#eventSessionsSrv.session.get$()
                    .pipe(skip(1), take(1));
            }),
            tap(() => this.#ephemeralSrv.showSaveSuccess())
        );
    }

    openSidebarWithZone(row: GetSessionZoneDynamicPricesResponse): void {
        openDialog(this.#dialogSrv, SessionZoneDynamicPricesDetailsComponent,
            { zoneId: row.id_price_zone, zoneName: row.price_zone_name },
            this.#viewContainerRef, DialogSize.LATERAL);
    }
}
