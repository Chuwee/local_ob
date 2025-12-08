import { BehaviorSubject } from 'rxjs';

export type VenueTplEditorActionStatus = 'ready' | 'svgMaxLengthRaised' | 'uncontrolledError' | 'forbidden';

export abstract class VenueTplEditorBaseAction {

    private readonly _status = new BehaviorSubject<VenueTplEditorActionStatus>(null);

    readonly status$ = this._status.asObservable();

    tplFix: boolean;

    setReadyStatus(): void {
        this.status = 'ready';
    }

    protected set status(status: VenueTplEditorActionStatus) {
        this._status.next(status);
    }

    protected get isReady(): boolean {
        return this._status.value === 'ready';
    }

    executeRedo(): void {
        if (this._status.value === 'ready') {
            this.do();
        } else {
            console.warn('trying to redo not ready action', this);
        }
    }

    executeUndo(): void {
        if (this._status.value === 'ready') {
            this.undo();
        } else {
            console.warn('trying to undo not ready action', this);
        }
    }

    // eslint-disable-next-line unused-imports/no-unused-vars
    canCombineWith(action: VenueTplEditorBaseAction): boolean {
        return false;
    }

    protected abstract do(): void;

    protected abstract undo(): void;

    protected changedFieldsComparator(changedFields1: { [key: string]: boolean }, changedFields2: { [key: string]: boolean }): boolean {
        const changedFields1List = Object.keys(changedFields1).filter(key => changedFields1[key]);
        const changedFields2List = Object.keys(changedFields2).filter(key => changedFields2[key]);
        return changedFields1List.length === 0 || changedFields2List.length === 0
            || (changedFields1List.length === changedFields2List.length
                && changedFields1List.every(key => changedFields2List.includes(key)));
    }
}

export interface VenueTplEditorActionHistory {
    undoActions: VenueTplEditorBaseAction[];
    redoActions: VenueTplEditorBaseAction[];
}
