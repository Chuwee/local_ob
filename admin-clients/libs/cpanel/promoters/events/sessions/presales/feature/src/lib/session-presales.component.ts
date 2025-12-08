import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import {
    type PresaleDetailComponent, PresaleRedirectionPolicyComponent,
    PresalesListComponent
} from '@admin-clients/cpanel/shared/feature/presales';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import {
    DateTimeModule, MessageDialogService, SearchablePaginatedSelectionModule, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, signal, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-session-presales',
    templateUrl: './session-presales.component.html',
    styleUrls: ['./session-presales.component.scss'],
    imports: [
        TranslatePipe, MaterialModule, FormsModule, ReactiveFormsModule, SearchablePaginatedSelectionModule,
        DateTimeModule, PresalesListComponent, FormContainerComponent, PresaleRedirectionPolicyComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionPresalesComponent implements OnInit, OnDestroy {
    readonly #sessionsService = inject(EventSessionsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #eventsSrv = inject(EventsService);
    readonly #presalesSrv = inject(PRESALES_SERVICE);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);

    #entityId: number;

    readonly $redirectionPolicy = viewChild(PresaleRedirectionPolicyComponent);

    openedId = null;

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#eventsSrv.ratesRestrictions.inProgress$(),
        this.#eventsSrv.eventRates.inProgress$(),
        this.#sessionsService.presales.loading$()
    ]));

    readonly $presaleDetailsElements = signal<PresaleDetailComponent[]>([]);
    readonly $event = toSignal(this.#eventsSrv.event.get$().pipe(filter(Boolean)));
    readonly isAvetEvent = this.$event()?.type === EventType.avet;
    readonly externalInventoryProvider = this.$event()?.additional_config?.inventory_provider;

    readonly $isSmartBooking = toSignal(combineLatest([
        this.#eventsSrv.event.get$(),
        this.#venueTemplatesSrv.venueTpl.get$()
    ]).pipe(
        filter(resp => resp.every(Boolean)),
        map(([event, venuetpl]) => (venuetpl.type === VenueTemplateType.activity && event.type === EventType.avet))
    ));

    ngOnInit(): void {
        this.#sessionsService.session.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(session => {
            this.#entityId = session.entity.id;
            this.#presalesSrv.load();
            this.#loadEntityCustomTypes(this.#entityId);
            this.#venueTemplatesSrv.venueTpl.load(session.venue_template.id);
        });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
    }

    canDeactivate(): Observable<boolean> {
        const presaleFormDirty = this.$presaleDetailsElements()?.some(presale => presale.form.dirty);
        const redirectionPolicyFormDirty = this.$redirectionPolicy()?.form.dirty;

        if (presaleFormDirty || redirectionPolicyFormDirty) {
            return this.#msgDialogSrv.openRichUnsavedChangesWarn()
                .pipe(
                    switchMap(result => {
                        if (result === UnsavedChangesDialogResult.continue) {
                            return of(true);
                        } else if (result === UnsavedChangesDialogResult.save) {
                            const sessionPresalesRedirectionPolicy$ = redirectionPolicyFormDirty
                                ? this.$redirectionPolicy().save$()
                                : of(null);

                            const presalesToSave = this.$presaleDetailsElements()
                                ?.filter(presale => presale.form.dirty)
                                .map(presale => presale.save$());

                            return forkJoin([
                                ...presalesToSave,
                                sessionPresalesRedirectionPolicy$
                            ]).pipe(
                                switchMap(() => of(true)),
                                catchError(() => of(false))
                            );
                        }
                        return of(false);
                    })
                );
        }
        return of(true);
    }

    cancel(): void {
        this.$redirectionPolicy()?.cancel();
    }

    save(): void {
        this.$redirectionPolicy()?.save();
    }

    #loadEntityCustomTypes(entityId: number): void {
        this.#entitiesSrv.entityCustomerTypes.load(entityId);
    }
}
