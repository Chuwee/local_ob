import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';

export class EditSeatsAction extends VenueTplEditorBaseAction {

    private _prevSeatData: Partial<EdSeat>[];

    constructor(private readonly _newSeatData: Partial<EdSeat>[], private _venueTplEdMapSrv: VenueTplEditorVenueMapService) {
        super();
        this._newSeatData = this._newSeatData.map(seatData => ({ ...seatData }));
        this._venueTplEdMapSrv.getVenueItems$()
            .pipe(take(1))
            .subscribe(items => {
                this._prevSeatData = this._newSeatData.map(seatData => ({ ...items.seats.get(seatData.id) }));
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._venueTplEdMapSrv.editSeats(this._newSeatData.map(seatData => ({ ...seatData, modify: true })));
    }

    protected undo(): void {
        this._venueTplEdMapSrv.editSeats(this._prevSeatData.map(seatData => ({ ...seatData })));
    }
}
