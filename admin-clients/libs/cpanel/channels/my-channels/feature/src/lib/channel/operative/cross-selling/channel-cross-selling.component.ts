import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import {
    ChannelsService, ChannelSuggestion, ChannelSuggestionType,
    ChannelsExtendedService,
    GetChannelSuggestionsDataResponse, GetChannelSuggestionsRequest
} from '@admin-clients/cpanel/channels/data-access';
import {
    DialogSize, ObMatDialogConfig, SearchablePaginatedListComponent,
    EphemeralMessageService, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take } from 'rxjs/operators';
import { NewCrossSellingItemDialogComponent } from './create-dialog/new-cross-selling-item-dialog.component';
import { ChannelCrossSellingSuggestionsComponent } from './sugggestions-details/channel-cross-selling-suggestions.component';

@Component({
    selector: 'app-channel-cross-selling',
    templateUrl: './channel-cross-selling.component.html',
    styleUrls: ['./channel-cross-selling.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MatButtonModule, MatIconModule,
        MatTooltipModule, FormContainerComponent, TranslatePipe, SearchablePaginatedListComponent, DateTimePipe
    ]
})
export class ChannelCrossSellingComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _channelService = inject(ChannelsService);
    private readonly _channelExtSrv = inject(ChannelsExtendedService);
    private readonly _detailOverlayService = inject(DetailOverlayService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);

    private _channelId: number;

    readonly pageSize = 10;
    readonly channelSuggestionType = ChannelSuggestionType;
    readonly dateTimeFormats = DateTimeFormats;

    readonly dateFormat = DateTimeFormats.shortDateTimeWithWeek;
    readonly dateFilterPipe = new DateTimePipe();

    readonly suggestions$ = this._channelExtSrv.channelSuggestions.getData$()
        .pipe(
            filter(Boolean),
            map(suggestions =>
                suggestions.map(data => ({
                    ...data,
                    targets: data.targets.sort((a, b) =>
                        new Date(a.start_date)?.getTime() - new Date(b.start_date)?.getTime()
                    )
                }))
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly suggestionsMetadata$ = this._channelExtSrv.channelSuggestions.getMetadata$();

    readonly isInProgress$ = this._channelExtSrv.channelSuggestions.loading$();

    ngOnInit(): void {
        this._channelService.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => this._channelId = channel.id);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelExtSrv.channelSuggestions.clear();
        this._detailOverlayService.close();
    }

    open(row: GetChannelSuggestionsDataResponse): void {
        this._detailOverlayService.open(
            ChannelCrossSellingSuggestionsComponent,
            new DetailOverlayData({ source: row?.source, targets: row?.targets, channelId: this._channelId }, row?.source.name)
        );
    }

    openNewCrossSellingItemDialog(): void {
        this._matDialog.closeAll();
        this._matDialog.open(NewCrossSellingItemDialogComponent, new ObMatDialogConfig({ channelId: this._channelId }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                take(1)
            )
            .subscribe(channelSuggestion => {
                this._channelExtSrv.channelSuggestions.get$().pipe(
                    filter(Boolean),
                    map(channelSuggestionsRes => channelSuggestionsRes.data.find(
                        e => (e.source.id === channelSuggestion.id) && (e.source.type === channelSuggestion.type)
                    )),
                    first(Boolean)
                ).subscribe(response => this.open(response));
                this._ephemeralMsg.showCreateSuccess();
                this.loadChannelSuggestions({ limit: this.pageSize });
            });
    }

    openDeleteSourceTargetsDialog(suggestionData: GetChannelSuggestionsDataResponse): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.CROSS_SELLING.DELETE_TARGETS_TITLE',
            message: 'CHANNELS.CROSS_SELLING.DELETE_TARGETS_WARN',
            messageParams: { name: suggestionData.source.name },
            actionLabel: 'FORMS.ACTIONS.DELETE'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._channelExtSrv.channelSuggestions.deleteSourceTargets(this._channelId, suggestionData.source))
            )
            .subscribe(() => {
                this._channelExtSrv.channelSuggestions.load(this._channelId, { limit: this.pageSize });
                this._ephemeralMsg.showDeleteSuccess();
            });
    }

    generateTooltipContent(targets: ChannelSuggestion[]): string {
        return targets.map(target => target.parent_name ?
            `${this.dateFilterPipe.transform(target.start_date, this.dateFormat)} - ${target.parent_name}`
            : target.name
        ).join(', ');
    }

    loadChannelSuggestions(filters?: Partial<GetChannelSuggestionsRequest>): void {
        this._channelExtSrv.channelSuggestions.load(this._channelId, filters);
    }
}
