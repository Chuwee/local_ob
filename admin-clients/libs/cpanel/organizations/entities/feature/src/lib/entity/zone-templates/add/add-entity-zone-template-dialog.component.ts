import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-entity-zone-template-dialog',
    templateUrl: './add-entity-zone-template-dialog.component.html',
    imports: [
        TranslatePipe, ReactiveFormsModule, MatIcon, MatDialogModule, MatButtonModule,
        MatError, MatFormFieldModule, FormControlErrorsComponent, MatInput
    ],
    styleUrls: ['./add-entity-zone-template-dialog.component.scss']
})
export class AddEntityZoneTemplateDialogComponent implements OnInit {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<AddEntityZoneTemplateDialogComponent, boolean>);
    readonly #data = inject<{ entityId: number }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        name: [null as string, Validators.required],
        code: [null as string, Validators.required]
    });

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    close(): void {
        this.#dialogRef.close(false);
    }

    createTemplate(): void {
        if (this.form.dirty) {
            this.#entitiesSrv.zoneTemplates.post(this.#data.entityId, this.form.value)
                .pipe(first(Boolean))
                .subscribe(template => {
                    this.#entitiesSrv.zoneTemplates.load(this.#data.entityId, { limit: 999, offset: 0 });
                    this.#dialogRef.close(template.id);
                });
        }
    }
}
