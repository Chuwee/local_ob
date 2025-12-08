import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import {
    VenueTemplate, VenueTemplateFieldsRestrictions, VenueTemplateScope, VenueTemplatesService, VenueTemplatesState
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-clone-promoter-venue-template-dialog',
    templateUrl: './clone-promoter-venue-template-dialog.component.html',
    styleUrls: ['./clone-promoter-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        VenueTemplatesService, VenueTemplatesState,
        venuesProviders
    ],
    imports: [
        MatDialogModule, MatFormFieldModule, MatIcon, ReactiveFormsModule, TranslatePipe, MatButtonModule,
        AsyncPipe, FlexLayoutModule, MatInput
    ]
})
export class ClonePromoterVenueTemplateDialogComponent implements OnInit {
    private _fromVenueTemplate: VenueTemplate;

    restrictions = VenueTemplateFieldsRestrictions;
    form: UntypedFormGroup;
    isLoading$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<ClonePromoterVenueTemplateDialogComponent>,
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
        this.isLoading$ = this._venueTemplatesService.isVenueTemplateSaving$();
    }

    createVenueTemplate(): void {
        if (this.isValid()) {
            this._venueTemplatesService.createVenueTemplate({
                name: this.form.value.name,
                from_template_id: this._fromVenueTemplate.id,
                venue_id: this._fromVenueTemplate.venue.id,
                entity_id: this._fromVenueTemplate.entity.id,
                type: this._fromVenueTemplate.type,
                scope: VenueTemplateScope.standard
            })
                .subscribe(() => this.close());
        }
    }

    close(): void {
        this._dialogRef.close();
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
