import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService, EventSurcharge, EventSurchargeType } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, EntitySurchargeType } from '@admin-clients/shared/common/data-access';
import { cleanRangesBeforeSave, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { RangeElement } from '@admin-clients/shared-utility-models';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit, Signal,
    viewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { delay, EMPTY, firstValueFrom, merge, Observable, switchMap, throwError } from 'rxjs';
import { filter, first, map, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-event-surcharges',
    templateUrl: './event-surcharges.component.html',
    styleUrls: ['./event-surcharges.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventSurchargesComponent implements OnInit, OnDestroy, WritingComponent, AfterViewInit {

    private readonly _eventsService = inject(EventsService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _route = inject(ActivatedRoute);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _destroyRef = inject(DestroyRef);

    private readonly _event$ = this._eventsService.event.get$();

    private readonly _secondaryMarketSurcharges = viewChild<ElementRef>('secondaryMarket');
    private readonly _changeServiceSurcharges = viewChild<ElementRef>('changeService');
    private _importBtnClicked = false;

    #secondaryMarketEnabled = false;
    #changeSeatEnabled = false;

    readonly types = EventSurchargeType;
    readonly form = this._fb.group({});

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this._eventsService.eventSurcharges.inProgress$(),
        this._entitiesService.surcharges.inProgress$()
    ]);

    readonly changeSeatSurchargesAvailable$ = this._eventsService.event.get$()
        .pipe(
            map(event => !!event?.settings?.change_seat_settings?.enable),
            tap(enabled => this.#changeSeatEnabled = enabled)
        );

    readonly hasSecondaryMarket$ = this._entitiesService.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_secondary_market),
        tap(enabled => this.#secondaryMarketEnabled = enabled)
    );

    readonly surcharges$ = merge(
        this._eventsService.eventSurcharges.get$(),
        this._entitiesService.surcharges.get$().pipe(
            filter(Boolean),
            withLatestFrom(this._eventsService.eventSurcharges.get$().pipe(filter(Boolean))),
            map(([entitySurcharges, eventSurcharges]) => {
                eventSurcharges
                    .find(surchages => surchages.type === EventSurchargeType.generic)
                    .ranges = entitySurcharges.find(surcharges => surcharges.type === EntitySurchargeType.generic).ranges;
                this._importBtnClicked = true;
                return eventSurcharges;
            })
        )
    );

    surcharges = new Map<EventSurchargeType, RangeElement[]>();

    readonly currency$ = this._eventsService.event.get$()
        .pipe(map(event => event.currency_code));

    ngAfterViewInit(): void {
        this.surcharges$.pipe(
            first(Boolean),
            switchMap(() => {
                const queryParam = this._route.snapshot.queryParamMap.get('from');
                let elemSignal: Signal<ElementRef>;
                let result: Observable<boolean>;
                if (queryParam === 'secondary-market') {
                    elemSignal = this._secondaryMarketSurcharges;
                    result = this.hasSecondaryMarket$;
                } else if (queryParam === 'change-seats') {
                    elemSignal = this._changeServiceSurcharges;
                    result = this.changeSeatSurchargesAvailable$;
                }
                return result ? result.pipe(
                    first(Boolean),
                    map(() => elemSignal)
                ) : EMPTY;
            }),
            delay(0)
        )
            .subscribe(elemSignal =>
                elemSignal().nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
            );
    }

    ngOnInit(): void {
        this._eventsService.eventSurcharges.clear();
        this._entitiesService.surcharges.clear();

        this.configureSurchargeLimit(this.types.generic);
        this.configureSurchargeLimit(this.types.promotion);
        this.configureSurchargeLimit(this.types.invitation);
        this.configureSurchargeLimit(this.types.secondaryMarket);
        this.configureSurchargeLimit(this.types.changeSeat);

        this.surcharges$
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(eventSurcharges => {
                this.initializeMap();
                eventSurcharges.forEach(surcharge => {
                    this.surcharges.set(surcharge.type, surcharge.ranges);
                    if (surcharge.type !== EventSurchargeType.secondaryMarket
                        && surcharge.type !== EventSurchargeType.changeSeat) {
                        this.setSurchargeLimit(surcharge);
                    }
                });
                if (this._importBtnClicked) {
                    this._importBtnClicked = false;
                    setTimeout(() => {
                        this.form.get(EventSurchargeType.generic).markAsDirty();
                        this._ref.markForCheck();
                    });
                } else {
                    this.form.markAsPristine();
                    setTimeout(() => this._ref.markForCheck());
                }
            });

        this._event$.pipe(first())
            .subscribe(event => this._eventsService.eventSurcharges.load(event.id.toString()));
    }

    ngOnDestroy(): void {
        this._eventsService.eventSurcharges.clear();
        this._entitiesService.surcharges.clear();
    }

    async importEntitySurcharges(): Promise<void> {
        const event = await firstValueFrom(this._event$);
        this._entitiesService.surcharges.load(event.entity.id, event.currency_code);
    }

    async cancel(): Promise<void> {
        const event = await firstValueFrom(this._event$);
        this._eventsService.eventSurcharges.load(event.id.toString());
    }

    async save(): Promise<void> {
        const event = await firstValueFrom(this._event$);
        this.save$()
            .subscribe(() => {
                this._eventsService.eventSurcharges.load(event.id.toString());
                this._ref.markForCheck();
            });
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            const eventSurcharges: EventSurcharge[] = [];
            for (const [type] of this.surcharges) {
                if (type === EventSurchargeType.secondaryMarket) {
                    if (this.#secondaryMarketEnabled) {
                        eventSurcharges.push({
                            type,
                            ranges: cleanRangesBeforeSave(this.form.get(type).value.ranges)
                        });
                    }
                } else if (type === EventSurchargeType.changeSeat) {
                    if (this.#changeSeatEnabled) {
                        eventSurcharges.push({
                            type,
                            ranges: cleanRangesBeforeSave(this.form.get(type).value.ranges)
                        });
                    }
                } else {
                    eventSurcharges.push({
                        type,
                        limit: this.form.get(type).value.limit,
                        ranges: cleanRangesBeforeSave(this.form.get(type).value.ranges),
                        allow_channel_use_alternative_charges: this.form.get(type).value.allow_channel_use_alternative_charges
                    });
                }
            }
            return this._event$.pipe(
                first(),
                switchMap(event => this._eventsService.eventSurcharges.create(
                    event.id.toString(),
                    eventSurcharges
                )),
                tap(() => this._ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            this._ref.markForCheck();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    private initializeMap(): void {
        this.surcharges = new Map();
        this.surcharges.set(this.types.generic, [{ from: 0, values: {} }]);
        this.surcharges.set(this.types.invitation, [{ from: 0, values: {} }]);
        this.surcharges.set(this.types.promotion, [{ from: 0, values: {} }]);
        this.surcharges.set(this.types.secondaryMarket, [{ from: 0, values: {} }]);
        this.surcharges.set(this.types.changeSeat, [{ from: 0, values: {} }]);
    }

    private configureSurchargeLimit(surchargeType: EventSurchargeType): void {
        const surchargeTypeGroup = this._fb.group({});

        surchargeTypeGroup.addControl('limit', this._fb.group({
            enabled: [false],
            min: [{ value: undefined, disabled: true }],
            max: [{ value: undefined, disabled: true }]
        }));

        if (surchargeType === this.types.promotion) {
            surchargeTypeGroup.addControl('allow_channel_use_alternative_charges', this._fb.control(false));
        }

        this.form.addControl(surchargeType, surchargeTypeGroup);

        (this.form.get(`${surchargeType}.limit.enabled`).valueChanges as Observable<boolean>)
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(isEnabled => {
                const minCtrl = this.form.get(`${surchargeType}.limit.min`);
                const maxCtrl = this.form.get(`${surchargeType}.limit.max`);
                if (isEnabled) {
                    minCtrl.enable();
                    minCtrl.setValidators([Validators.required, Validators.min(0)]);
                    maxCtrl.enable();
                    maxCtrl.setValidators([Validators.required, Validators.min(0)]);
                } else {
                    minCtrl.disable();
                    minCtrl.setValidators([]);
                    maxCtrl.disable();
                    maxCtrl.setValidators([]);
                }
                minCtrl.updateValueAndValidity();
                maxCtrl.updateValueAndValidity();
            });
    }

    private setSurchargeLimit(surcharge: EventSurcharge): void {
        this.form.get(`${surcharge.type}.limit.enabled`)?.setValue(surcharge.limit.enabled);
        this.form.get(`${surcharge.type}.limit.min`)?.setValue(surcharge.limit.min);
        this.form.get(`${surcharge.type}.limit.max`)?.setValue(surcharge.limit.max);
        if (surcharge.type === this.types.promotion) {
            this.form.get(`${surcharge.type}.allow_channel_use_alternative_charges`)
                ?.setValue(surcharge.allow_channel_use_alternative_charges);
        }
    }
}
