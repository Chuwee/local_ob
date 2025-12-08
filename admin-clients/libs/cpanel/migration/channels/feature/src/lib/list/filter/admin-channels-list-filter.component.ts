import { ChannelStatus, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    EntitiesBaseService, EntitiesFilterFields, Entity,
    EntityStatus, GetEntitiesRequest
} from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterItemValue,
    FilterWrapped, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-admin-channels-list-filter',
    templateUrl: './admin-channels-list-filter.component.html',
    styles: `.filter-block-input{ min-width: 300px;}`,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        AsyncPipe,
        MaterialModule,
        SelectServerSearchComponent,
        FlexLayoutModule
    ]
})
export class AdminChannelsListFilterComponent extends FilterWrapped implements OnInit {
    #fb = inject(UntypedFormBuilder);
    #entitiesService = inject(EntitiesBaseService);
    #translate = inject(TranslateService);

    readonly #statusStructure: Record<string, boolean> = {
        active: false,
        pending: false,
        blocked: false,
        blockedTemporarily: false
    };

    readonly #formStructure: Record<string, string> = {
        entity: null,
        type: null
    };

    readonly moreEntitiesAvailable$ = this.#entitiesService.entityList.getMetadata$()
        .pipe(map(metadata => !metadata || metadata.total > metadata.offset + metadata.limit));

    entities$: Observable<Entity[]>;

    filtersForm: UntypedFormGroup;
    readonly types = Object.values(ChannelType)
        .map(type => ({ id: type, name: `CHANNELS.TYPE_OPTS.${type}` }));

    @Input() canSelectEntity$: Observable<boolean>;

    constructor() {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        const status = this.#fb.group(Object.assign({}, this.#statusStructure));
        this.filtersForm = this.#fb.group(Object.assign({ status }, this.#formStructure));
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this.#entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStatus(),
            this.getFilterType()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        } else if (key === 'STATUS') {
            const statusCheck: string = Object.keys(ChannelStatus).find(channelKey => ChannelStatus[channelKey] === value);
            this.filtersForm.get(`status.${statusCheck}`).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const status: Record<string, boolean> = Object.assign({}, this.#statusStructure);
        const formFields: {
            status: Record<string, boolean>;
            type?: { id: string; name: string };
        } = Object.assign({ status }, this.#formStructure);

        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }

        if (params['status']) {
            params['status'].split(',').forEach((statusValue: ChannelStatus) => {
                const statusKey = Object.keys(ChannelStatus).find(key => ChannelStatus[key] === statusValue) || null;
                if (statusKey) {
                    formFields.status[statusKey] = true;
                }
            });
        }
        return applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id').pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            type: 'CHANNEL_ENTITY',
            fields: [EntitiesFilterFields.name],
            status: [EntityStatus.active]
        };
        if (!nextPage) {
            this.#entitiesService.entityList.load({ ...request, q });
        } else {
            this.#entitiesService.entityList.loadMore({ ...request, q });
        }
    }

    private getFilterEntity(): FilterItem {
        const filterItem = new FilterItem('ENTITY', this.#translate.instant('CHANNELS.ENTITY'));
        const value = this.filtersForm.value.entity;
        if (value) {
            filterItem.values = [new FilterItemValue(value.id, value.name)];
            filterItem.urlQueryParams['entity'] = value.id;
        }
        return filterItem;
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translate.instant('CHANNELS.STATUS'));
        const value = this.filtersForm.value.status;
        const channelStatusAux = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (channelStatusAux.length > 0) {
            filterItem.values = channelStatusAux.map(statusCheck =>
                new FilterItemValue(
                    ChannelStatus[statusCheck],
                    this.#translate.instant(`CHANNELS.STATUS_OPTS.${ChannelStatus[statusCheck]}`)
                ));
            filterItem.urlQueryParams['status'] = channelStatusAux.map(statusCheck => ChannelStatus[statusCheck]).join(',');
        }
        return filterItem;
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('TYPE')
            .labelKey('CHANNELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }
}
