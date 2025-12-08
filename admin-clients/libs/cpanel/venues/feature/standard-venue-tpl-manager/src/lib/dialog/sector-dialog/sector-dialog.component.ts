import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Sector, StdVenueTplService, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTemplateSectorDialogData } from '../../models/venue-template-sector-dialog-data.model';
import { SectorActionType } from '../../models/venue-tpl-tree-dialog-type.enum';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { VenueTemplateTreeService } from '../../tree-view/venue-template-tree.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule
    ],
    selector: 'app-sector-dialog',
    templateUrl: './sector-dialog.component.html',
    styleUrls: ['./sector-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SectorDialogComponent implements OnInit {

    form: UntypedFormGroup;
    venueTemplateFieldsRestrictions = VenueTemplateFieldsRestrictions;
    windowTitleKey: string;
    windowCommitLabelKey: string;
    loading$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<SectorDialogComponent>,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        private _standardVenueTemplateService: StandardVenueTemplateBaseService,
        private _venueTemplateTreeSrv: VenueTemplateTreeService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: VenueTemplateSectorDialogData) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.loading$ = this._stdVenueTplSrv.isSectorSaving$();
        let nameFieldValue: string = null;
        switch (this._data.action) {
            case SectorActionType.create:
            case SectorActionType.increaseCreate:
                this.windowTitleKey = 'VENUE_TPLS.ADD_SECTOR';
                this.windowCommitLabelKey = 'FORMS.ACTIONS.CREATE';
                break;
            case SectorActionType.clone:
                this.windowTitleKey = 'VENUE_TPLS.CLONE_SECTOR';
                this.windowCommitLabelKey = 'FORMS.ACTIONS.CLONE';
                break;
            case SectorActionType.editName:
            case SectorActionType.increaseEdit:
                this.windowTitleKey = 'VENUE_TPL_MGR.ACTIONS.EDIT_SECTOR';
                this.windowCommitLabelKey = 'FORMS.ACTIONS.SAVE';
                nameFieldValue = this._data.sector.name;
                break;
        }
        this.form = this._fb.group({
            name: [nameFieldValue, [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.sectorNameLength)]]
        }
        );
    }

    commitForm(): void {
        if (this.form.valid) {
            switch (this._data.action) {
                case SectorActionType.create:
                    this._stdVenueTplSrv.createSector(this._data.venueTemplate.id, this.form.get('name').value)
                        .subscribe(({ id }) => this.close(id));
                    break;
                case SectorActionType.clone:
                    this._stdVenueTplSrv.cloneSector(this._data.venueTemplate.id, this._data.sector.id, this.form.get('name').value)
                        .subscribe(({ id }) => this.close(id));
                    break;
                case SectorActionType.editName:
                    this._stdVenueTplSrv.updateSectorName(this._data.venueTemplate.id, {
                        itemType: VenueTemplateItemType.sector,
                        id: this._data.sector.id,
                        name: this.form.get('name').value
                    } as Sector)
                        .subscribe(() => this.close(this._data.sector.id));
                    break;
                case SectorActionType.increaseCreate:
                    this._venueTemplateTreeSrv.createSectorCapacityIncrease(this.form.get('name').value)
                        .pipe(take(1))
                        .subscribe(id => this.close(id));
                    break;
                case SectorActionType.increaseEdit:
                    this._venueTemplateTreeSrv.updateSectorCapacityIncrease(this._data.sector.id, this.form.get('name').value);
                    this.close(Number(this._data.sector.id));
            }
        }
    }

    close(newSectorId: number = null): void {
        this._dialogRef.close(newSectorId);
    }
}
