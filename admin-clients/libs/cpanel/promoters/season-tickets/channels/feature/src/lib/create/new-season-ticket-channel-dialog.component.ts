import { Metadata } from '@OneboxTM/utils-state';
import { GetChannelsRequest, ChannelType, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import {
    ContextNotificationComponent,
    DialogSize,
    SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSelectionList } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, Subject } from 'rxjs';
import { debounceTime, filter, first, map, startWith, takeUntil } from 'rxjs/operators';
import { VmSeasonTicketChannel } from '../models/vm-season-ticket-channel.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        SearchInputComponent,
        EllipsifyDirective,
        ContextNotificationComponent
    ],
    selector: 'app-season-ticket-new-channel-dialog',
    templateUrl: './new-season-ticket-channel-dialog.component.html',
    styleUrls: ['./new-season-ticket-channel-dialog.component.scss']
})
export class NewSeasonTicketChannelDialogComponent implements OnInit, OnDestroy {

    @ViewChild(MatSelectionList, { static: true }) private _selectionList: MatSelectionList;
    private _request = new GetChannelsRequest();
    private _actions = new Map([
        ['SEARCH_INPUT', value => this._request.name = !value ? null : value],
        ['TYPE', value => this._request.type = value]
    ]);

    private readonly _formStructure = { type: null };
    private _onDestroy = new Subject<void>();
    private _newChannels = false;
    channelsSelected$: Observable<number>;
    channelsListData$: Observable<VmSeasonTicketChannel[]>;
    channelListMetadata$: Observable<Metadata>;
    isChannelListLoading$: Observable<boolean>;
    isChannelListSaving$: Observable<boolean>;
    newSeasonTicketChannelForm: UntypedFormGroup;
    filtersForm: UntypedFormGroup;
    channelType = ChannelType;

    constructor(
        private _dialogRef: MatDialogRef<NewSeasonTicketChannelDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _channelsService: ChannelsService,
        private _seasonTicketChannelService: SeasonTicketChannelsService,
        @Inject(MAT_DIALOG_DATA) private _data: { seasonTicketId: number }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.loadChannels('TYPE', null);
        this.channelsListData$ = combineLatest([
            this._channelsService.channelsList.getList$(),
            this._seasonTicketChannelService.seasonTicketChannelList.getData$()
        ])
            .pipe(
                filter(([channelListData, seasonTicketChannelList]) =>
                    !!channelListData && !!seasonTicketChannelList),
                map(([channelListData, seasonTicketChannelList]): VmSeasonTicketChannel[] => {
                    const channelListIds = seasonTicketChannelList.map(seasonChannel => seasonChannel.channel.id);
                    return channelListData.map(channelData => ({
                        ...channelData,
                        selected: channelListIds.includes(channelData.id)
                    }));
                })
            );
        this.filtersForm = this._fb.group({ ...this._formStructure });
        this.filtersForm.valueChanges
            .pipe(takeUntil(this._onDestroy), debounceTime(300))
            .subscribe(values =>
                this.loadChannels('TYPE', values.type ? values.type : null));
        this.channelListMetadata$ = this._channelsService.channelsList.getMetadata$();
        this.isChannelListLoading$ = this._channelsService.isChannelsListLoading$();
        this.isChannelListSaving$ = this._seasonTicketChannelService.seasonTicketChannelList.loading$();
        this.newSeasonTicketChannelForm = this._fb.group({
            channelsList: [null, Validators.required]
        });
        this.channelsSelected$ = this.newSeasonTicketChannelForm.valueChanges
            .pipe(map(value => value.channelsList.length), startWith(0));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._dialogRef.close(this._newChannels);
    }

    loadChannels(type: string, value: any): void {
        if (this._selectionList.selectedOptions.selected.length !== 0) {
            this._selectionList.deselectAll();
        }
        const action = this._actions.get(`${type}`);
        action.call(this, value);
        this._request.includeThirdPartyChannels = true;
        this._channelsService.channelsList.load(this._request);
    }

    close(): void {
        this._dialogRef.close(this._newChannels);
    }

    addNewSeasonTicketChannel(): void {
        const channelIds = [...this.newSeasonTicketChannelForm.value.channelsList];
        if (this.newSeasonTicketChannelForm.valid) {
            const addSeasonTicket$ = channelIds.map(channelId =>
                this._seasonTicketChannelService.seasonTicketChannelList.add(this._data.seasonTicketId, channelId));
            forkJoin(addSeasonTicket$).pipe(first()).subscribe(() => {
                this._newChannels = true;
                this._seasonTicketChannelService.seasonTicketChannelList.load(this._data.seasonTicketId, this._request);
                this.close();
            });
        }
    }

}
