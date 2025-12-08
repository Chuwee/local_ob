import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import {
    type PresaleDetailComponent, PresaleRedirectionPolicyComponent,
    PresalesListComponent
} from '@admin-clients/cpanel/shared/feature/presales';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, UnsavedChangesDialogResult } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, filter, forkJoin, Observable, of, switchMap } from 'rxjs';

@Component({
    selector: 'app-season-ticket-operative-presales',
    templateUrl: './season-ticket-operative-presales.component.html',
    styleUrls: ['./season-ticket-operative-presales.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, FormContainerComponent, ReactiveFormsModule, MatSpinner,
        MatIconModule, PresalesListComponent, PresaleRedirectionPolicyComponent
    ]
})
export class SeasonTicketOperativePresalesComponent implements OnDestroy {

    readonly $redirectionPolicy = viewChild(PresaleRedirectionPolicyComponent);

    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #presalesSrv = inject(PRESALES_SERVICE);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly externalInventoryProvider = this.$seasonTicket()?.additional_config?.inventory_provider;
    readonly $presaleDetailsElements = signal<PresaleDetailComponent[]>([]);
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketSrv.seasonTicketPresales.loading$()
    ]));

    openedId = null;

    constructor() {
        this.#seasonTicketSrv.seasonTicket.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(seasonTicket => {
            this.#presalesSrv.load();
            this.#entitiesSrv.entityCustomerTypes.load(seasonTicket.entity.id);
        });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
    }

    save(): void {
        this.$redirectionPolicy()?.save();
    }

    cancel(): void {
        this.$redirectionPolicy()?.cancel();
    }

    canDeactivate(): Observable<boolean> {
        const presaleFormDirty = this.$presaleDetailsElements()?.some(presale => presale.form.dirty);
        const redirectionPoliciFormDirty = this.$redirectionPolicy()?.form.dirty;

        if (presaleFormDirty || redirectionPoliciFormDirty) {
            return this.#msgDialogSrv.openRichUnsavedChangesWarn()
                .pipe(
                    switchMap(result => {
                        if (result === UnsavedChangesDialogResult.continue) {
                            return of(true);
                        } else if (result === UnsavedChangesDialogResult.save) {
                            const sessionPresalesRedirectionPolicy$ = redirectionPoliciFormDirty
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
}
