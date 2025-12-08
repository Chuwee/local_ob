import { ChannelSuggestion, ChannelSuggestionType, GetChannelSuggestionsDataResponse } from '@admin-clients/cpanel/channels/data-access';
import { SessionsFilterFields, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    GetSaleRequestSessionsRequest, GetSalesRequestsRequest,
    provideSalesRequestService, SalesRequestsEventStatus, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { HelpButtonComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, Id } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { AsyncValidatorFn, ReactiveFormsModule, ValidationErrors } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, filter, first, map, Observable, shareReplay, Subject } from 'rxjs';
import { NewCrossSellingItemDialogFormType } from '../form-type';
import { CrossSellingSessionsFilterComponent } from './filter/cross-selling-sessions-filter.component';

const PAGE_SIZE = 5;
@Component({
    selector: 'app-channel-cross-selling-selector-table',
    templateUrl: './channel-cross-selling-selector-table.component.html',
    styleUrls: ['./channel-cross-selling-selector-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, SearchablePaginatedSelectionModule,
        CommonModule, ReactiveFormsModule, DateTimePipe, FlexModule, FlexLayoutModule,
        CrossSellingSessionsFilterComponent, HelpButtonComponent
    ],
    providers: [SalesRequestsService, provideSalesRequestService()]
})
export class ChannelCrossSellingSelectorTableComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _formReceived = new BehaviorSubject<NewCrossSellingItemDialogFormType>(null);
    private readonly _salesRequestService = inject(SalesRequestsService);

    private _form?: NewCrossSellingItemDialogFormType;

    private _filters: GetSaleRequestSessionsRequest = {
        limit: PAGE_SIZE,
        sort: `${SessionsFilterFields.startDate}:asc`
    };

    readonly catalogueSalesRequestsData$ = this._salesRequestService.saleRequests.getData$()
        .pipe(
            filter(Boolean),
            map(saleRequests => saleRequests.map(saleRequest => ({
                id: saleRequest.event.id,
                name: saleRequest.event.name,
                type: ChannelSuggestionType.event,
                saleRequestId: saleRequest.id
            }))),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly catalogueSalesRequestsMetadata$ = this._salesRequestService.saleRequests.getMetadata$();

    readonly isLoadingSalesRequests$ = this._salesRequestService.saleRequests.loading$();

    readonly sessionsData$ = this._salesRequestService.saleRequestSessions.getData$().pipe(
        filter(Boolean),
        map(saleRequestSessions => saleRequestSessions.map(saleRequestSession => ({
            id: saleRequestSession.id,
            name: saleRequestSession.name,
            start_date: saleRequestSession.date.start,
            type: ChannelSuggestionType.session
        })))
    );

    readonly sessionsMetadata$ = this._salesRequestService.saleRequestSessions.getMetadata$();
    readonly isLoadingSessions$ = this._salesRequestService.saleRequestSessions.loading$();

    readonly pageSize = 5;
    readonly dateTimeFormats = DateTimeFormats;

    @Input() channelId: number;
    @Input() suggestionsData$: Observable<GetChannelSuggestionsDataResponse[]>;
    @Input() eventsToDisable: Id[] = [];
    @Input() sessionsToDisable: Id[] = [];
    @Input() maxSelection = 10;
    @Input() currencyCode: string;

    @Input()
    set form(value: NewCrossSellingItemDialogFormType) {
        this._form = value;
        if (this.suggestionsData$) {
            this._form.controls.events.setAsyncValidators(this.getEventsValidator());
            this._form.controls.sessions.setAsyncValidators(this.getSessionsValidator());
        }
        this._formReceived.next(value);
    }

    get form(): NewCrossSellingItemDialogFormType {
        return this._form;
    }

    loadSalesRequests(filters: Partial<GetSalesRequestsRequest>): void {
        this._salesRequestService.saleRequests.clear();
        this._salesRequestService.saleRequests.load({
            channel: this.channelId,
            event_status: [SalesRequestsEventStatus.inProgramming, SalesRequestsEventStatus.planned, SalesRequestsEventStatus.ready],
            currencyCode: this.currencyCode,
            ...filters
        });
    }

    loadSaleRequestSessions(filters: Partial<GetSaleRequestSessionsRequest>): void {
        this._filters = {
            ...this._filters,
            ...filters
        };
        this._salesRequestService.saleRequestSessions.clear();
        this._salesRequestService.saleRequestSessions.load({
            saleRequestId: this.form.value.events[0].saleRequestId.toString(),
            status: [SessionStatus.preview, SessionStatus.scheduled, SessionStatus.ready],
            ...this._filters
        });
    }

    getEventsValidator(): AsyncValidatorFn {
        return () => this.suggestionsData$
            .pipe(
                first(Boolean),
                map(sourceSuggestions => {
                    if (!this.form.value.allSessions) {
                        return null;
                    } else {
                        let result: ValidationErrors;
                        if (this.form.controls.events.value.some(event => this.eventsToDisable.find(e => e.id === event.id))) {
                            result = {
                                selfSelectedEvent: true
                            };
                        } else {
                            sourceSuggestions.forEach(suggestion => {
                                if (!result) {
                                    const prevTargets = suggestion.targets;
                                    const currentTargets = this.form.controls.events.value.filter(event =>
                                        !prevTargets.find(target => target.type === ChannelSuggestionType.event && target.id === event.id)
                                    );
                                    if (prevTargets.length + currentTargets.length > this.maxSelection) {
                                        result = {
                                            maxSuggestionsLimitExceeded: this.maxSelection
                                        };
                                    }
                                }
                            });
                        }
                        return result || null;
                    }
                })
            );
    }

    getSessionsValidator(): AsyncValidatorFn {
        return () => this.suggestionsData$
            .pipe(
                first(Boolean),
                map(sourceSuggestions => {
                    if (this.form.value.allSessions) {
                        return null;
                    } else {
                        let result: ValidationErrors;
                        sourceSuggestions.forEach(suggestion => {
                            if (!result) {
                                const prevTargets = suggestion.targets;
                                const currentTargets = this.form.controls.sessions.value.filter(session =>
                                    !prevTargets.find(
                                        target => target.type === ChannelSuggestionType.session && target.id === session.id
                                    )
                                );
                                if (prevTargets.length + currentTargets.length > this.maxSelection) {
                                    result = {
                                        maxSuggestionsLimitExceeded: this.maxSelection
                                    };
                                }
                            }
                        });
                        return result || null;
                    }
                })
            );
    }

    get shouldDisableEvent(): (channelSuggestion: ChannelSuggestion) => boolean {
        return channelSuggestion => this.form.controls.events.value?.length >= this.maxSelection
            && !this.form.value.events.some(event => event.id === channelSuggestion.id);
    }

    get shouldDisableSession(): (channelSuggestion: ChannelSuggestion) => boolean {
        return channelSuggestion => (this.form.controls.sessions.value?.length >= this.maxSelection
            || this.sessionsToDisable.some(session => session.id === channelSuggestion.id))
            && !this.form.value.sessions.some(session => session.id === channelSuggestion.id);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._formReceived.complete();
    }
}
