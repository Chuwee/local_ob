import { Metadata } from '@OneboxTM/utils-state';
import { Id } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, ContentChild, EventEmitter, Input, OnDestroy, OnInit, Output, TemplateRef
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatListModule } from '@angular/material/list';
import { PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, takeUntil } from 'rxjs/operators';
import { EmptyStateTinyComponent } from '../empty-state-tiny/empty-state-tiny.component';
import { SearchInputComponent } from '../search-input/search-input.component';
import {
    pageChangeDebounceTime, pageSize, SearchablePaginatedListLoadEvent, searchChangeDebounceTime
} from './searchable-paginated-list.model';

@Component({
    selector: 'app-searchable-paginated-list',
    templateUrl: './searchable-paginated-list.component.html',
    styleUrls: ['./searchable-paginated-list.component.scss'],
    imports: [
        CommonModule, SearchInputComponent, MatPaginatorModule, MatListModule, MatProgressSpinnerModule, TranslatePipe,
        FlexLayoutModule, EmptyStateTinyComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchablePaginatedListComponent implements OnInit, OnDestroy {
    private _search: string;
    private _onDestroy = new Subject<void>();

    @ContentChild('optionTemplate') optionTemplateRef?: TemplateRef<unknown>;

    @Input() data$: Observable<Id[]>;
    @Input() metadata$: Observable<Metadata>;
    @Input() loading$: Observable<boolean>;
    @Input() pageSize: number = pageSize;
    @Input() placeholder?: string;
    @Input() hoverable = false;
    @Input() hideSearch = false;
    @Input() emptyListTitle;
    @Input() smallRows = true;
    @Input() emptyListDescription;

    @Output() loadData = new EventEmitter<SearchablePaginatedListLoadEvent>();
    @Output() pageChanged = new EventEmitter<PageEvent>();
    @Output() searchChanged = new EventEmitter<string>();

    constructor() { }

    ngOnInit(): void {
        this.emitLoad();

        this.data$ = this.data$
            .pipe(
                filter(list => !!list)
            );

        this.pageChanged
            .pipe(
                debounceTime(pageChangeDebounceTime),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                this.emitLoad(event);
            });

        this.searchChanged
            .pipe(
                debounceTime(searchChangeDebounceTime),
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(search => {
                this._search = search;
                this.emitLoad();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private emitLoad(pageOptions?: PageEvent): void {
        this.loadData.emit({
            limit: this.pageSize,
            q: this._search,
            offset: this.pageSize * (pageOptions ? pageOptions.pageIndex : 0)
        });
    }
}
