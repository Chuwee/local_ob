import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorService } from '../venue-tpl-editor.service';

export class EditIncreaseModeAction extends VenueTplEditorBaseAction {

    constructor(private _editorSrv: VenueTplEditorService) {
        super();
        this.setReadyStatus();
        this.do();
    }

    protected do(): void {
        this._editorSrv.capacityIncrease.setInSetup(true);
    }

    protected undo(): void {
        this._editorSrv.capacityIncrease.setInSetup(false);
    }
}
