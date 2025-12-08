import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { B2bConditions, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityFilterButtonComponent, EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import {
    EntitiesBaseService, EntitiesFilterFields, Entity, GetEntitiesRequest
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, of, shareReplay, Subject, switchMap, tap, throwError } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { B2bConditionsFormComponent } from '../generic-conditions-form/b2b-conditions-form.component';

@Component({
    selector: 'app-entity-b2b-conditions',
    templateUrl: './entity-b2b-conditions.component.html',
    styleUrls: ['./entity-b2b-conditions.component.scss'],
    imports: [
        AsyncPipe,
        TranslatePipe,
        ReactiveFormsModule,
        EntityFilterModule,
        FlexLayoutModule,
        FormContainerComponent,
        B2bConditionsFormComponent,
        EmptyStateComponent,
        MatProgressSpinner,
        MatIcon,
        MatButton
    ],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityB2bConditionsComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _entityId: number;
    @ViewChild(B2bConditionsFormComponent)
    private _conditionsFormComponent: B2bConditionsFormComponent;

    private readonly _fb = inject(FormBuilder);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _b2bSrv = inject(B2bService);
    private readonly _msgDialogService = inject(MessageDialogService);
    private readonly _ephemeralMsgSrv = inject(EphemeralMessageService);
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);

    @ViewChild('entityFilterButton') private _entityFilterButton: EntityFilterButtonComponent;

    readonly noEntityConditionsFound$ = this._b2bSrv.getConditions$().pipe(map(conditions => !conditions));
    getConditions$: () => Observable<B2bConditions>;
    readonly form = this._fb.group({});
    readonly entity$: Observable<Entity> = this._entitiesService.getEntity$();

    readonly isInProgress$ = booleanOrMerge([
        this._entitiesService.isEntityLoading$(),
        this._entitiesService.isEntitySaving$(),
        this._b2bSrv.isConditionsInProgress$()
    ]);

    readonly getB2bEntitiesRequest: GetEntitiesRequest = {
        limit: 999,
        offset: 0,
        sort: 'name:asc',
        fields: [EntitiesFilterFields.name],
        b2b_enabled: true
    };

    readonly canReadMultipleEntities$ = this._authSrv.canReadMultipleEntities$();

    readonly isEntitySelected$ = this._authSrv.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultEnt => {
                if (canReadMultEnt) {
                    return this.listFiltersService.onFilterValuesApplied$()
                        .pipe(map(filterItems => !!filterItems?.find(filterItem => filterItem.key === 'ENTITY')?.values?.length));
                } else {
                    return of(true);
                }
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $currencies = toSignal(this._authSrv.getLoggedUser$()
        .pipe(
            filter(Boolean),
            map(user => AuthenticationService.operatorCurrencyCodes(user) ?? [user.currency])
        )
    );

    ngOnInit(): void {
        this.getConditions$ = () => this._b2bSrv.getConditions$();
        this.entity$
            .pipe(takeUntil(this._onDestroy))
            .subscribe((entity: Entity) => {
                if (entity?.id) {
                    this._entityId = entity?.id;
                    this._router.navigate([], {
                        queryParams: { entityId: entity.id },
                        relativeTo: this._route,
                        queryParamsHandling: 'merge'
                    });
                    this._b2bSrv.loadConditions('ENTITY', { entity_id: this._entityId });
                }
            });
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._entityFilterButton
        ]);
    }

    override ngOnDestroy(): void {
        this._entitiesService.clearEntity();
        this._b2bSrv.clearConditions();
        this._onDestroy.next(null);
        this._onDestroy.complete();
        super.ngOnDestroy();
    }

    loadData(filterItems?: FilterItem[]): void {
        const filterEntityItem = filterItems.find(filterItem => filterItem.key === 'ENTITY');
        if (filterEntityItem?.values?.length > 0) {
            this._entitiesService.loadEntity(filterEntityItem.values[0].value);
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this._b2bSrv.loadConditions('ENTITY', { entity_id: this._entityId });
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const conditions = this._conditionsFormComponent.getNormalizedConditions();
            return this._b2bSrv.saveConditions('ENTITY', {
                id: this._entityId,
                conditions
            }).pipe(tap(() => this._ephemeralMsgSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._b2bSrv.loadConditions('ENTITY', { entity_id: this._entityId });
    }

    deleteEntityConditions(): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'PROFESSIONAL_SELLING.DELETE_ENTITY_B2B_CONDITIONS_CONFIRM_MSG',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._b2bSrv.deleteConditions('ENTITY', {
                    entity_id: this._entityId
                }))
            )
            .subscribe(() => {
                this._ephemeralMsgSrv.showDeleteSuccess();
                this._b2bSrv.loadConditions('ENTITY', { entity_id: this._entityId });
            });
    }
}
