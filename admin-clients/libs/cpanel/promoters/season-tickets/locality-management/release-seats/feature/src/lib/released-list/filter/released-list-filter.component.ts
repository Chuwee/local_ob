
import { SeasonTicketReleaseSeatListRequest, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketSession, SeasonTicketSessionsService, SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { ReleaseDataSessionStatus } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe } from '@angular/common';
import { Component, ChangeDetectionStrategy, inject, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, first, map } from 'rxjs';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';

@Component({
    selector: 'app-release-list-filter',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, AsyncPipe, SelectSearchComponent, DateTimePipe
    ],
    templateUrl: './released-list-filter.component.html',
    styleUrls: ['./released-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReleasedListFilterComponent implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #sessionsListSrv = inject(SeasonTicketSessionsService);

    #request: SeasonTicketReleaseSeatListRequest;
    readonly dateTimeFormats = DateTimeFormats;
    readonly #originalSessionListSubject = new BehaviorSubject<SeasonTicketSession[]>([]);
    readonly originalSessionList$ = this.#originalSessionListSubject.asObservable();
    readonly $seasonTicketId = toSignal(this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(
            first(Boolean),
            map(seasonTicket => seasonTicket.id)
        ));

    readonly statusList = Object.values([ReleaseDataSessionStatus.sold, ReleaseDataSessionStatus.released])
        .map(type => ({ id: type, name: `SEASON_TICKET.RELEASE_SEAT.RELEASED_LIST.STATUS_OPTS.${type}` }));

    @Output() requestChanged = new EventEmitter<SeasonTicketReleaseSeatListRequest>();
    @Input() form: FormGroup<{ session_id: FormControl<string>; release_status: FormControl<ReleaseDataSessionStatus[]> }>;
    @Input() set sessionLists(value: SeasonTicketSession[]) {
        this.#originalSessionListSubject.next(value);
    }

    @Input() set request(value: SeasonTicketReleaseSeatListRequest) {
        this.#request = value;
    }

    ngOnInit(): void {
        this.#sessionsListSrv.sessions.load(String(this.$seasonTicketId()), { status: SeasonTicketSessionStatus.assigned, limit: 999 });
    }

    changeSessionHandler(sessionSelected: MatSelectChange): void {
        this.initRequest({ session_id: sessionSelected.value });
        this.form.controls.release_status.enable();
    }

    changeStatusHandler(isStatusOpened: boolean): void {
        if (!isStatusOpened) {
            const statusValue = this.form.controls.release_status.value.length > 0
                ? this.form.controls.release_status.value : [ReleaseDataSessionStatus.sold, ReleaseDataSessionStatus.released];
            this.initRequest({ release_status: statusValue });
        }
    }

    initRequest(newRequest?: SeasonTicketReleaseSeatListRequest): void {
        this.#request = {
            limit: this.#request.limit,
            offset: 0,
            session_id: newRequest?.session_id ?? this.#request.session_id,
            release_status: newRequest?.release_status ?? this.#request.release_status
        };
        this.requestChanged.emit(this.#request);
    }
}
