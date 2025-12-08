import { B2bApi, B2bClientReduced, B2bService, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import {
    PutEventChannelB2bAssignations, QuotaAssignations, QuotaAssignationsType
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { PROFESSIONAL_SELLING_SERVICE } from '@admin-clients/cpanel/promoters/shared/data-access';
import {
    EphemeralMessageService, PaginatedSelectionLoadEvent,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { Id, PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, TrackByFunction, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatLabel } from '@angular/material/form-field';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, Subject, throwError } from 'rxjs';
import { debounceTime, filter, first, map, shareReplay, startWith, switchMap, tap } from 'rxjs/operators';
import { ProfessionalSellingFiltersComponent } from './filters/promoters-professional-selling-filters.component';
import { Counter } from './model/counter';

const PAGE_SIZE = 8;

@Component({
    selector: 'app-promoters-professional-selling',
    templateUrl: './promoters-professional-selling.component.html',
    styleUrls: ['./promoters-professional-selling.component.scss'],
    providers: [B2bService, B2bApi, B2bState],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, NgIf, ReactiveFormsModule, TranslatePipe, FormContainerComponent,
        SearchablePaginatedSelectionModule, ProfessionalSellingFiltersComponent, ArchivedEventMgrComponent,
        MatRadioModule, MatLabel, MatCheckboxModule, MatTableModule, MatSpinner
    ]
})
export class PromotersProfessionalSellingComponent implements OnInit, OnDestroy, WritingComponent {

    readonly #profesionalSellingSrv = inject(PROFESSIONAL_SELLING_SERVICE);
    readonly #b2bService = inject(B2bService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #filters: PageableFilter = { limit: PAGE_SIZE };
    readonly #refreshClientsList = new Subject<void>();
    readonly #indexQuotaMap = new Map<number, number>();
    readonly #quotaIndexMap = new Map<number, number>();
    readonly #clientsCounters = new Map<number, BehaviorSubject<Counter>>();
    readonly #quotasCounters = new Map<number, BehaviorSubject<Counter>>();

    #contextId: number;
    #channelId: number;

    readonly #filteredClients$ = this.#refreshClientsList.pipe(
        switchMap(() => this.clients$),
        map(clients => this.#filterClientsBySelected([...clients])),
        map(clients => clients.map(client => ({ ...client, totals$: this.#clientTotals$(client.id) }))),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly context = this.#profesionalSellingSrv.context;
    readonly loading = new BehaviorSubject(false);
    readonly pageSize = PAGE_SIZE;
    readonly quotaAssignationsTypes = QuotaAssignationsType;

    readonly form = this.#fb.group({
        type: null as QuotaAssignationsType,
        clients: this.#fb.record<FormArray<FormControl<boolean>>>({})
    });

    readonly filters = this.#fb.group({
        clients: this.#fb.nonNullable.control<number[]>([]),
        quotas: this.#fb.nonNullable.control<number[]>([])
    });

    readonly $loadingOrSaving = toSignal(booleanOrMerge([
        this.#b2bService.b2bClientsList.loading$(),
        this.#profesionalSellingSrv.channel.inProgress$(),
        this.#profesionalSellingSrv.isB2bAssignationsInProgress$(),
        this.loading.asObservable()
    ]));

    readonly filteredPagedClients$ = this.#filteredClients$
        .pipe(
            map(clients => {
                const page = this.#filters.offset / this.#filters.limit, pageSize = this.pageSize;
                return clients?.slice(page * pageSize, (page + 1) * pageSize);
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly metadata$ = this.#filteredClients$
        .pipe(
            map(filteredClients => ({
                offset: this.#filters.offset,
                limit: Math.min(this.#filters.limit, filteredClients.length),
                total: filteredClients.length
            })),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly quotas$ = this.#profesionalSellingSrv.channel.get$()
        .pipe(
            filter(Boolean),
            map(({ quotas }) => quotas),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly idNameQuotas$ = this.quotas$.pipe(map(quotas => quotas.map(({ id, description }) => ({ id, name: description }))));

    readonly assignations$ = this.#profesionalSellingSrv.channel.get$()
        .pipe(
            filter(Boolean),
            // eslint-disable-next-line @typescript-eslint/naming-convention
            tap(({ event, channel, season_ticket }) => {
                this.#contextId = event?.id || season_ticket?.id;
                this.#channelId = channel.id;
            }),
            switchMap(() => {
                this.#profesionalSellingSrv.clearB2bAssignations();
                this.#profesionalSellingSrv.loadB2bAssignations(this.#contextId, this.#channelId);
                return this.#profesionalSellingSrv.getB2bAssignations$();
            }),
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly clients$ = this.#b2bService.b2bClientsList.getList$()
        .pipe(
            filter(Boolean),
            map(clients => clients.map(client => ({ ...client, id: client.client_id }))),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly columns$ = combineLatest([
        this.filters.controls.quotas.valueChanges.pipe(
            startWith(this.filters.controls.quotas.value),
            map(quotas => quotas?.length > 0 ? quotas.map(quota => `${quota}`) : null)
        ),
        this.quotas$.pipe(
            startWith([]),
            map(quotas => quotas?.length > 0 ? quotas.map(({ id }) => `${id}`) : null)
        )
    ]).pipe(
        map(([filtered, quotas]) => filtered || quotas || []),
        map(quotas => ['client', ...quotas]),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly data$ = combineLatest([this.quotas$, this.clients$, this.assignations$])
        .pipe(
            filter(values => values.every(Boolean)),
            tap(([quotas, clients, { assignations, type }]) => {
                this.loading.next(true);

                this.form.reset();
                this.form.controls.type.setValue(type, { onlySelf: true });

                const clientsCtrl = this.form.controls.clients;

                clients?.forEach(client => {

                    const clientAssignations = assignations?.filter(assignation =>
                        assignation.all_clients || !!assignation.clients?.find(elem => elem.id === client.id)
                    );

                    const { selected, ctrls } = quotas.reduce<{ ctrls: FormControl<boolean>[]; selected: number[] }>(
                        (acc, quota) => {
                            const checked = !!clientAssignations?.find(elem => elem.quota.id === quota.id);
                            acc.ctrls.push(new FormControl(checked));
                            checked && acc.selected.push(quota.id);
                            return acc;
                        },
                        { ctrls: [], selected: [] }
                    );

                    const counter = new Counter({ total: quotas.length, ids: new Set(selected) });

                    this.#clientsCounters.set(client.id, new BehaviorSubject(counter));

                    clientsCtrl.controls[client.id.toString()]?.clear();
                    clientsCtrl.setControl(client.id?.toString(), new FormArray(ctrls));
                });
            }),
            map(([quotas, clients, { assignations }]) => quotas.map((quota, i) => {

                // set quota id to index map (optimization)
                this.#indexQuotaMap.set(i, quota.id);
                this.#quotaIndexMap.set(quota.id, i);

                const assignation = assignations?.find(assign => assign.quota.id === quota.id);
                const all = assignation?.all_clients;
                const selected = (all ? clients : assignation?.clients || []).map(client => client.id);
                const counter = new Counter({ total: clients.length, ids: new Set(selected) });

                this.#quotasCounters.set(quota.id, new BehaviorSubject(counter));

                this.loading.next(false);

                return {
                    ...quota,
                    totals$: this.#quotaTotals$(quota.id)
                };
            })),
            tap(() => this.#refreshClientsList.next()),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly trackById: TrackByFunction<{ id: number }> = (_, item) => item.id;

    ngOnInit(): void {
        this.#profesionalSellingSrv.getEntityId()
            .pipe(first(Boolean))
            .subscribe(entityId =>
                this.#b2bService.b2bClientsList.loadAll({ entity_id: entityId, sort: 'name:asc' })
            );

        this.filters.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(() =>
            this.filterChangeHandler({ limit: this.#filters.limit, offset: 0 })
        );
    }

    ngOnDestroy(): void {
        this.#refreshClientsList.complete();
        this.#profesionalSellingSrv.clearB2bAssignations();
        this.#b2bService.b2bClientsList.clear();
    }

    filterChangeHandler({ limit, offset }: PaginatedSelectionLoadEvent): void {
        Object.assign(this.#filters, { limit, offset });
        this.#refreshClientsList.next(null);
    }

    cancel(): void {
        this.#reloadModel();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { type, ...values } = this.form.value;
            const req: PutEventChannelB2bAssignations = { type };
            let obs: Observable<PutEventChannelB2bAssignations['assignations']> = of(null);

            if (type === QuotaAssignationsType.specific) {
                obs = combineLatest([this.clients$, this.quotas$]).pipe(
                    first(),
                    map(([clients, quotas]) =>
                        quotas.map((quota, index) => {
                            const allClients = clients.every(client => values.clients[client.id][index]);
                            const assignation: QuotaAssignations<Id> = {
                                all_clients: allClients,
                                quota: { id: quota.id }
                            };
                            if (!allClients) {
                                assignation.clients = clients
                                    .filter(client => values.clients[client.id][index])
                                    .map(client => ({ id: client.id }));
                            }
                            return assignation;
                        })
                    )
                );
            }

            return obs.pipe(
                switchMap(assignations => {
                    if (assignations) {
                        req.assignations = assignations;
                    }
                    return this.#profesionalSellingSrv.updateB2bAssignations(this.#contextId, this.#channelId, req);
                }),
                tap(() => {
                    this.#ephemeralSrv.showSaveSuccess();
                    this.#reloadModel();
                })
            );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    onQuotaCheckboxClick(checked: boolean, quotaId: number): void {
        const index = this.#quotaIndexMap.get(quotaId);
        this.#getFilteredFormControlsByQuotaIndex(index).forEach(({ ctrl, clientId }) => {
            ctrl.setValue(checked);
            this.#modifyCounter(checked, quotaId, clientId);
        });
        this.form.markAsDirty({ onlySelf: true });
    }

    onClientCheckboxClick(checked: boolean, clientId: number): void {
        this.#getFilteredFormControlsByClient(clientId).forEach(({ ctrl, quotaId }) => {
            ctrl.setValue(checked);
            this.#modifyCounter(checked, quotaId, clientId);
        });
        this.form.markAsDirty({ onlySelf: true });
    }

    onCheckboxClick(checked: boolean, clientId: number, quotaId: number): void {
        this.#modifyCounter(checked, quotaId, clientId);
    }

    #modifyCounter(increase: boolean, quotaId: number, clientId: number): void {
        const [quotas, clients] = [this.#quotasCounters.get(quotaId), this.#clientsCounters.get(clientId)];
        quotas.next(increase ? quotas.value.add(clientId) : quotas.value.remove(clientId));
        clients.next(increase ? clients.value.add(quotaId) : clients.value.remove(quotaId));
    }

    readonly #quotaTotals$ = (quotaId: number): Observable<ReturnType<Counter['checkbox']>> => {
        const ctrl = this.filters.controls.clients;
        return combineLatest([
            ctrl.valueChanges.pipe(startWith(ctrl.value)),
            this.#quotasCounters.get(quotaId)
        ]).pipe(
            debounceTime(200),
            map(([clients, counter]) => (clients?.length ? counter.filter(clients) : counter).checkbox())
        );
    };

    readonly #clientTotals$ = (clientId: number): Observable<ReturnType<Counter['checkbox']>> => {
        const ctrl = this.filters.controls.quotas;
        return combineLatest([
            ctrl.valueChanges.pipe(startWith(ctrl.value)),
            this.#clientsCounters.get(clientId)
        ]).pipe(
            debounceTime(100),
            map(([quotas, counter]) => quotas?.length ? counter.filter(quotas) : counter),
            map(counter => counter.checkbox())
        );

    };

    #reloadModel(): void {
        this.form.markAsPristine();
        this.#profesionalSellingSrv.loadB2bAssignations(this.#contextId, this.#channelId);
    }

    #filterClientsBySelected = (clients: B2bClientReduced[]): B2bClientReduced[] => {
        const filteredClients = this.filters.controls.clients?.value;
        if (!filteredClients?.length) return clients;
        return clients.filter(client => filteredClients.some(value => value === client.id));
    };

    #getFilteredFormControlsByQuotaIndex(quotaIndex: number): { ctrl: FormControl<boolean>; clientId: number }[] {
        const filtered = this.filters.controls.clients?.value;
        const controls = this.form.controls.clients.controls;
        return Object.keys(controls)
            .filter(key => !filtered?.length || filtered.find(elem => elem.toString() === key))
            .map(key => ({ ctrl: controls[key].at(quotaIndex), clientId: +key }));
    }

    #getFilteredFormControlsByClient(clientId: number): { ctrl: FormControl<boolean>; quotaId: number }[] {
        const filtered = this.filters.controls.quotas?.value;
        const controls = this.form.controls.clients.controls[clientId].controls;
        return controls.filter((_, i) => !filtered?.length || filtered.find(elem => elem === this.#indexQuotaMap.get(i)))
            .map((ctrl, i) => ({ ctrl, quotaId: this.#indexQuotaMap.get(i) }));
    }

}
