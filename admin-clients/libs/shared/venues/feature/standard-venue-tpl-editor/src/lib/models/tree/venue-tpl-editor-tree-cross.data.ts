import { VenueTplEditorSelection } from '../venue-tpl-editor-selection.model';

export interface VenueTplEditorTreeCrossData {
    selection?: VenueTplEditorSelection;
    currentViewId?: number;
    filterInViewItems?: boolean;
    aisleCounter?: number;
}
