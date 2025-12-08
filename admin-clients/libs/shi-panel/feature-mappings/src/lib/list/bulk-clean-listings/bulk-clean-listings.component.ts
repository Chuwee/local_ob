
import { CSV_FILE_PROCESSOR, CsvErrorEnum, CsvFile, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { Chip, ChipsComponent, DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Subject, takeUntil } from 'rxjs';
import { ImportEventsSelectionComponent } from './csv-selection/import-events-selection.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        ImportEventsSelectionComponent,
        ChipsComponent
    ],
    selector: 'app-bulk-clean-listings',
    templateUrl: './bulk-clean-listings.component.html',
    styleUrls: ['./bulk-clean-listings.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: BulkCleanListingsComponent
    }]
})
export class BulkCleanListingsComponent extends ObDialog<BulkCleanListingsComponent, {}, { idsToClean: number[] }> implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<BulkCleanListingsComponent>);

    readonly form = this.#fb.group({
        uploadManually: [null as boolean, [Validators.required]],
        idsToClean: [[] as number[], [Validators.required, Validators.minLength(1)]],
        manuallyIdInput: ['', [c => this.eventIdFormatValidator(c)]]
    });

    readonly csvSelectionForm = this.#fb.group({
        selection: [{ file: null, processedFile: null } as CsvFile, [
            requiredFieldsInOneControl(Object.keys({ file: null, processedFile: null })),
            csvValidator(CsvErrorEnum.csvProcessorFileError)
        ]]
    });

    readonly chipsEventsToClean$ = new BehaviorSubject<Chip[]>([]);

    readonly maxEventsToClean = 10;

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.form.controls.uploadManually.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.csvSelectionForm.reset();
            this.form.controls.idsToClean.reset();
            this.form.controls.manuallyIdInput.setValue('');
            this.chipsEventsToClean$.next([]);
        });

        this.csvSelectionForm.controls.selection.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.controls.idsToClean.reset();
        });
    }

    updateIdsToCleanHandler(shiIdsToImport: number[]): void {
        this.form.controls.idsToClean.setValue(shiIdsToImport);
    }

    close(idsToClean?: number[]): void {
        this.#dialogRef.close(idsToClean ? { idsToClean } : null);
    }

    cleanEvents(): void {
        this.close(this.form.value.idsToClean);
    }

    addEventChip(): void {
        if (this.form.controls.manuallyIdInput.valid) {
            const eventId = this.form.value.manuallyIdInput;
            if (!this.form.controls.idsToClean.valid) {
                this.form.controls.idsToClean.setValue([parseInt(eventId)]);
                this.chipsEventsToClean$.next([{ label: eventId, value: eventId }]);
            } else if (!this.form.value.idsToClean?.includes(parseInt(eventId))) {
                this.form.controls.idsToClean.setValue(this.form.value.idsToClean.concat([parseInt(eventId)]));
                this.chipsEventsToClean$.next(this.chipsEventsToClean$.getValue().concat([{ label: eventId, value: eventId }]));
            }
            this.form.controls.manuallyIdInput.setValue('');
        } else {
            this.form.controls.manuallyIdInput.markAsTouched();
        }
    }

    removeChip(chip: Chip): void {
        this.form.controls.idsToClean.setValue(this.form.value.idsToClean.filter(id => id !== chip.value));
        this.chipsEventsToClean$.next(this.chipsEventsToClean$.getValue().filter(c => c.value !== chip.value));
        this.form.controls.manuallyIdInput.setValue('');
    }

    private eventIdFormatValidator(control: AbstractControl): ValidationErrors | null {
        const regex = /^[0-9]*$/;
        if (regex.test(control.value?.toString())) {
            return null;
        } else {
            return { invalidEventIdFormat: true };
        }
    }
}
