import { switchMap } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdSector } from '../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../utils/editor-id-generator.utils';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';

export class NewSectorAction extends VenueTplEditorBaseAction {

    private _nearestSector: EdSector;
    private _addedSector: EdSector;

    constructor(
        private _sectorData: { id: number; name: string; code: string },
        private _venueTplEdMapSrv: VenueTplEditorVenueMapService,
        private _venueTplEdSrv: VenueTplEditorService
    ) {
        super();
        _sectorData.id = IdGenerator.getTempId();
        this.setReadyStatus();
        this.do();
    }

    protected do(): void {
        this._venueTplEdMapSrv.getVenueMap$()
            .pipe(
                take(1),
                tap(venueMap => this._nearestSector = venueMap.sectors[venueMap.sectors.length - 1]),
                switchMap(() => this._venueTplEdMapSrv.addSector(this._sectorData))
            )
            .subscribe(sector => this._addedSector = sector);
    }

    protected undo(): void {
        this._venueTplEdMapSrv.deleteSector(this._addedSector);
    }
}
