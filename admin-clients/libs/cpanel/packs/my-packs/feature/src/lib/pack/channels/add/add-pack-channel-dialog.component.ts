import { Channel, ChannelListElement, ChannelType, ChannelsService, GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import {
    DialogSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, startWith } from 'rxjs/operators';

const PAGE_SIZE = 20;
const MAX_CHANNEL = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-pack-channel-dialog',
    templateUrl: './add-pack-channel-dialog.component.html',
    imports: [
        MaterialModule, FlexLayoutModule, TranslatePipe, ReactiveFormsModule, SearchablePaginatedSelectionModule, CommonModule,
        EllipsifyDirective
    ],
    styleUrls: ['./add-pack-channel-dialog.component.scss']
})
export class AddPackChannelDialogComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #packsService = inject(PacksService);
    readonly #channelsService = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<AddPackChannelDialogComponent, boolean>);
    readonly #data = inject<{ packId: number }>(MAT_DIALOG_DATA);

    #hasNewChannels = false;
    #lastSelected: ChannelListElement[] = [];
    #filters: GetChannelsRequest = { limit: PAGE_SIZE, offset: 0 };

    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;
    readonly maxChannel = MAX_CHANNEL;

    readonly selectedForm = this.#fb.control({ value: [] as ChannelListElement[], disabled: true }, Validators.required);

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#channelsService.isChannelsListLoading$(),
        this.#packsService.pack.channels.loading$()
    ]);

    readonly channelsListData$ = combineLatest([
        this.#channelsService.channelsList.getList$(),
        this.#packsService.pack.channels.getData$()
    ]).pipe(
        filter(([channelListData, packChannelList]) => !!channelListData && !!packChannelList),
        map(([channelListData, packChannelList]): Channel & { selected: boolean }[] => {
            const channelListIds = packChannelList.map(packChannel => packChannel.channel.id);
            return channelListData.map(channelData => ({
                ...channelData,
                selected: channelListIds.includes(channelData.id)
            }));
        })
    );

    readonly selectedChannels$ = this.selectedForm.valueChanges.pipe(
        map(selected => {
            if (!selected || selected.length === 0) {
                return [];
            }
            this.#lastSelected = selected;
            return selected?.sort((a, b) => a.name.localeCompare(b.name));
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly channelsListMetadata$ = this.#channelsService.channelsList.getMetadata$();
    readonly channelsSelected$ = this.selectedChannels$.pipe(map(selected => selected.length), startWith(0));

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#dialogRef.disableClose = false;
    }

    ngAfterViewInit(): void {
        FormControlHandler.checkAndRefreshDirtyState(
            this.selectedForm,
            this.#lastSelected
        );
    }

    ngOnDestroy(): void {
        this.#channelsService.channelsList.clear();
        this.#dialogRef.close(this.#hasNewChannels);
    }

    shouldDisableChannel: (d: Channel & { selected: boolean }) => boolean = (d: Channel & { selected: boolean }) => d.selected;

    loadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filters = { ...this.#filters, limit, offset, name: q?.length ? q : null, includeThirdPartyChannels: true };
        this.#channelsService.channelsList.load(this.#filters);
    }

    close(): void {
        this.#dialogRef.close(this.#hasNewChannels);
    }

    addChannels(): void {
        if (this.selectedForm.dirty) {
            const ids = this.selectedForm.value.map(channel => channel.id);
            this.#packsService.pack.channels.post(this.#data.packId, ids)
                .pipe(first())
                .subscribe(() => {
                    this.#hasNewChannels = true;
                    this.#filters = { ...this.#filters };
                    this.#packsService.pack.channels.load(this.#data.packId);
                    this.#dialogRef.close(this.#hasNewChannels);
                });
        }
    }

    checkElement(id: number): boolean {
        return this.selectedForm.value.find(e => e.id === id) !== undefined;
    }

    filterChannels(type: ChannelType): void {
        this.#filters = { ...this.#filters, type: type || null };
        this.#channelsService.channelsList.load(this.#filters);
    }
}
