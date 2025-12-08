import { FieldData, FieldDataGroup } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, QueryList, ViewChildren } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule, MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { DialogSize } from '../dialog/models/dialog-size.enum';

@Component({
    selector: 'app-col-selection-dialog',
    templateUrl: './col-selection-dialog.component.html',
    styleUrls: ['./col-selection-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatButtonModule,
        MatCheckboxModule,
        MatDialogModule,
        MatDividerModule,
        MatIconModule,
        MatListModule,
        TranslatePipe
    ]
})
export class ColSelectionDialogComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    @ViewChildren(MatSelectionList)
    private _selectionList: QueryList<MatSelectionList>;

    title: string;
    info: string;
    fieldDataGroups: FieldDataGroup[];
    selectedFields: FieldData[];

    constructor(
        private _dialogRef: MatDialogRef<ColSelectionDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data: {
            title?: string;
            info?: string;
            fieldDataGroups: FieldDataGroup[];
            selectedFields: FieldData[];
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.title = data.title;
        this.info = data.info;
        this.fieldDataGroups = data.fieldDataGroups;
        if (data.selectedFields?.length) {
            this.selectedFields = data.selectedFields;
        } else {
            this.selectedFields = data.fieldDataGroups
                .map(fieldGroup => fieldGroup.fields)
                .reduce((previousValue, currentValue) => currentValue.concat(...previousValue))
                .filter(field => field.isDefault);
        }
        //includes parent groups to selection if required
        const currentColumnDataFields = this.selectedFields.map(col => col.field);
        const parentsToAddToSelection: FieldDataGroup[] = [];
        data.fieldDataGroups
            .filter(columnDataGroup => !currentColumnDataFields.includes(columnDataGroup.field))
            .filter(columnDataGroup => columnDataGroup.fields.every(column => currentColumnDataFields.includes(column.field)))
            .forEach(columnDataGroup => parentsToAddToSelection.push(columnDataGroup));
        this.selectedFields.push(...parentsToAddToSelection);
    }

    defaultSelection(): void {
        this.fieldDataGroups.forEach(colDataGroup =>
            colDataGroup.fields.forEach(colData => this.toggleIfNeeded(colData.field, colData.isDefault)));
    }

    deselectAll(): void {
        this._selectionList.get(0).options.forEach(option => {
            if (!option.disabled) option.selected = false;
        });
    }

    selectionChange(event: MatSelectionListChange): void {
        if (event.options[0].value?.fields) {
            event.options[0].value.fields.forEach(childDataField => this.toggleIfNeeded(childDataField.field, event.options[0].selected));
        } else {
            const columnDataGroup = this.fieldDataGroups
                .find(colGroup => colGroup.fields.includes(event.options[0].value));
            this.toggleParent(columnDataGroup);
        }
    }

    commitSelection(): void {
        this._dialogRef.close(
            this._selectionList.get(0).selectedOptions.selected
                .filter(selected => !selected.value?.fields)
                .map(selected => selected.value)
        );
    }

    close(): void {
        this._dialogRef.close();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private toggleParent(parentDataField: FieldDataGroup): void {
        this.toggleIfNeeded(
            parentDataField.field,
            parentDataField.fields
                .every(field => this._selectionList.get(0).options.find(item => field.field === item.value.field).selected)
        );
    }

    private toggleIfNeeded(field: string, isSelected: boolean): void {
        const matField = this._selectionList.get(0).options.find(item => field === item.value.field);
        if (matField && isSelected !== matField.selected) {
            matField.toggle();
        }
    }

}
