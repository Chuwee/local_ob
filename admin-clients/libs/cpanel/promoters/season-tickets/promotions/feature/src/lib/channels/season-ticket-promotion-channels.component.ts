import { Channel, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PromotionChannelsScope } from '@admin-clients/cpanel/promoters/data-access';
import {
    SeasonTicketChannelsApi, SeasonTicketChannelsService, SeasonTicketChannelsState
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketPromotionsService } from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnInit,
    inject,
    DestroyRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';

const PAGE_SIZE = 10;
@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        SearchablePaginatedSelectionModule,
        CommonModule
    ],
    providers: [
        SeasonTicketChannelsApi,
        SeasonTicketChannelsService,
        SeasonTicketChannelsState
    ],
    selector: 'app-season-ticket-promotion-channels',
    templateUrl: './season-ticket-promotion-channels.component.html',
    styleUrls: ['./season-ticket-promotion-channels.component.scss']
})
export class SeasonTicketPromotionChannelsComponent implements OnInit {
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _seasonTicketChannelService = inject(SeasonTicketChannelsService);
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    private _channelsFilters: BehaviorSubject<{ type: ChannelType; name: string }> = new BehaviorSubject({ type: null, name: null });

    readonly channelType = ChannelType;
    readonly promotionChannelsScope = PromotionChannelsScope;
    readonly metadata$ = this._seasonTicketChannelService.seasonTicketChannelList.getMetadata$();
    readonly isLoading$ = this._seasonTicketChannelService.seasonTicketChannelList.loading$();

    readonly channelsListData$ = combineLatest([
        this._seasonTicketChannelService.seasonTicketChannelList.getData$(),
        this._channelsFilters
    ])
        .pipe(map(([stChannels, filters]) =>
            stChannels?.map(stChannel => stChannel.channel)
                .filter(stChannel =>
                    (!filters.type || stChannel.type === filters.type)
                    && (!filters.name || stChannel.name.toLowerCase().includes(filters.name.toLowerCase()))
                )
        ));

    value: Channel[];
    pageSize = PAGE_SIZE;

    @Input() channelsForm: UntypedFormGroup;
    @Input() seasonTicketId: number;
    @Input() promotionId: number;

    ngOnInit(): void {
        this._seasonTicketChannelService.seasonTicketChannelList.load(
            this.seasonTicketId, { limit: this.pageSize, offset: 0, sort: 'name:asc' }
        );

        this.channelsForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((channelsScope: PromotionChannelsScope) =>
                channelsScope === PromotionChannelsScope.restricted ?
                    this.channelsForm.get('selected').enable() : this.channelsForm.get('selected').disable()
            );

        combineLatest([
            this._stPromotionsSrv.promotionChannels.get$(),
            this.channelsForm.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promChannels]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.channelsForm.get('type'), promChannels?.type);
                FormControlHandler.checkAndRefreshDirtyState(this.channelsForm.get('selected'), promChannels?.channels || []);
                this._ref.markForCheck();
            });

        this._stPromotionsSrv.promotionChannels.get$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(promChannels => {
                this.channelsForm.patchValue({
                    type: promChannels?.type,
                    selected: promChannels?.channels || []
                });
                this.channelsForm.markAsPristine();
                this.channelsForm.markAsUntouched();
            });
    }

    filterChannels(type: ChannelType): void {
        type = type || null;
        const filters = this._channelsFilters.getValue();
        if (type !== filters.type) {
            this._channelsFilters.next({
                type,
                name: filters.name
            });
        }
    }

    filterChangeHandler(filters: Partial<PageableFilter>): void {
        this._seasonTicketChannelService.seasonTicketChannelList.load(this.seasonTicketId, { limit: this.pageSize, offset: 0, ...filters });
    }
}
