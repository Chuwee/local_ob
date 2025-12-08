
import { CSV_FILE_PROCESSOR, CsvErrorEnum, CsvFile, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BulkCreateMappingSelectionComponent } from './csv-selection/bulk-create-mapping-selection.component';
import { MappingToCreate } from '../../models/mapping.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        BulkCreateMappingSelectionComponent
    ],
    selector: 'app-bulk-create-mapping',
    templateUrl: './bulk-create-mapping.component.html',
    styleUrls: ['./bulk-create-mapping.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: BulkCreateMappingComponent
    }]
})
export class BulkCreateMappingComponent extends ObDialog<BulkCreateMappingComponent, {}, { mappingsToCreate: MappingToCreate[] }> implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<BulkCreateMappingComponent>);

    readonly form = this.#fb.group({
        mappingsToCreate: [[] as MappingToCreate[], [Validators.required, Validators.minLength(1)]]
    });

    readonly csvSelectionForm = this.#fb.group({
        selection: [{ file: null, processedFile: null } as CsvFile, [
            requiredFieldsInOneControl(Object.keys({ file: null, processedFile: null })),
            csvValidator(CsvErrorEnum.csvProcessorFileError)
        ]]
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.csvSelectionForm.controls.selection.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.controls.mappingsToCreate.reset();
        });
    }

    updateMappingsToCreateHandler(mappingsToCreate: MappingToCreate[]): void {
        this.form.controls.mappingsToCreate.setValue(mappingsToCreate);
    }

    close(mappingsToCreate?: MappingToCreate[]): void {
        this.#dialogRef.close(mappingsToCreate ? { mappingsToCreate } : null);
    }

    createMappings(): void {
        this.close(this.form.value.mappingsToCreate);
    }
}
