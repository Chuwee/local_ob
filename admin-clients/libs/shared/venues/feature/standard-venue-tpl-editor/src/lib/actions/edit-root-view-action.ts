import { VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorViewData } from '../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditRootViewAction extends VenueTplEditorBaseAction {

    private _newViewModify: boolean;
    private _prevViewModify: boolean;
    private _prevRootView: VenueTemplateView;

    constructor(private readonly _viewData: VenueTplEditorViewData, private readonly _viewsSrv: VenueTplEditorViewsService) {
        super();
        this._viewsSrv.getViewDatas$()
            .pipe(take(1))
            .subscribe(views => {
                this._newViewModify = !!_viewData.modify;
                const rootViewData = views.find(viewData => viewData.view.root);
                this._prevRootView = rootViewData.view;
                this._prevViewModify = !!rootViewData.modify;
                this.setReadyStatus();
                this.do();
            });
    }

    protected do(): void {
        this._viewsSrv.changeRootView(this._viewData.view);
    }

    protected undo(): void {
        this._viewsSrv.changeRootView(this._prevRootView, this._prevViewModify, this._newViewModify);
    }
}
