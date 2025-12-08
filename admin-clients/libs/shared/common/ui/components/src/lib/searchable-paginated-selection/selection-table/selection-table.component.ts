import { compareWithIdOrCode, Id, IdOrCode } from '@admin-clients/shared/data-access/models';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import {
    AfterContentInit, ChangeDetectionStrategy, ChangeDetectorRef,
    Component, ContentChild, ContentChildren, EventEmitter, input, Input, OnDestroy, Output,
    QueryList, TemplateRef, TrackByFunction, ViewChild
} from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { MatColumnDef, MatTable } from '@angular/material/table';
import { debounceTime, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-selection-table',
    templateUrl: './selection-table.component.html',
    styleUrls: ['./selection-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SelectionTableComponent<T> implements OnDestroy, AfterContentInit {
    private _onDestroy = new Subject<void>();
    private _isSelectionDisabled = false;

    @Input() pageForm: UntypedFormControl;
    @Input() data: (IdOrCode)[];
    @Input() columns: string[];
    @Input() selectionChanged = new EventEmitter<{ deleted: (IdOrCode)[]; selected: (IdOrCode)[] }>();
    @Input() linkedRows = false;
    @Input() linkParamKey: string;
    @Input() overrideClasses: string;
    @Input() columnsDisabled: number[];
    @Input() margin: boolean = false;
    @Input() mainPage: boolean = false;
    @Input() canEditSelection: boolean = true;

    @Input() set selectionDisabled(val: BooleanInput) {
        this._isSelectionDisabled = coerceBooleanProperty(val);
    }

    get selectionDisabled(): boolean {
        return this._isSelectionDisabled;
    }

    @Output() rowClicked = new EventEmitter<T>();

    @ViewChild(MatTable, { static: true }) table: MatTable<Id>;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild('selectAllTemplate') selectAllTemplateRef?: TemplateRef<unknown>;
    @ContentChild('noResultsTemplate') noResultsTemplateRef?: TemplateRef<unknown>;

    trackBy = input<TrackByFunction<IdOrCode>>((_, item) => 'id' in item ? item.id : item.code);

    constructor(private _ref: ChangeDetectorRef) { }

    ngAfterContentInit(): void {
        this.columnDefs.forEach(columnDef => this.table.addColumnDef(columnDef));

        this.pageForm?.valueChanges
            .pipe(
                debounceTime(500),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this._ref.markForCheck());
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    isSelected(idOrCode: IdOrCode): boolean {
        return !!this.pageForm?.value?.find(x => compareWithIdOrCode(x, idOrCode));
    }

    changeSelection(idOrCode: IdOrCode, index: number, row?: T): void {
        if (this.mainPage && row) {
            this.rowClicked.emit(row);
        } else if (!this.selectionDisabled && !this.columnsDisabled?.includes(index)) {
            const oldValues = this.pageForm.value || [];
            if (this.isSelected(idOrCode)) {
                this.pageForm.patchValue(oldValues.filter(x => compareWithIdOrCode(x, idOrCode)));
                this.selectionChanged.emit({ selected: [], deleted: [idOrCode] });
            } else {
                this.pageForm.patchValue(oldValues.concat(idOrCode));
                this.selectionChanged.emit({ selected: [idOrCode], deleted: [] });
            }
        }
    }

    isArray(data: (IdOrCode)[] | { [key: string]: (IdOrCode)[] }): boolean {
        return Array.isArray(data);
    }

    isEmptyStructure(data: (IdOrCode)[] | { [key: string]: (IdOrCode)[] }): boolean {
        if (!data) {
            return true;
        } else if (this.isArray(data)) {
            return data.length === 0;
        } else { // map
            return Object.values(data).every(subcollection => subcollection.length === 0);
        }
    }
}
