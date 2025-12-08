import { Metadata } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { AsyncPipe } from '@angular/common';
import {
    AfterContentInit, QueryList, TrackByFunction, ViewChild,
    ChangeDetectionStrategy, Component, ContentChildren, EventEmitter, input, Input, OnInit, Output
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatColumnDef, MatTable, MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, debounceTime, distinctUntilChanged, filter, map, Observable, of, shareReplay, tap } from 'rxjs';
import { EmptyStateTinyComponent } from '../empty-state-tiny/empty-state-tiny.component';
import { SearchInputComponent } from '../search-input/search-input.component';

export interface SearchTableChangeEvent {
    offset: number;
    q: string;
    limit: number;
}

@Component({
    imports: [
        MatPaginatorModule,
        MatTableModule,
        SearchInputComponent,
        TranslatePipe,
        AsyncPipe,
        FlexLayoutModule,
        EmptyStateTinyComponent
    ],
    selector: 'app-search-table',
    templateUrl: './search-table.component.html',
    styleUrls: ['./search-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchTableComponent<T> implements OnInit, AfterContentInit {

    private readonly _searchFilter = new BehaviorSubject<string>(null);
    private readonly _pageFilter = new BehaviorSubject<number>(null);
    private readonly _change = new EventEmitter<SearchTableChangeEvent>();
    private _forceLoad = false;

    @ContentChildren(MatColumnDef) private _columns: QueryList<MatColumnDef>;
    @ViewChild(MatTable, { static: true }) private _table: MatTable<unknown>;
    @ViewChild(SearchInputComponent) private _searchInput: SearchInputComponent;

    table$: Observable<{ data: unknown[]; total: number; page: number }>;
    total: number;

    get page(): number { return this._pageFilter.value; }

    get q(): string { return this._searchFilter.value; }

    trackBy = input<TrackByFunction<T>>((index, item) => item);

    @Input() columns: string[];
    @Input() dataSource: Observable<T[]>;
    @Input() metadata?: Observable<Metadata>; // if metadata is not defined then does a virtual search & pagination
    @Input() pageSize = 10;
    @Input() header = true;
    @Input() searchOnInteraction = true;
    @Input() placeholder;
    @Input() emptyListTitle: string;
    @Input() emptyListDescription: string;
    @Output() changed = this._change
        .pipe(
            distinctUntilChanged((prev, curr) => !this._forceLoad && prev.q === curr.q && prev.offset === curr.offset)
        );

    @Input() filter = (q: string, elem: T): boolean => {
        if (!(elem instanceof Object)) {
            return false;
        }
        const objElem = elem as IdName;
        return objElem.id?.toString() === q ||
            objElem.name?.toLowerCase().includes(q.toLowerCase());
    };

    ngOnInit(): void {
        this.fireChange();
        this.table$ = combineLatest([
            this.dataSource,
            this._searchFilter.pipe(distinctUntilChanged(), tap(() => this._pageFilter.next(0))),
            this._pageFilter.pipe(distinctUntilChanged()),
            (this.metadata || of(null))
        ]).pipe(
            debounceTime(5),
            filter(Boolean),
            map(([data, , page, metadata]) => [this.searchData(data), page, metadata] as [unknown[], number, Metadata]),
            tap(([data, , metadata]) => this.total = metadata?.total || data?.length),
            map(([data, page]) => ({
                data: this.paginateData(data),
                total: this.total,
                page,
                startItem: this.startItem(),
                endItem: this.endItem()
            })),
            shareReplay(1)
        );
    }

    ngAfterContentInit(): void {
        this._table && this._columns?.forEach(columnDef => this._table.addColumnDef(columnDef));
    }

    changeSearch(q: string): void {
        this._searchFilter.next(q || null);
        this.fireChange();
    }

    changePage({ pageIndex: page }: Partial<PageEvent>, forceLoad = false): void {
        this._pageFilter.next(page);
        this.fireChange(forceLoad);
    }

    clearFilter(): void {
        !this._searchFilter.value ? this.fireChange(true) : this._searchInput.clearSearchInputValue();
    }

    private fireChange(forceLoad = false): void {
        this._forceLoad = forceLoad;
        const page = this.page, limit = this.pageSize, q = this.q;
        this._change.emit({ offset: page * limit, q, limit });
    }

    private startItem(): number {
        return this.total !== 0 ? (this.page * this.pageSize) + 1 : 0;
    }

    private endItem(): number {
        const page = this.page, pageSize = this.pageSize, total = this.total;
        return Math.min((page + 1) * pageSize, total);
    }

    private searchData(data: T[]): unknown[] {
        if (this.metadata) {
            return data;
        }
        const q = this.q;
        return data?.filter(elem => (!q || this.filter(q, elem)));
    }

    private paginateData(data: unknown[]): unknown[] {
        if (this.metadata) {
            return data;
        }
        const page = this.page, pageSize = this.pageSize;
        return data?.slice(page * pageSize, (page + 1) * pageSize);
    }

}
