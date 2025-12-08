import { Channel, ChannelListElement, ChannelType, ChannelsService, GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    DialogSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, startWith } from 'rxjs/operators';

const PAGE_SIZE = 20;
const MAX_CHANNEL = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-product-channel-dialog',
    templateUrl: './add-product-channel-dialog.component.html',
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule,
        MatDialogTitle, MatDialogContent, MatDialogActions, MatButton, MatProgressSpinner, MatIconButton, MatIcon, MatTooltip,
        SearchablePaginatedSelectionModule, EllipsifyDirective
    ],
    styleUrls: ['./add-product-channel-dialog.component.scss']
})
export class AddProductChannelDialogComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #productsService = inject(ProductsService);
    readonly #channelsService = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<AddProductChannelDialogComponent, boolean>);
    readonly #data = inject<{ productId: number }>(MAT_DIALOG_DATA);

    #hasNewChannels = false;
    #lastSelected: ChannelListElement[] = [];
    #filters: GetChannelsRequest = { limit: PAGE_SIZE, offset: 0, type: [ChannelType.web, ChannelType.boxOffice, ChannelType.webB2B] };

    readonly pageSize = PAGE_SIZE;
    readonly maxChannel = MAX_CHANNEL;

    readonly selectedForm = this.#fb.control({ value: [] as ChannelListElement[], disabled: true }, Validators.required);

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#channelsService.isChannelsListLoading$(),
        this.#productsService.product.channelsList.inProgress$()
    ]);

    readonly channelsListData$ = combineLatest([
        this.#channelsService.channelsList.getList$(),
        this.#productsService.product.channelsList.get$()
    ])
        .pipe(
            filter(([channelListData, productChannelList]) => !!channelListData && !!productChannelList),
            map(([channelListData, productChannelList]): Channel & { selected: boolean }[] => {
                const channelListIds = productChannelList.map(productChannel => productChannel.channel.id);
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
            this.#productsService.product.channelsList.post(this.#data.productId, ids)
                .pipe(first())
                .subscribe(() => {
                    this.#hasNewChannels = true;
                    this.#filters = { ...this.#filters };
                    this.#productsService.product.channelsList.load(this.#data.productId);
                    this.#dialogRef.close(this.#hasNewChannels);
                });
        }
    }

    checkElement(id: number): boolean {
        return this.selectedForm.value.find(e => e.id === id) !== undefined;
    }
}
