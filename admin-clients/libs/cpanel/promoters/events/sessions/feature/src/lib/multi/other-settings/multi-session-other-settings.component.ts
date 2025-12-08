import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, viewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion, MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, first, map, Observable, switchMap, take, tap, throwError } from 'rxjs';
import {
    MultiSessionChangesVerificationDialogComponent
} from '../changes-verification/multi-session-changes-verification-dialog.component';

@Component({
    selector: 'app-multi-session-other-settings',
    templateUrl: './multi-session-other-settings.component.html',
    imports: [FormContainerComponent, MatProgressSpinner, MatExpansionModule,
        MatCheckbox, TranslatePipe, MatAccordion, ReactiveFormsModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiSessionOtherSettingsComponent implements WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #matDialog = inject(MatDialog);

    readonly $matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    readonly $loading = toSignal(
        booleanOrMerge([
            this.#sessionsSrv.isUpdateSessionsInProgress$(),
            this.#sessionsSrv.isSessionsGroupsLoading$(),
            this.#sessionsSrv.isSessionSaving$()
        ])
    );

    readonly $showOrphanSeats = toSignal(this.#eventsSrv.event.get$()
        .pipe(
            first(Boolean),
            map(event => !Boolean(event.type === EventType.activity || event.type === EventType.themePark))
        )
    );

    readonly form = this.#fb.group({
        enableOrphanSeatsProtection: false
    });

    save$(): Observable<boolean> {
        if (this.form.valid) {
            return combineLatest([
                this.#sessionsSrv.getSelectedSessions$().pipe(take(1)),
                this.#eventsSrv.event.get$().pipe(take(1))
            ]).pipe(
                switchMap(([sessions, event]) => {
                    const sessionIds = sessions.map(sw => sw.session.id);
                    const putSessions = {
                        ids: sessionIds,
                        value: { settings: { enable_orphan_seats: this.form.value.enableOrphanSeatsProtection } }
                    };
                    return this.#matDialog.open<
                        MultiSessionChangesVerificationDialogComponent, { eventId; putSessions; sessions }, boolean
                    >(MultiSessionChangesVerificationDialogComponent, new ObMatDialogConfig({
                        eventId: event.id,
                        putSessions,
                        sessions
                    })).beforeClosed()
                        .pipe(
                            tap(isSaved => {
                                if (isSaved) {
                                    this.form.reset();
                                    this.#sessionsSrv.setRefreshSessionsList();
                                }
                            })
                        );
                }),
                take(1)
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().pipe(take(1)).subscribe();
    }

    reload(): void {
        this.form.reset({ enableOrphanSeatsProtection: false });
    }
}