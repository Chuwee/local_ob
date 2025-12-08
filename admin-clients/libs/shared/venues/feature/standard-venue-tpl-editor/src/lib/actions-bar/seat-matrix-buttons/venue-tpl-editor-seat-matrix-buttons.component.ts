import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, filter, map, withLatestFrom } from 'rxjs/operators';
import { EditorMode } from '../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorSeatMatrixService } from '../../venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe
    ],
    selector: 'app-venue-tpl-editor-seat-matrix-buttons',
    templateUrl: './venue-tpl-editor-seat-matrix-buttons.component.html',
    styleUrls: ['./venue-tpl-editor-seat-matrix-buttons.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixButtonsComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    readonly numSeats$ = this._seatMatrixSrv.getSeatMatrixConf$()
        .pipe(
            filter(Boolean),
            debounceTime(10),
            withLatestFrom(this._venueMapSrv.getVenueMap$(), this._viewSrv.getViewData$()),
            map(([conf, venueMap, viewData]) => {
                if (!conf.continueRows) {
                    return conf.matrix ? conf.matrix.seats * conf.matrix.rows : 0;
                } else {
                    const rows = venueMap.sectors.find(sector => sector.id === conf.rowContinuation.sector)
                        ?.rows
                        ?.filter(row => !row.delete && row.seats.some(seat => !seat.delete && seat.view === viewData.view.id))
                        ?.sort((a, b) => a.order - b.order)
                        ?? [];
                    const fromRowIndex = rows.indexOf(rows.find(row => row.id === conf.rowContinuation.fromRow));
                    const toRowIndex = rows.indexOf(rows.find(row => row.id === conf.rowContinuation.toRow));
                    const numRows = Math.max(fromRowIndex, toRowIndex) - Math.min(fromRowIndex, toRowIndex) + 1;
                    return numRows * conf.rowContinuation.seats;
                }
            })
        );

    setBaseMode(): void {
        this._seatMatrixSrv.cancelSeatMatrix();
        this._editorSrv.modes.setEditorMode(EditorMode.base);
    }

    commitSeatMatrix(): void {
        this._seatMatrixSrv.commitSeatMatrix();
    }
}
