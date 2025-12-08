import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-promotion-tpls-list-filter',
    templateUrl: './promotion-tpls-list-filter.component.html',
    styleUrls: ['./promotion-tpls-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TranslatePipe, MaterialModule, AsyncPipe,
        SelectSearchComponent, FlexLayoutModule, EllipsifyDirective
    ]
})
export class PromotionTemplatesListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formStructure = {
        entity: null,
        type: null
    };

    entities$: Observable<Entity[]>;
    types = Object.keys(PromotionType)
        .map(type => ({ id: type, name: `EVENTS.PROMOTIONS.TYPE_OPTS.${PromotionType[type]}.LONG` }));

    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesService: EntitiesBaseService,
        private _translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterType()
        ];
    }

    removeFilter(key: string): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);
        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('EVENTS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('TYPE')
            .labelKey('EVENTS.PROMOTIONS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }
}
