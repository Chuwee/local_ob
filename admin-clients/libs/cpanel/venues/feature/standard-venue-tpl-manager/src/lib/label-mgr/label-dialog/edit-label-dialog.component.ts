import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ColorPickerComponent, DefaultColors, DialogSize, EphemeralMessageService, ObDialog
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { EditLabelDialogData } from './edit-label-dialog-data';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        ColorPickerComponent
    ],
    selector: 'app-edit-label-dialog',
    templateUrl: './edit-label-dialog.component.html',
    styleUrls: ['./edit-label-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditLabelDialogComponent extends ObDialog<EditLabelDialogComponent, EditLabelDialogData, boolean> {

    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);

    readonly form = this.#fb.group({
        id: this.data.label && Number(this.data.label.id),
        name: [this.data.label?.literal, [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.labelNameLength)]],
        code: this.#fb
            .control({
                value: this.data.label?.code,
                disabled: this.data.labelGroupType === VenueTemplateLabelGroupType.blockingReason
            }, {
                validators: [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.labelCodeLength)]
            }),
        color: [this.data.label?.color ?? this.#getRandomColor(), [Validators.required]]
    });

    readonly templateColors$ = this.#standardVenueTemplateSrv.getLabelGroups$()
        .pipe(map(labelGroups =>
            Array.from(new Set<string>(labelGroups.flatMap(labelGroup =>
                labelGroup.editable ? labelGroup.labels.map(label => label.color).filter(color => color !== '#FFFFFF') : []
            )))
        ));

    readonly loading$ = booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplateGatesLoading$(),
        this.#venueTemplatesSrv.isVenueTemplateBlockingReasonsLoading$(),
        this.#venueTemplatesSrv.isVenueTemplateQuotasLoading$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$()
    ]);

    readonly title = this.#getTitleKey();

    constructor() {
        super(DialogSize.MEDIUM, false);
        if (this.data.isCreation && this.form.controls.code.enabled) {
            FormControlHandler.reflectControlValue(
                this.form.controls.name,
                this.form.controls.code,
                VenueTemplateFieldsRestrictions.labelCodeLength
            )
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe();
        }
    }

    save(): void {
        if (this.form.valid) {
            this.#getSaveObs().subscribe(() => {
                this.dialogRef.close(true);
                this.#ephemeralSrv.showSuccess({ msgKey: this.#getCreationSuccessMessageKey(this.data.labelGroupType) });
            });
        }
    }

    cancel(): void {
        this.dialogRef.close();
    }

    #getRandomColor(): string {
        const colors = Object.values(DefaultColors);
        return colors[Math.floor(Math.random() * (colors.length - 1))];
    }

    #getTitleKey(): string {
        switch (this.data.labelGroupType) {
            case VenueTemplateLabelGroupType.priceType:
                return this.data.isCreation ? 'VENUE_TPL_MGR.TITLES.NEW_PRICE_TYPE' : 'VENUE_TPL_MGR.TITLES.PRICE_TYPE_EDITION';
            case VenueTemplateLabelGroupType.quota:
                return this.data.isCreation ? 'VENUE_TPLS.NEW_QUOTA' : 'VENUE_TPL_MGR.TITLES.QUOTA_EDITION';
            case VenueTemplateLabelGroupType.gate:
                return this.data.isCreation ? 'VENUE_TPLS.NEW_GATE' : 'VENUE_TPL_MGR.TITLES.GATE_EDITION';
            case VenueTemplateLabelGroupType.blockingReason:
                return this.data.isCreation ? 'VENUE_TPL_MGR.TITLES.NEW_BLOCKING_REASON' : 'VENUE_TPL_MGR.TITLES.BLOCKING_REASON_EDITION';
            case VenueTemplateLabelGroupType.firstCustomLabelGroup:
                return this.data.isCreation ? 'VENUE_TPL_MGR.TITLES.NEW_CUSTOM_GROUP' : 'VENUE_TPL_MGR.TITLES.CUSTOM_GROUP_EDITION';
            case VenueTemplateLabelGroupType.secondCustomLabelGroup:
                return this.data.isCreation ? 'VENUE_TPL_MGR.TITLES.NEW_CUSTOM_GROUP' : 'VENUE_TPL_MGR.TITLES.CUSTOM_GROUP_EDITION';
        }
    }

    #getSaveObs(): Observable<unknown> {
        const value = this.form.getRawValue();
        const labelGroupType = this.data.labelGroupType;
        const templateId = this.data.templateId;
        switch (labelGroupType) {
            case VenueTemplateLabelGroupType.blockingReason:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.addVenueTemplateBlockingReason(templateId, value)
                    : this.#venueTemplatesSrv.updateVenueTemplateBlockingReason(templateId, value);
            case VenueTemplateLabelGroupType.priceType:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.addVenueTemplatePriceType(templateId, value)
                    : this.#venueTemplatesSrv.updateVenueTemplatePriceType(templateId, value);
            case VenueTemplateLabelGroupType.quota:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.addVenueTemplateQuota(templateId, value)
                    : this.#venueTemplatesSrv.updateVenueTemplateQuota(templateId, value);
            case VenueTemplateLabelGroupType.gate:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.addVenueTemplateGate(templateId, value)
                    : this.#venueTemplatesSrv.updateVenueTemplateGate(templateId, value);
            case VenueTemplateLabelGroupType.firstCustomLabelGroup:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.firstCustomTagGroupLabels.create(templateId, this.data.groupId, value)
                    : this.#venueTemplatesSrv.firstCustomTagGroupLabels.update(templateId, this.data.groupId, value.id, value);
            case VenueTemplateLabelGroupType.secondCustomLabelGroup:
                return this.data.isCreation ?
                    this.#venueTemplatesSrv.secondCustomTagGroupLabels.create(templateId, this.data.groupId, value)
                    : this.#venueTemplatesSrv.secondCustomTagGroupLabels.update(templateId, this.data.groupId, value.id, value);
        }
    }

    #getCreationSuccessMessageKey(selectedLabelGroupType: VenueTemplateLabelGroupType): string {
        switch (selectedLabelGroupType) {
            case VenueTemplateLabelGroupType.blockingReason:
                return 'VENUE_TPLS.CREATE_BLOCKING_REASON_SUCCESS';
            case VenueTemplateLabelGroupType.priceType:
                return 'VENUE_TPLS.CREATE_PRICE_TYPE_SUCCESS';
            case VenueTemplateLabelGroupType.quota:
                return 'VENUE_TPLS.CREATE_QUOTA_SUCCESS';
            case VenueTemplateLabelGroupType.gate:
                return 'VENUE_TPLS.CREATE_GATE_SUCCESS';
            case VenueTemplateLabelGroupType.firstCustomLabelGroup:
                return 'VENUE_TPLS.CREATE_CUSTOM_GROUP_LABEL_SUCCESS';
            case VenueTemplateLabelGroupType.secondCustomLabelGroup:
                return 'VENUE_TPLS.CREATE_CUSTOM_GROUP_LABEL_SUCCESS';
        }
    }
}
