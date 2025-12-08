import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';

export class EditRowAction extends VenueTplEditorBaseAction {

    private _prevRowData: {
        name: string;
        modify: boolean;
    };

    constructor(
        private _newRowData: { id: number; name: string },
        private _venueTplEdMapSrv: VenueTplEditorVenueMapService
    ) {
        super();
        this._venueTplEdMapSrv.getVenueItems$()
            .pipe(take(1))
            .subscribe(items => {
                const row = items.rows.get(_newRowData.id);
                this._prevRowData = {
                    name: row.name,
                    modify: !!row.modify
                };
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._venueTplEdMapSrv.editRow({ id: this._newRowData.id, name: this._newRowData.name });
    }

    protected undo(): void {
        this._venueTplEdMapSrv.editRow({ id: this._newRowData.id, name: this._prevRowData.name }, this._prevRowData.modify);
    }

}
