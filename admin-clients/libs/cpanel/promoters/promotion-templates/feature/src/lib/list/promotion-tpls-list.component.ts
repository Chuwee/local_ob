import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    GetPromotionTplsRequest,
    PromotionTplListElement,
    PromotionTpl,
    PromotionTplsService
} from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    DialogSize, MessageDialogService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService,
    SortFilterComponent, PaginatorComponent, SearchInputComponent, FilterItem, EphemeralMessageService,
    PopoverComponent,
    ChipsComponent,
    ChipsFilterDirective,
    ContextNotificationComponent,
    PopoverFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { NewPromotionTemplateDialogComponent } from '../create/new-promotion-tpl-dialog.component';
import { favoritePromotionLiterals } from '../models/favorite-promotion-literals';
import { PromotionTemplatesListFilterComponent } from './filter/promotion-tpls-list-filter.component';

@Component({
    selector: 'app-promotion-tpls-list',
    templateUrl: './promotion-tpls-list.component.html',
    styleUrls: ['./promotion-tpls-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MaterialModule, PopoverComponent, PopoverFilterDirective, PaginatorComponent, RouterLink,
        SearchInputComponent, AsyncPipe, ChipsComponent, ChipsFilterDirective, DefaultIconComponent,
        ContextNotificationComponent, PromotionTemplatesListFilterComponent, FlexLayoutModule
    ]
})
export class PromotionTemplatesListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    #request: GetPromotionTplsRequest;
    #sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(PromotionTemplatesListFilterComponent) private _filterComponent: PromotionTemplatesListFilterComponent;

    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    promotionTemplatesPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    promotionTemplatesMetadata$: Observable<Metadata>;
    isPromotionTemplatesLoading$: Observable<boolean>;
    promotionTemplates$: Observable<PromotionTplListElement[]>;
    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    updateFavorite: (promotionTplId: number, isFavorite: boolean) => Observable<boolean>;

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();
    readonly displayedColumns$ = this.canSelectEntity$
        .pipe(
            map(canSelectEntity => {
                if (canSelectEntity) {
                    return ['default', 'name', 'entity', 'type', 'actions'];
                } else {
                    return ['default', 'name', 'type', 'actions'];
                }
            })
        );

    readonly favoritePromotionLiterals = favoritePromotionLiterals;

    trackByFn = (_, item: PromotionTplListElement): number => item.id;

    ngOnInit(): void {
        this.promotionTemplatesMetadata$ = this.#promotionTplsSrv.getPromotionTemplatesMetadata$();
        this.isPromotionTemplatesLoading$ = this.#promotionTplsSrv.isPromotionTemplatesLoading$();
        this.promotionTemplates$ = this.#promotionTplsSrv.getPromotionTemplatesData$()
            .pipe(
                filter(promotionTemplates => !!promotionTemplates)
            );
        this.updateFavorite = (promotionTplId, isFavorite) =>
            this.#promotionTplsSrv.updatePromotionTemplateFavorite(promotionTplId, isFavorite)
                .pipe(tap(() => this.loadPromotionTemplates()));
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {};
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
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                    case 'TYPE':
                        this.#request.type = PromotionType[values[0].value];
                        break;
                }
            }
        });

        this.loadPromotionTemplates();
    }

    openNewPromotionTemplateDialog(): void {
        this.#matDialog.open(NewPromotionTemplateDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(promotionTemplateId => {
                if (promotionTemplateId) {
                    this.#router.navigate([promotionTemplateId, 'general-data'], { relativeTo: this.#route });
                }
            });
    }

    openDeletePromotionTemplateDialog(promotionTemplate: PromotionTpl): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'EVENT_PROMOTION_TEMPLATE.DELETE_TPL_TITLE',
            message: 'EVENT_PROMOTION_TEMPLATE.DELETE_TPL_WARNING_MESSAGE',
            messageParams: { promotionTemplateName: promotionTemplate.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#promotionTplsSrv.deletePromotionTemplate(promotionTemplate.id)
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'EVENT_PROMOTION_TEMPLATE.DELETE_TPL_SUCCESS',
                                msgParams: { promotionTemplateName: promotionTemplate.name }
                            });
                            this.loadPromotionTemplates();
                        });
                }
            });
    }

    private loadPromotionTemplates(): void {
        this.#promotionTplsSrv.loadPromotionTemplates(this.#request);
    }
}
