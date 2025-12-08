import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventTierConditions, EventTiers, EventTiersService, PostEventTier, PutEventTier, VMEventTiers
} from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplate, VenueTemplatePriceType, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { BehaviorSubject, combineLatest, concat, forkJoin, Observable, throwError } from 'rxjs';
import { filter, finalize, first, map, mapTo, shareReplay, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-tiers',
    templateUrl: './event-tiers.component.html',
    styleUrls: ['./event-tiers.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTiersComponent implements OnInit, OnDestroy {
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #eventTiersService = inject(EventTiersService);
    readonly #eventsService = inject(EventsService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);

    private readonly _triggerEventTierChange = new BehaviorSubject<VMEventTiers[]>(null);
    private readonly _triggerEventTierChange$ = this._triggerEventTierChange.asObservable();
    private readonly _cancelBtnClickSbj = new BehaviorSubject<void>(null);
    private readonly _dirtyFG = new Map();
    private _deletedEventTiers: VMEventTiers[] = [];
    private _newEventTiers: VMEventTiers[] = [];
    private _eventTiersCopy: VMEventTiers[];
    private _eventPriceType: VenueTemplatePriceType[] = [];
    private _tplId: number;

    readonly cancelBtnClickSbj$ = this._cancelBtnClickSbj.asObservable();
    readonly tiersTableHead = ['price_zone', 'tiers_name', 'condition_change', 'price', 'start_date',
        'sell_limit', 'tier-actions', 'actions'];

    readonly eventTierConditions = EventTierConditions;
    eventId: string;
    venueTemplates$: Observable<VenueTemplate[]>;
    vmEventTiers$: Observable<EventTiers[]>;
    isOver = false;

    isTierInProgress$ = booleanOrMerge([
        this.#eventTiersService.isEventTiersListInProgress$(),
        this.#eventTiersService.isEventTiersListSaveInProgress$(),
        this.#eventTiersService.isEventTierLimitRemoveInProgress$()
    ]);

    readonly currency$ = this.#eventsService.event.get$()
        .pipe(map(event => event.currency_code));

    readonly venueTplCtrl = this.#fb.control(null);
    readonly tiersForm = this.#fb.group({
        tiersTable: this.#fb.array([]),
        draggableTable: this.#fb.group({
            enabled: false
        })
    });

    get tiersTable(): UntypedFormArray {
        return this.tiersForm.get('tiersTable') as UntypedFormArray;
    }

    get draggableTable(): UntypedFormGroup {
        return this.tiersForm.get('draggableTable') as UntypedFormGroup;
    }

    ngOnInit(): void {
        this.clearValues();

        this.venueTemplates$ = this.#venueTemplatesService.getVenueTemplatesList$()
            .pipe(
                filter(v => v !== null),
                map(value => value.data),
                tap(venueTpls => {
                    this.venueTplCtrl.setValue(venueTpls?.[0]);
                    this._tplId = venueTpls[0].id;
                    this.#venueTemplatesService.loadVenueTemplateQuotas(venueTpls[0].id);
                }),
                shareReplay(1)
            );

        this.#eventsService.event.get$()
            .pipe(first(event => event !== null))
            .subscribe(event => {
                this.eventId = event.id.toString();
                const req = {
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc',
                    eventId: event.id,
                    status: [VenueTemplateStatus.active]
                };
                this.#venueTemplatesService.loadVenueTemplatesList(req);
            });

        this.vmEventTiers$ = combineLatest([
            this.#eventTiersService.getEventTiersListData$(),
            this.#venueTemplatesService.getVenueTemplatePriceTypes$(),
            this._triggerEventTierChange$
        ])
            .pipe(
                filter(([eventTiers, priceTypes]) => !!eventTiers && !!priceTypes),
                tap(([_, priceTypes]) => {
                    this._eventPriceType = priceTypes;
                }),
                map(([eventTiers, priceTypes, triggerTierChange]) => triggerTierChange ?
                    this.setPriortiy(triggerTierChange, priceTypes, true) :
                    this.setPriortiy(eventTiers.map(evT => ({ ...evT, priority: 0 })), priceTypes)),
                tap(eventTiers => this._eventTiersCopy = eventTiers),
                map(eventTiers => this.setVmEventTier(eventTiers)),
                tap(eventTiers => this.draggableTable.get('enabled').setValue(eventTiers.some(eventTier => !!eventTier.priority))),
                tap(sortedVmEventTiers => this.setTierFormControls(sortedVmEventTiers))
            );

        combineLatest([
            this.venueTplCtrl.valueChanges,
            this.cancelBtnClickSbj$
        ]).pipe(
            filter(([venueTpl]) => venueTpl?.id),
            tap(() => {
                this.clearValues();
                this._newEventTiers = [];
                this._deletedEventTiers = [];
                this._eventPriceType = [];
            }),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([venueTpl]) => {
            const req = {
                limit: 999,
                offset: 0,
                venue_template_id: venueTpl.id.toString()
            };
            this._tplId = venueTpl.id.toString();
            this._triggerEventTierChange.next(null);
            this.#venueTemplatesService.loadVenueTemplateQuotas(venueTpl.id);
            this.#eventTiersService.loadEventTiersList(this.eventId, req);
            this.#venueTemplatesService.loadVenueTemplatePriceTypes(venueTpl.id);
        });
    }

    ngOnDestroy(): void {
        this.clearValues();
    }

    cancel(): void {
        this._triggerEventTierChange.next(null);
        this._cancelBtnClickSbj.next();
        this.tiersForm.markAsPristine();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        if (this.tiersForm.valid && this.tiersForm.dirty) {
            const obsToSave$: { others: Observable<boolean>[]; delete: Observable<boolean>[]; create: Observable<boolean>[] }
                = { others: [], delete: [], create: [] };
            const tiersTable = this.tiersForm.controls['tiersTable'] as UntypedFormGroup;
            this.updateEventTiersCopy();

            if (this._eventPriceType.length || this.draggableTable.dirty) {
                obsToSave$.others.push(...this.updatePriority());
            }
            const deletedTierIds = this._deletedEventTiers.map(t => t.id);
            if (!!this._deletedEventTiers && this.tiersForm.valid && this.tiersForm.dirty) {
                obsToSave$.delete = this.deleteTier();
                this._deletedEventTiers = [];
            }
            if (!!this._newEventTiers && this.tiersForm.valid && this.tiersForm.dirty) {
                obsToSave$.create = this.addTiers();
                this._newEventTiers = [];
            }
            tiersTable.value.filter(tier => !deletedTierIds.includes(tier.id)).forEach((tier, index) => {
                const actualFGroup = tiersTable.controls[index] as UntypedFormGroup;
                if (actualFGroup?.valid && actualFGroup.dirty && tier.id > 0) {
                    obsToSave$.others.push(...this.updateTier(actualFGroup, this._eventTiersCopy.find(tierCopy =>
                        tierCopy.id === tier.id
                    )));
                }
            });
            if (this._dirtyFG.size > 0) {
                this._dirtyFG.forEach(fg => {
                    if (!fg.value || deletedTierIds.includes(fg.value.id)) {
                        return;
                    }
                    if (fg.value.id > 0) {
                        obsToSave$.others.push(...this.updateTier(fg, this._eventTiersCopy.find(tierCopy => tierCopy.id === fg.value.id)));
                    }
                });
            }
            return forkJoin([concat(forkJoin(obsToSave$.delete), forkJoin(obsToSave$.create), forkJoin(obsToSave$.others))])
                .pipe(finalize(() => {
                    this.tiersForm.markAsPristine();
                    this._eventTiersCopy = [];
                    this._dirtyFG.clear();
                    this._cancelBtnClickSbj.next();
                }),
                    tap(() => this.#ref.markForCheck()));
        } else {
            this.tiersForm.markAllAsTouched();
            this.#ref.markForCheck();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    deleteEventTier(tier: EventTiers): void {
        let dialog$: Observable<boolean>;
        if (tier.active) {
            dialog$ = this.#msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.DELETE_ACTIVE_EVENT_TIER',
                message: 'EVENTS.TIERS.DELETE_EVENT_WARNING',
                messageParams: { eventTier: tier.name },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            });
        } else {
            dialog$ = this.#msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.DELETE_EVENT_TIER',
                message: 'EVENTS.TIERS.DELETE_EVENT_INFO',
                messageParams: { eventTier: tier.name },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            });
        }
        dialog$.subscribe(success => {
            if (success) {
                const eventTierIndex = this._eventTiersCopy.findIndex(tierCopy => tierCopy.id === tier.id);
                const deletedTier = this._eventTiersCopy.splice(eventTierIndex, 1);
                this._newEventTiers = this._newEventTiers.filter(t => t.id !== tier.id);
                this._deletedEventTiers.push(...deletedTier);
                this.updateEventTiersCopy();
                this._triggerEventTierChange.next([...this._eventTiersCopy]);
                this.tiersForm.markAsDirty();
            }
        });
    }

    tiersTableValue(index: number, rowId: string): string {
        return this.tiersTable.at(index).get([rowId]).value;
    }

    isTdShown(index: number, eventTiers: EventTiers[]): boolean {
        return index === 0 || !(eventTiers?.[index]?.price_type.id === eventTiers[index - 1].price_type.id);
    }

    isLastTier(priceZoneId: number): boolean {
        const totalTiers = this._eventTiersCopy.filter(tierCopy => tierCopy.price_type.id === priceZoneId);
        return totalTiers.length !== 1;
    }

    isNewTier(tierId: number): boolean {
        return tierId > -1;
    }

    onDragChange(checked: boolean): void {
        if (!checked) {
            this.#msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'EVENTS.DRAG_MODAL_OPTION_TITLE',
                message: 'EVENTS.DRAG_MODAL_OPTION_INFO',
                actionLabel: 'FORMS.ACTIONS.YES',
                showCancelButton: true
            })
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(isConfirmed => {
                    if (!isConfirmed) {
                        this.draggableTable.get('enabled').setValue(true);
                    } else {
                        this._eventTiersCopy = this._eventTiersCopy
                            .map(eventTier => ({ ...eventTier, priority: 0 }));
                        this._triggerEventTierChange.next(this._eventTiersCopy);
                        this.tiersForm.markAsDirty();
                    }
                });
        }
    }

    addNewEventTier(row: VMEventTiers): void {
        const tiersFiltered = this.sortVmTierDate(this._eventTiersCopy.filter(eventTier => eventTier.price_type.id === row.price_type.id));
        const newTiersFiltered = this._newEventTiers.filter(eventTier => eventTier.price_type.id === row.price_type.id);
        const lastTierDate = newTiersFiltered.length === 0 ?
            tiersFiltered[tiersFiltered.length - 1].start_date : newTiersFiltered[newTiersFiltered.length - 1].start_date;
        const m = moment(lastTierDate).set({ hour: 0, minute: 0 }).add(1, 'days');
        const lastTierPriority = newTiersFiltered.length === 0 ?
            tiersFiltered[tiersFiltered.length - 1].priority : newTiersFiltered[newTiersFiltered.length - 1].priority;
        const tiersLength = this._newEventTiers.length;
        const newTier = {
            id: tiersLength === 0 ? tiersLength - 1 : this._newEventTiers[tiersLength - 1].id - 1,
            name: '',
            active: false,
            price_type: {
                id: row.price_type.id,
                name: row.price_type.name
            },
            start_date: m.format(),
            price: 0,
            on_sale: false,
            priority: lastTierPriority,
            condition: this.eventTierConditions.date
        } as VMEventTiers;

        this._newEventTiers.push(newTier);
        this.updateEventTiersCopy();
        this._triggerEventTierChange.next([...this._eventTiersCopy, newTier]);
    }

    onListDrop(event: CdkDragDrop<string[]>): void {
        this.tiersForm.markAsDirty();
        const previousItems = this._eventTiersCopy.filter(eventPrice => eventPrice.price_type.id === event.item.data.price_type.id);
        const previousIndexs = previousItems.map(prevTier => this._eventTiersCopy.findIndex(tierCopy => tierCopy.id === prevTier.id));
        previousIndexs.forEach((previousIndex, index) => moveItemInArray(this._eventTiersCopy, previousIndex, event.currentIndex + index));
        this._eventTiersCopy = this._eventTiersCopy
            .map((eventTier, index) => ({ ...eventTier, priority: index }));
        this.updateEventTiersCopy();
        this._triggerEventTierChange.next(this._eventTiersCopy);
    }

    changedCondition(index: number, id: string, condition: EventTierConditions): void {
        const limitCtrl = this.tiersTable.at(index).get(`${id}limit`);
        if (condition === this.eventTierConditions.stockLimit) {
            limitCtrl.enable({ emitEvent: false });
            limitCtrl.setValidators([Validators.required, Validators.min(1)]);
        } else {
            limitCtrl.setValidators([]);
            limitCtrl.disable({ emitEvent: false });
        }
        limitCtrl.markAsTouched();
        limitCtrl.updateValueAndValidity();
    }

    private setTierFormControls(sortedVmEventTiers: EventTiers[]): void {
        this.tiersTable.controls.forEach(control => {
            if (control.dirty) {
                this._dirtyFG.set(control.value.id, control);
            }
        });
        this.tiersForm.removeControl('tiersTable');
        this.tiersForm.addControl('tiersTable', this.#fb.array([]));
        sortedVmEventTiers.forEach(tier => {
            const fgroup = this.#fb.group({
                id: [tier.id],
                [`${tier.id}name`]: [tier.name, [Validators.required]],
                [`${tier.id}price`]: [tier.price, [Validators.required]],
                [`${tier.id}start_date`]: [tier.start_date, [Validators.required]],
                [`${tier.id}condition`]: [{
                    value: !tier.on_sale ? this.eventTierConditions.noSales : tier.condition,
                    disabled: !this.isNewTier(tier.id)
                },
                [Validators.required]],
                [`${tier.id}limit`]: [{
                    value: tier.limit,
                    disabled: tier.condition === this.eventTierConditions.date
                }]
            });
            if (tier.id < 0) {
                fgroup.markAsDirty();
                this.tiersForm.markAsDirty();
            }
            this.tiersTable.push(fgroup);
        });
    }

    private updateEventTiersCopy(): void {
        if (this.tiersForm.dirty) {
            const deletedTierIds = this._deletedEventTiers.map(t => t.id);
            this.tiersTable.value.forEach((tier: EventTiers, index: number) => {
                if (deletedTierIds.includes(tier.id)) {
                    return;
                }
                const actualFGroup = this.tiersTable.controls[index] as UntypedFormGroup;
                if (actualFGroup.valid && actualFGroup.dirty) {
                    const tierCopyIndex = this._eventTiersCopy.findIndex(tierCopy => tierCopy.id === tier.id);
                    const newTierCopyIndex = this._newEventTiers.findIndex(newTier => newTier.id === tier.id);
                    const name = actualFGroup.get(`${tier.id}name`);
                    if (name?.dirty) {
                        tierCopyIndex > -1 ?
                            this._eventTiersCopy[tierCopyIndex].name = tier[`${tier.id}name`] :
                            this._newEventTiers[newTierCopyIndex].name = tier[`${tier.id}name`];
                    }
                    const price = actualFGroup.get(`${tier.id}price`);
                    if (price?.dirty) {
                        tierCopyIndex > -1 ?
                            this._eventTiersCopy[tierCopyIndex].price = tier[`${tier.id}price`] :
                            this._newEventTiers[newTierCopyIndex].price = tier[`${tier.id}price`];
                    }
                    const startDate = actualFGroup.get(`${tier.id}start_date`);
                    if (startDate?.dirty) {
                        tierCopyIndex > -1 ?
                            this._eventTiersCopy[tierCopyIndex].start_date = tier[`${tier.id}start_date`] :
                            this._newEventTiers[newTierCopyIndex].start_date = tier[`${tier.id}start_date`];
                    }
                    const condition = actualFGroup.get(`${tier.id}condition`);
                    if (condition?.dirty) {
                        tierCopyIndex > -1 ?
                            this._eventTiersCopy[tierCopyIndex].condition = tier[`${tier.id}condition`] :
                            this._newEventTiers[newTierCopyIndex].condition = tier[`${tier.id}condition`];
                    }
                    const limit = actualFGroup.get(`${tier.id}limit`);
                    if (limit?.dirty) {
                        tierCopyIndex > -1 ?
                            this._eventTiersCopy[tierCopyIndex].limit = tier[`${tier.id}limit`] :
                            this._newEventTiers[newTierCopyIndex].limit = tier[`${tier.id}limit`];
                    }
                }
            });
        }
    }

    private setVmEventTier(eventTiers: VMEventTiers[]): VMEventTiers[] {
        const sortedTiers = this.sortVmTierDate(eventTiers
            .sort((tierA, tierB) => tierA.active === tierB.active ? 0 : tierA.active ? -1 : 1));
        const priceZoneTiers = [
            ...sortedTiers,
            ...this.removeDuplicatePriceZones(sortedTiers)
        ];
        const isPriorty = eventTiers.some(pz => pz.priority > 0);
        return isPriorty ? this.sortVmTierPriority(priceZoneTiers)
            : this.sortVmTierPriceType(this.sortVmTierDate(priceZoneTiers));
    }

    private setPriortiy(eventTiers: VMEventTiers[], venueTemplatePriceType: VenueTemplatePriceType[], isTrigger = false): VMEventTiers[] {
        const isPriorty = eventTiers.some(pz => pz.priority > 0) || isTrigger;
        return eventTiers.map(eventTier => ({
            ...eventTier, priority: isPriorty ? eventTier.priority : venueTemplatePriceType
                .find(priceType => priceType.id === eventTier.price_type.id).priority
        }));
    }

    private removeDuplicatePriceZones(vmTiers: VMEventTiers[]): VMEventTiers[] {
        return vmTiers.filter((vmTier, index) => vmTiers.findIndex(tier => tier.price_type.id === vmTier.price_type.id) === index);
    }

    private sortVmTierDate(vmTier: VMEventTiers[]): VMEventTiers[] {
        return vmTier.sort((a, b) => moment.utc(a.start_date).diff(moment.utc(b.start_date)));
    }

    private sortVmTierPriceType(vmTier: VMEventTiers[]): VMEventTiers[] {
        return vmTier.sort((a, b) => a.price_type.id - b.price_type.id);
    }

    private sortVmTierPriority(vmTier: VMEventTiers[]): VMEventTiers[] {
        const vmTierDateSort = vmTier
            .sort((a, b) => a.priority - b.priority)
            .reduce((pv, cv) => {
                pv[cv.price_type.id] = pv[cv.price_type.id] || [];
                pv[cv.price_type.id].push(cv);
                return pv;
            }, {});
        return this.flat(Object.values(vmTierDateSort as VMEventTiers)
            .sort((a, b) => a[0].priority - b[0].priority)
            .map((value: VMEventTiers[]) => this.sortVmTierDate(value)));
    }

    private deleteTier(): Observable<boolean>[] {
        return this._deletedEventTiers.filter(eventTier => eventTier.id > 0).map(eventTier =>
            this.#eventTiersService.deleteEventTier(this.eventId, eventTier.id.toString()).pipe(mapTo(this.tiersForm.valid)));
    }

    private addTiers(): Observable<boolean>[] {
        return this._newEventTiers
            .map(tier => {
                const tierForm = this.tiersTable.value.find(t => t.id === tier.id);
                const newEventTier = {
                    name: tierForm[`${tier.id}name`],
                    start_date: tierForm[`${tier.id}start_date`],
                    price_type_id: tier.price_type.id,
                    price: tierForm[`${tier.id}price`]
                } as PostEventTier;
                return this.#eventTiersService.createEventTier(this.eventId, newEventTier).pipe(mapTo(this.tiersForm.valid));
            });
    }

    private updateTier(actualFGroup: UntypedFormGroup, tier: EventTiers): Observable<boolean>[] {
        const obs$ = [];
        const newEventTier = {} as PutEventTier;
        const name = actualFGroup.get(`${tier.id}name`);
        if (name?.dirty) { newEventTier.name = tier.name; }
        const price = actualFGroup.get(`${tier.id}price`);
        if (price?.dirty) { newEventTier.price = tier.price; }
        const startDate = actualFGroup.get(`${tier.id}start_date`);
        if (startDate?.dirty) { newEventTier.start_date = tier.start_date; }
        const limit = actualFGroup.get(`${tier.id}limit`);
        if (limit?.dirty) {
            if (tier.limit.toString() === '') {
                obs$.push(this.#eventTiersService.deleteTierLimit(this.eventId, tier.id.toString()).pipe(mapTo(this.tiersForm.valid)));
            } else {
                newEventTier.limit = tier.limit;
            }
        }
        const condition = actualFGroup.get(`${tier.id}condition`);
        if (condition?.dirty) {
            newEventTier.on_sale = true;
            newEventTier.condition = tier.condition;
        }
        if (newEventTier.condition === this.eventTierConditions.noSales) {
            actualFGroup.get(tier.condition)?.markAsPristine();
            newEventTier.condition = this.eventTierConditions.date;
            newEventTier.on_sale = false;
        }
        obs$.push(this.#eventTiersService.saveEventTier(this.eventId, tier.id.toString(), newEventTier).pipe(mapTo(this.tiersForm.valid)));
        return obs$;
    }

    private updatePriority(): Observable<boolean>[] {
        const priceTypes = this._eventPriceType.map(evPriceType => {
            const actualTier = this._eventTiersCopy.find(evTierCopy => evTierCopy.price_type.id === evPriceType.id);
            return {
                id: evPriceType.id,
                priority: actualTier.priority
            } as VenueTemplatePriceType;
        });
        return priceTypes.length > 1 ?
            priceTypes
                .sort((a, b) => a.priority - b.priority)
                .map((priceType, index) => ({ ...priceType, priority: this.draggableTable.get('enabled').value ? index : 0 }))
                .map(priceType => this.#venueTemplatesService.updateVenueTemplatePriceType(+this._tplId, priceType).pipe(mapTo(true))) :
            priceTypes
                .map(priceType => this.#venueTemplatesService.updateVenueTemplatePriceType(+this._tplId, priceType).pipe(mapTo(true)));

    }

    private flat(arr): VMEventTiers[] {
        return arr.reduce((flat, toFlatten) => flat.concat(Array.isArray(toFlatten) ? this.flat(toFlatten) : toFlatten), []);
    }

    private clearValues(): void {
        this.#eventTiersService.clearEventTiersList();
        this.#venueTemplatesService.clearVenueTemplateList();
        this.#venueTemplatesService.clearVenueTemplatePriceTypes();
    }

}
