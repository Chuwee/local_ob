import { ChannelType, ChannelsService, GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import { PromotionChannelsScope } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject, switchMap } from 'rxjs';
import { filter, take, takeUntil } from 'rxjs/operators';

const PAGE_SIZE = 10;
@Component({
    selector: 'app-promotion-tpl-channels',
    templateUrl: './promotion-tpl-channels.component.html',
    styleUrls: ['./promotion-tpl-channels.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, SearchablePaginatedSelectionModule,
        FlexLayoutModule, KeyValuePipe
    ]
})
export class PromotionTplChannelsComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _changeDet = inject(ChangeDetectorRef);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _promotionTplsSrv = inject(PromotionTplsService);

    private _filter: GetChannelsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'name:asc',
        includeThirdPartyChannels: true
    };

    readonly channelType = ChannelType;
    readonly promotionChannelsScope = PromotionChannelsScope;
    readonly pageSize = PAGE_SIZE;
    readonly metadata$ = this._channelsService.channelsList.getMetadata$();
    readonly isLoading$ = this._channelsService.isChannelLoading$();
    readonly typeControl = new FormControl(null as ChannelType);
    readonly channelsListData$ = this._channelsService.channelsList.getList$();

    @Input() channelsForm: UntypedFormGroup;
    @Input() promotionId: number;

    ngOnInit(): void {
        this.channelsForm.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((channelsScope: PromotionChannelsScope) =>
                channelsScope === PromotionChannelsScope.restricted ?
                    this.channelsForm.get('selected').enable() : this.channelsForm.get('selected').disable()
            );

        combineLatest([
            this._promotionTplsSrv.getPromotionTplChannels$(),
            this.channelsForm.valueChanges // only used as a trigger
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([promChannels]) => {
                if (promChannels) {
                    FormControlHandler.checkAndRefreshDirtyState(this.channelsForm.get('type'), promChannels.type);
                    FormControlHandler.checkAndRefreshDirtyState(this.channelsForm.get('selected'), promChannels.channels || []);
                    this._changeDet.markForCheck();
                }
            });

        this.typeControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => {
                this._filter = { ...this._filter, type };
                this.loadChannelsList();
            });

        this._channelsService.channelsList.getList$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy),
                switchMap(() => this._promotionTplsSrv.getPromotionTplChannels$()),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(promChannels => {
                if (promChannels) {
                    this.channelsForm.patchValue({
                        selected: promChannels.channels || [],
                        ...(promChannels.type && { type: promChannels.type })
                    });
                    this.channelsForm.markAsPristine();
                    this.channelsForm.markAsUntouched();
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    reloadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filter = { ...this._filter, limit, offset, name: q };
        this.loadChannelsList();
    }

    private loadChannelsList(): void {
        this._promotionTplsSrv.getPromotionTemplate$()
            .pipe(
                filter(Boolean),
                take(1)
            )
            .subscribe(tpl => {
                this._filter.entityId = tpl?.entity?.id || this._filter.entityId;
                this._channelsService.channelsList.load(this._filter);
            });
    }
}
