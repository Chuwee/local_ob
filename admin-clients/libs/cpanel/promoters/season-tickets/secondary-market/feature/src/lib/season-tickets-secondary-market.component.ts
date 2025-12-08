import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketSessionsApi, SeasonTicketSessionsService, SeasonTicketSessionsState
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { SecondaryMarketService, SecondaryMarketConfig } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { SecondaryMarketConfigComponent } from '@admin-clients/cpanel/promoters/secondary-market/feature';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, ViewChild, AfterViewInit, OnDestroy, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError, Observable, first, tap, filter, of, delay, switchMap, combineLatest } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};
@Component({
    selector: 'ob-season-tickets-secondary-market',
    providers: [SeasonTicketSessionsApi, SeasonTicketSessionsState, SeasonTicketSessionsService],
    imports: [CommonModule, FormContainerComponent, SecondaryMarketConfigComponent, MatCheckbox,
        ReactiveFormsModule, MatProgressSpinnerModule, MatSlideToggleModule, TranslatePipe, MatExpansionModule],
    templateUrl: './season-tickets-secondary-market.component.html',
    styleUrls: ['./season-tickets-secondary-market.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketsSecondaryMarketComponent implements AfterViewInit, OnDestroy {
    readonly #destroy = inject(DestroyRef);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #secondaryMarketSrv = inject(SecondaryMarketService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    @ViewChild(SecondaryMarketConfigComponent, { static: true })
    readonly configurationComponent: SecondaryMarketConfigComponent;

    $seasonTicket = signal(null as SeasonTicket);

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    get form() {
        return this.configurationComponent?.form;
    }

    loading$ = booleanOrMerge([
        this.#secondaryMarketSrv.seasonTicketConfiguration.loading$(),
        this.#seasonTicketSessionsSrv.sessions.loading$()
    ]);

    ngAfterViewInit(): void {
        this.#seasonTicketSrv.seasonTicket.get$().pipe(
            first(Boolean),
            tap(seasonTicket => this.#secondaryMarketSrv.seasonTicketConfiguration.load(seasonTicket.id)),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(seasonTicket => {
            this.$seasonTicket.set(seasonTicket);
            this.#seasonTicketSessionsSrv.sessions.load(seasonTicket.id.toString(), { limit: 0 });
        });

        combineLatest([
            this.#secondaryMarketSrv.seasonTicketConfiguration.get$().pipe(filter(Boolean)),
            this.#seasonTicketSessionsSrv.sessions.getSummary$().pipe(first(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroy))
            .subscribe(([settings, summary]) => this.form.reset({ num_sessions: summary.assigned_sessions, ...settings }));
    }

    ngOnDestroy(): void {
        this.#secondaryMarketSrv.seasonTicketConfiguration.clear();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            return throwError(() => scrollIntoFirstInvalidFieldOrErrorMsg());
        }
        return this.#secondaryMarketSrv.seasonTicketConfiguration.save(this.$seasonTicket().id, this.form.value as SecondaryMarketConfig).pipe(
            tap(() => this.form.reset(this.form.value)),
            tap(() => this.#ephemeralSrv.showSaveSuccess())
        );
    }

    handleStatusChange(isActive: boolean): void {
        if (this.form.controls.commission?.dirty || this.form.controls.price?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.form.controls.enabled.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted => {
                    if (saveAccepted) {
                        return this.save$();
                    } else {
                        this.cancel();
                        return of(false);
                    }
                }
                )
            ).subscribe();
        } else {
            this.save$().subscribe();
        }
    }

    cancel(): void {
        this.#secondaryMarketSrv.seasonTicketConfiguration.load(this.$seasonTicket().id);
    }
}
