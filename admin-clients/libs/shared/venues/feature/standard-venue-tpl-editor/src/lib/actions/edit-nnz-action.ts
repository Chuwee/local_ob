import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { EdNotNumberedZone } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';

export class EditNnzAction extends VenueTplEditorBaseAction {

    private readonly _changedFields = {
        name: false,
        sector: false,
        capacity: false
    };

    private _prevNNZ: Partial<EdNotNumberedZone>;

    get itemId(): number {
        return this._newNnz.id;
    }

    get changedFields(): { [key: string]: boolean } {
        return this._changedFields;
    }

    constructor(private readonly _newNnz: Partial<EdNotNumberedZone>, private _mapSrv: VenueTplEditorVenueMapService) {
        super();
        // creates a copy to avoid indirect external changes
        this._newNnz = { ...this._newNnz };
        this._mapSrv.getVenueMap$()
            .pipe(take(1))
            .subscribe(venueMap => {
                // creates a copy to avoid indirect external changes
                this._prevNNZ = { ...venueMap.sectors.flatMap(sector => sector.notNumberedZones).find(nnz => nnz.id === _newNnz.id) };
                this._changedFields.name = this._newNnz.name !== undefined && this._newNnz.name !== this._prevNNZ.name;
                this._changedFields.sector = this._newNnz.sector !== undefined && this._newNnz.sector !== this._prevNNZ.sector;
                this._changedFields.capacity =  this._newNnz.capacity !== undefined && this._newNnz.capacity !== this._prevNNZ.capacity;
                this.setReadyStatus();
                this.do();
            });
    }

    override canCombineWith(action: VenueTplEditorBaseAction): boolean {
        return action instanceof EditNnzAction
            && action.itemId === this.itemId
            && this.changedFieldsComparator(this.changedFields, action.changedFields);
    }

    protected do(): void {
        this._mapSrv.editNotNumberedZone(this._newNnz, true);
    }

    protected undo(): void {
        this._mapSrv.editNotNumberedZone(this._prevNNZ, this._prevNNZ.modify);
    }
}
