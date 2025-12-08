/* eslint-disable @typescript-eslint/dot-notation */

import { Terminal } from '@admin-clients/cpanel-channels-terminals-data-access';
import {
    EntitiesBaseService, EntitiesFilterFields, EntityStatus, GetEntitiesRequest
} from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterWrapped, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of, startWith } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        SelectServerSearchComponent
    ],
    selector: 'app-terminals-list-filter',
    templateUrl: './terminals-list-filter.component.html',
    styleUrls: ['./terminals-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TerminalsListFilterComponent extends FilterWrapped {

    private readonly _fb = inject(FormBuilder);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _translateSrv = inject(TranslateService);

    readonly form = this._fb.group({
        entity: null as IdName,
        licenseEnabled: null as boolean,
        type: null as Terminal['type'] // secret filter, only by url param
    });

    @Output()
    readonly filterActive$ = this.form.valueChanges.pipe(
        startWith(this.form.value),
        map(value => !!Object.values(value).filter(Boolean).length)
    );

    readonly entities$ = this._entitiesSrv.entityList.getData$();

    readonly moreEntitiesAvailable$ = this._entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => !metadata || metadata.total > metadata.offset + metadata.offset));

    constructor() {
        super();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterEnabled(),
            this.getFilterType()
        ]
            .filter(Boolean);
    }

    removeFilter(key: string): void {
        if (key === 'ENTITY') {
            this.form.controls.entity.reset();
        } else if (key === 'ENABLED') {
            this.form.controls.licenseEnabled.reset();
        } else if (key === 'TYPE') {
            this.form.controls.type.reset();
        }
    }

    resetFilters(): void {
        this.form.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const value = this.form.getRawValue();

        if (params['enabled']) {
            value.licenseEnabled = params['enabled'] === String(true);
        } else {
            value.licenseEnabled = null;
        }
        const asyncFields: Observable<unknown>[] = [];
        if (params['entity']) {
            asyncFields.push(applyAsyncFieldOnServerStream$(
                value, 'entity', params['entity'], this._entitiesSrv.getCachedEntities$([Number(params['entity'])])
            ));
        } else {
            asyncFields.push(of(null));
        }
        if (params['type']) {
            value.type = params['type'];
        } else {
            value.type = null;
        }
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.form.patchValue(value, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            q,
            type: 'CHANNEL_ENTITY',
            fields: [EntitiesFilterFields.name],
            status: [EntityStatus.active]
        };
        if (!nextPage) {
            this._entitiesSrv.entityList.load(request);
        } else {
            this._entitiesSrv.entityList.loadMore(request);
        }
    }

    private getFilterEntity(): FilterItem {
        return this.form.value.entity ?
            new FilterItemBuilder(this._translateSrv)
                .key('ENTITY')
                .labelKey('FORMS.LABELS.ENTITY')
                .queryParam('entity')
                .value({ id: String(this.form.value.entity.id), name: this.form.value.entity.name })
                .build()
            : null;
    }

    private getFilterEnabled(): FilterItem {
        return this.form.value.licenseEnabled !== null ?
            new FilterItemBuilder(this._translateSrv)
                .key('ENABLED')
                .labelKey('FORMS.LABELS.STATUS')
                .queryParam('enabled')
                .value({
                    id: String(this.form.value.licenseEnabled),
                    name: this._translateSrv.instant(this.form.value.licenseEnabled ? 'FORMS.LABELS.ACTIVE' : 'FORMS.LABELS.INACTIVE')
                })
                .build()
            : null;
    }

    private getFilterType(): FilterItem {
        return this.form.value.type !== null ?
            new FilterItemBuilder(this._translateSrv)
                .key('TYPE')
                .labelKey('FORMS.LABELS.TYPE')
                .queryParam('type')
                .value({ id: String(this.form.value.type), name: String(this.form.value.type) })
                .build()
            : null;
    }
}

