import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditCurrentViewAction extends VenueTplEditorBaseAction {

    private _sourceViewId: number;

    constructor(private _targetViewId: number, private _viewsSrv: VenueTplEditorViewsService) {
        super();
        this._viewsSrv.getViewData$()
            .pipe(take(1))
            .subscribe(viewData => {
                this._sourceViewId = viewData?.view.id;
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._viewsSrv.setCurrentView(this._targetViewId);
    }

    protected undo(): void {
        this._viewsSrv.setCurrentView(this._sourceViewId);
    }
}
