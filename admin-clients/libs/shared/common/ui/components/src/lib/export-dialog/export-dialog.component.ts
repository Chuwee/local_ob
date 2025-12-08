import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AUTHENTICATION_SERVICE, AuthService } from '@admin-clients/shared/core/data-access';
import { ExportDialogData, ExportFormat, ExportRequest, FieldData, FieldDataGroup } from '@admin-clients/shared/data-access/models';
import { UpperCasePipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, signal, viewChild } from '@angular/core';
import { ReactiveFormsModule, UntypedFormControl, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule, MatSelectionList, MatSelectionListChange } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { first } from 'rxjs/operators';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { ObDialog } from '../dialog/ob-dialog';
import { ExportService } from './export.service';

@Component({
    selector: 'app-export-dialog',
    templateUrl: './export-dialog.component.html',
    styleUrls: ['./export-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ExportService],
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        MatButtonModule,
        MatCheckboxModule,
        MatDialogModule,
        MatDividerModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        UpperCasePipe
    ]
})
export class ExportDialogComponent
    extends ObDialog<ExportDialogComponent, ExportDialogData, ExportRequest>
    implements OnInit, AfterViewInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();
    private _exportDataFormat: ExportFormat;
    private _fieldOrder: Record<string, number>;

    private readonly _selectionList = viewChild<MatSelectionList>('selectionList');

    exportDataFields: FieldDataGroup[];
    selectedFields: FieldData[];
    email: UntypedFormControl;

    readonly $groupStates = signal<Map<string, boolean>>(new Map());

    constructor(
        private _exportSrv: ExportService,
        @Inject(AUTHENTICATION_SERVICE) private _auth: AuthService
    ) {
        super(DialogSize.MEDIUM);
        this.dialogRef.disableClose = false;
        this._fieldOrder = {};
        this._exportDataFormat = this.data.exportFormat;
        this.exportDataFields = this.data.exportData;
        // Check if we have a selectedFields array in the localStorage
        this.#setFieldOrder();
        const availableCols = this.selectedFields = this.data.exportData.flatMap(fieldGroup => fieldGroup.fields);
        if (this.data.selectedFields?.length) {
            this.selectedFields = this.data.selectedFields.map(field => availableCols.find(col => col.field === field)).filter(Boolean);
        } else {
            this.selectedFields = availableCols.filter(field => field.isDefault);
        }
        //includes parent groups to selection if required
        const currentColumnDataFields = this.selectedFields.map(col => col.field);
        const parentsToAddToSelection: FieldDataGroup[] = [];
        this.data.exportData
            .filter(columnDataGroup => !currentColumnDataFields.includes(columnDataGroup.field))
            .filter(columnDataGroup => columnDataGroup.fields.every(column => currentColumnDataFields.includes(column.field)))
            .forEach(columnDataGroup => parentsToAddToSelection.push(columnDataGroup));
        this.selectedFields.push(...parentsToAddToSelection);
    }

    ngOnInit(): void {
        this.email = new UntypedFormControl(null, [Validators.required, Validators.email]);

        //Add logged user email as default
        this._auth.getLoggedUser$().pipe(first(user => !!user))
            .subscribe(user => user.email ? this.email.setValue(user.email) : this.email.setValue(user.username));
    }

    ngAfterViewInit(): void {
        this.#updateGroupStates();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    defaultSelection(): void {
        this.exportDataFields.forEach(parentDataField => {
            parentDataField.fields.forEach(childDataField => this.#toggleIfNeeded(childDataField.field, childDataField.isDefault));
            this.#toggleParent(parentDataField);
        });
        this.#updateGroupStates();
    }

    selectionChange(event: MatSelectionListChange): void {
        if (event.options[0].value?.fields) {
            event.options[0].value.fields.forEach(childDataField => this.#toggleIfNeeded(childDataField.field, event.options[0].selected));
        } else {
            const parentDataField = this.exportDataFields
                .find(exportDataField => exportDataField.fields.find(field => field.field === event.options[0].value.field));
            this.#toggleParent(parentDataField);
        }
        this.#updateGroupStates();
    }

    exportList(): void {
        const fields = this._selectionList()?.selectedOptions.selected
            .filter(selected => !selected.value?.fields)
            .sort((selectedA, selectedB) =>
                this._fieldOrder[selectedA.value.field] - this._fieldOrder[selectedB.value.field])
            .map(selected => this._exportSrv.prepareField(selected.value.field, selected.value.fieldKey));
        const delivery = this._exportSrv.prepareDeliveryData(this.email.value);
        const exportRequest = { fields, delivery, format: this._exportDataFormat };

        this.dialogRef.close(exportRequest);
    }

    close(): void {
        this.dialogRef.close();
    }

    selectAll(): void {
        this._selectionList()?.selectAll();
        this.#updateGroupStates();
    }

    deselectAll(): void {
        this._selectionList()?.deselectAll();
        this.#updateGroupStates();
    }

    #setFieldOrder(): void {
        let i = 0;
        this.exportDataFields.forEach(parentDataField =>
            parentDataField.fields.forEach(childDataField => this._fieldOrder[childDataField.field] = ++i));
    }

    #toggleParent(parentDataField: FieldDataGroup): void {
        this.#toggleIfNeeded(parentDataField.field, parentDataField.fields
            .every(field => this._selectionList()?.options.find(item => field.field === item.value.field)?.selected));
    }

    #toggleIfNeeded(field: string, isSelected: boolean): void {
        const matField = this._selectionList()?.options.find(item => field === item.value.field);
        if (matField && isSelected !== matField.selected) {
            matField.toggle();
        }
    }

    #updateGroupStates(): void {
        const selectionList = this._selectionList();
        if (!selectionList?.options) return;

        const newStates = new Map<string, boolean>();

        this.exportDataFields.forEach(group => {
            const selectedInGroup = group.fields.filter(field =>
                selectionList.options.find(opt => opt.value?.field === field.field)?.selected
            );

            const isIndeterminate = selectedInGroup.length > 0 &&
                selectedInGroup.length < group.fields.length;

            newStates.set(group.field, isIndeterminate);
        });

        this.$groupStates.set(newStates);
    }
}
