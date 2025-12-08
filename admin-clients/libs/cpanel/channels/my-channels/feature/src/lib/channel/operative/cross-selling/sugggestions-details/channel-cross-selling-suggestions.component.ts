import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import { ChannelsExtendedService, ChannelSuggestionType, ChannelSuggestion } from '@admin-clients/cpanel/channels/data-access';
import {
    DialogSize, EmptyStateTinyComponent, EphemeralMessageService,
    MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    MatDialog
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { NewCrossSellingItemDialogComponent } from '../create-dialog/new-cross-selling-item-dialog.component';

@Component({
    selector: 'app-channel-cross-selling-suggestions',
    templateUrl: './channel-cross-selling-suggestions.component.html',
    styleUrls: ['./channel-cross-selling-suggestions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, CommonModule, FlexLayoutModule, EmptyStateTinyComponent, DateTimePipe,
        EllipsifyDirective
    ]
})
export class ChannelCrossSellingSuggestionsComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _matDialog = inject(MatDialog);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _channelService = inject(ChannelsExtendedService);
    private readonly _detailOverlayService = inject(DetailOverlayService);

    readonly pageSize = 10;
    readonly channelSuggestionType = ChannelSuggestionType;
    readonly data = inject(DetailOverlayData).data;
    readonly dateFormat = DateTimeFormats.shortDateTimeWithWeek;

    readonly isDesktop$ = isHandsetOrTablet$().pipe(map(value => !value));
    readonly dateTimeFormats = DateTimeFormats;
    readonly crossSellingTargets$ = this._channelService.channelSuggestions.get$().pipe(
        filter(Boolean),
        map(channelSuggestionsRes => channelSuggestionsRes.data.find(
            e => (e.source?.id === this.data.source?.id) && (e.source?.type === this.data.source?.type)
        )?.targets?.sort((a, b) => new Date(a.start_date)?.getTime() - new Date(b.start_date)?.getTime()) || []),
        tap(targets => {
            if (targets.length === 0) {
                this._detailOverlayService.close();
            }
        })
    );

    readonly isLoading$ = this._channelService.channelSuggestions.loading$();

    openDeleteSuggestionDialog(suggestion: ChannelSuggestion): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.CROSS_SELLING.DELETE_SUGGESTION_TITLE',
            message: 'CHANNELS.CROSS_SELLING.DELETE_SUGGESTION_WARN',
            messageParams: { eventName: suggestion.name },
            actionLabel: 'FORMS.ACTIONS.DELETE'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._channelService.channelSuggestions.delete(this.data.channelId, this.data.source, suggestion))
            )
            .subscribe(() => {
                this._channelService.channelSuggestions.load(this.data.channelId, { limit: this.pageSize });
                this._ephemeralMsg.showDeleteSuccess();
            });
    }

    openNewCrossSellingItemDialogWithoutSteps(): void {
        this._matDialog.open(
            NewCrossSellingItemDialogComponent,
            new ObMatDialogConfig(
                { channelId: this.data.channelId, source: this.data.source, targets: this.data.targets }
            )
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => {
                this._channelService.channelSuggestions.load(this.data.channelId);
                this._ephemeralMsg.showCreateSuccess();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
