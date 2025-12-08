import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EditRowAction } from '../../actions/edit-row-action';
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
    selector: 'app-venue-tpl-editor-row-dialog',
    templateUrl: './venue-tpl-editor-row-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-row-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorRowDialogComponent extends ObDialog<VenueTplEditorRowDialogComponent, VenueTplEditorBaseDialogData, boolean> {

    private _editorSrv = inject(VenueTplEditorService);
    private _mapSrv = inject(VenueTplEditorVenueMapService);

    @ViewChild(VenueTplEditorBaseDialogComponent)
    private _baseCom: VenueTplEditorBaseDialogComponent;

    readonly nameMaxLength = VenueTemplateFieldsRestrictions.rowNameLength;
    readonly rows$ = this._mapSrv.getVenueMap$().pipe(
        map(venueMap => venueMap.sectors.filter(s => !s.delete).find(sector => !!sector.rows.find(row => row.id === this.data.id)).rows)
    );

    constructor() {
        super(DialogSize.MEDIUM);
    }

    commit(): void {
        this._editorSrv.history.enqueue(new EditRowAction(
            {
                id: this.data?.id,
                name: this._baseCom.form.value.name
            },
            this._mapSrv
        ));
        this.close(true);
    }

    close(edited = false): void {
        this.dialogRef.close(edited);
    }
}
