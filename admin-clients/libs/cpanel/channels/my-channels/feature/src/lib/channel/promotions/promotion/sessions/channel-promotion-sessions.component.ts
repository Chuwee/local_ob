import { AfterContentInit, ChangeDetectionStrategy, Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, shareReplay, startWith, switchMap, take } from 'rxjs/operators';
import { ChannelPromotionEventScope, ChannelPromotionSessionScope, ChannelPromotionsService } from '@admin-clients/cpanel-channels-promotions-data-access';
import { SalesRequestsService, provideSalesRequestService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { PromotionEventListElement } from '../events/promotion-events.model';
import { ChannelPromotionSessionsEventSelectionListElement as EventElement } from './channel-promotion-sessions.model';

@Component({
    selector: 'app-channel-promotion-sessions',
    templateUrl: './channel-promotion-sessions.component.html',
    styleUrls: ['./channel-promotion-sessions.component.scss'],
    providers: [provideSalesRequestService()],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionSessionComponent implements OnInit, AfterContentInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelPromotionsService = inject(ChannelPromotionsService);
    readonly #salesRequestsService = inject(SalesRequestsService);
    #eventsScope: ChannelPromotionEventScope;
    readonly scope = ChannelPromotionSessionScope;
    readonly dateTimeFormats = DateTimeFormats;

    sessionForm: UntypedFormGroup;
    events$: Observable<EventElement[]>;
    isSelectionDisabled: boolean;

    @Input() channelId: number;
    @Input() entityId: number;
    @Input() form: UntypedFormGroup;
    @Input() promotionId: number;

    ngOnInit(): void {
        this.sessionForm = this.form.get('sessions') as UntypedFormGroup;

        const selectableEvents$ = this.form.get('events.type').valueChanges.pipe(
            distinctUntilChanged(),
            switchMap(eventScope => {
                this.#eventsScope = eventScope;
                if (eventScope === ChannelPromotionEventScope.restricted) {
                    return parentSelectedEvents$;
                } else if (eventScope === ChannelPromotionEventScope.all) {
                    return allEvents$;
                } else {
                    return of([]);
                }
            }),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

        this.form.get('events').valueChanges.pipe(
            debounceTime(550),
            switchMap(() => selectableEvents$.pipe(take(1)))
        ).subscribe(events => {
            this.sessionForm.get('type').enable();
            if (events?.length > 0) {
                this.isSelectionDisabled = false;
            } else {
                if (this.#eventsScope === null) {
                    this.sessionForm.get('type').disable();
                }
                this.isSelectionDisabled = true;
            }
        });

        const parentSelectedEvents$: Observable<EventElement[]> =
            this.form.get('events.selected').valueChanges.pipe(
                startWith(this.form.get('events.selected').value || []),
                debounceTime(500), // avoids a processing data while user is clicking several options
                map((selected: PromotionEventListElement[]) =>
                    selected.map(elem => ({
                        id: elem.saleReqId,
                        name: elem.name
                    })).sort((a, b) =>
                        a.name.localeCompare(b.name)
                    )
                ),
                takeUntilDestroyed(this.#onDestroy),
                shareReplay(1)
            );

        const allEvents$: Observable<EventElement[]> =
            this.#salesRequestsService.getSalesRequestsListData$().pipe(
                filter(Boolean),
                map(list => list.map(elem => ({
                    id: elem.id,
                    name: elem.event.name
                })))
            );

        this.events$ = selectableEvents$;

        this.sessionForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy), distinctUntilChanged())
            .subscribe((scope: ChannelPromotionSessionScope) => {
                scope === ChannelPromotionSessionScope.restricted ?
                    this.sessionForm.get('selected').enable() : this.sessionForm.get('selected').disable();
            });
    }

    ngAfterContentInit(): void {
        this.#channelPromotionsService.getPromotionSessions$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(sessions => {
            setTimeout(() => {
                this.sessionForm.reset({
                    type: sessions.type || null,
                    selected: sessions.sessions || []
                });
            });
        });
    }
}
