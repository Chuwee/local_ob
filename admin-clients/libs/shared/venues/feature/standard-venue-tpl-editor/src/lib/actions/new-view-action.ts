import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorViewData } from '../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewViewAction extends VenueTplEditorBaseAction {

    private _prevActiveView: VenueTplEditorViewData;
    private _viewData: VenueTplEditorViewData;

    constructor(private _newView: { id: number; name: string; code: string }, private _viewsSrv: VenueTplEditorViewsService) {
        super();
        this._viewsSrv.getViewData$()
            .pipe(take(1))
            .subscribe(viewData => {
                this._prevActiveView = viewData;
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._viewsSrv.addView(this._newView).subscribe(view => this._viewData = view);
        this._viewsSrv.setCurrentView(this._viewData.view.id);
    }

    protected undo(): void {
        this._viewsSrv.undoAddView(this._viewData.view.id);
        this._viewsSrv.setCurrentView(this._prevActiveView.view.id);
    }
}
