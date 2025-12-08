import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelEvent, ChannelsExtendedService,
    ChannelsService, GetChannelEventsRequest, IsWebV4$Pipe, IsWebB2bPipe, PutChannelEventRequest
} from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateTinyComponent, EphemeralMessageService,
    ObMatDialogConfig, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { AsyncPipe, CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatListOption } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, forkJoin, Observable, throwError } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { AddCarouselEventsDialogComponent } from './add-carousel-events-dialog/add-carousel-events-dialog.component';

@Component({
    selector: 'app-channel-catalog-config',
    templateUrl: './channel-catalog-config.component.html',
    styleUrls: ['./channel-catalog-config.component.scss'],
    imports: [
        CommonModule, FormContainerComponent, ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        FormControlErrorsComponent, AsyncPipe, SearchInputComponent, CdkDropList, DateTimePipe, CdkDrag,
        EmptyStateTinyComponent, MaterialModule, IsWebV4$Pipe, IsWebB2bPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelCatalogConfigComponent implements AfterViewInit, OnDestroy, WritingComponent {
    #fb = inject(FormBuilder);
    #channelsService = inject(ChannelsService);
    #channelsExtSrv = inject(ChannelsExtendedService);
    #ephemeralMessageService = inject(EphemeralMessageService);
    #entitiesSrv = inject(EntitiesBaseService);
    #channelId: number;
    #orderedEventsLength: number;

    #filters: GetChannelEventsRequest = {
        offset: 0,
        limit: 999
    };

    readonly #searchChangeDebounceTime = 100;
    readonly #matDialog = inject(MatDialog);

    readonly dateTimeFormats = DateTimeFormats;
    readonly maxCarouselEvents = 5;
    readonly $channel = toSignal(this.#channelsService.getChannel$());

    form = this.#fb.group({
        chEvents: this.#fb.group({}),
        chCategoriesEnabled: this.#fb.control(null as boolean),
        chPacksEventsEnabled: this.#fb.control(null as boolean)
    });

    isInProgress$ = booleanOrMerge([
        this.#channelsExtSrv.isChannelEventsLoading$(),
        this.#channelsExtSrv.isChannelCatalogSaving$()
    ]);

    totalEvents$: Observable<number>;
    carouselChannelEvents$: Observable<ChannelEvent[]>;
    orderedChannelEvents$: Observable<ChannelEvent[]>;
    unorderedChannelEvents$: Observable<ChannelEvent[]>;
    searchChannelEventsChanged = new BehaviorSubject<string>('');

    get chEventsFormGroup(): FormGroup {
        return this.form.get('chEvents') as FormGroup;
    }

    ngAfterViewInit(): void {
        const allChannelEvents$ = this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                tap(channel => {
                    this.form.controls.chCategoriesEnabled.setValue(channel.settings?.enable_B2B_event_category_filter);
                    this.form.controls.chPacksEventsEnabled.setValue(channel.settings?.enable_packs_and_events_catalog);
                    this.#channelId = channel.id;
                }),
                switchMap(channel => {
                    this.loadChannelEvents();
                    this.#entitiesSrv.loadEntity(channel?.entity?.id);
                    return this.#channelsExtSrv.getChannelEventsData$();
                }),
                filter(chEvents => !!chEvents),
                tap(chEvents => {
                    chEvents.forEach(chEv => {
                        this.chEventsFormGroup.setControl(String(chEv.event.id), this.#fb.group({
                            position: chEv.catalog?.position,
                            visible: chEv.catalog?.visible,
                            carousel_position: chEv.catalog?.carousel_position
                        }));
                    });
                    this.form.markAsPristine();
                }),
                shareReplay(1)
            );

        this.totalEvents$ = allChannelEvents$.pipe(map(events => events.length));

        const allChannelEventsUpdated$ = combineLatest([
            allChannelEvents$,
            this.chEventsFormGroup.valueChanges
        ]).pipe(
            map(([chEvents, chEventsFormValues]: [ChannelEvent[], { [id: string]: { position: number; visible: boolean } }]) =>
                chEventsFormValues ?
                    chEvents.map(chEv => {
                        if (chEventsFormValues[chEv.event.id]) {
                            chEv.catalog.position = chEventsFormValues[chEv.event.id].position;
                            chEv.catalog.visible = chEventsFormValues[chEv.event.id].visible;
                        }
                        return chEv;
                    }) : chEvents
            ),
            shareReplay(1)
        );

        const debouncedSearchChannelEventsChanged$ = this.searchChannelEventsChanged
            .pipe(
                debounceTime(this.#searchChangeDebounceTime),
                distinctUntilChanged()
            );

        const filteredChannelEvents$ = combineLatest([
            allChannelEventsUpdated$,
            debouncedSearchChannelEventsChanged$
        ]).pipe(
            map(([chEvents, searchStr]) => chEvents
                .map(chEv => ({
                    ...chEv,
                    filtered: !chEv.event.name.toLowerCase().includes(searchStr.toLowerCase())
                }))
            ),
            shareReplay(1)
        );

        this.orderedChannelEvents$ = filteredChannelEvents$
            .pipe(
                map(chEvents => {
                    const orderedEvents = chEvents
                        .filter(chEv => chEv.catalog.position != null)
                        .sort((a, b) => a.catalog.position - b.catalog.position);
                    this.#orderedEventsLength = orderedEvents.length;
                    return orderedEvents;
                }),
                shareReplay(1)
            );

        this.unorderedChannelEvents$ = filteredChannelEvents$
            .pipe(
                map(chEvents => chEvents
                    .filter(chEv => chEv.catalog.position == null)
                    .sort((a, b) => new Date(a.event.start_date).getTime() - new Date(b.event.start_date).getTime())
                ),
                shareReplay(1)
            );

        this.carouselChannelEvents$ = combineLatest([
            allChannelEvents$,
            this.chEventsFormGroup.valueChanges.pipe(shareReplay(1))
        ]).pipe(
            map(([chEvents, chEventsFormValues]:
                [ChannelEvent[], { [id: string]: { position: number; visible: boolean; carousel_position?: number } }]) => {
                if (chEventsFormValues) {
                    const orderedCarouselArray = chEvents.map(chEv => {
                        if (chEventsFormValues[chEv.event.id]) {
                            chEv.catalog.position = chEventsFormValues[chEv.event.id].position;
                            chEv.catalog.visible = chEventsFormValues[chEv.event.id].visible;
                            chEv.catalog.carousel_position = chEventsFormValues[chEv.event.id].carousel_position;
                            return chEv;
                        }
                        return null;
                    }).filter(chEvent => chEvent?.catalog?.carousel_position !== null)
                        .sort((a, b) => a.catalog.carousel_position - b.catalog.carousel_position);
                    return orderedCarouselArray;
                } else {
                    return [];
                }
            }
            ),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearChannelEvents();
    }

    moveToOrderedEvents(selectedEvents: MatListOption[]): void {
        const updatedEvents: Record<string, unknown> = {};
        selectedEvents.forEach((selEvent, index) => {
            updatedEvents[selEvent.value.event.id] = {
                position: this.#orderedEventsLength + index,
                visible: true
            };
        });
        this.chEventsFormGroup.patchValue(updatedEvents);
        this.form.markAsDirty();
    }

    dropItem(event: CdkDragDrop<ChannelEvent[]>): void {
        const updatedEvents: Record<string, unknown> = {};
        let orderedList: ChannelEvent[];
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
            orderedList = event.container.data;
        } else {
            transferArrayItem(event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex);
            orderedList = event.previousContainer.data;
            updatedEvents[event.container.data[event.currentIndex].event.id] = {
                position: null
            };
        }
        orderedList.forEach((chEv, i) => {
            updatedEvents[chEv.event.id] = {
                position: i
            };
        });
        this.chEventsFormGroup.patchValue(updatedEvents);
        this.form.markAsDirty();
    }

    dropItemCarousel(event: CdkDragDrop<ChannelEvent[]>): void {
        const updatedEvents: Record<string, unknown> = {};
        let orderedList: ChannelEvent[];
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
            orderedList = event.container.data;
        } else {
            transferArrayItem(event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex);
            orderedList = event.previousContainer.data;
            updatedEvents[event.container.data[event.currentIndex].event.id] = {
                position: null
            };
        }
        orderedList.forEach((chEv, i) => {
            updatedEvents[chEv.event.id] = {
                carousel_position: i
            };
        });
        this.chEventsFormGroup.patchValue(updatedEvents);
        this.form.markAsDirty();
    }

    setVisibleEvent(eventId: number): void {
        this.chEventsFormGroup.patchValue({
            [String(eventId)]: { visible: !this.chEventsFormGroup.get(String(eventId)).value.visible }
        });
        this.form.markAsDirty();
    }

    isVisibleEvent(eventId: number): void {
        return this.chEventsFormGroup.get(String(eventId)).value.visible;
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this.form.controls.chCategoriesEnabled.dirty || this.form.controls.chPacksEventsEnabled.dirty) {
                obs$.push(this.#channelsService.saveChannel(this.#channelId, {
                    settings: {
                        ...(this.form.controls.chCategoriesEnabled.dirty
                            // eslint-disable-next-line @typescript-eslint/naming-convention
                            ? { enable_B2B_event_category_filter: this.form.value.chCategoriesEnabled } : {}),
                        ...(this.form.controls.chPacksEventsEnabled.dirty
                            ? { enable_packs_and_events_catalog: this.form.value.chPacksEventsEnabled } : {})
                    }
                }));
            }

            const updatedCatalog = this.chEventsFormGroup.value as { [id: string]: { position: number; visible: boolean } };
            const req: PutChannelEventRequest[] = Object.keys(updatedCatalog).map(key => ({
                event_id: Number(key),
                catalog: updatedCatalog[key]
            }));
            obs$.push(this.#channelsExtSrv.saveChannelEvents(this.#channelId, req));

            return forkJoin(obs$)
                .pipe(tap(() => this.#ephemeralMessageService.showSaveSuccess()));

        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadChannelEvents();
        });
    }

    cancel(): void {
        this.loadChannelEvents();
        this.#channelsService.loadChannel(`${this.#channelId}`);
    }

    openAddCarouselEventsDialog(events?: ChannelEvent[]): void {
        this.#matDialog.closeAll();
        this.#matDialog.open(AddCarouselEventsDialogComponent, new ObMatDialogConfig({
            channelId: this.#channelId,
            events: events ?? []
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                take(1)
            )
            .subscribe(data => {
                const updatedEvents: Record<string, unknown> = {};
                Object.keys(this.chEventsFormGroup.controls).forEach(value => {
                    {
                        updatedEvents[value] = {
                            carousel_position: null
                        };
                    }
                });

                data.events.forEach((selEvent, index) => {
                    updatedEvents[selEvent.event.id] = {
                        carousel_position: index
                    };
                });

                this.chEventsFormGroup.patchValue(updatedEvents);
                this.form.markAsDirty();
            });

    }

    private loadChannelEvents(): void {
        this.#channelsExtSrv.loadChannelEvents(this.#channelId, this.#filters);
    }
}
