import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { map, take } from 'rxjs/operators';
import { EditSeatsAction } from '../../actions/edit-seats-action';
import { EditorMode } from '../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorBlocksSetupService } from '../../venue-tpl-editor-blocks-setup.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe
    ],
    selector: 'app-venue-tpl-editor-blocks-setup-buttons',
    templateUrl: './venue-tpl-editor-blocks-setup-buttons.component.html',
    styleUrls: ['./venue-tpl-editor-blocks-setup-buttons.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorBlocksSetupButtonsComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _blocksSetupSrv = inject(VenueTplEditorBlocksSetupService);

    readonly blocksSetupNotModifiedSeats$ = this._blocksSetupSrv.getModifiedSeats$()
        .pipe(map(modifiedSeats => modifiedSeats.length === 0));

    setBaseMode(): void {
        this._editorSrv.modes.setEditorMode(EditorMode.base);
    }

    commitAislesConfiguration(): void {
        this._blocksSetupSrv.getModifiedSeats$()
            .pipe(take(1))
            .subscribe(seats => {
                this._editorSrv.history.enqueue(new EditSeatsAction(seats, this._venueMapSrv));
                this.setBaseMode();
            });
    }
}
