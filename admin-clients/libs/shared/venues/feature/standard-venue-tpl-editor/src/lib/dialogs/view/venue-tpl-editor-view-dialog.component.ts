import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { NewViewAction } from '../../actions/new-view-action';
import { IdGenerator } from '../../utils/editor-id-generator.utils';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';
import { VenueTplEditorBaseDialogComponent } from '../edit-base/venue-tpl-editor-base-dialog.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        VenueTplEditorBaseDialogComponent
    ],
    selector: 'app-venue-tpl-editor-view-dialog',
    templateUrl: './venue-tpl-editor-view-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-view-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorViewDialogComponent extends ObDialog<VenueTplEditorViewDialogComponent, void, void> {

    @ViewChild(VenueTplEditorBaseDialogComponent)
    private readonly _baseCom: VenueTplEditorBaseDialogComponent;

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);

    readonly title = 'VENUE_TPL_EDITOR.NEW_VIEW_TITLE';
    readonly nameMaxLength = VenueTemplateFieldsRestrictions.viewNameLength;
    readonly codeMaxLength = VenueTemplateFieldsRestrictions.viewCodeLength;

    readonly existentViews = this._viewSrv.getViewDatas$()
        .pipe(map(vds => vds.filter(vd => !vd.delete).map(vd => ({ name: vd.view.name, code: vd.view.code }))));

    constructor() {
        super(DialogSize.MEDIUM);
    }

    commit(): void {
        const value = {
            id: IdGenerator.getTempId(),
            name: this._baseCom.form.value.name,
            code: this._baseCom.form.value.code
        };
        this._editorSrv.history.enqueue(new NewViewAction(value, this._viewSrv));
        this.dialogRef.close();
    }

    close(): void {
        this.dialogRef.close();
    }
}
