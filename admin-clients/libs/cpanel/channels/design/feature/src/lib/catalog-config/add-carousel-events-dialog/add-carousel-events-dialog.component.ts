import {
    ChannelEvent, ChannelsExtendedService, ChannelsExtendedState,
    GetChannelEventsRequest
} from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, ObDialog, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { AfterContentInit, ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-add-carousel-events-dialog',
    templateUrl: './add-carousel-events-dialog.component.html',
    styleUrls: ['./add-carousel-events-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule,
        CommonModule, MatDividerModule, FlexLayoutModule,
        SearchablePaginatedSelectionModule
    ],
    providers: [ChannelsExtendedService, ChannelsExtendedState]
})
export class AddCarouselEventsDialogComponent
    extends ObDialog<
        AddCarouselEventsDialogComponent,
        { channelId: number; events: ChannelEvent[] },
        { events: ChannelEvent[] }
    >
    implements OnDestroy, AfterContentInit {

    readonly #onDestroy: Subject<void> = new Subject();

    readonly #channelService = inject(ChannelsExtendedService);
    readonly #fb = inject(FormBuilder);

    readonly maxSelection = 5;
    readonly pageSize = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly form = this.#fb.group({
        events: [[] as ChannelEvent[]]
    }
    );

    readonly isLoading$ = this.#channelService.isChannelEventsLoading$();
    readonly eventsData$ = this.#channelService.getChannelEventsData$().pipe(
        map(events => events || [])
    );

    readonly eventsMetadata$ = this.#channelService.getChannelEventsMetadata$();

    readonly $selected = toSignal(this.form.controls.events.valueChanges, { initialValue: [] });
    constructor() {
        super(DialogSize.LARGE);
    }

    ngAfterContentInit(): void {
        setTimeout(() => {
            this.form.controls.events.reset([...this.data.events].reverse());
            this.form.markAsPristine();
        });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    close(): void {
        this.dialogRef.close();
    }

    loadEvents(filters: Partial<GetChannelEventsRequest>): void {
        this.#channelService.clearChannelEvents();
        this.#channelService.loadChannelEvents(this.data.channelId, { ...filters, published: true });
    }

    get shouldDisableEvent(): (value: IdName) => boolean {
        return (value: IdName) => this.form.controls.events.value?.length >= this.maxSelection
            && this.form.controls.events.value.filter(item => item.id === value.id).length !== 1;
    }

    save(): void {
        this.dialogRef.close({ events: [...this.form.controls.events.value].reverse() });
    }

}
