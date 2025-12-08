import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { VenueTemplate, VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-clone-event-venue-template-dialog',
    templateUrl: './clone-event-venue-template-dialog.component.html',
    styleUrls: ['./clone-event-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        venuesProviders
    ],
    imports: [
        MatDialogModule, MatFormFieldModule, MatIcon, ReactiveFormsModule, TranslatePipe, MatButtonModule,
        FlexLayoutModule, MatInput
    ]
})
export class CloneEventVenueTemplateDialogComponent implements OnInit {
    private _fromVenueTemplate: VenueTemplate;

    form: UntypedFormGroup;
    restrictions = VenueTemplateFieldsRestrictions;

    constructor(
        private _dialogRef: MatDialogRef<CloneEventVenueTemplateDialogComponent>,
        private _venueTemplatesService: VenueTemplatesService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) data: { fromVenueTemplate: VenueTemplate }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._fromVenueTemplate = data.fromVenueTemplate;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            name: [null, [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.nameLength)]]
        });
    }

    cloneVenueTemplate(): void {
        if (this.isValid()) {
            const fromVenueTemplateId = this._fromVenueTemplate.id;
            this._venueTemplatesService.cloneVenueTemplate(fromVenueTemplateId, { name: this.form.value.name })
                .subscribe(id => this.close(id));
        }
    }

    close(newVenueTemplateId: number = null): void {
        this._dialogRef.close(newVenueTemplateId);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            return false;
        }
    }
}
