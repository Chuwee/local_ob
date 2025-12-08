import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { ContextNotificationComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { distinctUntilChanged, first, skip, switchMap, tap } from 'rxjs/operators';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { forkJoin, Observable, throwError } from 'rxjs';
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'app-season-ticket-ticket-redemption',
    imports: [
        FormContainerComponent, AsyncPipe, TranslatePipe, ContextNotificationComponent, MatProgressBar, MatCheckbox,
        ReactiveFormsModule, MatProgressSpinner, MatIcon
    ],
    templateUrl: './season-ticket-ticket-redemption.component.html',
    styleUrl: './season-ticket-ticket-redemption.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SeasonTicketTicketRedemptionComponent {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly $ticketRedemption = toSignal(this.#seasonTicketSrv.ticketRedemption.get$());

    readonly isGenerationStatusInProgress$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged());

    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$().pipe(distinctUntilChanged());
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketSrv.ticketRedemption.loading$(),
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
    ]);

    readonly form = this.#fb.group({
        enabled: [false]
    });

    constructor() {
        effect(() => {
            this.#seasonTicketSrv.ticketRedemption.load(this.$seasonTicket()?.id);
        });
        effect(() => {
            this.form.reset({
                enabled: this.$ticketRedemption()?.enabled || false
            });
        });

    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const updateTicketRedemption = this.form.getRawValue();

            obs$.push(this.#seasonTicketSrv.ticketRedemption.update(this.$seasonTicket()?.id, updateTicketRedemption));

            return forkJoin(obs$).pipe(
                switchMap(() => {
                    this.#ephemeralMessageSrv.showSuccess({
                        msgKey: 'SEASON_TICKET.UPDATE_SUCCESS',
                        msgParams: { seasonTicketName: this.$seasonTicket()?.name }
                    });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                    this.#seasonTicketSrv.ticketRedemption.load(this.$seasonTicket()?.id);
                    return this.#seasonTicketSrv.seasonTicket.get$().pipe(skip(1), first());
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#seasonTicketSrv.ticketRedemption.load(this.$seasonTicket()?.id);
    }
}
