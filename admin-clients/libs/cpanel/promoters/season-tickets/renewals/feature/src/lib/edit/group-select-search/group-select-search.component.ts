import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, Output, ViewChild } from '@angular/core';
import { ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MatSelectSearchComponent, NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, startWith, takeUntil } from 'rxjs/operators';

export interface GroupSelectSearchModel {
    name: string;
    data: Record<string, string>[];
}

@Component({
    selector: 'app-group-select-search',
    templateUrl: './group-select-search.component.html',
    styleUrls: ['./group-select-search.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [NgxMatSelectSearchModule, TranslatePipe, ReactiveFormsModule]
})
export class GroupSelectSearchComponent implements AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _filteredOptionGroups$: Observable<GroupSelectSearchModel[]>;
    @ViewChild(MatSelectSearchComponent) selector: MatSelectSearchComponent;
    readonly filterControl: UntypedFormControl = new UntypedFormControl();

    @Input()
    optionGroups$: Observable<GroupSelectSearchModel[]>;

    @Input()
    placeholderLabel;

    @Input()
    searchFields: Record<string, string>;

    @Output()
    get filteredOptionGroups$(): Observable<GroupSelectSearchModel[]> {
        return this._filteredOptionGroups$;
    }

    constructor() {
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngAfterViewInit(): void {
        this._filteredOptionGroups$ = combineLatest([
            this.optionGroups$,
            this.filterControl.valueChanges.pipe(startWith(null as string))
        ]).pipe(
            map(([options, keyword]) => this.filter(options, keyword)),
            takeUntil(this._onDestroy)
        );
    }

    private filter(groups: GroupSelectSearchModel[], keyword: string): GroupSelectSearchModel[] {
        let result: GroupSelectSearchModel[];
        if (groups == null) {
            result = [];
        } else if (!keyword) {
            result = groups.slice();
        } else {
            result = groups.map(group => {
                const data = group.data.filter(option =>
                    option?.[this.searchFields[group.name]].toLowerCase().includes(keyword.toLowerCase()));
                return { ...group, data };
            }).filter(group => group.data.length > 0);
        }
        return result;
    }
}
