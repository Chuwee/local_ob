import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { map, Observable, of } from 'rxjs';

@Component({
    selector: 'app-packs-list-filter',
    templateUrl: './packs-list-filter.component.html',
    styleUrls: ['./packs-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, SelectSearchComponent, AsyncPipe, TranslatePipe, MatFormFieldModule, MatDividerModule,
        MatSelectModule, EllipsifyDirective
    ]
})
export class PacksListFilterComponent extends FilterWrapped {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #translate = inject(TranslateService);

    readonly $canSelectEntity = input<boolean>(false, { alias: 'canSelectEntity' });

    readonly $entities = computed(() => {
        if (this.$canSelectEntity()) {
            this.#entitiesSrv.entityList.load({
                limit: 999,
                sort: 'name:asc',
                fields: [EntitiesFilterFields.name]
            });
            return this.#entitiesSrv.entityList.getData$();
        } else {
            return of([] as Entity[]);
        }
    });

    readonly statusOptions = ['ACTIVE', 'INACTIVE'];
    readonly filtersForm = this.#fb.group({
        entity: [null as { id: string; name: string }],
        status: [null as string]
    });

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStatus()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.controls.entity.reset();
        } else if (key === 'STATUS') {
            this.filtersForm.controls.status.reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = {
            status: null
        };
        if (params['status']) {
            formFields.status = params['status'];
        }
        return applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.$entities(), 'id').pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterEntity(): FilterItem {
        const value = this.filtersForm.value.entity;
        const filterItem = new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(value ? { id: value.id, name: value.name } : null);
        return filterItem.build();
    }

    private getFilterStatus(): FilterItem {
        const value = this.filtersForm.value.status;
        const filterItem = new FilterItemBuilder(this.#translate)
            .key('STATUS')
            .labelKey('PACK.STATUS')
            .queryParam('status')
            .value(value ? { id: value, name: `PACK.STATUS_OPTS.${value}` } : null)
            .translateValue();
        return filterItem.build();
    }

}
