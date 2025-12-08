import {
    CollectivesService, CollectiveExternalValidatorAuthType, CollectiveEntity
} from '@admin-clients/cpanel/collectives/data-access';
import {
    ObMatDialogConfig,
    pageSize, SearchablePaginatedSelectionLoadEvent
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil } from 'rxjs/operators';
import { ValidatorAuthDialogComponent } from './validator-auth-edit/collective-entity-validator-auth-edit-dialog.component';

@Component({
    selector: 'app-collective-entity',
    templateUrl: './collective-entity.component.html',
    styleUrls: ['./collective-entity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CollectiveEntityComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _collectiveSrv = inject(CollectivesService);
    private readonly _matDialog = inject(MatDialog);

    private readonly _filter = new BehaviorSubject({
        offset: 0,
        q: null as string,
        selectedOnly: false
    });

    readonly selectedOnly$ = this._filter.asObservable().pipe(map(filter => filter.selectedOnly));

    readonly collectiveEntities$ = combineLatest([
        this._collectiveSrv.getCollectiveEntities$().pipe(filter(Boolean)),
        this._filter.asObservable()
    ])
        .pipe(
            map(([collectiveEntities, filter]) => {
                if (filter.selectedOnly) {
                    collectiveEntities = collectiveEntities.filter(entity =>
                        !!this.entitiesForm.value.find(collectiveEntity => collectiveEntity.id === entity.id));
                }
                if (filter.q?.length) {
                    collectiveEntities = collectiveEntities.filter(collectiveEntity =>
                        collectiveEntity.name.toLowerCase().includes(filter.q.toLowerCase()));
                }
                return {
                    data: collectiveEntities.slice(filter.offset, filter.offset + pageSize),
                    metadata: { total: collectiveEntities.length, offset: filter.offset, limit: pageSize }
                };
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly totalEntities$ = this.collectiveEntities$.pipe(map(ce => ce.metadata?.total));
    readonly collectiveEntityList$ = this.collectiveEntities$.pipe(map(ce => ce.data));
    readonly collectiveEntityMetadata$ = this.collectiveEntities$.pipe(map(ce => ce.metadata));

    readonly reqInProgress$ = booleanOrMerge([
        this._collectiveSrv.isCollectiveEntitiesLoading$(),
        this._collectiveSrv.isCollectiveEntitiesSaving$()
    ]);

    readonly isExternalUserPass$ = this._collectiveSrv.getCollective$()
        .pipe(
            filter(Boolean),
            map(collective => collective.external_validator?.external_validator_authentication
                === CollectiveExternalValidatorAuthType.userPassword
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    @Input() entitiesForm: FormControl<CollectiveEntity[]>;

    ngOnInit(): void {
        this._collectiveSrv.getCollective$()
            .pipe(first(Boolean))
            .subscribe(collective => this._collectiveSrv.loadCollectiveEntities(collective.id));

        this._collectiveSrv.getCollectiveEntities$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(collectiveEntities => this.updateForm(collectiveEntities));
    }

    ngOnDestroy(): void {
        this._collectiveSrv.clearCollectiveEntities();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    changeSelectedOnly(): void {
        this._filter.next({
            ...this._filter.value,
            selectedOnly: !this._filter.value.selectedOnly,
            offset: 0
        });
    }

    loadPagedCollectiveEntities({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this._filter.next({
            ...this._filter.value,
            q,
            offset
        });
    }

    openValidatorAuthEditDialog(collectiveEntity: CollectiveEntity): void {
        this._collectiveSrv.getCollective$()
            .pipe(
                first(Boolean),
                switchMap(collective =>
                    this._matDialog.open(ValidatorAuthDialogComponent, new ObMatDialogConfig({
                        collectiveId: collective.id,
                        collectiveEntityId: collectiveEntity.id,
                        validatorAuthUser: collectiveEntity.external_validator_properties?.user || null,
                        validatorAuthPwd: collectiveEntity.external_validator_properties?.password || null
                    })).beforeClosed()
                ),
                filter(Boolean),
                switchMap(() => this._collectiveSrv.getCollective$()),
                first(Boolean)
            )
            .subscribe(collective => this._collectiveSrv.loadCollectiveEntities(collective.id));
    }

    private updateForm(collectiveEntities: CollectiveEntity[]): void {
        this.entitiesForm.reset();
        if (collectiveEntities?.length) {
            this.entitiesForm.patchValue(
                collectiveEntities.filter(collectiveEntity => collectiveEntity.enabled)
            );
            this.entitiesForm.enable();
        } else {
            this.entitiesForm.disable();
        }
        this.entitiesForm.markAsPristine();
    }
}
