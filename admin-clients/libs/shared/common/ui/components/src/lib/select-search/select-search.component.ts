import { getOrPush } from '@admin-clients/shared/utility/utils';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    AfterViewInit, ChangeDetectionStrategy, Component,
    EventEmitter, Input, OnDestroy, Output
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, startWith, takeUntil, tap } from 'rxjs/operators';

@Component({
    imports: [
        NgxMatSelectSearchModule,
        ReactiveFormsModule,
        TranslatePipe
    ],
    selector: 'app-select-search',
    templateUrl: './select-search.component.html',
    styleUrls: ['./select-search.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectSearchComponent<TData> implements AfterViewInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private _filteredOptions$: Observable<TData[]>;
    private _optionsCache: TData[] = [];
    private _lastKeyword = '';
    private _autofocus = false;

    protected readonly control = new FormControl('');

    @Input()
    get autofocus(): boolean {
        return this._autofocus;
    }

    set autofocus(value: boolean) {
        this._autofocus = coerceBooleanProperty(value);
    }

    @Input()
    options$: Observable<TData[]>;

    @Input()
    placeholderLabel = '';

    @Input()
    noEntriesFoundLabel: string;

    @Input()
    searchField: string;

    @Input()
    requireSelection: boolean;

    @Input()
    serverSideFetch = false;

    @Output()
    keyTrigger = new EventEmitter<string>();

    @Output() getFilteredOptions$(): Observable<TData[]> {
        return this._filteredOptions$;
    }

    ngAfterViewInit(): void {
        this._filteredOptions$ = combineLatest([
            this.options$,
            this.control.valueChanges.pipe(
                startWith('' as string),
                tap(keyword => {
                    if (this.shouldEmit(keyword)) {
                        this.keyTrigger.emit(keyword);
                    }
                    this._lastKeyword = keyword;
                })
            )
        ]).pipe(
            map(([options, keyword]) => {
                const filteredOptions = this.filter(options, keyword);
                if (!this.serverSideFetch) {
                    return filteredOptions;
                }
                return filteredOptions.map(item => {
                    if (item) {
                        return getOrPush(this._optionsCache as object[], item as object) as TData;
                    }
                    return item;
                });
            }),
            takeUntil(this._onDestroy)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    get search(): string {
        return this.control.value;
    }

    private filter(options: TData[], keyword: string): TData[] {
        keyword = keyword.toLowerCase();
        let result: TData[];
        if (options == null) {
            result = [];
        } else if (!keyword) {
            if (this.requireSelection) {
                result = options.slice();
            } else {
                result = [null].concat(options);
            }
        } else if (!this.searchField) {
            result = options.filter(option => !!option && String(option).toLowerCase().includes(keyword));
        } else {
            const searchFieldSegments = this.searchField.split('.');
            result = options.filter(option => {
                searchFieldSegments.forEach(segment => option = option?.[segment]);
                return !!option && String(option).toLowerCase().includes(keyword);
            });
        }
        return result;
    }

    private shouldEmit(keyword): boolean {
        return this.serverSideFetch && keyword !== null && keyword !== '' && this._lastKeyword !== keyword;
    }
}
