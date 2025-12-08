import {
    BulkResultsFilter, EventSessionsService, MultiSessionChangesElem, PutSessionsResponse, SessionBulkStatus, SessionWrapper
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BehaviorSubject, combineLatest, iif, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, shareReplay, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-multi-session-changes-verification-dialog',
    templateUrl: './multi-session-changes-verification-dialog.component.html',
    styleUrls: ['./multi-session-changes-verification-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MultiSessionChangesVerificationDialogComponent implements OnInit {
    private _isActionExecuted = new BehaviorSubject<boolean>(false);
    private _result$: Observable<PutSessionsResponse[]>;
    isActionExecuted$ = this._isActionExecuted.asObservable();
    tableColumns = ['status', 'name', 'date'];
    dateTimeFormats = DateTimeFormats;
    sessionBulkStatus = SessionBulkStatus;
    filteredResult$: Observable<MultiSessionChangesElem[]>;
    totals$: Observable<{ errors: number; sessions: number }>;
    isActionInProgress$: Observable<boolean>;
    bulkResultsFilter = BulkResultsFilter;
    resultsFilter = new UntypedFormControl(BulkResultsFilter.all);

    constructor(
        private _sessionsSrv: EventSessionsService,
        private _dialogRef: MatDialogRef<MultiSessionChangesVerificationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: {
            eventId: number;
            putSessions: { [key: string]: unknown };
            sessions: SessionWrapper[];
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = true;
    }

    ngOnInit(): void {
        this.isActionInProgress$ = this._sessionsSrv.isUpdateSessionsInProgress$()
            .pipe(shareReplay(1));
        this._result$ = this.isActionExecuted$
            .pipe(
                mergeMap(isExecuted =>
                    iif(
                        () => isExecuted,
                        this._sessionsSrv.updateSessions(this._data.eventId, this._data.putSessions),
                        this._sessionsSrv.updateSessions(this._data.eventId, this._data.putSessions, true)
                    )
                ),
                catchError(_ => {
                    this.close();
                    return of([]);
                }),
                shareReplay(1)
            );

        this.totals$ = this._result$
            .pipe(
                map(result => ({
                    errors: result.filter(elem => elem.status === SessionBulkStatus.error).length,
                    sessions: result.length
                }))
            );

        this.filteredResult$ = combineLatest([
            this._result$,
            this.resultsFilter.valueChanges
                .pipe(startWith(this.resultsFilter.value))
        ])
            .pipe(
                map(([result, filter]: [PutSessionsResponse[], BulkResultsFilter]) => {
                    let filteredResult = result.map(elem => {
                        const sw = this._data.sessions.find(swEl => swEl.session.id === elem.id);
                        const resultElem: MultiSessionChangesElem = {
                            id: sw.session.id,
                            name: sw.session.name,
                            date: sw.session.start_date,
                            status: elem.status
                        };
                        if (elem.status === SessionBulkStatus.error) {
                            resultElem.errorKey = elem.detail.code;
                        }
                        return resultElem;
                    });

                    switch (filter) {
                        case BulkResultsFilter.noCompatibles:
                            filteredResult = filteredResult.filter(elem => elem.status === SessionBulkStatus.error);
                            break;
                        case BulkResultsFilter.compatibles:
                            filteredResult = filteredResult.filter(elem => elem.status !== SessionBulkStatus.error);
                            break;
                    }

                    return filteredResult;
                })
            );
    }

    close(): void {
        this._dialogRef.close(this._isActionExecuted.getValue());
    }

    updateOrClose(): void {
        if (!this._isActionExecuted.getValue()) {
            this._isActionExecuted.next(true);
        } else {
            this.close();
        }
    }
}
