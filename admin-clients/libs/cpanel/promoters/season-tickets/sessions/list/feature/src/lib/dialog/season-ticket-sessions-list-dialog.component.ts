import {
    AssignedSessionsNumber, SeasonTicketSession, SeasonTicketSessionStatus, UnassignedSessionsNumber
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { VmSeasonTicketSession } from '@admin-clients/cpanel-promoters-season-tickets-sessions-list-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

interface SavedSessionsNumber {
    saved: number;
    toSave: number;
}

interface OptionsNumber {
    notAssigned: number;
    notUnassigned: number;
}

@Component({
    selector: 'app-season-ticket-sessions-list-dialog',
    templateUrl: './season-ticket-sessions-list-dialog.component.html',
    styleUrls: ['./season-ticket-sessions-list-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})

export class SeasonTicketSessionsListDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChild(MatSort, { static: true }) private _martSort: MatSort;
    displayedColumns = ['info', 'session_name', 'session_starting_date', 'event_name'];
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    dateTimeFormats = DateTimeFormats;
    seasonTicketSessionStatus = SeasonTicketSessionStatus;
    dataSource: MatTableDataSource<VmSeasonTicketSession>;
    filteredSessionsControl: UntypedFormControl;
    numberOfSavedSessions: SavedSessionsNumber = { saved: 0, toSave: 0 };
    options: OptionsNumber = { notAssigned: 0, notUnassigned: 0 };

    constructor(
        private _dialogRef: MatDialogRef<SeasonTicketSessionsListDialogComponent>,
        private _formBuilder: UntypedFormBuilder,
        private _translate: TranslateService,
        @Inject(MAT_DIALOG_DATA) private _data: {
            sessions: VmSeasonTicketSession[];
            numberOfAssignedSessions: AssignedSessionsNumber;
            numberOfUnassignedSessions: UnassignedSessionsNumber;
        }
    ) {
        this.setNumberOfSavedSessions();
        this.setOptions();
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.setSessionsDatasource();
        this.setFilteredSessions();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    sessionsToCopy(): string {
        let sessionsForClipboard = ' ';
        if (this.dataSource.filteredData?.length) {
            sessionsForClipboard = this.dataSource.filteredData.map(session =>
                `${session.session_name}${String.fromCharCode(9)}${session.session_starting_date}${String.fromCharCode(9)}${session.event_name}`)
                .join('\r\n');
        }
        return sessionsForClipboard;
    }

    close(sessionId: number = null): void {
        this._dialogRef.close(sessionId);
    }

    reasonTooltip(session: VmSeasonTicketSession): string {
        return session.status === SeasonTicketSessionStatus.notAssigned ?
            this._translate.instant(`SEASON_TICKET.${session.session_not_valid_reason}`) :
            this._translate.instant(`SEASON_TICKET.${session.sessions_not_unassigned_reason}`);
    }

    private setNumberOfSavedSessions(): void {
        this.numberOfSavedSessions.saved = (this._data.numberOfAssignedSessions?.assigned ?? 0) +
            (this._data.numberOfUnassignedSessions?.unAssigned ?? 0);
        this.numberOfSavedSessions.toSave = (this._data.numberOfAssignedSessions?.toAssign ?? 0) +
            (this._data.numberOfUnassignedSessions?.toUnAssign ?? 0);
    }

    private setOptions(): void {
        this.options.notAssigned = (this._data.numberOfAssignedSessions?.toAssign ?? 0) -
            (this._data.numberOfAssignedSessions?.assigned ?? 0);
        this.options.notUnassigned = (this._data.numberOfUnassignedSessions?.toUnAssign ?? 0) -
            (this._data.numberOfUnassignedSessions?.unAssigned ?? 0);
    }

    private setSessionsDatasource(): void {
        const sessions = this._data.sessions.filter(session =>
            (session.is_process_session_assignment_done && session.status === SeasonTicketSessionStatus.notAssigned) ||
            (session.is_process_session_unassignment_done && session.status === SeasonTicketSessionStatus.assigned));
        this.dataSource = new MatTableDataSource(sessions);

    }

    private setFilteredSessions(): void {
        this.filteredSessionsControl = this.options.notAssigned ?
            this._formBuilder.control(SeasonTicketSessionStatus.notAssigned) :
            this._formBuilder.control(SeasonTicketSessionStatus.assigned);
        this.dataSource.filterPredicate = ((data: SeasonTicketSession, filter: string) => !filter || data.status === filter);
        this.dataSource.filter = this.filteredSessionsControl.value;
        this.filteredSessionsControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                this.dataSource.filter = value;
            });
        this.dataSource.sort = this._martSort;
    }
}
