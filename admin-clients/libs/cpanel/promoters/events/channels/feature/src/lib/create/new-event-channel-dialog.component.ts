import { Metadata } from '@OneboxTM/utils-state';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService, GetEventChannelsCandidatesRequest } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { OrderChannel } from '@admin-clients/cpanel-sales-data-access';
import {
    DialogSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators
} from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay, startWith, takeUntil } from 'rxjs/operators';
import { VmEventChannel } from '../models/vm-event-channel.model';

const PAGE_SIZE = 20;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-event-new-channel-dialog',
    templateUrl: './new-event-channel-dialog.component.html',
    imports: [
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SearchablePaginatedSelectionModule,
        CommonModule,
        EllipsifyDirective
    ],
    styleUrls: ['./new-event-channel-dialog.component.scss']
})
export class NewEventChannelDialogComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _hasNewChannels = false;
    private _eventChannelService: EventChannelsService;
    private _lastSelected;
    private _filters: GetEventChannelsCandidatesRequest = { limit: PAGE_SIZE, offset: 0 };

    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;

    newEventChannelForm: UntypedFormGroup;
    selectedForm = new UntypedFormControl({ value: [], disabled: true }, Validators.required);
    isLoadingOrSaving$: Observable<boolean>;
    channelsListData$: Observable<VmEventChannel[]>;
    selectedChannels$: Observable<OrderChannel[]>;
    channelsListMetadata$: Observable<Metadata>;
    channelsSelected$: Observable<number>;

    constructor(
        private _dialogRef: MatDialogRef<NewEventChannelDialogComponent, boolean>,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: { eventId: number; eventChannelService: EventChannelsService }
    ) {
        this._eventChannelService = this._data.eventChannelService;
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.newEventChannelForm = this._fb.group({
            channels: this._fb.group({
                type: null,
                selected: this.selectedForm
            })
        });

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._eventChannelService.eventChannelsCandidatesList.inProgress$(),
            this._eventChannelService.eventChannelsList.inProgress$()
        ]);

        this.selectedChannels$ = this.selectedForm.valueChanges
            .pipe(
                map(selected => {
                    if (!selected || selected.length === 0) {
                        return [];
                    }
                    this._lastSelected = selected;
                    return selected?.sort((a, b) => a.name.localeCompare(b.name));
                }),
                shareReplay(1)
            );

        this.channelsListData$ = combineLatest([
            this._eventChannelService.eventChannelsCandidatesList.getData$(),
            this._eventChannelService.eventChannelsList.getData$()
        ])
            .pipe(
                filter(values => values.every(Boolean)),
                map(([channelListData, eventChannelList]): VmEventChannel[] => {
                    const channelListIds = eventChannelList.map(eventChannel => eventChannel.channel.id);
                    return channelListData.map(channelData => ({
                        ...channelData,
                        selected: channelListIds.includes(channelData.id)
                    }));
                })
            );

        this.channelsListMetadata$ = this._eventChannelService.eventChannelsCandidatesList.getMetaData$();
        this.channelsSelected$ = this.selectedChannels$.pipe(map(selected => selected.length), startWith(0));
    }

    ngAfterViewInit(): void {
        this.formChangesHandler();
    }

    ngOnDestroy(): void {
        this._eventChannelService.eventChannelsCandidatesList.clear();
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._dialogRef.close(this._hasNewChannels);
    }

    shouldDisableChannel: (d: VmEventChannel) => boolean = (d: VmEventChannel) =>
        d.selected || (this.selectedForm.value.length >= 10 && (this.selectedForm.value.find(e => e.id === d.id) === undefined));

    filterChannels(type: ChannelType): void {
        this._filters = { ...this._filters, type: type || null };
        this._eventChannelService.eventChannelsCandidatesList.load(this._data.eventId, this._filters);
    }

    loadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, name: q?.length ? q : null };
        this._filters.includeThirdPartyChannels = true;
        this._eventChannelService.eventChannelsCandidatesList.load(this._data.eventId, this._filters);
    }

    close(): void {
        this._dialogRef.close(this._hasNewChannels);
    }

    addNewEventChannel(): void {
        if (this.newEventChannelForm.dirty) {
            const addEvent$ = this.selectedForm.value.map(channel =>
                this._eventChannelService.addEventChannel(this._data.eventId, channel.id));
            forkJoin(addEvent$).pipe(first()).subscribe(() => {
                this._hasNewChannels = true;
                this._filters = { ...this._filters, type: null };
                this._eventChannelService.eventChannelsList.load(this._data.eventId, this._filters);
                this._dialogRef.close(this._hasNewChannels);
            });
        }
    }

    checkElement(id: number): boolean {
        return this.selectedForm.value.find(e => e.id === id) !== undefined;
    }

    private formChangesHandler(): void {
        this.channelsListData$
            .pipe(
                takeUntil(this._onDestroy)
            )
            .subscribe(channelList => {
                const selectedChannels = channelList.filter(channel => channel.selected);
                this.updateForm(selectedChannels);
            });
        FormControlHandler.checkAndRefreshDirtyState(
            this.selectedForm,
            this._lastSelected
        );
    }

    private updateForm(channels): void {
        this.newEventChannelForm.patchValue({ selected: { type: channels?.scope, selected: channels?.items } });
    }
}
