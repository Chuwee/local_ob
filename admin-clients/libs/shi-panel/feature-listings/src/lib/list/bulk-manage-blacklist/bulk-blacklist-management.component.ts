
import { CSV_FILE_PROCESSOR, CsvErrorEnum, CsvFile, csvValidator } from '@admin-clients/shared/common/feature/csv';
import { Chip, ChipsComponent, DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { requiredFieldsInOneControl } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, FormControl, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { ImportSelectionComponent } from './csv-selection/import-selection.component';

@Component({
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        ImportSelectionComponent,
        ChipsComponent
    ],
    selector: 'app-bulk-blacklist-management',
    templateUrl: './bulk-blacklist-management.component.html',
    styleUrls: ['./bulk-blacklist-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: BulkBlacklistManagementComponent
    }]
})
export class BulkBlacklistManagementComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<BulkBlacklistManagementComponent>);

    readonly form = this.#fb.group({
        action: [null as 'BLACKLIST' | 'WHITELIST', [Validators.required]],
        level: [null as 'EVENT' | 'CODE', [Validators.required]],
        uploadManually: [null as boolean, [Validators.required]],
        eventIdsToChange: [[] as number[]],
        codesToChange: [[] as string[]],
        manuallyEventIdInput: [null as number, [c => this.eventIdFormatValidator(c)]],
        manuallyCodeInput: ['', [c => this.codesFormatValidator(c)]]
    });

    readonly csvSelectionForm = this.#fb.group({
        selection: [{ file: null, processedFile: null } as CsvFile, [
            requiredFieldsInOneControl(Object.keys({ file: null, processedFile: null })),
            csvValidator(CsvErrorEnum.csvProcessorFileError)
        ]]
    });

    readonly chipsIdsToChange$ = new BehaviorSubject<Chip[]>([]);

    readonly maxToChange = 10;

    constructor() {
        this.form.controls.action.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            if (this.form.controls.action.valid) {
                this.form.controls.level.enable();
            }
        });

        this.form.controls.level.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            if (this.form.controls.level.valid) {
                this.form.controls.uploadManually.enable();
            }
        });

        this.form.controls.uploadManually.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.csvSelectionForm.reset();
            this.form.controls.eventIdsToChange.setValue([]);
            this.form.controls.codesToChange.setValue([]);
            this.form.controls.manuallyEventIdInput.reset();
            this.form.controls.manuallyCodeInput.reset();
            this.chipsIdsToChange$.next([]);
        });

        this.csvSelectionForm.controls.selection.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.form.controls.eventIdsToChange.setValue([]);
            this.form.controls.codesToChange.setValue([]);
        });

        this.form.controls.level.disable();
        this.form.controls.uploadManually.disable();
        this.#dialogRef.addPanelClass([DialogSize.LARGE, 'no-padding']);
    }

    updateEventIdsToChangeHandler(eventIds: number[]): void {
        this.form.controls.eventIdsToChange.setValue(eventIds);
    }

    updateCodesToChangeHandler(codes: string[]): void {
        this.form.controls.codesToChange.setValue(codes);
    }

    close(blacklisted?: boolean, idsToChange?: number[], codesToChange?: string[]): void {
        if (idsToChange && codesToChange) {
            this.#dialogRef.close({ blacklisted, event_ids: idsToChange || [], codes: codesToChange || [] });
        } else {
            this.#dialogRef.close();
        }
    }

    change(): void {
        this.close(this.form.value.action === 'BLACKLIST', this.form.value.eventIdsToChange, this.form.value.codesToChange);
    }

    addChipByLevel(level: string): void {
        if (level === 'EVENT') {
            this.addChip(this.form.controls.manuallyEventIdInput, this.form.controls.eventIdsToChange);
        } else {
            this.addChip(this.form.controls.manuallyCodeInput, this.form.controls.codesToChange);
        }
    }

    addChip<T>(inputControl: FormControl<T>, formControl: FormControl<T[]>): void {
        if (inputControl.valid) {
            if (formControl.value.length === 0) {
                formControl.setValue([inputControl.value]);
                this.chipsIdsToChange$.next([{ label: inputControl.value.toString(), value: inputControl.value }]);
            } else if (!formControl.value?.includes(inputControl.value)) {
                formControl.setValue(formControl.value.concat([inputControl.value]));
                this.chipsIdsToChange$.next(this.chipsIdsToChange$.getValue().concat([{ label: inputControl.value.toString(), value: inputControl.value }]));
            }
            inputControl.reset();
        } else {
            inputControl.markAsTouched();
        }
    }

    removeChip(chip: Chip): void {
        if (this.form.value.level === 'EVENT') {
            this.form.controls.eventIdsToChange.setValue(this.form.value.eventIdsToChange.filter(id => id !== chip.value));
            this.form.controls.manuallyEventIdInput.reset();
        } else {
            this.form.controls.codesToChange.setValue(this.form.value.codesToChange.filter(id => id !== chip.value));
            this.form.controls.manuallyCodeInput.reset();
        }
        this.chipsIdsToChange$.next(this.chipsIdsToChange$.getValue().filter(c => c.value !== chip.value));
    }

    private eventIdFormatValidator(control: AbstractControl): ValidationErrors | null {
        const regex = /^[0-9]*$/;
        if (!control.value || regex.test(control.value)) {
            return null;
        } else {
            return { invalidEventIdFormat: true };
        }
    }

    private codesFormatValidator(control: AbstractControl): ValidationErrors | null {
        const regex = /^[A-Z0-9]*$/;
        if (!control.value || regex.test(control.value)) {
            return null;
        } else {
            return { invalidCodeFormat: true };
        }
    }
}
