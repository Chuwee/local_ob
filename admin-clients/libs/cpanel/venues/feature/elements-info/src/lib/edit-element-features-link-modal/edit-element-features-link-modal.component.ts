import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FeatureAction, FeatureType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import {
    MAT_DIALOG_DATA,
    MatDialogRef,
    MatDialogModule
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-edit-element-features-link-modal',
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        MatDialogModule,
        MaterialModule,
        FlexModule,
        FormControlErrorsComponent
    ],
    templateUrl: './edit-element-features-link-modal.component.html',
    styleUrls: ['./edit-element-features-link-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementFeaturesLinkModalComponent implements OnDestroy {
    private readonly _dialogRef = inject(MatDialogRef<EditElementFeaturesLinkModalComponent>);
    private _onDestroy = new Subject<void>();

    private readonly _oldFeature = inject(MAT_DIALOG_DATA).feature?.value;
    readonly form = inject(MAT_DIALOG_DATA).feature;

    readonly linkActions = Object.values(FeatureAction);
    readonly featureType = FeatureType;

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);

        this.form.controls.type.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(featureType => {
                if (featureType === FeatureType.link) {
                    this.form.controls.url.enable();
                    this.form.controls.action.enable();
                } else {
                    this.form.controls.url.disable();
                    this.form.controls.action.disable();
                }
            });
    }

    close(): void {
        this.form.patchValue(this._oldFeature);
        this._dialogRef.close();
    }

    save(): void {
        if (this.form.valid) {
            this._dialogRef.close();
        } else {
            this.form.markAllAsTouched();
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }
}
