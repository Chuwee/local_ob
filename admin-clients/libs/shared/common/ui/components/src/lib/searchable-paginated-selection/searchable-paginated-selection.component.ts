import { Metadata } from '@OneboxTM/utils-state';
import { compareWithIdOrCode, IdOrCode } from '@admin-clients/shared/data-access/models';
import {
    Attribute, booleanAttribute, ChangeDetectionStrategy, Component, ContentChild, DestroyRef, EventEmitter, inject, Input, OnDestroy, OnInit, Optional,
    Output, TemplateRef, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormControl } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Params } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, tap } from 'rxjs/operators';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { FilterItem, FilterItemValue } from '../list-filters/filter-item.model';
import { FilterComponent } from '../list-filters/filter.component';
import { MessageDialogService } from '../message-dialog/message-dialog.service';
import { SearchInputComponent } from '../search-input/search-input.component';
import {
    pageChangeDebounceTime, SearchablePaginatedSelectionLoadEvent, pageSize,
    searchChangeDebounceTime
} from './searchable-paginated-selection.model';

@Component({
    selector: 'app-searchable-paginated-selection',
    templateUrl: './searchable-paginated-selection.component.html',
    styleUrls: ['./searchable-paginated-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SearchablePaginatedSelectionComponent extends FilterComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #filterItem = new FilterItem('PAGINATION', null);

    #search: string;
    #selected: (IdOrCode)[] = [];
    #pageState: Subject<void> = new Subject();

    @ViewChild(MatPaginator) private _matPaginator: MatPaginator;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;

    @Input() data$: Observable<(IdOrCode)[] | { [key: string]: (IdOrCode)[] }>;
    @Input() metadata$: Observable<Metadata>;
    @Input() loading$?: Observable<boolean>;
    @Input() form?: UntypedFormControl;
    @Input() placeholder?: string;
    @Input() saveChangesWarn = false;
    @Input() hideSearch = false;
    @Input() paginatorPosition: 'above' | 'below' = 'above';
    @Input() mainPage: boolean = false;
    @Input() canChange$ = of(true);
    @Input({ transform: booleanAttribute }) searchDisabled = false;

    @Output() selected = new EventEmitter<(IdOrCode)[] | { [key: string]: (IdOrCode)[] }>();
    @Output() loadData = new EventEmitter<SearchablePaginatedSelectionLoadEvent>();
    @Output() pageChanged = new EventEmitter<PageEvent>();
    @Output() searchChanged = new EventEmitter<string>();

    @ContentChild('selectionTemplate') selectionTemplateRef?: TemplateRef<unknown>;

    readonly pageChange: EventEmitter<number> = new EventEmitter();
    selectionChanged = new EventEmitter<{ deleted: (IdOrCode)[]; selected: (IdOrCode)[] }>();
    pageForm = new UntypedFormControl([]);
    hidePagination$;

    get useBoxedOptions(): boolean {
        return this._classNames?.includes('boxed-options') || false;
    }

    get search(): string {
        return this.#search;
    }

    inputPageSize: number;

    constructor(
        private _msgDialogService: MessageDialogService,
        @Optional() @Attribute('class') private _classNames: string
    ) {
        super();
    }

    firstPage(): void {
        this._matPaginator.pageIndex = 0;
        this.#pageState.next();
    }

    get length(): number {
        return this._matPaginator.length;
    }

    @Input()
    set length(value: number) {
        this._matPaginator.length = value;
        this.#pageState.next();
    }

    get pageSize(): number {
        return this.inputPageSize || pageSize;
    }

    @Input()
    set pageSize(value: number) {
        this.inputPageSize = value;
    }

    get pageIndex(): number {
        return this._matPaginator?.pageIndex;
    }

    @Input()
    set pageIndex(value: number) {
        this._matPaginator.pageIndex = value;
        this.pageChange.emit(this._matPaginator.pageIndex);
        this.#pageState.next();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params['offset']) {
            const offset = Number(params['offset']);
            this._matPaginator.pageIndex = Math.floor(offset / this.pageSize);
            this.#pageState.next();
        } else {
            this.firstPage();
        }
        if (params['q']) {
            this._searchInputComponent.applyFiltersByUrlParams$(params);
        }
        return of(this.getFilters().concat(this._searchInputComponent.getFilters()));
    }

    getFilters(): FilterItem[] {
        const offsetValue = this.pageIndex * this.pageSize;
        this.#filterItem.values = [new FilterItemValue({
            limit: this._matPaginator.pageSize,
            offset: offsetValue
        }, null)];
        if (offsetValue > 0) {
            this.#filterItem.urlQueryParams['offset'] = offsetValue;
        } else {
            this.#filterItem.urlQueryParams = {};
        }
        return [this.#filterItem];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'SEARCH_INPUT') {
            this._searchInputComponent.removeFilter(key, value);
        }
        this.firstPage();
    }

    resetFilters(): void {
        this.firstPage();
        this._searchInputComponent.resetFilters();
    }

    ngOnInit(): void {
        if (this.mainPage) {
            this.pageChange.subscribe(() => this.filtersSubject.next(this.getFilters().concat(this._searchInputComponent.getFilters())));
        }
        this.emitLoad();
        this.data$.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.pageForm.patchValue(this.#selected)); // set current page selected items

        this.form?.valueChanges
            .pipe(
                startWith(this.form.value),
                takeUntilDestroyed(this.#destroyRef))
            .subscribe(selected => {
                this.#selected = selected || [];
                this.selected.next(this.#selected);
                this.pageForm.patchValue(this.#selected);
            });

        this.pageChanged
            .pipe(
                debounceTime(pageChangeDebounceTime),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(event => {
                this.addSelected(this.pageForm.value);
                this.emitLoad(event);
            });

        this.searchChanged
            .pipe(
                debounceTime(searchChangeDebounceTime),
                distinctUntilChanged(),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(search => {
                this.addSelected(this.pageForm.value);
                this.#search = search;
                this.firstPage();
                this.emitLoad();
                if (this.#filterItem.values?.length > 0) {
                    this.#filterItem.values[0].value.offset = 0;
                    this.#filterItem.urlQueryParams = {};
                    this.filtersSubject.next(this.filtersSubject.getValue().concat(this._searchInputComponent.getFilters()));
                }
            });

        this.selectionChanged
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selectionChange => {
                this.removeSelected(selectionChange.deleted);
                this.addSelected(selectionChange.selected);
                this.selected.emit(this.#selected);
                this.form?.setValue(this.#selected);
                this.form?.markAsTouched();
                this.form?.markAsDirty();
            });
    }

    canPageChange(event: PageEvent): void {
        if (this.mainPage) {
            this.pageIndex = event.pageIndex;
        }
        if (!this.saveChangesWarn || !this.form?.dirty) {
            this.pageChanged.emit(event);
        } else {
            this._matPaginator.pageIndex = event.previousPageIndex;
            this._msgDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: 'TITLES.UNSAVED_CHANGES',
                message: 'FORMS.UNSAVED_CHANGES_WARN',
                actionLabel: 'NAV_ACTIONS.NAVIGATE_ANYWAY',
                showCancelButton: true,
                cancelLabel: 'FORMS.ACTIONS.GO_BACK'
            })
                .pipe(filter(Boolean))
                .subscribe(() => {
                    this.pageChanged.emit(event);
                    this._matPaginator.pageIndex = event.pageIndex;
                });
        }
    }

    private emitLoad(pageOptions?: PageEvent): void {
        this.loadData.emit({
            limit: this.pageSize,
            q: this.#search,
            offset: this.pageSize * (pageOptions ? pageOptions.pageIndex : 0)
        });
    }

    private addSelected(selected: (IdOrCode)[]): void {
        this.#selected = [...selected, ...this.#selected].reduce((acc, ele) => {
            const obj = acc.find(x => compareWithIdOrCode(x, ele));
            return obj ? acc : acc.concat(ele);
        }, []);
    }

    private removeSelected(selected: (IdOrCode)[]): void {
        this.#selected = this.#selected.filter(el => !selected.find(elem => compareWithIdOrCode(elem, el)));
    }

    get selectedItems(): (IdOrCode)[] {
        return this.#selected;
    }

    getPaginatorStartItem(metadata: Metadata): number {
        if (metadata?.total === 0) return 0;
        return metadata?.offset + 1;
    }

    getPaginatorEndItem(metadata: Metadata): number {
        return Math.min(metadata?.offset + metadata?.limit, metadata?.total);
    }
}
