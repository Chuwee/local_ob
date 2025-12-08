import {
    BulkResultsFilter, DeleteSessionsResponse, EventSessionsService, MultiSessionChangesElem, SessionBulkStatus, SessionWrapper
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BehaviorSubject, combineLatest, iif, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, shareReplay, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-multi-session-delete-dialog',
    templateUrl: './multi-session-delete-dialog.component.html',
    styleUrls: ['./multi-session-delete-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MultiSessionDeleteDialogComponent implements OnInit {
    private _isActionExecuted = new BehaviorSubject<boolean>(false);
    private _result$: Observable<DeleteSessionsResponse[]>;
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
        private _dialogRef: MatDialogRef<MultiSessionDeleteDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: {
            eventId: number;
            sessions: SessionWrapper[];
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = true;
    }

    ngOnInit(): void {
        this.isActionInProgress$ = this._sessionsSrv.isDeleteSessionsInProgress$()
            .pipe(shareReplay(1));
        this._result$ = this.isActionExecuted$
            .pipe(
                mergeMap(isExecuted =>
                    iif(
                        () => isExecuted,
                        this._sessionsSrv.deleteSessions(this._data.eventId, this._data.sessions.map(sw => sw.session.id), false),
                        this._sessionsSrv.deleteSessions(this._data.eventId, this._data.sessions.map(sw => sw.session.id), true)
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
                map(([result, filter]: [DeleteSessionsResponse[], BulkResultsFilter]) => {
                    let filteredResult = result.map((elem, index) => {
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

    deleteOrClose(): void {
        if (!this._isActionExecuted.getValue()) {
            this._isActionExecuted.next(true);
        } else {
            this.close();
        }
    }
}
