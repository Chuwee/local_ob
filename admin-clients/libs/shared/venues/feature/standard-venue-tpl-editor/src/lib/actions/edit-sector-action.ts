import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdSector } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';

export class EditSectorAction extends VenueTplEditorBaseAction {

    private _prevSectorData: {
        id: number;
        name: string;
        code: string;
        modify: boolean;
    };

    private _sector: EdSector;

    constructor(
        private _sectorData: { id: number; name: string; code: string },
        private _venueTplEdMapSrv: VenueTplEditorVenueMapService,
        private _venueTplEdSrv: VenueTplEditorService
    ) {
        super();
        this._venueTplEdMapSrv.getVenueMap$()
            .pipe(take(1))
            .subscribe(venueMap => {
                this._sector = venueMap.sectors.find(s => s.id === this._sectorData.id);
                this._prevSectorData = {
                    id: this._sector.id,
                    name: this._sector.name,
                    code: this._sector.code,
                    modify: !!this._sector.modify
                };
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._venueTplEdMapSrv.editSector(this._sectorData);
    }

    protected undo(): void {
        this._venueTplEdMapSrv.editSector(this._prevSectorData);
    }
}
