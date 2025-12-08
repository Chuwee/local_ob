import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, MessageDialogService, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, filter, first, map, Observable, startWith, switchMap, throwError } from 'rxjs';

@Component({
    imports: [
        TranslatePipe, AsyncPipe, MatIcon, MatIconButton, MatTooltip, MatExpansionPanel, MatExpansionPanelHeader,
        MatExpansionPanelTitle, SearchablePaginatedSelectionModule, FormContainerComponent, ReactiveFormsModule
    ],
    selector: 'app-entity-managed-entities',
    templateUrl: './entity-managed-entities.component.html',
    styleUrls: ['./entity-managed-entities.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityManagedEntitiesComponent implements OnInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly #showSelectedEntitiesOnly = new BehaviorSubject(false);
    #filters: GetEntitiesRequest;
    #entityId: number;

    readonly PAGE_LIMIT = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly form = this.#fb.group({
        entities: this.#fb.control<Entity[]>([])
    });

    readonly availableEntitiesMetadata$ = this.#entitiesSrv.entityList.getMetadata$();
    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.entityList.inProgress$(),
        this.#entitiesSrv.managedEntitiesList.inProgress$()
    ]);

    readonly isHandsetOrTablet$ = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly showSelectedEntitiesOnly$ = this.#showSelectedEntitiesOnly.asObservable();

    readonly entitiesList$ = this.showSelectedEntitiesOnly$
        .pipe(
            switchMap(isActive => isActive ?
                this.form.controls.entities.valueChanges.pipe(startWith(this.form.value.entities))
                : this.#entitiesSrv.entityList.getData$()
            )
        );

    ngOnInit(): void {
        this.load();
        this.#entitiesSrv.managedEntitiesList.getData$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(entities => {
                this.form.controls.entities.markAsPristine();
                this.form.controls.entities.setValue(entities);
            });
        this.#entitiesSrv.getEntity$()
            .pipe(first(entity => !!entity))
            .subscribe(entity => {
                this.#entityId = entity.id;
                this.#entitiesSrv.managedEntitiesList.load(entity.id, { offset: 0, limit: 999 });
            });

    }

    load(): void {
        this.loadAvailableEntities(this.#filters?.offset, this.#filters?.q);
        if (this.#entityId) {
            this.#entitiesSrv.managedEntitiesList.load(this.#entityId, { offset: 0, limit: 999 });
        }
    }

    loadAvailableEntities(offset = 0, q?: string): void {
        this.#entitiesSrv.entityList.load({ sort: 'name:asc', limit: this.PAGE_LIMIT, offset, q });
    }

    changeVisibleList(): void {
        this.#showSelectedEntitiesOnly.next(!this.#showSelectedEntitiesOnly.value);
    }

    filterChangeHandlerAvailableEntities(filters: GetEntitiesRequest): void {
        if (this.#showSelectedEntitiesOnly.value) {
            this.#showSelectedEntitiesOnly.next(false);
        }
        this.#filters = filters;
        this.loadAvailableEntities(filters.offset, filters.q);
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMsgSrv.showSaveSuccess();
                this.load();
            }, error: () => this.load()
        });
    }

    save$(): Observable<void> {
        if (this.form.value.entities?.length) {
            return this.#entitiesSrv.managedEntitiesList.update$(this.#entityId,
                this.form.controls.entities.value.map(value => ({ id: value.id, name: value.name })));
        }
        return this.#msgDialogSrv.showAlert({
            title: 'ENTITY.GENERAL_DATA.MANAGED_ENTITIES_ONE_REQUIRED.TITLE',
            message: 'ENTITY.GENERAL_DATA.MANAGED_ENTITIES_ONE_REQUIRED.MESSAGE',
            actionLabel: 'FORMS.ACTIONS.OK'
        }).pipe(switchMap(() => throwError(() => ({}))));
    }

    cancel(): void {
        this.load();
    }
}
