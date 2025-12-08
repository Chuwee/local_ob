/* eslint-disable @typescript-eslint/naming-convention */
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    AfterViewInit, booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnDestroy,
    Output, ViewChild
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatFormField, MatPrefix, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { Params } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, fromEvent, merge, Observable, of, Subscription } from 'rxjs';
import { debounceTime, filter, switchMap, take, tap } from 'rxjs/operators';
import { FilterItem, FilterItemValue } from '../list-filters/filter-item.model';
import { FilterComponent } from '../list-filters/filter.component';

@Component({
    imports: [MatIconButton, MatPrefix, MatSuffix, MatFormField, MatIcon, MatInput, TranslatePipe],
    selector: 'app-search-input',
    templateUrl: './search-input.component.html',
    styleUrls: ['./search-input.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchInputComponent extends FilterComponent implements AfterViewInit, OnDestroy {
    private readonly _filterItem: FilterItem;
    private _searchSubscription: Subscription;
    private _searchOnInteraction = false;

    @ViewChild('searchField', { read: ElementRef }) searchField: ElementRef;
    @ViewChild('searchBtn', { read: ElementRef }) searchBtn: ElementRef;
    isSearchFilterActive = false;
    @Output() valueChanged = new EventEmitter<string>();

    @Input()
    set initValue(value: string) {
        if (value && this.searchField) {
            this.setValue(value);
        }
    }

    @Input({ transform: booleanAttribute }) disabled = false;
    @Input() placeholder: string;
    @Input() canChange$ = of(true);

    @Input()
    get searchOnInteraction(): boolean { return this._searchOnInteraction; }

    set searchOnInteraction(value: boolean) { this._searchOnInteraction = coerceBooleanProperty(value); }

    constructor(private _changeDetectorRef: ChangeDetectorRef) {
        super();
        this._filterItem = new FilterItem('SEARCH_INPUT', null);
    }

    ngAfterViewInit(): void {
        const searchFieldKeyUp$ = this.searchOnInteraction ?
            fromEvent<KeyboardEvent>(this.searchField.nativeElement, 'keyup')
                .pipe(debounceTime(250)) : EMPTY;
        const searchFieldEnter$ = fromEvent<KeyboardEvent>(this.searchField.nativeElement, 'keyup')
            .pipe(
                filter(event => event?.key === 'Enter')
            );
        const searchBtnClick$ = fromEvent<MouseEvent>(this.searchBtn.nativeElement, 'click');
        this._searchSubscription = merge(searchFieldEnter$, searchBtnClick$, searchFieldKeyUp$)
            .pipe(
                debounceTime(300),
                switchMap(() => {
                    const value = this.searchField.nativeElement.value as string;
                    this.setValue(value);
                    return this.canChange$.pipe(
                        filter(canChange => canChange),
                        tap(() => {
                            setTimeout(() => {
                                this.notifyChanges(value);
                            });
                        }),
                        take(1)
                    );
                })
            )
            .subscribe();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._searchSubscription.unsubscribe();
    }

    clearSearchInputValue(): void {
        this.canChange$.pipe(
            filter(canChange => canChange),
            take(1)
        ).subscribe(() => {
            this.setValue(null);
            this.notifyChanges('');
        });
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        this.setValue(params['q']);
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        const value = this.searchField.nativeElement.value as string;
        if (value && value.length > 0) {
            this._filterItem.values = [new FilterItemValue(value, null)];
            this._filterItem.urlQueryParams['q'] = value;
        } else {
            this._filterItem.values = null;
            this._filterItem.urlQueryParams = {};
        }
        return [this._filterItem];
    }

    removeFilter(key: string, _value_: unknown): void {
        if (key === 'SEARCH_INPUT') {
            this.setValue(null);
        }
    }

    resetFilters(): void {
        this.setValue(null);
    }

    private setValue(value: string): void {
        if (value && value.length > 0) {
            this.searchField.nativeElement.value = value;
            this.isSearchFilterActive = true;
        } else {
            this.searchField.nativeElement.value = '';
            this.isSearchFilterActive = false;
        }
        this._changeDetectorRef.detectChanges();
    }

    private notifyChanges(value: string): void {
        this.valueChanged.emit(value);
        this.filtersSubject.next(this.getFilters());
    }
}
