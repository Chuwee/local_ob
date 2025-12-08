import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity, EntityStatus, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    ChipsComponent,
    ChipsFilterDirective,
    ContextNotificationComponent,
    DialogSize, EphemeralMessageService,
    FilterItem,
    ListFilteredComponent, ListFiltersService,
    MessageDialogService, ObMatDialogConfig,
    PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent,
    SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, inject, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { NewEntityDialogComponent } from '../create/new-entity-dialog.component';
import { EntitiesFilterComponent } from './filter/entities-filter.component';

const writeRoles = [UserRoles.OPR_MGR];

@Component({
    selector: 'app-entities-list',
    templateUrl: './entities-list.component.html',
    styleUrls: ['./entities-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        MaterialModule,
        SearchInputComponent,
        FlexLayoutModule,
        PaginatorComponent,
        ContextNotificationComponent,
        PopoverFilterDirective,
        PopoverComponent,
        ChipsComponent,
        ChipsFilterDirective,
        RouterLink,
        EntitiesFilterComponent
    ]
})
export class EntitiesListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #route = inject(ActivatedRoute);
    readonly #translate = inject(TranslateService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralService = inject(EphemeralMessageService);

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(EntitiesFilterComponent) private _filterComponent: EntitiesFilterComponent;

    #sortFilterComponent: SortFilterComponent;
    #request: GetEntitiesRequest;

    pageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    entityStatusList = EntityStatus;

    readonly userCanWrite$ = this.#auth.hasLoggedUserSomeRoles$(writeRoles);
    readonly metadata$ = this.#entitiesSrv.entityList.getMetadata$();
    readonly isLoading$ = this.#entitiesSrv.entityList.inProgress$();
    readonly entities$ = this.#entitiesSrv.entityList.getData$()
        .pipe(
            filter(entities => !!entities),
            map(entities => entities
                .map(entity =>
                ({
                    ...entity, types: entity.settings?.types?.map(type =>
                        this.#translate.instant('ENTITY.TYPE_OPTS.' + type))
                })))
        );

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    readonly $user = toSignal(this.#auth.getLoggedUser$());
    readonly $isSysAdmin = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));
    readonly $displayedColumns = computed(() => {
        const columns = ['name', 'types', 'phone', 'email', 'status', 'actions'];
        if (this.$isSysAdmin()) columns.unshift('operator');
        return columns;
    });

    updateStatus = (id: number, status: EntityStatus): Observable<void> => this.#entitiesSrv.updateEntity(id, { status });
    trackByFn = (_, entity: Entity): number => entity.id;

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = { include_entity_admin: true };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'TYPE':
                        this.#request.type = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values[0].value;
                        break;
                    case 'OPERATOR':
                        this.#request.operator_id = values[0].value;
                        break;
                }
            }
        });
        this.#loadEntities();
    }

    openNewEntityDialog(): void {
        this.#matDialog.open(NewEntityDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(created => !!created))
            .subscribe(id => {
                this.#ephemeralService.showSuccess({ msgKey: 'EVENTS.CREATE_ENTITY_SUCCESS' });
                this.router.navigate([id], { relativeTo: this.#route });
            });
    }

    openDeleteEntityDialog(entity: Entity): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_ENTITY',
            message: 'ENTITY.DELETE_WARNING',
            messageParams: { name: entity.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#entitiesSrv.entity.delete(entity.id))
            )
            .subscribe(() => {
                this.#ephemeralService.showSuccess({ msgKey: 'EVENTS.DELETE_ENTITY_SUCCESS', msgParams: { name: entity.name } });
                this.#loadEntities();
            });
    }

    disabledEntity(entityId: number): boolean {
        return entityId === this.$user().entity?.id && this.$user().entity?.id === this.$user()?.operator.id;
    }

    #loadEntities(): void {
        this.#entitiesSrv.entityList.load(this.#request);
        this.#ref.detectChanges();
    }
}
