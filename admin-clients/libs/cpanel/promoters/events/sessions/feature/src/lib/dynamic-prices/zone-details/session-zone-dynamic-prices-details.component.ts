import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    DialogSize, EmptyStateTinyComponent, EphemeralMessageService, ObDialog, openDialog, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalCurrencyPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, cloneObject } from '@admin-clients/shared/utility/utils';
import {
    ChangeDetectionStrategy, Component, computed, inject, OnDestroy, signal, ViewContainerRef
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog, MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SessionZoneDynamicPricesDialogComponent } from './dialog/session-zone-dynamic-prices-dialog.component';
import { SessionZoneDynamicPriceTranslationPipe } from './session-zone-dynamic-price-translation.pipe';
import { VmSessionZoneDynamicPrice } from './vm-session-zone-dynamic-prices.model';

@Component({
    selector: 'app-session-zone-dynamic-prices-details',
    templateUrl: './session-zone-dynamic-prices-details.component.html',
    styleUrls: ['./session-zone-dynamic-prices-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatIcon, MatButton, MatExpansionModule, MatTableModule, MatProgressSpinner, ReactiveFormsModule,
        EmptyStateTinyComponent, LocalCurrencyPipe, MatIconButton, MatMenu, MatMenuItem, MatMenuTrigger, MatDialogContent,
        MatDialogTitle, MatDialogActions, LocalDateTimePipe, TabsMenuComponent, TabDirective, SessionZoneDynamicPriceTranslationPipe
    ]
})
export class SessionZoneDynamicPricesDetailsComponent extends ObDialog<SessionZoneDynamicPricesDetailsComponent,
    { zoneId: number; zoneName: string }, unknown> implements OnDestroy {
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #dialogSrv = inject(MatDialog);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #$vmZoneDynamicPrices = signal<VmSessionZoneDynamicPrice[]>([]);
    readonly #$event = toSignal(this.#eventsSrv.event.get$());

    readonly $languages = computed(() => this.#$event()?.settings.languages.selected);
    readonly $defaultLanguageIndex = computed(() =>
        this.#$event()?.settings.languages.selected.findIndex(language => language === this.#$event()?.settings.languages.default));

    readonly $session = toSignal(this.#eventSessionsSrv.session.get$());
    readonly $dirty = signal(false);
    readonly $zoneDynamicPrices = toSignal(this.#eventSessionsSrv.zoneDynamicPrices.get$()
        .pipe(tap(() => this.$dirty.set(false))));

    readonly dateTimeFormats = DateTimeFormats;
    readonly $currency = computed(() => this.#$event().currency_code);
    readonly $vmZoneDynamicPrices = computed<VmSessionZoneDynamicPrice[]>(() => {
        if (this.$dirty()) {
            return this.#$vmZoneDynamicPrices();
        } else if (this.$zoneDynamicPrices()?.dynamic_prices?.length) {
            return cloneObject(this.$zoneDynamicPrices().dynamic_prices);
        } else {
            return [];
        }
    });

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#eventSessionsSrv.zoneDynamicPrices.loading$(),
        this.#eventSessionsSrv.isSessionSaving$()
    ]));

    readonly displayedColumns = ['name', 'price'];

    constructor() {
        super(DialogSize.LATERAL, true);
        this.#eventSessionsSrv.zoneDynamicPrices.load(this.$session().event.id, this.$session().id,
            this.data.zoneId);
    }

    ngOnDestroy(): void {
        this.#eventSessionsSrv.zoneDynamicPrices.clear();
    }

    cancelChanges(): void {
        this.$dirty.set(false);
        this.#eventSessionsSrv.zoneDynamicPrices.load(this.$session().event.id, this.$session().id,
            this.data.zoneId);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#eventSessionsSrv.zoneDynamicPrices.load(this.$session().event.id, this.$session().id,
                this.data.zoneId);
            this.#eventSessionsSrv.dynamicPrices.load(this.$session().event.id, this.$session().id);
        });
    }

    save$(): Observable<unknown> {
        return this.#eventSessionsSrv.zoneDynamicPrices.post(
            this.$session().event.id, this.$session().id, this.data.zoneId, this.#$vmZoneDynamicPrices()
        ).pipe(tap(() => this.#ephemeralMessageSrv.showSuccess(
            { msgKey: 'EVENTS.SESSION.DYNAMIC_PRICES.FORMS.FEEDBACK.ZONE_DYNAMIC_PRICES_MODIFIED' })));
    }

    newZoneDynamicPrice(): void {
        openDialog(this.#dialogSrv, SessionZoneDynamicPricesDialogComponent,
            {
                vmZoneDynamicPrices: this.$vmZoneDynamicPrices(), activeEditable: this.$zoneDynamicPrices().editable,
                capacity: this.$zoneDynamicPrices()?.capacity
            },
            this.#viewContainerRef
        ).beforeClosed()
            .subscribe(vmSessionZoneDynamicPrice => {
                if (vmSessionZoneDynamicPrice) {
                    vmSessionZoneDynamicPrice.order = this.$vmZoneDynamicPrices().length;
                    this.#$vmZoneDynamicPrices.set(this.$vmZoneDynamicPrices().concat(vmSessionZoneDynamicPrice));
                    this.$dirty.set(true);
                }
            });
    }

    deleteZoneDynamicPrice(vmZoneDynamicPriceToDelete: VmSessionZoneDynamicPrice): void {
        let deletedIndex: number;
        const vmZoneDynamicPrices = this.$vmZoneDynamicPrices()
            .filter((vmZoneDynamicPrice, index) => {
                if (vmZoneDynamicPrice.order === vmZoneDynamicPriceToDelete.order) {
                    deletedIndex = index;
                    return false;
                }
                return true;
            })
            .map((dynamicPrice, index) => {
                if (index >= deletedIndex) {
                    return {
                        ...dynamicPrice,
                        order: dynamicPrice.order - 1
                    };
                }
                return dynamicPrice;
            });
        this.#$vmZoneDynamicPrices.set(vmZoneDynamicPrices);
        this.$dirty.set(true);
    }

    editZoneDynamicPrice(vmZoneDynamicPriceToEdit: VmSessionZoneDynamicPrice): void {
        openDialog(this.#dialogSrv, SessionZoneDynamicPricesDialogComponent,
            {
                vmZoneDynamicPrices: this.$vmZoneDynamicPrices(), vmZoneDynamicPriceToEdit,
                activeEditable: this.$zoneDynamicPrices().editable && vmZoneDynamicPriceToEdit.status_dynamic_price === 'PENDING',
                capacity: this.$zoneDynamicPrices().capacity
            },
            this.#viewContainerRef)
            .beforeClosed()
            .subscribe(vmSessionZoneDynamicPrice => {
                if (vmSessionZoneDynamicPrice) {
                    const vmDynamicPrices = this.$vmZoneDynamicPrices().map(dynamicPrice => {
                        if (dynamicPrice.order === vmSessionZoneDynamicPrice.order) {
                            return vmSessionZoneDynamicPrice;
                        }
                        return dynamicPrice;
                    });
                    this.#$vmZoneDynamicPrices.set(vmDynamicPrices);
                    this.$dirty.set(true);
                }
            });
    }
}
