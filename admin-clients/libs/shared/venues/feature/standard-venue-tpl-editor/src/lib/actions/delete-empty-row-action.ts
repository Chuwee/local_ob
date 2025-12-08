import { EdRow } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';

export class DeleteEmptyRowAction extends VenueTplEditorBaseAction {

    constructor(
        private readonly _row: EdRow,
        private readonly _mapSrv: VenueTplEditorVenueMapService
    ) {
        super();
        if (!_row.seats?.length || _row.seats.every(seat => seat.delete)) {
            this.setReadyStatus();
            this.do();
        } else {
            this.status = 'forbidden';
            console.error('This action must not be performed with not empty rows, delete all row seats instead');
        }
    }

    protected do(): void {
        this._mapSrv.deleteRow(this._row);
    }

    protected undo(): void {
        this._mapSrv.deleteRow(this._row, true);
    }
}
