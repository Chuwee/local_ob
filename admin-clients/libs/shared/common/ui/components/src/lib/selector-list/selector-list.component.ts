/* eslint-disable @typescript-eslint/naming-convention */
import { IdName } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, ContentChild, EventEmitter, HostBinding, Input, OnDestroy, OnInit, Optional, Self, TemplateRef,
    ViewChild
} from '@angular/core';
import { ControlValueAccessor, NgControl, ReactiveFormsModule } from '@angular/forms';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { combineLatest, Observable, Subject } from 'rxjs';
import { debounceTime, map, startWith, takeUntil, tap } from 'rxjs/operators';
import { SearchInputComponent } from '../search-input/search-input.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-selector-list',
    templateUrl: './selector-list.component.html',
    styleUrls: ['./selector-list.component.scss'],
    imports: [
        SearchInputComponent,
        MatListOption,
        MatSelectionList,
        ReactiveFormsModule,
        CommonModule
    ]
})
export class SelectorListComponent implements ControlValueAccessor, OnInit, OnDestroy {

    @ViewChild('selectionlist', { static: true })
    private _selectList: MatSelectionList;

    private _onDestroy = new Subject<void>();
    private _onChange: ((value: IdName[]) => void);

    static nextId = 0;

    @HostBinding()
    id = `event-selector-list-${SelectorListComponent.nextId++}`;

    value: IdName[];

    @Input() loadBy: (filter: string) => void;

    @Input() externalOptions$: Observable<IdName[]>;

    @Input() listTitle = '';

    @Input() searchInputPlaceholder = '';

    @ContentChild('optionTemplate') optionTemplateRef?: TemplateRef<unknown>;

    options$: Observable<IdName[]>;
    searchkeyTrigger = new EventEmitter();

    constructor(@Optional() @Self() public ngControl: NgControl) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    compareById(option: IdName, option2: IdName): boolean {
        return option?.id === option2?.id;
    }

    isSelected(value, item): boolean {
        return value === item;
    }

    onContainerClick(): void {
        this._selectList.focus();
    }

    registerOnChange(onChange: ((value: unknown) => void)): void {
        this._onChange = onChange;
        this._selectList.registerOnChange(val => {
            this._onChange(val);
        });
    }

    registerOnTouched(onTouched: (() => void)): void {
        this._selectList.registerOnTouched(onTouched);
    }

    setDisabledState(isDisabled: boolean): void {
        if (isDisabled) {
            this.ngControl.control.disable();
        } else {
            this.ngControl.control.enable();
        }
    }

    writeValue(val: IdName[]): void {
        this.value = val;
    }

    ngOnInit(): void {
        this._selectList.selectionChange
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => this.value = [value?.options?.[0]?.value]);

        this.options$ = combineLatest([
            this.externalOptions$,
            this.searchkeyTrigger.pipe(startWith(null as string))
        ]).pipe(
            takeUntil(this._onDestroy),
            map(([options, keyword]) => options?.filter(this.filterByKeyword(keyword))),
            tap(options => this.value?.[0] && !options.find(elem => this.value?.[0].id === elem.id) && options.unshift(this.value?.[0]))
        );

        this.searchkeyTrigger
            .pipe(debounceTime(200), takeUntil(this._onDestroy))
            .subscribe(q => this.loadBy(q === '' ? null : q));
    }

    ngOnDestroy(): void {
        this._selectList.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    filterByKeyword(keyword: string): ((option: IdName) => boolean) {
        if (keyword) {
            return option => option.name?.toLowerCase().includes(keyword.toLowerCase());
        } else {
            return () => true;
        }
    }

}
