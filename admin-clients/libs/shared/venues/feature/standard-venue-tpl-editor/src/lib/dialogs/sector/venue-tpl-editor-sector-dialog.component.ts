import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EditSectorAction } from '../../actions/edit-sector-action';
import { NewSectorAction } from '../../actions/new-sector-action';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';
import { VenueTplEditorBaseDialogComponent } from '../edit-base/venue-tpl-editor-base-dialog.component';
import { VenueTplEditorBaseDialogData } from '../edit-base/venue-tpl-editor-base-dialog.data';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        VenueTplEditorBaseDialogComponent
    ],
    selector: 'app-venue-tpl-editor-sector-dialog',
    templateUrl: './venue-tpl-editor-sector-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-sector-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSectorDialogComponent
    extends ObDialog<VenueTplEditorSectorDialogComponent, VenueTplEditorBaseDialogData, boolean> {

    private _editorSrv = inject(VenueTplEditorService);
    private _mapSrv = inject(VenueTplEditorVenueMapService);

    @ViewChild(VenueTplEditorBaseDialogComponent)
    private _baseCom: VenueTplEditorBaseDialogComponent;

    readonly title = this.data?.id ? 'VENUE_TPL_EDITOR.EDIT_SECTOR' : 'VENUE_TPL_EDITOR.ADD_SECTOR';
    readonly nameMaxLength = VenueTemplateFieldsRestrictions.sectorNameLength;
    readonly codeMaxLength = VenueTemplateFieldsRestrictions.sectorCodeLength;
    readonly sectors$ = this._mapSrv.getVenueMap$().pipe(
        map(venueMap => venueMap.sectors.filter(s => !s.delete && this.data?.id !== s.id).map(s => ({ name: s.name, code: s.code })))
    );

    constructor() {
        super(DialogSize.MEDIUM);
    }

    commit(): void {
        const value = {
            id: this.data?.id,
            name: this._baseCom.form.value.name,
            code: this._baseCom.form.value.code
        };
        if (this.data?.id) {
            this._editorSrv.history.enqueue(new EditSectorAction(value, this._mapSrv, this._editorSrv));
        } else {
            this._editorSrv.history.enqueue(new NewSectorAction(value, this._mapSrv, this._editorSrv));
        }
        this.close(true);
    }

    close(edited = false): void {
        this.dialogRef.close(edited);
    }
}
