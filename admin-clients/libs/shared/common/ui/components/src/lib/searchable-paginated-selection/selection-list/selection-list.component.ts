import { compareWithIdOrCode, IdOrCode } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import {
    Attribute, ChangeDetectionStrategy, Component, ContentChild, EventEmitter, Input, OnDestroy, OnInit, Optional, TemplateRef
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatListOption, MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { pairwise, startWith, takeUntil } from 'rxjs';
import { ContextNotificationComponent } from '../../context-notification/context-notification.component';

@Component({
    selector: 'app-selection-list',
    templateUrl: './selection-list.component.html',
    styleUrls: ['./selection-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatSelectionList, MatListOption, MatTooltip,
        ReactiveFormsModule, FlexLayoutModule, CommonModule, TranslatePipe,
        ContextNotificationComponent
    ]
})
export class SelectionListComponent implements OnInit, OnDestroy {
    private _onDestroy = new EventEmitter<void>();
    private _previouslySelected: IdOrCode[];

    @Input() pageForm: UntypedFormControl;
    @Input() noPadding = false;
    @Input() data: (IdOrCode)[];
    @Input() selectionChanged = new EventEmitter<{ selected: (IdOrCode)[]; deleted: (IdOrCode)[] }>();
    @Input() selectionDisabled = false;
    @Input() multiple = true;
    @Input() disabledTooltip: string = '';

    @ContentChild('groupHeaderTemplate') groupHeaderTemplateRef?: TemplateRef<unknown>;
    @ContentChild('optionTemplate') optionTemplateRef?: TemplateRef<unknown>;
    @ContentChild('noResultsTemplate') noResultsTemplateRef?: TemplateRef<unknown>;

    get useSmallRows(): boolean {
        return this._classNames?.includes('small-rows') || false;
    }

    get useBoxedOptions(): boolean {
        return this._classNames?.includes('boxed-options') || false;
    }

    compareWith = compareWithIdOrCode;

    constructor(@Optional() @Attribute('class') private _classNames: string) { }

    ngOnInit(): void {
        if (!this.multiple) {
            this.pageForm.valueChanges
                .pipe(
                    startWith(null as IdOrCode[]),
                    pairwise(),
                    takeUntil(this._onDestroy)
                )
                .subscribe(([prev]: [IdOrCode[], IdOrCode[]]) => {
                    this._previouslySelected = prev;
                });
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    @Input() disabledOptionPredicate: (elem: IdOrCode) => boolean = () => false;

    changeSelection(selectionChange: MatSelectionListChange): void {
        const deleted = this.multiple
            ? selectionChange.options.filter(option => !option.selected).map(option => option.value)
            : this._previouslySelected;
        const selected = selectionChange.options.filter(option => option.selected).map(option => option.value);
        this.selectionChanged.emit({ selected, deleted });
    }

    isArray(data: (IdOrCode)[] | { [key: string]: (IdOrCode)[] }): boolean {
        return Array.isArray(data);
    }

    isEmptyStructure(data: (IdOrCode)[] | { [key: string]: (IdOrCode)[] }): boolean {
        if (!data) {
            return true;
        } else if (Array.isArray(data)) {
            return data.length === 0;
        } else { // map
            return Object.values(data).every(subcollection => subcollection.length === 0);
        }
    }
}
