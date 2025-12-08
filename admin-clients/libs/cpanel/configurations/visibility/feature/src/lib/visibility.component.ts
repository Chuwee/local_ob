import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    VisibilityDestinyEntityType, EntitiesService, OriginEntityVisibility
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, SelectOption, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { Validators, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, Subject, takeUntil, throwError, map, of, tap } from 'rxjs';
import { VisibilityConfigurationEntitiesComponent } from './configuration-entities/visibility-configuration-entities.component';

@Component({
    selector: 'app-visibility',
    imports: [
        ReactiveFormsModule, FormContainerComponent, TranslatePipe, MaterialModule, SelectSearchComponent,
        FormControlErrorsComponent, SelectServerSearchComponent, VisibilityConfigurationEntitiesComponent,
        AsyncPipe, NgIf, NgForOf, FlexLayoutModule
    ],
    templateUrl: './visibility.component.html',
    styleUrls: ['./visibility.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VisibilityComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _entitiesSrv = inject(EntitiesService);
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _fb = inject(FormBuilder);
    private readonly PAGE_LIMIT = 100;

    originForm = this._fb.group({
        operator_id: [null as number, Validators.required],
        entity: [{ value: null as SelectOption, disabled: true }, [Validators.required]]
    });

    form = this._fb.group({
        type: [{ value: null as VisibilityDestinyEntityType, disabled: true }, Validators.required]
    });

    reqInProgress$ = booleanOrMerge([
        this._operatorsSrv.operators.loading$(),
        this._entitiesSrv.entityList.inProgress$(),
        this._entitiesSrv.entityVisibility.inProgress$()
    ]);

    operators$ = this._operatorsSrv.operators.getData$().pipe(filter(Boolean), takeUntil(this._onDestroy));

    entities$ = this._entitiesSrv.entityList.getData$().pipe(filter(Boolean), takeUntil(this._onDestroy));

    moreEntitiesAvailable$ = this._entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

    visibilityType = VisibilityDestinyEntityType;
    visibilityTypesList = Object.values(this.visibilityType);
    visibility$: Observable<OriginEntityVisibility>;

    ngOnInit(): void {
        this._operatorsSrv.operators.load({ limit: 999, offset: 0, fields: ['name'] });

        this.originForm.controls.operator_id.valueChanges
            .pipe(takeUntil(this._onDestroy), filter(Boolean))
            .subscribe(operatorId => {
                this.originForm.controls.entity.enable();
                this.originForm.controls.entity.reset(null);
                this._entitiesSrv.entityList.load({ limit: 100, sort: 'name:asc', offset: 0, operator_id: operatorId });
            });

        this.originForm.controls.entity.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(entity => {
                const typeCtrl = this.form.controls.type;
                if (entity) {
                    typeCtrl.reset(null);
                    typeCtrl.enable();
                    this._entitiesSrv.entityVisibility.load(+entity.id);
                } else {
                    typeCtrl.reset(null);
                    typeCtrl.disable();
                }
            });

        this._entitiesSrv.entityVisibility.get$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(visibility => {
                this.visibility$ = of(visibility);
                this.form.controls.type.reset(visibility.type);
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesSrv.entityVisibility.clear();
    }

    cancel(): void {
        this._entitiesSrv.entityVisibility.load(+this.originForm.controls.entity.value.id);
    }

    save(): void {
        this.save$().subscribe(() => this._entitiesSrv.entityVisibility.load(+this.originForm.controls.entity.value.id));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { type, ...visibilities } = this.form.value;
            const visibility: OriginEntityVisibility = type !== 'FILTERED' ? { type } : { type, ...visibilities };

            return this._entitiesSrv.entityVisibility.update(+this.originForm.controls.entity.value.id, visibility)
                .pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            return throwError(() => 'invalid form');
        }
    }

    loadEntities(q: string, next = false): void {
        this._entitiesSrv.loadServerSearchEntityList({
            limit: this.PAGE_LIMIT,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            q,
            operator_id: this.originForm.controls.operator_id.value
        }, next);
    }
}
