import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityFilterButtonComponent, EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import { EntitiesBaseService, EntitiesFilterFields, Entity, GetEntitiesRequest, PutEntity } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateComponent, EphemeralMessageService, FilterItem,
    ListFilteredComponent, ListFiltersService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map, Observable, of, shareReplay, Subject, switchMap, tap, throwError } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-notification-setting-details',
    templateUrl: './notification-setting-details.component.html',
    styleUrls: ['./notification-setting-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    imports: [
        FlexModule, MaterialModule, EntityFilterModule, ExtendedModule, EmptyStateComponent, FormContainerComponent,
        FormsModule, ReactiveFormsModule, FormControlErrorsComponent, NgIf, AsyncPipe, TranslatePipe
    ]
})
export class NotificationSettingDetailsComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesBaseService);

    @ViewChild('entityFilterButton')
    private _entityFilterButton: EntityFilterButtonComponent;

    readonly entity$: Observable<Entity> = this._entitiesService.getEntity$();

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this._entitiesService.isEntityLoading$(),
        this._entitiesService.isEntitySaving$()
    ]);

    readonly entitiesRequest: GetEntitiesRequest = {
        limit: 999,
        offset: 0,
        sort: 'name:asc',
        fields: [EntitiesFilterFields.name],
        allow_massive_email: true
    };

    readonly isEntitySelected$ = this._auth.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultEnt =>
                !canReadMultEnt ?
                    of(true) :
                    this.listFiltersService.onFilterValuesApplied$()
                        .pipe(map(filterItems => !!filterItems?.find(filterItem => filterItem.key === 'ENTITY')?.values?.length))
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly canReadMultipleEntities$ = this._auth.canReadMultipleEntities$();

    readonly form = this._fb.group({
        send_limit: [{ value: null, disabled: !this.canReadMultipleEntities$ }, [Validators.required, Validators.min(0)]]
    });

    ngOnInit(): void {
        this.entity$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(entity => this.form.patchValue({ send_limit: entity?.settings?.notifications?.email?.send_limit }));
    }

    ngAfterViewInit(): void {
        super.initListFilteredComponent([this._entityFilterButton]);
    }

    override ngOnDestroy(): void {
        this._entitiesService.clearEntity();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadData(filterItems?: FilterItem[]): void {
        const filterEntityItem = filterItems.find(filterItem => filterItem.key === 'ENTITY');
        if (filterEntityItem?.values?.length > 0) {
            this._entitiesService.loadEntity(filterEntityItem.values[0].value);
        }
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this._entitiesService.getEntity$()
                .pipe(
                    take(1),
                    switchMap(entity => {
                        const updatedEntity: PutEntity = {
                            settings: { notifications: { email: { send_limit: this.form.value.send_limit } } }
                        };
                        return this._entitiesService.updateEntity(entity.id, updatedEntity)
                            .pipe(tap(() => {
                                this._ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                                this._entitiesService.loadEntity(entity.id);
                                this.form.markAsPristine();
                            }));
                    })
                );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._entitiesService.getEntity$()
            .pipe(take(1))
            .subscribe(entity => this._entitiesService.loadEntity(entity.id));
    }
}
