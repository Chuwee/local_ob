import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdNotNumberedZone, EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from '../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class DeleteViewAction extends VenueTplEditorBaseAction {

    private _seatToDelete: EdSeat[];
    private _nnzToDelete: EdNotNumberedZone[];

    constructor(
        private readonly _viewData: VenueTplEditorViewData,
        private readonly _viewsSrv: VenueTplEditorViewsService,
        private readonly _venueMapSrv: VenueTplEditorVenueMapService
    ) {
        super();
        this._venueMapSrv.getVenueItems$()
            .pipe(take(1))
            .subscribe(venueItems => {
                this._seatToDelete =
                    Array.from(venueItems.seats.values()).filter(item => !item.delete && item.view === this._viewData.view.id);
                this._nnzToDelete =
                    Array.from(venueItems.nnzs.values()).filter(item => !item.delete && item.view === this._viewData.view.id);
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._venueMapSrv.deleteSeats(this._seatToDelete);
        this._venueMapSrv.deleteNNZs(this._nnzToDelete);
        this._viewsSrv.removeView(this._viewData);
    }

    protected undo(): void {
        this._venueMapSrv.deleteSeats(this._seatToDelete, true);
        this._venueMapSrv.deleteNNZs(this._nnzToDelete, true);
        this._viewsSrv.removeView(this._viewData, true);
    }
}
