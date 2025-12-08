import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { VenueTemplateInteractive, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import {
    ExportInteractiveVenueTemplateFileDialogComponent
} from './export-interactive-venue-template-file/export-interactive-venue-template-file-dialog.component';

@Component({
    selector: 'app-interactive-venue-template-dialog',
    templateUrl: './interactive-venue-template-dialog.component.html',
    styleUrls: ['./interactive-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatIcon, MatDivider, MatFormFieldModule, MatSlideToggleModule, MatSelectModule, TranslatePipe,
        ReactiveFormsModule, FormControlErrorsComponent, UpperCasePipe, MatDialogModule, MatButtonModule,
        FlexLayoutModule, MatInput
    ]
})
export class InteractiveVenueTemplateDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<InteractiveVenueTemplateDialogComponent>);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #onDestroy: Subject<void> = new Subject();
    readonly form = this.#fb.group({
        enabled: false,
        multimedia_content_code: [{ value: null, disabled: true }, Validators.required],
        external_minimap_id: [{ value: null, disabled: true }],
        external_plugin_ids: [[] as { id: number; name: string; type: string }[]]
    });

    plugins: { id: number; name: string; type: string; enabled: boolean }[];

    constructor(
        @Inject(MAT_DIALOG_DATA) private _data: { venueTemplateId: number; venueTemplateInteractive: VenueTemplateInteractive }
    ) {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.plugins = this._data.venueTemplateInteractive?.external_plugins || [];

        this.form.patchValue({
            enabled: this._data.venueTemplateInteractive.enabled,
            multimedia_content_code: this._data.venueTemplateInteractive?.multimedia_content_code,
            external_minimap_id: this._data.venueTemplateInteractive?.external_minimap_id,
            external_plugin_ids: this._data.venueTemplateInteractive?.external_plugins.filter(plugin => plugin.enabled).map(plugin => plugin.id) || []
        });
        this.checkFieldsEnabled();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    updateVenueTemplateInteractive(): void {
        if (this.form.valid) {
            const data = this.form.value;
            this.#venueTemplatesService.updateVenueTemplateInteractive(this._data.venueTemplateId, { ...data })
                .subscribe(() => {
                    this.#venueTemplatesService.loadVenueTemplateInteractive(this._data.venueTemplateId);
                    this.close(true);
                });
        } else {
            this.form.markAllAsTouched();
        }
    }

    checkFieldsEnabled(): void {
        const enabled = this.form.get('enabled').value;
        if (enabled) {
            this.form.get('multimedia_content_code').enable();
            this.form.get('external_minimap_id').enable();
        } else {
            this.form.get('multimedia_content_code').disable();
            this.form.get('external_minimap_id').disable();
        }
    }

    close(edited = false): void {
        this.#dialogRef.close(edited);
    }

    openExportInteractiveVenueTemplateFileDialog(): void {
        this.#matDialog.open<ExportInteractiveVenueTemplateFileDialogComponent, number, ExportResponse>(
            ExportInteractiveVenueTemplateFileDialogComponent, new ObMatDialogConfig<number>(this._data.venueTemplateId)
        ).beforeClosed()
            .subscribe(exportResponse => {
                if (exportResponse) {
                    this.#ephemeralMsg.showSuccess({
                        msgKey: 'ACTIONS.EXPORT.OK.MESSAGE'
                    });
                }
            });
    }
}
