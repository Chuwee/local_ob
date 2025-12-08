import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import {
    GetEntitiesRequest, PutEntity, EntitiesBaseService, Entity, aggDataEntitiesLicenses
} from '@admin-clients/shared/common/data-access';
import {
    AggregatedDataComponent, EphemeralMessageService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AggregatedData } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, forkJoin, map, Observable, Subject, tap, throwError } from 'rxjs';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-entities-licenses-list',
    templateUrl: './entities-licenses-list.component.html',
    styleUrls: ['./entities-licenses-list.component.scss'],
    imports: [
        MaterialModule, SearchablePaginatedSelectionModule, FormContainerComponent, ReactiveFormsModule, AggregatedDataComponent,
        TranslatePipe, FlexLayoutModule, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntitiesLicensesComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _filters: GetEntitiesRequest = { limit: PAGE_SIZE };

    readonly pageSize = PAGE_SIZE;

    form: UntypedFormGroup;
    entitiesForm: UntypedFormGroup;
    reqInProgress$: Observable<boolean>;
    entities$: Observable<Entity[]>;
    entitiesLicensesAggData$: Observable<AggregatedData>;
    entitiesMetadata$: Observable<Metadata>;
    aggDataEntitiesLicenses = aggDataEntitiesLicenses;
    columns = ['name', 'operator', 'basic', 'advanced'];

    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _ephemeralSrv: EphemeralMessageService,
        private _entitiesSrv: EntitiesBaseService,
        private _fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this._entitiesSrv.entitiesUsersLimits.load();
        this.form = this._fb.group({});
        this.model();
    }

    ngOnDestroy(): void {
        this._entitiesSrv.entitiesUsersLimits.clear();
        this._entitiesSrv.entityList.clear();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadList(this._filters);
            this._entitiesSrv.entitiesUsersLimits.load();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            Object.keys(this.entitiesForm.controls).forEach(id => {
                const entityCtrl = this.entitiesForm.get(id);
                if (entityCtrl.dirty) {
                    const updatedEntity: PutEntity = {
                        settings: {
                            bi_users: {
                                basic_permissions_limit: Number(entityCtrl.value.basic),
                                advanced_permissions_limit: Number(entityCtrl.value.advanced)
                            }
                        }
                    };
                    obs$.push(this._entitiesSrv.updateEntity(Number(id), updatedEntity));
                }
            });
            return forkJoin(obs$)
                .pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._entitiesSrv.entityList.load(this._filters);
    }

    loadList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, q: q?.length ? q : null, include_entity_admin: true };
        this._entitiesSrv.entityList.load(this._filters);
    }

    private model(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._entitiesSrv.entityList.inProgress$(),
            this._entitiesSrv.isEntitySaving$(),
            this._entitiesSrv.entitiesUsersLimits.inProgress$()
        ]);

        this.entities$ = this._entitiesSrv.entityList.getData$()
            .pipe(
                filter(list => !!list),
                tap(entities => {
                    this.entitiesForm = this._fb.group(
                        entities.reduce<Record<string, UntypedFormGroup>>((acc, entity) =>
                        (acc[entity.id] = this._fb.group({
                            basic: entity.settings?.bi_users?.basic_permissions_limit,
                            advanced: entity.settings?.bi_users?.advanced_permissions_limit
                        }), acc), {})
                    );
                    this.form.setControl('entities', this.entitiesForm);
                    this.form.markAsPristine();
                })
            );

        this.entitiesMetadata$ = this._entitiesSrv.entityList.getMetadata$()
            .pipe(filter(md => !!md));

        this.entitiesLicensesAggData$ = this._entitiesSrv.entitiesUsersLimits.getEntitiesMetadata$();
    }

}
