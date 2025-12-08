import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ExportDelivery, ExportDeliveryType, ExportField, ExportFormat, ExportResponse } from '@admin-clients/shared/data-access/models';
import { InteractiveVenueTemplateFileOption, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { first, map, takeUntil } from 'rxjs/operators';
import { interactiveVenueTemplateSeatsColumnList } from './interactive-venue-template-seats-column-list';
import { interactiveVenueTemplateSectorsColumnList } from './interactive-venue-template-sectors-column-list';

@Component({
    selector: 'app-export-interactive-venue-template-file-dialog',
    templateUrl: './export-interactive-venue-template-file-dialog.component.html',
    styleUrls: ['./export-interactive-venue-template-file-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule, MatIcon, ReactiveFormsModule, MatFormFieldModule, MatLabel, MatInput,
        FormControlErrorsComponent, FlexLayoutModule, TranslatePipe, MatButtonModule,
        UpperCasePipe, MatProgressSpinner, AsyncPipe
    ]
})
export class ExportInteractiveVenueTemplateFileDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    readonly fileOptions = InteractiveVenueTemplateFileOption;

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<ExportInteractiveVenueTemplateFileDialogComponent, ExportResponse>,
        private _venueTemplatesService: VenueTemplatesService,
        private _fb: UntypedFormBuilder,
        private _auth: AuthenticationService,
        private _translate: TranslateService,
        @Inject(MAT_DIALOG_DATA) private _venueTemplateId: number
    ) {
        this._dialogRef.addPanelClass(DialogSize.SMALL);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            email: [null, [Validators.required, Validators.email]]
        });

        //Add logged user email as default
        this._auth.getLoggedUser$()
            .pipe(
                first(user => !!user),
                map(user => user.email),
                takeUntil(this._onDestroy)
            )
            .subscribe(email => this.form.get('email').setValue(email));

        this.isInProgress$ = this._venueTemplatesService.isExportInteractiveVenueTemplatesFileLoading$();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(exportResponse: ExportResponse = null): void {
        this._dialogRef.close(exportResponse);
    }

    exportFile(fileOption: InteractiveVenueTemplateFileOption): void {
        if (this.form.valid) {
            let exportRequest$: Observable<ExportResponse>;
            const delivery: ExportDelivery = {
                type: ExportDeliveryType.email,
                properties: {
                    address: this.form.get('email').value
                }
            };

            if (fileOption === InteractiveVenueTemplateFileOption.jsonViews) {
                const format = ExportFormat.json;
                const request = {
                    format,
                    delivery
                };
                exportRequest$ = this._venueTemplatesService.exportInteractiveVenueTemplateFile(
                    this._venueTemplateId, request, InteractiveVenueTemplateFileOption.jsonViews
                );
            } else if (fileOption === InteractiveVenueTemplateFileOption.csvSeats) {
                const format = ExportFormat.csv;
                const request = {
                    fields: this.prepareFieldTranslation(interactiveVenueTemplateSeatsColumnList),
                    format,
                    delivery
                };
                exportRequest$ = this._venueTemplatesService.exportInteractiveVenueTemplateFile(
                    this._venueTemplateId, request, InteractiveVenueTemplateFileOption.csvSeats
                );
            } else if (fileOption === InteractiveVenueTemplateFileOption.csvSectors) {
                const format = ExportFormat.csv;
                const request = {
                    fields: this.prepareFieldTranslation(interactiveVenueTemplateSectorsColumnList),
                    format,
                    delivery
                };
                exportRequest$ = this._venueTemplatesService.exportInteractiveVenueTemplateFile(
                    this._venueTemplateId, request, InteractiveVenueTemplateFileOption.csvSectors
                );
            }

            exportRequest$.subscribe(exportResponse => this.close(exportResponse));
        } else {
            this.form.markAllAsTouched();
        }
    }

    private prepareFieldTranslation(columnList: ExportField[]): ExportField[] {
        return columnList.map(exportField => ({ ...exportField, name: this._translate.instant(exportField.name) }));
    }

}
