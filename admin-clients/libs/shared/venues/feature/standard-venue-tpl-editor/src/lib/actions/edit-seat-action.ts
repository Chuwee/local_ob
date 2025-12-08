import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';

export class EditSeatAction extends VenueTplEditorBaseAction {

    private readonly _changedFields = {
        name: false,
        order: false,
        weight: false
    };

    private _prevSeatData: Partial<EdSeat>;

    get itemId(): number {
        return this._newSeatData.id;
    }

    get changedFields(): { [key: string]: boolean } {
        return this._changedFields;
    }

    constructor(private readonly _newSeatData: Partial<EdSeat>, private _venueTplEdMapSrv: VenueTplEditorVenueMapService) {
        super();
        this._newSeatData = { ...this._newSeatData };
        this._venueTplEdMapSrv.getVenueItems$()
            .pipe(take(1))
            .subscribe(items => {
                this._prevSeatData = { ...items.seats.get(this._newSeatData.id) };
                this._changedFields.name = this._newSeatData.name != null && this._newSeatData.name !== this._prevSeatData.name;
                this._changedFields.order = this._newSeatData.order != null && this._newSeatData.order !== this._prevSeatData.order;
                this._changedFields.weight = this._newSeatData.weight != null && this._newSeatData.weight !== this._prevSeatData.weight;
                this.setReadyStatus();
                this.do();
            });
    }

    override canCombineWith(action: VenueTplEditorBaseAction): boolean {
        return action instanceof EditSeatAction
            && this.itemId === action.itemId
            && this.changedFieldsComparator(this.changedFields, action.changedFields);
    }

    protected do(): void {
        this._venueTplEdMapSrv.editSeats([{ ...this._newSeatData, modify: true }]);
    }

    protected undo(): void {
        this._venueTplEdMapSrv.editSeats([{ ...this._prevSeatData }]);
    }
}
