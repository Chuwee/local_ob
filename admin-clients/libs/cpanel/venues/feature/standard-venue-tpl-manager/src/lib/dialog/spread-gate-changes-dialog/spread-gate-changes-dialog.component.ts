import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { GateUpdateType, occupiedStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, Validators, FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { StandardVenueTemplateSelectionService } from '../../services/standard-venue-template-selection.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-spread-gate-changes-dialog',
    templateUrl: './spread-gate-changes-dialog.component.html',
    styleUrls: ['./spread-gate-changes-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SpreadGateChangesDialogComponent {
    readonly gateUpdateTypes = Object.values(GateUpdateType);
    readonly form = new FormGroup({
        strategy: new FormControl<GateUpdateType>(null, Validators.required)
    });

    seats = 0;
    nnzLocations = 0;

    constructor(
        private _dialogRef: MatDialogRef<SpreadGateChangesDialogComponent, GateUpdateType>,
        private _standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        combineLatest([
            this._standardVenueTemplateSelectionSrv.getSelectedVenueItems$(),
            this._standardVenueTemplateSrv.getVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([selectedItems, venueItems]) => {
                if (selectedItems.seats.size) {
                    this.seats = Array.from(selectedItems.seats)
                        .filter(seatId => occupiedStatus.includes(venueItems.seats.get(seatId).status)).length;
                }
                if (selectedItems.nnzs.size) {
                    this.nnzLocations = Array.from(selectedItems.nnzs)
                        .reduce((locations, id) => {
                            const value = venueItems.nnzs.get(id).statusCounters.find(c => occupiedStatus.includes(c.status))?.count;
                            return locations + (value === undefined ? 0 : value);
                        }, 0);
                }
            });
    }

    selectMode(): void {
        if (this.form.valid) {
            this._dialogRef.close(this.form.value.strategy);
        }
    }

    close(type: GateUpdateType = null): void {
        this._dialogRef.close(type);
    }
}
