import { inject, Injectable } from '@angular/core';
import { combineLatest, first, Observable, switchMap } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';
import { VenueTplEditorActionHistory, VenueTplEditorBaseAction } from './models/actions/venue-tpl-editor-base-action';
import { CapacityEditionCapability } from './models/venue-tpl-editor-capacity-edition-capability';
import { EditorMode, InteractionMode, VisualizationMode } from './models/venue-tpl-editor-modes.enum';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

type UndoType = 'undo' | 'redo' | 'fixUndo';

@Injectable()
export class VenueTplEditorService {

    private readonly _venueTplEdState = inject(VenueTplEditorState);

    readonly history = Object.freeze({
        enqueue: (action: VenueTplEditorBaseAction) => this.enqueueAction(action),
        enqueueFix: (action: VenueTplEditorBaseAction) => this.enqueueAction(action, true),
        undo: () => this.moveInHistory('undo'),
        fixUndo: () => this.moveInHistory('fixUndo'),
        redo: () => this.moveInHistory('redo'),
        hasUndoSteps$: () => this._venueTplEdState.history.getValue$().pipe(map(h => !!h.undoActions.length)),
        hasRedoSteps$: () => this._venueTplEdState.history.getValue$().pipe(map(h => !!h.redoActions.length)),
        clear: () => this._venueTplEdState.history.setValue({ undoActions: [], redoActions: [] }),
        errors$: () => this._venueTplEdState.actionErrors.getValue$()
    });

    readonly modes = Object.freeze({
        getOperatorMode$: () => this._venueTplEdState.operatorMode.getValue$(),
        setOperatorMode: (v: boolean) => this._venueTplEdState.operatorMode.setValue(v),
        getEditorMode$: () => this._venueTplEdState.mode.getValue$(),
        setEditorMode: (v: EditorMode) => this._venueTplEdState.mode.setValue(v),
        getInteractionMode$: () => this._venueTplEdState.interactionMode.getValue$(),
        setInteractionMode: (v: InteractionMode) => this._venueTplEdState.interactionMode.setValue(v),
        getVisualizationModes$: () => this._venueTplEdState.visualizationModes.getValue$(),
        setVisualizationModes: (v: VisualizationMode []) => this._venueTplEdState.visualizationModes.setValue(v)
    });

    readonly mmcIntegrationEnabled = Object.freeze({
        get$: () => this._venueTplEdState.mmcIntegrationEnabled.getValue$(),
        set: (v: boolean) => this._venueTplEdState.mmcIntegrationEnabled.setValue(v)
    });

    readonly inUse = Object.freeze({
        get$: () => this._venueTplEdState.inUse.getValue$(),
        set: (v: boolean) => this._venueTplEdState.inUse.setValue(v)
    });

    readonly capacityIncrease = Object.freeze({
        setEnabled: (v: boolean) => this._venueTplEdState.capacityIncreaseEnabled.setValue(v),
        setInProgress: (v: boolean) => this._venueTplEdState.increasingCapacity.setValue(v),
        setInSetup: (v: boolean) => this._venueTplEdState.inCapacityIncrease.setValue(v),
        isEnabled$: () => this._venueTplEdState.capacityIncreaseEnabled.getValue$(),
        isInProgress$: () => this._venueTplEdState.increasingCapacity.getValue$(),
        isInSetup: () => this._venueTplEdState.inCapacityIncrease.getValue$()
    });

    // Resets all state properties that determines the editor's lifecycle, must be called only with non modified data.
    resetState(): void {
        this.modes.setEditorMode(EditorMode.base);
        this.capacityIncrease.setInSetup(false);
        this.history.clear();
    }

    // combines inUse sate with capacityIncrease state to determine if the user can
    // create or edit the capacity elements of the template (seats and not numbered zones)
    getCapacityEditionCapability$(): Observable<CapacityEditionCapability> {
        return combineLatest([this.inUse.get$(), this.capacityIncrease.isInSetup()]).pipe(map(([inUse, inSetup]) =>
            !inUse ? CapacityEditionCapability.total :
                inSetup ? CapacityEditionCapability.increase :
                    CapacityEditionCapability.denied
        ));
    }

    // History

    private enqueueAction(action: VenueTplEditorBaseAction, tplFix: boolean = false): void {
        action.tplFix = tplFix;
        action.status$
            .pipe(
                first(Boolean),
                tap(status => {
                    if (status !== 'ready') {
                        this._venueTplEdState.actionErrors.setValue(status);
                    }
                }),
                filter(status => status === 'ready'),
                switchMap(() => this._venueTplEdState.history.getValue$()),
                take(1)
            )
            .subscribe(history => {
                history.undoActions.push(action);
                if (history.redoActions?.length) {
                    history.redoActions = [];
                }
                this._venueTplEdState.history.setValue(history);
            });
    }

    private moveInHistory(undoType: UndoType): Observable<boolean> {
        return this._venueTplEdState.history.getValue$().pipe(
            take(1),
            map(history => this.syncMoveInHistory(history, undoType))
        );
    }

    private syncMoveInHistory(history: VenueTplEditorActionHistory, undoType: UndoType): boolean {
        const sourceActions = undoType !== 'redo' ? history.undoActions : history.redoActions;
        const targetActions = undoType !== 'redo' ? history.redoActions : history.undoActions;
        let result = false;
        if (sourceActions.length) {
            if (!sourceActions[sourceActions.length - 1].tplFix || undoType !== 'undo') {
                const action = sourceActions.pop();
                undoType !== 'redo' ? action.executeUndo() : action.executeRedo();
                targetActions.push(action);
                this._venueTplEdState.history.setValue({ ...history });
                if (sourceActions.length
                    && !action.tplFix && !sourceActions[sourceActions.length - 1].tplFix // template fix actions cannot be combined
                    && sourceActions[sourceActions.length - 1].canCombineWith(action)
                ) {
                    this.syncMoveInHistory(history, undoType);
                }
                result = true;
            }
        }
        return result;
    }
}
