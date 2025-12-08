import { VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditViewAction extends VenueTplEditorBaseAction {

    private readonly _changedFields = {
        name: false,
        code: false
    };

    private _prevView: Partial<VenueTemplateView> ;
    private _prevModify: boolean;

    get itemId(): number {
        return this._newView.id;
    }

    get changedFields(): { [key: string]: boolean } {
        return this._changedFields;
    }

    constructor(
        private _newView: Partial<VenueTemplateView>,
        private _viewsSrv: VenueTplEditorViewsService,
        private _selectionSrv: VenueTplEditorSelectionService
    ) {
        super();
        this._viewsSrv.getViewDatas$()
            .pipe(take(1))
            .subscribe(viewDataList => {
                const viewData = viewDataList.find(viewData => viewData.view.id === this._newView.id);
                this._prevView = { ...viewData.view };
                this._prevModify = !!viewData.modify;
                this._changedFields.name = this._newView.name != null && this._newView.name !== this._prevView.name;
                this._changedFields.code = this._newView.code != null && this._newView.code !== this._prevView.code;
                this.setReadyStatus();
                this.do();
            });
    }

    override canCombineWith(action: VenueTplEditorBaseAction): boolean {
        return action instanceof EditViewAction
            && this.itemId === action.itemId
            && this.changedFieldsComparator(this.changedFields, action.changedFields);
    }

    protected do(): void {
        this._viewsSrv.updateView(this._newView, true);
    }

    protected undo(): void {
        this._viewsSrv.updateView(this._prevView, this._prevModify);
    }
}
